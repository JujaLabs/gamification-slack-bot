package ua.com.juja.microservices.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import ua.com.juja.microservices.gamification.slackbot.GamificationSlackBotApplication;
import ua.com.juja.microservices.gamification.slackbot.dao.feign.GamificationClient;
import ua.com.juja.microservices.gamification.slackbot.dao.feign.TeamsClient;
import ua.com.juja.microservices.gamification.slackbot.dao.feign.UsersClient;
import ua.com.juja.microservices.gamification.slackbot.model.DTO.SlackIdRequest;
import ua.com.juja.microservices.gamification.slackbot.model.DTO.TeamDTO;
import ua.com.juja.slack.command.handler.model.SlackParsedCommand;
import ua.com.juja.slack.command.handler.model.UserDTO;
import ua.com.juja.microservices.gamification.slackbot.model.DTO.UuidRequest;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.CodenjoyAchievement;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.DailyAchievement;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.InterviewAchievement;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.TeamAchievement;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.ThanksAchievement;
import ua.com.juja.microservices.utils.SlackUrlUtils;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Nikolay Horushko
 * @author Ivan Shapovalov
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GamificationSlackBotApplication.class})
@AutoConfigureMockMvc
public class GamificationSlackBotIntegrationTest {
    private final static String INSTANT_MESSAGE = "Your command accepted. Please wait...";
    private final static String CODENJOY_THANKS_MESSAGE = "Thanks, we awarded the users. " +
            "First place: %s, Second place: %s, Third place: %s";
    private final static String DAILY_THANKS_MESSAGE = "Thanks, your daily report saved.";
    private final static String THANKS_ONE_THANKS_MESSAGE = "Thanks, your 'thanks' for %s saved.";
    private final static String INTERVIEW_THANKS_MESSAGE = "Thanks. Your interview saved.";
    private final static String TEAM_THANKS_MESSAGE =
            "Thanks, your team report saved. Members: [@from-user, @slack2, @slack2, @slack3]";
    private final static String responseUrl = "http://example.com";

    @Inject
    private RestTemplate restTemplate;

    @Inject
    private MockMvc mvc;
    private MockRestServiceServer mockServer;

    private String gamificationSlackbotDailyUrl="/v1/commands/daily";
    private String gamificationSlackbotThanksUrl="/v1/commands/thanks";
    private String gamificationSlackbotCodenjoyUrl="/v1/commands/codenjoy";
    private String gamificationSlackbotInterviewUrl="/v1/commands/interview";
    private String gamificationSlackbotTeamUrl="/v1/commands/team";

    private UserDTO user1 = new UserDTO("f2034f22-562b-4e02-bfcf-ec615c1ba62b", "U1DR97JLA");
    private UserDTO user2 = new UserDTO("f2034f33-563c-4e03-bfcf-ec615c1ba63c", "U2DR97JLA");
    private UserDTO user3 = new UserDTO("f2034f44-563d-4e04-bfcf-ec615c1ba64d", "U3DR97JLA");
    private UserDTO userFrom = new UserDTO("f2034f11-561a-4e01-bfcf-ec615c1ba61a", "UNJSD9OKM");

    @MockBean
    private GamificationClient gamificationClient;
    @MockBean
    private TeamsClient teamsClient;
    @MockBean
    private UsersClient usersClient;

    @Before
    public void setup() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void onReceiveSlashCommandCodenjoyReturnOkRichMessage() throws Exception {
        //given
        final String codenjoyCommandText = "-1th <@U1DR97JLA|slackName1> -2th <@U2DR97JLA|slackName2> -3th <@U3DR97JLA|slackName3>";
        final List<UserDTO> usersInText = Arrays.asList(user1, user2, user3);
        final SlackParsedCommand slackParsedCommand = new SlackParsedCommand(userFrom, codenjoyCommandText, usersInText);
        final CodenjoyAchievement expectedCodenjoyAchievement = new CodenjoyAchievement(slackParsedCommand);

        final List<String> expectedRequestToUserServiceSlackIds = Arrays.asList(user1.getSlackId(), user2.getSlackId(),
                user3.getSlackId(), userFrom.getSlackId());
        final List<UserDTO> responseFromUserService = Arrays.asList(user1, user2, user3, userFrom);


        ArgumentCaptor<SlackIdRequest> captorSlackIdRequest = ArgumentCaptor.forClass(SlackIdRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackIdRequest.capture())).thenReturn(responseFromUserService);

        ArgumentCaptor<CodenjoyAchievement> captorCodenjoyAchievement = ArgumentCaptor.forClass(CodenjoyAchievement.class);
        final String[] achievementUuids = {"101", "102", "103"};
        when(gamificationClient.saveCodenjoyAchievement(captorCodenjoyAchievement.capture())).thenReturn(achievementUuids);

        mockSlackResponseUrl(responseUrl, new RichMessage(String.format(CODENJOY_THANKS_MESSAGE,
                user1.getSlackId(), user2.getSlackId(), user3.getSlackId())));

        //when
        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotCodenjoyUrl),
                SlackUrlUtils.getUriVars("slashCommandToken", "/codenjoy", codenjoyCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        //then
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(captorSlackIdRequest.getValue().getSlackIds())
                    .as("'captorSlackIdRequest' slackIds not contains 'slackIds'")
                    .containsExactlyInAnyOrder(expectedRequestToUserServiceSlackIds.toArray(new String[expectedRequestToUserServiceSlackIds.size()]));
            soft.assertThat(captorCodenjoyAchievement.getValue())
                    .as("'captorCodenjoyAchievement' is not 'codenjoyAchievement'")
                    .isEqualTo(expectedCodenjoyAchievement);
        });
        verify(usersClient).findUsersBySlackNames(captorSlackIdRequest.capture());
        verify(gamificationClient).saveCodenjoyAchievement(captorCodenjoyAchievement.capture());
        verifyNoMoreInteractions(usersClient, gamificationClient);
    }

    @Test
    public void returnOkMessageIfCondejoyCommandTokensWithoutSpaces() throws Exception {
        //given
        final String codenjoyCommandText = "-1th<@U1DR97JLA|slackName1> -2th<@U2DR97JLA|slackName2> -3th<@U3DR97JLA|slackName3>";
        final List<UserDTO> usersInText = Arrays.asList(user1, user2, user3);
        final SlackParsedCommand slackParsedCommand = new SlackParsedCommand(userFrom, codenjoyCommandText, usersInText);
        final CodenjoyAchievement expectedCodenjoyAchievement = new CodenjoyAchievement(slackParsedCommand);

        final List<String> expectedRequestToUserServiceSlackIds = Arrays.asList(user1.getSlackId(), user2.getSlackId(),
                user3.getSlackId(), userFrom.getSlackId());
        final List<UserDTO> responseFromUserService = Arrays.asList(user1, user2, user3, userFrom);

        ArgumentCaptor<SlackIdRequest> captorSlackIdsRequest = ArgumentCaptor.forClass(SlackIdRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackIdsRequest.capture())).thenReturn(responseFromUserService);

        ArgumentCaptor<CodenjoyAchievement> captorCodenjoyAchievement = ArgumentCaptor.forClass(CodenjoyAchievement.class);
        final String[] achievementUuids = {"101", "102", "103"};
        when(gamificationClient.saveCodenjoyAchievement(captorCodenjoyAchievement.capture())).thenReturn(achievementUuids);

        mockSlackResponseUrl(responseUrl, new RichMessage(String.format(CODENJOY_THANKS_MESSAGE,
                user1.getSlackId(), user2.getSlackId(), user3.getSlackId())));

        //when
        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotCodenjoyUrl),
                SlackUrlUtils.getUriVars("slashCommandToken", "/codenjoy", codenjoyCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        //then
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(captorSlackIdsRequest.getValue().getSlackIds())
                    .as("'captorSlackIdsRequest' slackIds not contains 'slackIds'")
                    .containsExactlyInAnyOrder(expectedRequestToUserServiceSlackIds.toArray(new String[expectedRequestToUserServiceSlackIds.size()]));
            soft.assertThat(captorCodenjoyAchievement.getValue())
                    .as("'captorCodenjoyAchievement' is not 'codenjoyAchievement'")
                    .isEqualTo(expectedCodenjoyAchievement);
        });
        verify(usersClient).findUsersBySlackNames(captorSlackIdsRequest.capture());
        verify(gamificationClient).saveCodenjoyAchievement(captorCodenjoyAchievement.capture());
        verifyNoMoreInteractions(usersClient, gamificationClient);
    }

    @Test
    public void returnOkMessageIfCondejoyCommandTokensInWrongOrder() throws Exception {
        //given
        final String codenjoyCommandText = "-2th <@U2DR97JLA|slackName2> -1th <@U1DR97JLA|slackName1> -3th <@U3DR97JLA|slackName3>";
        final List<UserDTO> usersInText = Arrays.asList(user2, user1, user3);
        final SlackParsedCommand slackParsedCommand = new SlackParsedCommand(userFrom, codenjoyCommandText, usersInText);
        final CodenjoyAchievement expectedCodenjoyAchievement = new CodenjoyAchievement(slackParsedCommand);

        final List<String> expectedRequestToUserServiceSlackIds = Arrays.asList(user1.getSlackId(), user2.getSlackId(),
                user3.getSlackId(), userFrom.getSlackId());
        final List<UserDTO> responseFromUserService = Arrays.asList(user1, user2, user3, userFrom);

        ArgumentCaptor<SlackIdRequest> captorSlackNameRequest = ArgumentCaptor.forClass(SlackIdRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackNameRequest.capture())).thenReturn(responseFromUserService);

        ArgumentCaptor<CodenjoyAchievement> captorCodenjoyAchievement = ArgumentCaptor.forClass(CodenjoyAchievement.class);
        final String[] achievementUuids = {"101", "102", "103"};
        when(gamificationClient.saveCodenjoyAchievement(captorCodenjoyAchievement.capture())).thenReturn(achievementUuids);

        mockSlackResponseUrl(responseUrl, new RichMessage(String.format(CODENJOY_THANKS_MESSAGE,
                user1.getSlackId(), user2.getSlackId(), user3.getSlackId())));

        //when
        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotCodenjoyUrl),
                SlackUrlUtils.getUriVars("slashCommandToken", "/codenjoy", codenjoyCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        //then
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(captorSlackNameRequest.getValue().getSlackIds())
                    .as("'captorSlackIdRequest' slackIds not contains 'slackIds'")
                    .containsExactlyInAnyOrder(expectedRequestToUserServiceSlackIds.toArray(new String[expectedRequestToUserServiceSlackIds.size()]));
            soft.assertThat(captorCodenjoyAchievement.getValue())
                    .as("'captorCodenjoyAchievement' is not 'codenjoyAchievement'")
                    .isEqualTo(expectedCodenjoyAchievement);
        });
        verify(usersClient).findUsersBySlackNames(captorSlackNameRequest.capture());
        verify(gamificationClient).saveCodenjoyAchievement(captorCodenjoyAchievement.capture());
        verifyNoMoreInteractions(usersClient, gamificationClient);
    }

    @Test
    public void returnErrorMessageIfCondejoyCommandWithout2thToken() throws Exception {
        //given
        final String codenjoyCommandText = "-1th <@U1DR97JLA|slackName1> <@U2DR97JLA|slackName2> -3th <@U3DR97JLA|slackName3>";

        final List<String> expectedRequestToUserServiceSlackIds = Arrays.asList(user1.getSlackId(), user2.getSlackId(),
                user3.getSlackId(), userFrom.getSlackId());
        final List<UserDTO> responseFromUserService = Arrays.asList(user1, user2, user3, userFrom);

        ArgumentCaptor<SlackIdRequest> captorSlackIdRequest = ArgumentCaptor.forClass(SlackIdRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackIdRequest.capture())).thenReturn(responseFromUserService);

        final String EXPECTED_RESPONSE_TO_SLACK = "Token '-2th' didn't find in the string '-1th <@U1DR97JLA|slackName1> <@U2DR97JLA|slackName2> " +
                "-3th <@U3DR97JLA|slackName3>'";
        mockSlackResponseUrl(responseUrl, new RichMessage(EXPECTED_RESPONSE_TO_SLACK));

        //when
        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotCodenjoyUrl),
                SlackUrlUtils.getUriVars("slashCommandToken", "/codenjoy", codenjoyCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        //then

        Assertions.assertThat(captorSlackIdRequest.getValue().getSlackIds())
                .as("'captorSlackIdRequest' slackIds not contains 'slackIds'")
                .containsExactlyInAnyOrder(expectedRequestToUserServiceSlackIds.toArray(new String[expectedRequestToUserServiceSlackIds.size()]));

        verify(usersClient).findUsersBySlackNames(captorSlackIdRequest.capture());
        verifyNoMoreInteractions(usersClient);
        verifyZeroInteractions(gamificationClient);
    }

    @Test
    public void onReceiveSlashCommandDailyReturnOkRichMessage() throws Exception {
        //given
        final String dailyCommandText = "I did smth today";
        final List<UserDTO> usersInText = Collections.emptyList();
        final SlackParsedCommand slackParsedCommand = new SlackParsedCommand(userFrom, dailyCommandText, usersInText);
        final DailyAchievement expectedDailyAchievement = new DailyAchievement(slackParsedCommand);

        final List<String> expectedRequestToUserServiceSlackIds = Collections.singletonList(userFrom.getSlackId());
        final List<UserDTO> responseFromUserService = Collections.singletonList(userFrom);

        ArgumentCaptor<SlackIdRequest> captorSlackNameRequest = ArgumentCaptor.forClass(SlackIdRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackNameRequest.capture())).thenReturn(responseFromUserService);

        ArgumentCaptor<DailyAchievement> captorDailyAchievement = ArgumentCaptor.forClass(DailyAchievement.class);
        final String[] achievementUuids = {"101"};
        when(gamificationClient.saveDailyAchievement(captorDailyAchievement.capture())).thenReturn(achievementUuids);

        mockSlackResponseUrl(responseUrl, new RichMessage(DAILY_THANKS_MESSAGE));

        //when
        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotDailyUrl),
                SlackUrlUtils.getUriVars("slashCommandToken", "/codenjoy", dailyCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        //then
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(captorSlackNameRequest.getValue().getSlackIds())
                    .as("'captorSlackIdRequest' slackIds not contains 'slackIds'")
                    .containsExactlyInAnyOrder(expectedRequestToUserServiceSlackIds.toArray(new String[expectedRequestToUserServiceSlackIds.size()]));
            soft.assertThat(captorDailyAchievement.getValue())
                    .as("'captorDailyAchievement' is not 'dailyAchievement'")
                    .isEqualTo(expectedDailyAchievement);
        });
        verify(usersClient).findUsersBySlackNames(captorSlackNameRequest.capture());
        verify(gamificationClient).saveDailyAchievement(captorDailyAchievement.capture());
        verifyNoMoreInteractions(usersClient, gamificationClient);
    }

    @Test
    public void onReceiveSlashCommandInterviewReturnOkRichMessage() throws Exception {
        //given
        final String interviewCommandText = "I went to an interview yesterday and got offer";
        final List<UserDTO> usersInText = Collections.emptyList();
        final SlackParsedCommand slackParsedCommand = new SlackParsedCommand(userFrom, interviewCommandText, usersInText);
        final InterviewAchievement expectedInterviewAchievement = new InterviewAchievement(slackParsedCommand);

        final List<String> expectedRequestToUserServiceSlackIds = Collections.singletonList(userFrom.getSlackId());
        final List<UserDTO> responseFromUserService = Collections.singletonList(userFrom);


        ArgumentCaptor<SlackIdRequest> captorSlackIdRequest = ArgumentCaptor.forClass(SlackIdRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackIdRequest.capture())).thenReturn(responseFromUserService);

        ArgumentCaptor<InterviewAchievement> captorInterviewAchievement = ArgumentCaptor.forClass(InterviewAchievement.class);
        final String[] achievementUuids = {"101"};
        when(gamificationClient.saveInterviewAchievement(captorInterviewAchievement.capture())).thenReturn(achievementUuids);

        mockSlackResponseUrl(responseUrl, new RichMessage(INTERVIEW_THANKS_MESSAGE));

        //when
        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotInterviewUrl),
                SlackUrlUtils.getUriVars("slashCommandToken", "/interview", interviewCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        //then
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(captorSlackIdRequest.getValue().getSlackIds())
                    .as("'captorSlackIdRequest' slackIds not contains 'slackIds'")
                    .containsExactlyInAnyOrder(expectedRequestToUserServiceSlackIds.toArray(new String[expectedRequestToUserServiceSlackIds.size()]));
            soft.assertThat(captorInterviewAchievement.getValue())
                    .as("'captorInterviewAchievement' is not 'interviewAchievement'")
                    .isEqualTo(expectedInterviewAchievement);
        });
        verify(usersClient).findUsersBySlackNames(captorSlackIdRequest.capture());
        verify(gamificationClient).saveInterviewAchievement(captorInterviewAchievement.capture());
        verifyNoMoreInteractions(usersClient, gamificationClient);
    }

    @Test
    public void onReceiveSlashCommandThanksReturnOkRichMessage() throws Exception {
        //given
        final String thanksCommandText = "<@U1DR97JLA|slackName1> thanks for your help!";
        final List<UserDTO> usersInText = Collections.singletonList(user1);
        final SlackParsedCommand slackParsedCommand = new SlackParsedCommand(userFrom, thanksCommandText, usersInText);
        final ThanksAchievement expectedThanksAchievement = new ThanksAchievement(slackParsedCommand);

        final List<String> expectedRequestToUserServiceSlackIds = Arrays.asList(userFrom.getSlackId(), user1.getSlackId());
        List<UserDTO> responseFromUserService = Arrays.asList(user1, userFrom);

        ArgumentCaptor<SlackIdRequest> captorSlackIdRequest = ArgumentCaptor.forClass(SlackIdRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackIdRequest.capture())).thenReturn(responseFromUserService);

        ArgumentCaptor<ThanksAchievement> captorThanksAchievement = ArgumentCaptor.forClass(ThanksAchievement.class);
        final String[] achievementUuids = {"101"};
        when(gamificationClient.saveThanksAchievement(captorThanksAchievement.capture())).thenReturn(achievementUuids);

        mockSlackResponseUrl(responseUrl, new RichMessage(String.format(THANKS_ONE_THANKS_MESSAGE, user1.getSlackId())));

        //when
        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotThanksUrl),
                SlackUrlUtils.getUriVars("slashCommandToken", "/thanks", thanksCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        //then
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(captorSlackIdRequest.getValue().getSlackIds())
                    .as("'captorSlackIdRequest' slackIds not contains 'slackIds'")
                    .containsExactlyInAnyOrder(expectedRequestToUserServiceSlackIds.toArray(new String[expectedRequestToUserServiceSlackIds.size()]));
            soft.assertThat(captorThanksAchievement.getValue())
                    .as("'captorThanksAchievement' is not 'thanksAchievement'")
                    .isEqualTo(expectedThanksAchievement);
        });
        verify(usersClient).findUsersBySlackNames(captorSlackIdRequest.capture());
        verify(gamificationClient).saveThanksAchievement(captorThanksAchievement.capture());
        verifyNoMoreInteractions(usersClient, gamificationClient);
    }

    @Test
    public void returnErrorMessageIfThanksCommandConsistTwoOrMoreSlackNames() throws Exception {
        //given
        final String thanksCommandText = "<@U1DR97JLA|slackName1> thanks <@U2DR97JLA|slackName2> for your help!";

        final List<String> expectedRequestToUserServiceSlackIds = Arrays.asList(userFrom.getSlackId(), user1.getSlackId(), user2.getSlackId());
        final List<UserDTO> responseFromUserService = Arrays.asList(user1, user2, userFrom);

        ArgumentCaptor<SlackIdRequest> captorSlackIdRequest = ArgumentCaptor.forClass(SlackIdRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackIdRequest.capture())).thenReturn(responseFromUserService);

        final String EXPECTED_RESPONSE_TO_SLACK = "We found 2 slack names in your command: '<@U1DR97JLA|slackName1> thanks " +
                "<@U2DR97JLA|slackName2> for your help!'  You can't send thanks more than one user.";
        mockSlackResponseUrl(responseUrl, new RichMessage(EXPECTED_RESPONSE_TO_SLACK));

        //when
        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotThanksUrl),
                SlackUrlUtils.getUriVars("slashCommandToken", "/thanks", thanksCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        //then
        Assertions.assertThat(captorSlackIdRequest.getValue().getSlackIds())
                .as("'captorSlackIdRequest' slackIds not contains 'slackIds'")
                .containsExactlyInAnyOrder(expectedRequestToUserServiceSlackIds.toArray(new String[expectedRequestToUserServiceSlackIds.size()]));

        verify(usersClient).findUsersBySlackNames(captorSlackIdRequest.capture());
        verifyNoMoreInteractions(usersClient);
        verifyZeroInteractions(gamificationClient);
    }


    @Test
    public void onReceiveSlashCommandTeamReturnOkRichMessage() throws Exception {
        //given
        final String teamCommandText = "";
        final List<UserDTO> usersInCommand = Collections.singletonList(userFrom);

        final List<String> slackIds = Collections.singletonList(userFrom.getSlackId());
        TeamDTO members = new TeamDTO(new LinkedHashSet<>(Arrays.asList(
                userFrom.getUuid(), user1.getUuid(), user2.getUuid(), user3.getUuid())));
        TeamAchievement teamAchievement = new TeamAchievement(userFrom.getUuid(), members.getMembers());
        ArgumentCaptor<SlackIdRequest> captorSlackNameRequest = ArgumentCaptor.forClass(SlackIdRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackNameRequest.capture())).thenReturn(usersInCommand);

        when(teamsClient.getTeamByUserUuid(userFrom.getUuid())).thenReturn(members);

        ArgumentCaptor<UuidRequest> captorUuidRequest = ArgumentCaptor.forClass(UuidRequest.class);
        Set<UserDTO> teamUsers = new LinkedHashSet<>(Arrays.asList(userFrom, user1, user2, user3));
        Set<String> teamUuids = new LinkedHashSet<>(Arrays.asList(
                userFrom.getUuid(), user1.getUuid(), user2.getUuid(), user3.getUuid()));
        when(usersClient.findUsersByUuids(captorUuidRequest.capture())).thenReturn(teamUsers);

        ArgumentCaptor<TeamAchievement> captorTeamAchievement = ArgumentCaptor.forClass(TeamAchievement.class);
        String[] achievementUuids = {"101", "102", "103", "104"};
        when(gamificationClient.saveTeamAchievement(captorTeamAchievement.capture())).thenReturn(achievementUuids);

        mockSlackResponseUrl(responseUrl, new RichMessage(TEAM_THANKS_MESSAGE));

        //when
        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotTeamUrl),
                SlackUrlUtils.getUriVars("slashCommandToken", "/team", teamCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        //then
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(captorSlackNameRequest.getValue().getSlackIds())
                    .as("'captorSlackIdRequest' slackIds not contains 'slackIds'")
                    .containsExactlyInAnyOrder(slackIds.toArray(new String[slackIds.size()]));
            soft.assertThat(captorTeamAchievement.getValue())
                    .as("'captorTeamAchievement' is not 'teamAchievement'")
                    .isEqualTo(teamAchievement);
            soft.assertThat(captorUuidRequest.getValue().getUuids())
                    .as("'captorUuidRequest' uuids not contains 'teamUuids'")
                    .containsExactlyInAnyOrder(teamUuids.toArray(new String[teamUuids.size()]));
        });
        verify(usersClient).findUsersBySlackNames(captorSlackNameRequest.capture());
        verify(teamsClient).getTeamByUserUuid(userFrom.getUuid());
        verify(usersClient).findUsersByUuids(captorUuidRequest.capture());
        verify(gamificationClient).saveTeamAchievement(captorTeamAchievement.capture());
        verifyNoMoreInteractions(usersClient, gamificationClient, teamsClient);
    }

    @Test
    public void onReceiveSlashCommandThanksWhenUsersServiceReturnErrorWithCorrectContent() throws Exception {
        //given
        String thanksCommandText = "<@U1DR97JLA|slackName1> thanks <@U2DR97JLA|slackName2> for your help!";
        List<String> slackNames = Arrays.asList(userFrom.getSlackId(), user1.getSlackId(), user2.getSlackId());
        String expectedJsonResponseBody =
                "status 400 reading UsersClient#findUserBySlacknames(); content:" +
                        "{\n" +
                        "  \"httpStatus\": 400,\n" +
                        "  \"internalErrorCode\": \"GMF-F2-D2\",\n" +
                        "  \"clientMessage\": \"Oops something went wrong :(\",\n" +
                        "  \"developerMessage\": \"General exception for this service\",\n" +
                        "  \"exceptionMessage\": \"very big and scare error\",\n" +
                        "  \"detailErrors\": []\n" +
                        "}";
        FeignException feignException = mock(FeignException.class);
        ArgumentCaptor<SlackIdRequest> captorSlackNameRequest = ArgumentCaptor.forClass(SlackIdRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackNameRequest.capture())).thenThrow(feignException);
        when(feignException.getMessage()).thenReturn(expectedJsonResponseBody);

        String EXPECTED_RESPONSE_TO_SLACK = "very big and scare error";
        mockSlackResponseUrl(responseUrl, new RichMessage(EXPECTED_RESPONSE_TO_SLACK));

        //when
        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotThanksUrl),
                SlackUrlUtils.getUriVars("slashCommandToken", "/thanks", thanksCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        Assertions.assertThat(captorSlackNameRequest.getValue().getSlackIds())
                .as("'captorSlackNameRequest' slacknames not contains 'slackNames'")
                .containsExactlyInAnyOrder(slackNames.toArray(new String[slackNames.size()]));
        verify(usersClient).findUsersBySlackNames(captorSlackNameRequest.capture());
        verifyNoMoreInteractions(usersClient);
        verifyZeroInteractions(gamificationClient);
    }

    @Test
    public void onReceiveSlashCommandThanksWhenGamificationServiceReturnErrorWithIncorrectContent()
            throws Exception {
        //given
        final String thanksCommandText = "<@U1DR97JLA|slackName1> thanks for your help!";
        final List<UserDTO> usersInCommand = Arrays.asList(user1, userFrom);
        final List<String> slackNames = Arrays.asList(userFrom.getSlackId(), user1.getSlackId());

        ArgumentCaptor<SlackIdRequest> captorSlackNameRequest = ArgumentCaptor.forClass(SlackIdRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackNameRequest.capture())).thenReturn(usersInCommand);

        String expectedJsonResponseBody =
                "status 400 reading UsersClient#findUserBySlacknames(); content: \n";
        ArgumentCaptor<ThanksAchievement> captorThanksAchievement = ArgumentCaptor.forClass(ThanksAchievement.class);
        FeignException feignException = mock(FeignException.class);
        when(gamificationClient.saveThanksAchievement(captorThanksAchievement.capture())).thenThrow(feignException);
        when(feignException.getMessage()).thenReturn(expectedJsonResponseBody);

        String EXPECTED_RESPONSE_TO_SLACK = "I'm, sorry. I cannot parse api error message from remote service :(";
        mockSlackResponseUrl(responseUrl, new RichMessage(EXPECTED_RESPONSE_TO_SLACK));

        //when
        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotThanksUrl),
                SlackUrlUtils.getUriVars("slashCommandToken", "/thanks", thanksCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        //then
        Assertions.assertThat(captorSlackNameRequest.getValue().getSlackIds())
                .as("'captorSlackNameRequest' slacknames not contains 'slackNames'")
                .containsExactlyInAnyOrder(slackNames.toArray(new String[slackNames.size()]));
        verify(usersClient).findUsersBySlackNames(captorSlackNameRequest.capture());
        verify(gamificationClient).saveThanksAchievement(captorThanksAchievement.capture());
        verifyNoMoreInteractions(gamificationClient, usersClient);

    }

    private void mockSlackResponseUrl(String expectedURI, RichMessage delayedMessage) {
        ObjectMapper mapper = new ObjectMapper();
        mockServer.expect(requestTo(expectedURI))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(),
                        containsString("application/json")))
                .andExpect(request -> assertThatJson(request.getBody().toString())
                        .isEqualTo(mapper.writeValueAsString(delayedMessage)))
                .andRespond(withSuccess("OK", MediaType.APPLICATION_FORM_URLENCODED));
    }
}