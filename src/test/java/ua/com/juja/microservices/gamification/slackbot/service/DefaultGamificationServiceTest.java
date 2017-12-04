package ua.com.juja.microservices.gamification.slackbot.service;

import ua.com.juja.microservices.gamification.slackbot.dao.GamificationRepository;
import ua.com.juja.slack.command.handler.model.SlackParsedCommand;
import ua.com.juja.slack.command.handler.model.UserDTO;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.InterviewAchievement;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.TeamAchievement;
import ua.com.juja.microservices.gamification.slackbot.model.DTO.TeamDTO;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.CodenjoyAchievement;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.DailyAchievement;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.ThanksAchievement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ua.com.juja.slack.command.handler.service.SlackCommandHandlerService;

import javax.inject.Inject;
import java.util.*;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Danil Kuznetsov
 * @author Nikolay Horushko
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultGamificationServiceTest {

    private final String FROM_USER_SLACK_ID = "UFDR97JLA";
    @MockBean
    private GamificationRepository gamificationRepository;
    @MockBean
    private TeamService teamService;
    @MockBean
    private UserService userService;
    @MockBean
    private SlackCommandHandlerService slackCommandHandlerService;
    @Inject
    private GamificationService gamificationService;
    private UserDTO fromUser = new UserDTO("uuid-from-user", FROM_USER_SLACK_ID);
    private UserDTO user1 = new UserDTO("uuid-user-1", "U1DR97JLA");
    private UserDTO user2 = new UserDTO("uuid-user-2", "U2DR97JLA");
    private UserDTO user3 = new UserDTO("uuid-user-3", "U3DR97JLA");

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldSaveNewDaily() {

        //given
        final String TEXT_COMMAND = "daily report";
        final List<UserDTO> usersInText = Collections.emptyList();
        final String[] SAVED_ACHIEVEMENT_ID = {"100"};
        final String EXPECTED_RESPONSE_TO_SLACK = "Thanks, your daily report saved.";


        when(slackCommandHandlerService.createSlackParsedCommand(FROM_USER_SLACK_ID, TEXT_COMMAND))
                .thenReturn(new SlackParsedCommand(fromUser, TEXT_COMMAND, usersInText));
        when(gamificationRepository.saveDailyAchievement(any(DailyAchievement.class)))
                .thenReturn(SAVED_ACHIEVEMENT_ID);

        //when
        String result = gamificationService.sendDailyAchievement(FROM_USER_SLACK_ID, TEXT_COMMAND);

        //then
        assertThat(result, equalTo(EXPECTED_RESPONSE_TO_SLACK));
        verify(slackCommandHandlerService).createSlackParsedCommand(FROM_USER_SLACK_ID, TEXT_COMMAND);
        verify(gamificationRepository).saveDailyAchievement(any(DailyAchievement.class));
        verifyNoMoreInteractions(slackCommandHandlerService, gamificationRepository);
    }

    @Test
    public void saveNewDailyWhenRepositoryReturnNotOneIdShouldErrorMessage() {
        //given
        final String TEXT_COMMAND = "daily report";
        final List<UserDTO> usersInText = Collections.emptyList();
        final String[] SAVED_ACHIEVEMENT_ID = {"100", "200"};
        final String EXPECTED_RESPONSE_TO_SLACK = "Something went wrong and we didn't save your daily report";


        when(slackCommandHandlerService.createSlackParsedCommand(FROM_USER_SLACK_ID, TEXT_COMMAND))
                .thenReturn(new SlackParsedCommand(fromUser, TEXT_COMMAND, usersInText));
        when(gamificationRepository.saveDailyAchievement(any(DailyAchievement.class)))
                .thenReturn(SAVED_ACHIEVEMENT_ID);

        //when
        String result = gamificationService.sendDailyAchievement(FROM_USER_SLACK_ID, TEXT_COMMAND);

        //then
        assertThat(result, equalTo(EXPECTED_RESPONSE_TO_SLACK));
        verify(slackCommandHandlerService).createSlackParsedCommand(FROM_USER_SLACK_ID, TEXT_COMMAND);
        verify(gamificationRepository).saveDailyAchievement(any(DailyAchievement.class));
        verifyNoMoreInteractions(slackCommandHandlerService, gamificationRepository);
    }

    @Test
    public void shouldSaveNewCodenjoy() {

        //given
        final String TEXT_COMMAND = "-1th <@U1DR97JLA|slackName1> -2th <@U2DR97JLA|slackName2> -3th <@U3DR97JLA|slackName3>";
        final List<UserDTO> users = Arrays.asList(user1, user2, user3);
        final String[] SAVED_ACHIEVEMENT_ID = {"100", "101", "102"};
        final String EXPECTED_RESPONSE_TO_SLACK = "Thanks, we awarded the users. First place: U1DR97JLA, " +
                "Second place: U2DR97JLA, Third place: U3DR97JLA";

        when(slackCommandHandlerService.createSlackParsedCommand(FROM_USER_SLACK_ID, TEXT_COMMAND))
                .thenReturn(new SlackParsedCommand(fromUser, TEXT_COMMAND, users));
        when(gamificationRepository.saveCodenjoyAchievement(any(CodenjoyAchievement.class)))
                .thenReturn(SAVED_ACHIEVEMENT_ID);

        //when
        String result = gamificationService.sendCodenjoyAchievement(FROM_USER_SLACK_ID, TEXT_COMMAND);

        //then
        assertThat(result, equalTo(EXPECTED_RESPONSE_TO_SLACK));
        verify(slackCommandHandlerService).createSlackParsedCommand(FROM_USER_SLACK_ID, TEXT_COMMAND);
        verify(gamificationRepository).saveCodenjoyAchievement(any(CodenjoyAchievement.class));
        verifyNoMoreInteractions(slackCommandHandlerService, gamificationRepository);
    }

    @Test
    public void saveNewCodenjoyWhenRepositoryReturnNotThreeIdShouldErrorMessage() {
        //given
        final String TEXT_COMMAND = "-1th <@U1DR97JLA|slackName1> -2th <@U2DR97JLA|slackName2> -3th <@U3DR97JLA|slackName3>";
        final List<UserDTO> usersInText = Arrays.asList(user1, user2, user3);
        final String[] SAVED_ACHIEVEMENT_ID = {"100", "101", "102", "104"};
        final String EXPECTED_RESPONSE_TO_SLACK = "Something went wrong and we didn't award the users";

        when(slackCommandHandlerService.createSlackParsedCommand(FROM_USER_SLACK_ID, TEXT_COMMAND))
                .thenReturn(new SlackParsedCommand(fromUser, TEXT_COMMAND, usersInText));
        when(gamificationRepository.saveCodenjoyAchievement(any(CodenjoyAchievement.class)))
                .thenReturn(SAVED_ACHIEVEMENT_ID);

        //when
        String result = gamificationService.sendCodenjoyAchievement(FROM_USER_SLACK_ID, TEXT_COMMAND);

        //then
        assertThat(result, equalTo(EXPECTED_RESPONSE_TO_SLACK));
        verify(slackCommandHandlerService).createSlackParsedCommand(FROM_USER_SLACK_ID, TEXT_COMMAND);
        verify(gamificationRepository).saveCodenjoyAchievement(any(CodenjoyAchievement.class));
        verifyNoMoreInteractions(slackCommandHandlerService, gamificationRepository);
    }

    @Test
    public void shouldSaveNewThanks() {

        //given
        final String TEXT_COMMAND = "thanks <@U1DR97JLA|slackName1> comment";
        final List<UserDTO> users = Arrays.asList(user1);
        final String[] SAVED_ACHIEVEMENT_ID = {"100"};
        final String EXPECTED_RESPONSE_TO_SLACK = "Thanks, your 'thanks' for U1DR97JLA saved.";

        when(slackCommandHandlerService.createSlackParsedCommand(FROM_USER_SLACK_ID, TEXT_COMMAND))
                .thenReturn(new SlackParsedCommand(fromUser, TEXT_COMMAND, users));
        when(gamificationRepository.saveThanksAchievement(any(ThanksAchievement.class)))
                .thenReturn(SAVED_ACHIEVEMENT_ID);

        //when
        String result = gamificationService.sendThanksAchievement(FROM_USER_SLACK_ID, TEXT_COMMAND);

        //then
        assertThat(result, equalTo(EXPECTED_RESPONSE_TO_SLACK));
        verify(slackCommandHandlerService).createSlackParsedCommand(FROM_USER_SLACK_ID, TEXT_COMMAND);
        verify(gamificationRepository).saveThanksAchievement(any(ThanksAchievement.class));
        verifyNoMoreInteractions(slackCommandHandlerService, gamificationRepository);
    }

    @Test
    public void shouldSaveSecondThanksAndAddExtraScore() {

        //given
        final String TEXT_COMMAND = "thanks <@U1DR97JLA|slackName1> comment";
        final List<UserDTO> usersInText = Arrays.asList(user1);
        final String[] SAVED_ACHIEVEMENT_ID = {"100", "101"};
        final String EXPECTED_RESPONSE_TO_SLACK = "Thanks, your 'thanks' for U1DR97JLA saved. Also you received +1 " +
                "for your activity.";

        when(slackCommandHandlerService.createSlackParsedCommand(FROM_USER_SLACK_ID, TEXT_COMMAND))
                .thenReturn(new SlackParsedCommand(fromUser, TEXT_COMMAND, usersInText));
        when(gamificationRepository.saveThanksAchievement(any(ThanksAchievement.class)))
                .thenReturn(SAVED_ACHIEVEMENT_ID);

        //when
        String result = gamificationService.sendThanksAchievement(FROM_USER_SLACK_ID, TEXT_COMMAND);

        //then
        assertThat(result, equalTo(EXPECTED_RESPONSE_TO_SLACK));
        verify(slackCommandHandlerService).createSlackParsedCommand(FROM_USER_SLACK_ID, TEXT_COMMAND);
        verify(gamificationRepository).saveThanksAchievement(any(ThanksAchievement.class));
        verifyNoMoreInteractions(slackCommandHandlerService, gamificationRepository);
    }

    @Test
    public void shouldSaveNewInterviewAchievement() {

        //given
        final String TEXT_COMMAND = "interview report";
        final List<UserDTO> usersInText = Collections.emptyList();
        final String[] SAVED_ACHIEVEMENT_ID = {"100"};
        final String EXPECTED_RESPONSE_TO_SLACK = "Thanks. Your interview saved.";

        when(slackCommandHandlerService.createSlackParsedCommand(FROM_USER_SLACK_ID, TEXT_COMMAND))
                .thenReturn(new SlackParsedCommand(fromUser, TEXT_COMMAND, usersInText));
        when(gamificationRepository.saveInterviewAchievement(any(InterviewAchievement.class)))
                .thenReturn(SAVED_ACHIEVEMENT_ID);

        //when
        String result = gamificationService.sendInterviewAchievement(FROM_USER_SLACK_ID, TEXT_COMMAND);

        //then
        assertThat(result, equalTo(EXPECTED_RESPONSE_TO_SLACK));
        verify(slackCommandHandlerService).createSlackParsedCommand(FROM_USER_SLACK_ID, TEXT_COMMAND);
        verify(gamificationRepository).saveInterviewAchievement(any(InterviewAchievement.class));
        verifyNoMoreInteractions(slackCommandHandlerService, gamificationRepository);
    }

    @Test
    public void saveNewInterviewWhenRepositoryReturnNotOneIdShouldErrorMessage() {
        //given
        final String TEXT_COMMAND = "interview report";
        final List<UserDTO> usersInText = Collections.emptyList();
        final String[] SAVED_ACHIEVEMENT_ID = {"100", "200"};
        final String EXPECTED_RESPONSE_TO_SLACK = "Something went wrong and we didn't save your interview";


        when(slackCommandHandlerService.createSlackParsedCommand(FROM_USER_SLACK_ID, TEXT_COMMAND))
                .thenReturn(new SlackParsedCommand(fromUser, TEXT_COMMAND, usersInText));
        when(gamificationRepository.saveInterviewAchievement(any(InterviewAchievement.class)))
                .thenReturn(SAVED_ACHIEVEMENT_ID);

        //when
        String result = gamificationService.sendInterviewAchievement(FROM_USER_SLACK_ID, TEXT_COMMAND);

        //then
        assertThat(result, equalTo(EXPECTED_RESPONSE_TO_SLACK));
        verify(slackCommandHandlerService).createSlackParsedCommand(FROM_USER_SLACK_ID, TEXT_COMMAND);
        verify(gamificationRepository).saveInterviewAchievement(any(InterviewAchievement.class));
        verifyNoMoreInteractions(slackCommandHandlerService, gamificationRepository);
    }

    @Test
    public void shouldSaveNewTeamAchievement() {

        //given
        final String textCommand = "team report";
        final List<UserDTO> usersInText = Collections.emptyList();
        final String[] ids = {"100", "101", "102", "103"};
        final String expectedResponceToSlack =
                "Thanks, your team report saved. Members: [@U1DR97JLA, @U2DR97JLA, @U3DR97JLA, @U4DR97JLA]";
        final Set<String> members = new LinkedHashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4"));
        final TeamDTO team = new TeamDTO(members);
        final Set<UserDTO> usersResponce = new LinkedHashSet<>(Arrays.asList(
                new UserDTO("uuid1", "@U1DR97JLA"),
                new UserDTO("uuid2", "@U2DR97JLA"),
                new UserDTO("uuid3", "@U3DR97JLA"),
                new UserDTO("uuid4", "@U4DR97JLA")));

        //when
        when(slackCommandHandlerService.createSlackParsedCommand(FROM_USER_SLACK_ID, textCommand))
                .thenReturn(new SlackParsedCommand(fromUser, textCommand, usersInText));
        when(teamService.getTeamByUserUuid(fromUser.getUuid())).thenReturn(team);
        when(userService.receiveUsersByUuids(members)).thenReturn(usersResponce);
        when(gamificationRepository.saveTeamAchievement(any(TeamAchievement.class))).thenReturn(ids);
        String result = gamificationService.sendTeamAchievement(FROM_USER_SLACK_ID, textCommand);

        //then
        assertThat(result, equalTo(expectedResponceToSlack));
    }

    @Test
    public void shouldSaveWrongNumberTeamAchievement() {

        //given
        final String textCommand = "team report";
        final List<UserDTO> usersInText = Collections.emptyList();
        final String[] ids = {"100", "101", "102"};
        final String expectedResponceToSlack = "Something went wrong during saving your team report";
        final Set<String> members = new LinkedHashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4"));
        final TeamDTO team = new TeamDTO(members);
        final Set<UserDTO> usersResponce = new LinkedHashSet<>(Arrays.asList(
                new UserDTO("uuid1", "@U1DR97JLA"),
                new UserDTO("uuid2", "@U2DR97JLA"),
                new UserDTO("uuid3", "@U3DR97JLA"),
                new UserDTO("uuid4", "@U4DR97JLA")));

        //when
        when(slackCommandHandlerService.createSlackParsedCommand(FROM_USER_SLACK_ID, textCommand))
                .thenReturn(new SlackParsedCommand(fromUser, textCommand, usersInText));
        when(teamService.getTeamByUserUuid(fromUser.getUuid())).thenReturn(team);
        when(userService.receiveUsersByUuids(members)).thenReturn(usersResponce);
        when(gamificationRepository.saveTeamAchievement(any(TeamAchievement.class))).thenReturn(ids);
        String result = gamificationService.sendTeamAchievement(FROM_USER_SLACK_ID, textCommand);

        //then
        assertThat(result, equalTo(expectedResponceToSlack));
    }
}