package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.dao.GamificationRepository;
import juja.microservices.gamification.slackbot.model.DTO.TeamDTO;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import juja.microservices.gamification.slackbot.model.SlackParsedCommand;
import juja.microservices.gamification.slackbot.model.achievements.*;
import juja.microservices.gamification.slackbot.service.impl.SlackCommandService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.*;

import static juja.microservices.gamification.slackbot.model.SlackParsedCommand.convertSlackUserInFullSlackFormat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Danil Kuznetsov
 * @author Nikolay Horushko
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultGamificationServiceTest {

    @MockBean
    private GamificationRepository gamificationRepository;
    @MockBean
    private TeamService teamService;
    @MockBean
    private UserService userService;
    @MockBean
    private SlackCommandService slackCommandService;
    @Inject
    private GamificationService gamificationService;

    private final String SLACK_USER_FROM = "from-user";
    private final String SLACK_USER1 = "slack1";
    private final String SLACK_USER2 = "slack2";
    private final String SLACK_USER3 = "slack3";

    private UserDTO userFrom = new UserDTO("uuid-from-user", SLACK_USER_FROM);
    private UserDTO user1 = new UserDTO("uuid-user-1", SLACK_USER1);
    private UserDTO user2 = new UserDTO("uuid-user-2", SLACK_USER2);
    private UserDTO user3 = new UserDTO("uuid-user-3", SLACK_USER3);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldSaveNewDaily() {

        //given
        String textCommand = "daily report";
        String[] savedAchievementId = {"100"};
        String expectedResponseToSlack = "Thanks, your daily report saved.";

        Map<String, UserDTO> users = new HashMap<>();
        users.put(userFrom.getSlackUser(), userFrom);

        when(slackCommandService.createSlackCommand(SLACK_USER_FROM, textCommand))
                .thenReturn(new SlackParsedCommand(userFrom.getSlackUser(), textCommand, users));
        when(gamificationRepository.saveDailyAchievement(any(DailyAchievement.class)))
                .thenReturn(savedAchievementId);

        //when
        String result = gamificationService.sendDailyAchievement(SLACK_USER_FROM, textCommand);

        //then
        assertThat(result, equalTo(expectedResponseToSlack));
        verify(slackCommandService).createSlackCommand(SLACK_USER_FROM, textCommand);
        verify(gamificationRepository).saveDailyAchievement(any(DailyAchievement.class));
        verifyNoMoreInteractions(slackCommandService, gamificationRepository);
    }

    @Test
    public void saveNewDailyWhenRepositoryReturnNotOneIdShouldErrorMessage() {
        //given
        String textCommand = "daily report";
        String[] savedAchievementId = {"100", "200"};
        String expectedResponseToSlack = "Something went wrong and we didn't save your daily report";

        Map<String, UserDTO> users = new HashMap<>();
        users.put(userFrom.getSlackUser(), userFrom);

        when(slackCommandService.createSlackCommand(SLACK_USER_FROM, textCommand))
                .thenReturn(new SlackParsedCommand(userFrom.getSlackUser(), textCommand, users));
        when(gamificationRepository.saveDailyAchievement(any(DailyAchievement.class)))
                .thenReturn(savedAchievementId);

        //when
        String result = gamificationService.sendDailyAchievement(SLACK_USER_FROM, textCommand);

        //then
        assertThat(result, equalTo(expectedResponseToSlack));
        verify(slackCommandService).createSlackCommand(SLACK_USER_FROM, textCommand);
        verify(gamificationRepository).saveDailyAchievement(any(DailyAchievement.class));
        verifyNoMoreInteractions(slackCommandService, gamificationRepository);
    }

    @Test
    public void shouldSaveNewCodenjoy() {

        //given
        String textCommand = String.format("-1th %s -2th %s -3th %s",
                convertSlackUserInFullSlackFormat(user1.getSlackUser()),
                convertSlackUserInFullSlackFormat(user2.getSlackUser()),
                convertSlackUserInFullSlackFormat(user3.getSlackUser())
        );

        String[] savedAchievementId = {"100", "101", "102"};
        String expectedResponseToSlack = String.format("Thanks, we awarded the users. " +
                        "First place: %s, Second place: %s, Third place: %s",
                user1.getSlackUser(), user2.getSlackUser(), user3.getSlackUser());

        Map<String, UserDTO> users = new HashMap<>();
        users.put(userFrom.getSlackUser(), userFrom);
        users.put(user1.getSlackUser(), user1);
        users.put(user2.getSlackUser(), user2);
        users.put(user3.getSlackUser(), user3);

        when(slackCommandService.createSlackCommand(SLACK_USER_FROM, textCommand))
                .thenReturn(new SlackParsedCommand(userFrom.getSlackUser(), textCommand, users));
        when(gamificationRepository.saveCodenjoyAchievement(any(CodenjoyAchievement.class)))
                .thenReturn(savedAchievementId);

        //when
        String result = gamificationService.sendCodenjoyAchievement(SLACK_USER_FROM, textCommand);

        //then
        assertThat(result, equalTo(expectedResponseToSlack));
        verify(slackCommandService).createSlackCommand(SLACK_USER_FROM, textCommand);
        verify(gamificationRepository).saveCodenjoyAchievement(any(CodenjoyAchievement.class));
        verifyNoMoreInteractions(slackCommandService, gamificationRepository);
    }

    @Test
    public void saveNewCodenjoyWhenRepositoryReturnNotThreeIdShouldErrorMessage() {
        //given

        String textCommand = String.format("-1th %s -2th %s -3th %s",
                convertSlackUserInFullSlackFormat(user1.getSlackUser()),
                convertSlackUserInFullSlackFormat(user2.getSlackUser()),
                convertSlackUserInFullSlackFormat(user3.getSlackUser())
        );

        String[] savedAchievementId = {"100", "101", "102", "104"};
        String expectedResponseToSlack = "Something went wrong and we didn't award the users";

        Map<String, UserDTO> users = new HashMap<>();
        users.put(userFrom.getSlackUser(), userFrom);
        users.put(user1.getSlackUser(), user1);
        users.put(user2.getSlackUser(), user2);
        users.put(user3.getSlackUser(), user3);

        when(slackCommandService.createSlackCommand(SLACK_USER_FROM, textCommand))
                .thenReturn(new SlackParsedCommand(userFrom.getSlackUser(), textCommand, users));
        when(gamificationRepository.saveCodenjoyAchievement(any(CodenjoyAchievement.class)))
                .thenReturn(savedAchievementId);

        //when
        String result = gamificationService.sendCodenjoyAchievement(SLACK_USER_FROM, textCommand);

        //then
        assertThat(result, equalTo(expectedResponseToSlack));
        verify(slackCommandService).createSlackCommand(SLACK_USER_FROM, textCommand);
        verify(gamificationRepository).saveCodenjoyAchievement(any(CodenjoyAchievement.class));
        verifyNoMoreInteractions(slackCommandService, gamificationRepository);
    }

    @Test
    public void shouldSaveNewThanks() {

        //given
        String textCommand = String.format("thanks %s comment",
                convertSlackUserInFullSlackFormat(user1.getSlackUser())
        );

        String[] savedAchievementId = {"100"};
        String expectedResponseToSlack = String.format("Thanks, your 'thanks' for %s saved.",
                user1.getSlackUser()
        );

        Map<String, UserDTO> users = new HashMap<>();
        users.put(userFrom.getSlackUser(), userFrom);
        users.put(user1.getSlackUser(), user1);

        when(slackCommandService.createSlackCommand(SLACK_USER_FROM, textCommand))
                .thenReturn(new SlackParsedCommand(userFrom.getSlackUser(), textCommand, users));
        when(gamificationRepository.saveThanksAchievement(any(ThanksAchievement.class)))
                .thenReturn(savedAchievementId);

        //when
        String result = gamificationService.sendThanksAchievement(SLACK_USER_FROM, textCommand);

        //then
        assertThat(result, equalTo(expectedResponseToSlack));
        verify(slackCommandService).createSlackCommand(SLACK_USER_FROM, textCommand);
        verify(gamificationRepository).saveThanksAchievement(any(ThanksAchievement.class));
        verifyNoMoreInteractions(slackCommandService, gamificationRepository);
    }

    @Test
    public void shouldSaveSecondThanksAndAddExtraScore() {

        //given
        final String textCommand = String.format("thanks %s comment",
                convertSlackUserInFullSlackFormat(user1.getSlackUser())
        );
        final String[] savedAchievementId = {"100", "101"};
        final String expectedResponseToSlack = String.format("Thanks, your 'thanks' for %s saved. " +
                "Also you received +1 for your activity.", user1.getSlackUser());

        Map<String, UserDTO> users = new HashMap<>();
        users.put(userFrom.getSlackUser(), userFrom);
        users.put(user1.getSlackUser(), user1);

        when(slackCommandService.createSlackCommand(SLACK_USER_FROM, textCommand))
                .thenReturn(new SlackParsedCommand(userFrom.getSlackUser(), textCommand, users));
        when(gamificationRepository.saveThanksAchievement(any(ThanksAchievement.class)))
                .thenReturn(savedAchievementId);

        //when
        String result = gamificationService.sendThanksAchievement(SLACK_USER_FROM, textCommand);

        //then
        assertThat(result, equalTo(expectedResponseToSlack));
        verify(slackCommandService).createSlackCommand(SLACK_USER_FROM, textCommand);
        verify(gamificationRepository).saveThanksAchievement(any(ThanksAchievement.class));
        verifyNoMoreInteractions(slackCommandService, gamificationRepository);
    }

    @Test
    public void shouldSaveNewInterviewAchievement() {

        //given
        String textCommand = "interview report";
        String[] savedAchievementId = {"100"};
        String expectedResponseToSlack = "Thanks. Your interview saved.";

        Map<String, UserDTO> users = new HashMap<>();
        users.put(userFrom.getSlackUser(), userFrom);

        when(slackCommandService.createSlackCommand(SLACK_USER_FROM, textCommand))
                .thenReturn(new SlackParsedCommand(userFrom.getSlackUser(), textCommand, users));
        when(gamificationRepository.saveInterviewAchievement(any(InterviewAchievement.class)))
                .thenReturn(savedAchievementId);

        //when
        String result = gamificationService.sendInterviewAchievement(SLACK_USER_FROM, textCommand);

        //then
        assertThat(result, equalTo(expectedResponseToSlack));
        verify(slackCommandService).createSlackCommand(SLACK_USER_FROM, textCommand);
        verify(gamificationRepository).saveInterviewAchievement(any(InterviewAchievement.class));
        verifyNoMoreInteractions(slackCommandService, gamificationRepository);
    }

    @Test
    public void saveNewInterviewWhenRepositoryReturnNotOneIdShouldErrorMessage() {
        //given
        String textCommand = "interview report";
        String[] savedAchievementId = {"100", "200"};
        String expectedResponseToSlack = "Something went wrong and we didn't save your interview";

        Map<String, UserDTO> users = new HashMap<>();
        users.put(userFrom.getSlackUser(), userFrom);

        when(slackCommandService.createSlackCommand(SLACK_USER_FROM, textCommand))
                .thenReturn(new SlackParsedCommand(userFrom.getSlackUser(), textCommand, users));
        when(gamificationRepository.saveInterviewAchievement(any(InterviewAchievement.class)))
                .thenReturn(savedAchievementId);

        //when
        String result = gamificationService.sendInterviewAchievement(SLACK_USER_FROM, textCommand);

        //then
        assertThat(result, equalTo(expectedResponseToSlack));
        verify(slackCommandService).createSlackCommand(SLACK_USER_FROM, textCommand);
        verify(gamificationRepository).saveInterviewAchievement(any(InterviewAchievement.class));
        verifyNoMoreInteractions(slackCommandService, gamificationRepository);
    }

    @Test
    public void shouldSaveNewTeamAchievement() {

        //given
        String textCommand = "team report";
        String[] ids = {"100", "101", "102", "103"};

        String expectedResponseToSlack = String.format("Thanks, your team report saved. Members: [%s, %s, %s, %s]",
                userFrom.getSlackUser(), user1.getSlackUser(), user2.getSlackUser(), user3.getSlackUser());

        Set<String> members = new LinkedHashSet<>(Arrays.asList(userFrom.getUuid(), user1.getUuid(),
                user2.getUuid(), user3.getUuid()));
        TeamDTO team = new TeamDTO(members);
        Set<UserDTO> usersResponse = new LinkedHashSet<>(Arrays.asList(userFrom, user1, user2, user3));

        Map<String, UserDTO> users = new HashMap<>();
        users.put(userFrom.getSlackUser(), userFrom);

        //when
        when(slackCommandService.createSlackCommand(SLACK_USER_FROM, textCommand))
                .thenReturn(new SlackParsedCommand(userFrom.getSlackUser(), textCommand, users));
        when(teamService.getTeamByUserUuid(userFrom.getUuid())).thenReturn(team);
        when(userService.findUsersByUuids(members)).thenReturn(usersResponse);
        when(gamificationRepository.saveTeamAchievement(any(TeamAchievement.class))).thenReturn(ids);

        String result = gamificationService.sendTeamAchievement(SLACK_USER_FROM, textCommand);

        //then
        assertThat(result, equalTo(expectedResponseToSlack));
    }

    @Test
    public void shouldSaveWrongNumberTeamAchievement() {

        //given
        String textCommand = "team report";
        String[] ids = {"100", "101", "102"};
        String expectedResponceToSlack = "Something went wrong during saving your team report";

        Set<String> members = new LinkedHashSet<>(Arrays.asList(userFrom.getUuid(), user1.getUuid(),
                user2.getUuid(), user3.getUuid()));

        TeamDTO team = new TeamDTO(members);
        Set<UserDTO> usersResponse = new LinkedHashSet<>(Arrays.asList(userFrom, user1, user2, user3));

        Map<String, UserDTO> users = new HashMap<>();
        users.put(userFrom.getSlackUser(), userFrom);

        //when
        when(slackCommandService.createSlackCommand(SLACK_USER_FROM, textCommand))
                .thenReturn(new SlackParsedCommand(userFrom.getSlackUser(), textCommand, users));
        when(teamService.getTeamByUserUuid(userFrom.getUuid())).thenReturn(team);
        when(userService.findUsersByUuids(members)).thenReturn(usersResponse);
        when(gamificationRepository.saveTeamAchievement(any(TeamAchievement.class))).thenReturn(ids);
        String result = gamificationService.sendTeamAchievement(SLACK_USER_FROM, textCommand);

        //then
        assertThat(result, equalTo(expectedResponceToSlack));
    }
}