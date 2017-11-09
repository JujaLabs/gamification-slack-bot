package ua.com.juja.microservices.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import ua.com.juja.microservices.gamification.slackbot.GamificationSlackBotApplication;
import ua.com.juja.microservices.gamification.slackbot.dao.feign.GamificationClient;
import ua.com.juja.microservices.gamification.slackbot.dao.feign.TeamsClient;
import ua.com.juja.microservices.gamification.slackbot.dao.feign.UsersClient;
import ua.com.juja.microservices.gamification.slackbot.model.DTO.SlackNameRequest;
import ua.com.juja.microservices.gamification.slackbot.model.DTO.TeamDTO;
import ua.com.juja.microservices.gamification.slackbot.model.DTO.UserDTO;
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

    private UserDTO user1 = new UserDTO("f2034f22-562b-4e02-bfcf-ec615c1ba62b", "@slack1");
    private UserDTO user2 = new UserDTO("f2034f33-563c-4e03-bfcf-ec615c1ba63c", "@slack2");
    private UserDTO user3 = new UserDTO("f2034f44-563d-4e04-bfcf-ec615c1ba64d", "@slack3");
    private UserDTO userFrom = new UserDTO("f2034f11-561a-4e01-bfcf-ec615c1ba61a", "@from-user");

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
        String codenjoyCommandText = "-1th @slack1 -2th @slack2 -3th @slack3";

        List<UserDTO> usersInCommand = Arrays.asList(user1, user2, user3, userFrom);
        List<String> slackNames = Arrays.asList(user1.getSlack(), user2.getSlack(),
                user3.getSlack(), userFrom.getSlack());
        CodenjoyAchievement codenjoyAchievement =
                new CodenjoyAchievement(userFrom.getUuid(), user1.getUuid(), user2.getUuid(), user3.getUuid());
        ArgumentCaptor<SlackNameRequest> captorSlackNameRequest = ArgumentCaptor.forClass(SlackNameRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackNameRequest.capture())).thenReturn(usersInCommand);

        ArgumentCaptor<CodenjoyAchievement> captorCodenjoyAchievement = ArgumentCaptor.forClass(CodenjoyAchievement.class);
        String[] achievementUuids = {"101", "102", "103"};
        when(gamificationClient.saveCodenjoyAchievement(captorCodenjoyAchievement.capture())).thenReturn(achievementUuids);

        mockSlackResponseUrl(responseUrl, new RichMessage(String.format(CODENJOY_THANKS_MESSAGE,
                user1.getSlack(), user2.getSlack(), user3.getSlack())));

        //when
        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotCodenjoyUrl),
                SlackUrlUtils.getUriVars("slashCommandToken", "/codenjoy", codenjoyCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        //then
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(captorSlackNameRequest.getValue().getSlackNames())
                    .as("'captorSlackNameRequest' slacknames not contains 'slackNames'")
                    .containsExactlyInAnyOrder(slackNames.toArray(new String[slackNames.size()]));
            soft.assertThat(captorCodenjoyAchievement.getValue())
                    .as("'captorCodenjoyAchievement' is not 'codenjoyAchievement'")
                    .isEqualTo(codenjoyAchievement);
        });
        verify(usersClient).findUsersBySlackNames(captorSlackNameRequest.capture());
        verify(gamificationClient).saveCodenjoyAchievement(captorCodenjoyAchievement.capture());
        verifyNoMoreInteractions(usersClient, gamificationClient);
    }

    @Test
    public void returnOkMessageIfCondejoyCommandTokensWithoutSpaces() throws Exception {
        //given
        String codenjoyCommandText = "-1th@slack1 -2th@slack2 -3th@slack3";
        List<UserDTO> usersInCommand = Arrays.asList(user1, user2, user3, userFrom);
        List<String> slackNames = Arrays.asList(user1.getSlack(), user2.getSlack(),
                user3.getSlack(), userFrom.getSlack());
        CodenjoyAchievement codenjoyAchievement =
                new CodenjoyAchievement(userFrom.getUuid(), user1.getUuid(), user2.getUuid(), user3.getUuid());
        ArgumentCaptor<SlackNameRequest> captorSlackNameRequest = ArgumentCaptor.forClass(SlackNameRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackNameRequest.capture())).thenReturn(usersInCommand);

        ArgumentCaptor<CodenjoyAchievement> captorCodenjoyAchievement = ArgumentCaptor.forClass(CodenjoyAchievement.class);
        String[] achievementUuids = {"101", "102", "103"};
        when(gamificationClient.saveCodenjoyAchievement(captorCodenjoyAchievement.capture())).thenReturn(achievementUuids);

        mockSlackResponseUrl(responseUrl, new RichMessage(String.format(CODENJOY_THANKS_MESSAGE,
                user1.getSlack(), user2.getSlack(), user3.getSlack())));

        //when
        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotCodenjoyUrl),
                SlackUrlUtils.getUriVars("slashCommandToken", "/codenjoy", codenjoyCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        //then
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(captorSlackNameRequest.getValue().getSlackNames())
                    .as("'captorSlackNameRequest' slacknames not contains 'slackNames'")
                    .containsExactlyInAnyOrder(slackNames.toArray(new String[slackNames.size()]));
            soft.assertThat(captorCodenjoyAchievement.getValue())
                    .as("'captorCodenjoyAchievement' is not 'codenjoyAchievement'")
                    .isEqualTo(codenjoyAchievement);
        });
        verify(usersClient).findUsersBySlackNames(captorSlackNameRequest.capture());
        verify(gamificationClient).saveCodenjoyAchievement(captorCodenjoyAchievement.capture());
        verifyNoMoreInteractions(usersClient, gamificationClient);
    }

    @Test
    public void returnOkMessageIfCondejoyCommandTokensInWrongOrder() throws Exception {
        //given
        String codenjoyCommandText = "-2th @slack2 -1th @slack1 -3th @slack3";
        List<UserDTO> usersInCommand = Arrays.asList(user1, user2, user3, userFrom);
        List<String> slackNames = Arrays.asList(user1.getSlack(), user2.getSlack(),
                user3.getSlack(), userFrom.getSlack());
        CodenjoyAchievement codenjoyAchievement =
                new CodenjoyAchievement(userFrom.getUuid(), user1.getUuid(), user2.getUuid(), user3.getUuid());
        ArgumentCaptor<SlackNameRequest> captorSlackNameRequest = ArgumentCaptor.forClass(SlackNameRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackNameRequest.capture())).thenReturn(usersInCommand);

        ArgumentCaptor<CodenjoyAchievement> captorCodenjoyAchievement = ArgumentCaptor.forClass(CodenjoyAchievement.class);
        String[] achievementUuids = {"101", "102", "103"};
        when(gamificationClient.saveCodenjoyAchievement(captorCodenjoyAchievement.capture())).thenReturn(achievementUuids);

        mockSlackResponseUrl(responseUrl, new RichMessage(String.format(CODENJOY_THANKS_MESSAGE,
                user1.getSlack(), user2.getSlack(), user3.getSlack())));

        //when
        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotCodenjoyUrl),
                SlackUrlUtils.getUriVars("slashCommandToken", "/codenjoy", codenjoyCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        //then
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(captorSlackNameRequest.getValue().getSlackNames())
                    .as("'captorSlackNameRequest' slacknames not contains 'slackNames'")
                    .containsExactlyInAnyOrder(slackNames.toArray(new String[slackNames.size()]));
            soft.assertThat(captorCodenjoyAchievement.getValue())
                    .as("'captorCodenjoyAchievement' is not 'codenjoyAchievement'")
                    .isEqualTo(codenjoyAchievement);
        });
        verify(usersClient).findUsersBySlackNames(captorSlackNameRequest.capture());
        verify(gamificationClient).saveCodenjoyAchievement(captorCodenjoyAchievement.capture());
        verifyNoMoreInteractions(usersClient, gamificationClient);
    }

    @Test
    public void returnErrorMessageIfCondejoyCommandWithout2thToken() throws Exception {
        //given
        String codenjoyCommandText = "-1th @slack1 @slack2 -3th @slack3";
        List<UserDTO> usersInCommand = Arrays.asList(user1, user2, user3, userFrom);
        List<String> slackNames = Arrays.asList(user1.getSlack(), user2.getSlack(),
                user3.getSlack(), userFrom.getSlack());
        ArgumentCaptor<SlackNameRequest> captorSlackNameRequest = ArgumentCaptor.forClass(SlackNameRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackNameRequest.capture())).thenReturn(usersInCommand);

        final String EXPECTED_RESPONSE_TO_SLACK = "Token '-2th' didn't find in the string '-1th @slack1 @slack2 " +
                "-3th @slack3'";
        mockSlackResponseUrl(responseUrl, new RichMessage(EXPECTED_RESPONSE_TO_SLACK));

        //when
        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotCodenjoyUrl),
                SlackUrlUtils.getUriVars("slashCommandToken", "/codenjoy", codenjoyCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        //then

        Assertions.assertThat(captorSlackNameRequest.getValue().getSlackNames())
                .as("'captorSlackNameRequest' slacknames not contains 'slackNames'")
                .containsExactlyInAnyOrder(slackNames.toArray(new String[slackNames.size()]));

        verify(usersClient).findUsersBySlackNames(captorSlackNameRequest.capture());
        verifyNoMoreInteractions(usersClient);
        verifyZeroInteractions(gamificationClient);
    }

    @Test
    public void onReceiveSlashCommandDailyReturnOkRichMessage() throws Exception {
        //given
        String dailyCommandText = "I did smth today";
        List<UserDTO> usersInCommand = Collections.singletonList(userFrom);

        List<String> slackNames = Collections.singletonList(userFrom.getSlack());
        DailyAchievement dailyAchievement = new DailyAchievement(userFrom.getUuid(), dailyCommandText);
        ArgumentCaptor<SlackNameRequest> captorSlackNameRequest = ArgumentCaptor.forClass(SlackNameRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackNameRequest.capture())).thenReturn(usersInCommand);

        ArgumentCaptor<DailyAchievement> captorDailyAchievement = ArgumentCaptor.forClass(DailyAchievement.class);
        String[] achievementUuids = {"101"};
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
            soft.assertThat(captorSlackNameRequest.getValue().getSlackNames())
                    .as("'captorSlackNameRequest' slacknames not contains 'slackNames'")
                    .containsExactlyInAnyOrder(slackNames.toArray(new String[slackNames.size()]));
            soft.assertThat(captorDailyAchievement.getValue())
                    .as("'captorDailyAchievement' is not 'dailyAchievement'")
                    .isEqualTo(dailyAchievement);
        });
        verify(usersClient).findUsersBySlackNames(captorSlackNameRequest.capture());
        verify(gamificationClient).saveDailyAchievement(captorDailyAchievement.capture());
        verifyNoMoreInteractions(usersClient, gamificationClient);
    }

    @Test
    public void onReceiveSlashCommandInterviewReturnOkRichMessage() throws Exception {
        //given
        String interviewCommandText = "I went to an interview yesterday and got offer";
        List<UserDTO> usersInCommand = Collections.singletonList(userFrom);

        List<String> slackNames = Collections.singletonList(userFrom.getSlack());
        InterviewAchievement interviewAchievement = new InterviewAchievement(userFrom.getUuid(), interviewCommandText);
        ArgumentCaptor<SlackNameRequest> captorSlackNameRequest = ArgumentCaptor.forClass(SlackNameRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackNameRequest.capture())).thenReturn(usersInCommand);

        ArgumentCaptor<InterviewAchievement> captorInterviewAchievement = ArgumentCaptor.forClass(InterviewAchievement.class);
        String[] achievementUuids = {"101"};
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
            soft.assertThat(captorSlackNameRequest.getValue().getSlackNames())
                    .as("'captorSlackNameRequest' slacknames not contains 'slackNames'")
                    .containsExactlyInAnyOrder(slackNames.toArray(new String[slackNames.size()]));
            soft.assertThat(captorInterviewAchievement.getValue())
                    .as("'captorInterviewAchievement' is not 'interviewAchievement'")
                    .isEqualTo(interviewAchievement);
        });
        verify(usersClient).findUsersBySlackNames(captorSlackNameRequest.capture());
        verify(gamificationClient).saveInterviewAchievement(captorInterviewAchievement.capture());
        verifyNoMoreInteractions(usersClient, gamificationClient);
    }

    @Test
    public void onReceiveSlashCommandThanksReturnOkRichMessage() throws Exception {
        //given
        final String thanksCommandText = "@slack1 thanks for your help!";
        final List<UserDTO> usersInCommand = Arrays.asList(user1, userFrom);

        List<String> slackNames = Arrays.asList(userFrom.getSlack(), user1.getSlack());
        ThanksAchievement thanksAchievement = new ThanksAchievement(userFrom.getUuid(), user1.getUuid(),
                "thanks for your help!");
        ArgumentCaptor<SlackNameRequest> captorSlackNameRequest = ArgumentCaptor.forClass(SlackNameRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackNameRequest.capture())).thenReturn(usersInCommand);

        ArgumentCaptor<ThanksAchievement> captorThanksAchievement = ArgumentCaptor.forClass(ThanksAchievement.class);
        String[] achievementUuids = {"101"};
        when(gamificationClient.saveThanksAchievement(captorThanksAchievement.capture())).thenReturn(achievementUuids);

        mockSlackResponseUrl(responseUrl, new RichMessage(String.format(THANKS_ONE_THANKS_MESSAGE, user1.getSlack())));

        //when
        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotThanksUrl),
                SlackUrlUtils.getUriVars("slashCommandToken", "/thanks", thanksCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        //then
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(captorSlackNameRequest.getValue().getSlackNames())
                    .as("'captorSlackNameRequest' slacknames not contains 'slackNames'")
                    .containsExactlyInAnyOrder(slackNames.toArray(new String[slackNames.size()]));
            soft.assertThat(captorThanksAchievement.getValue())
                    .as("'captorThanksAchievement' is not 'thanksAchievement'")
                    .isEqualTo(thanksAchievement);
        });
        verify(usersClient).findUsersBySlackNames(captorSlackNameRequest.capture());
        verify(gamificationClient).saveThanksAchievement(captorThanksAchievement.capture());
        verifyNoMoreInteractions(usersClient, gamificationClient);
    }

    @Test
    public void returnErrorMessageIfThanksCommandConsistTwoOrMoreSlackNames() throws Exception {
        //given
        final String thanksCommandText = "@slack1 thanks @slack2 for your help!";
        final List<UserDTO> usersInCommand = Arrays.asList(user1, user2, userFrom);

        List<String> slackNames = Arrays.asList(userFrom.getSlack(), user1.getSlack(), user2.getSlack());
        ArgumentCaptor<SlackNameRequest> captorSlackNameRequest = ArgumentCaptor.forClass(SlackNameRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackNameRequest.capture())).thenReturn(usersInCommand);

        final String EXPECTED_RESPONSE_TO_SLACK = "We found 2 slack names in your command: '@slack1 thanks " +
                "@slack2 for your help!'  You can't send thanks more than one user.";
        mockSlackResponseUrl(responseUrl, new RichMessage(EXPECTED_RESPONSE_TO_SLACK));

        //when
        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotThanksUrl),
                SlackUrlUtils.getUriVars("slashCommandToken", "/thanks", thanksCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        //then
        Assertions.assertThat(captorSlackNameRequest.getValue().getSlackNames())
                .as("'captorSlackNameRequest' slacknames not contains 'slackNames'")
                .containsExactlyInAnyOrder(slackNames.toArray(new String[slackNames.size()]));

        verify(usersClient).findUsersBySlackNames(captorSlackNameRequest.capture());
        verifyNoMoreInteractions(usersClient);
        verifyZeroInteractions(gamificationClient);
    }


    @Test
    public void onReceiveSlashCommandTeamReturnOkRichMessage() throws Exception {
        //given
        final String teamCommandText = "";
        final List<UserDTO> usersInCommand = Collections.singletonList(userFrom);

        List<String> slackNames = Collections.singletonList(userFrom.getSlack());
        TeamDTO members = new TeamDTO(new LinkedHashSet<>(Arrays.asList(
                userFrom.getUuid(), user1.getUuid(), user2.getUuid(), user3.getUuid())));
        TeamAchievement teamAchievement = new TeamAchievement(userFrom.getUuid(), members.getMembers());
        ArgumentCaptor<SlackNameRequest> captorSlackNameRequest = ArgumentCaptor.forClass(SlackNameRequest.class);
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
            soft.assertThat(captorSlackNameRequest.getValue().getSlackNames())
                    .as("'captorSlackNameRequest' slacknames not contains 'slackNames'")
                    .containsExactlyInAnyOrder(slackNames.toArray(new String[slackNames.size()]));
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
        String thanksCommandText = "@slack1 thanks @slack2 for your help!";
        List<String> slackNames = Arrays.asList(userFrom.getSlack(), user1.getSlack(), user2.getSlack());
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
        ArgumentCaptor<SlackNameRequest> captorSlackNameRequest = ArgumentCaptor.forClass(SlackNameRequest.class);
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

        Assertions.assertThat(captorSlackNameRequest.getValue().getSlackNames())
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
        final String thanksCommandText = "@slack1 thanks for your help!";
        final List<UserDTO> usersInCommand = Arrays.asList(user1, userFrom);
        List<String> slackNames = Arrays.asList(userFrom.getSlack(), user1.getSlack());

        ArgumentCaptor<SlackNameRequest> captorSlackNameRequest = ArgumentCaptor.forClass(SlackNameRequest.class);
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
        Assertions.assertThat(captorSlackNameRequest.getValue().getSlackNames())
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