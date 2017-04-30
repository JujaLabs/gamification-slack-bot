package juja.microservices.gamification.slackbot.controller;

import juja.microservices.gamification.slackbot.model.CodenjoyAchievement;
import juja.microservices.gamification.slackbot.model.DailyAchievement;
import juja.microservices.gamification.slackbot.model.InterviewAchievement;
import juja.microservices.gamification.slackbot.model.ThanksAchievement;
import juja.microservices.gamification.slackbot.service.GamificationService;
import juja.microservices.gamification.slackbot.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.inject.Inject;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Nikol on 3/11/2017.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(GamificationSlackCommandController.class)
public class GamificationSlackCommandControllerTest {

    private static final String SORRY_MESSAGE = "Sorry! You're not lucky enough to use our slack command.";
    private static final String NOT_FOUND_USERNAME =
            "Not found username for token '-2th'. Example for this command /codenjoy " +
                    "-1th @slack_nick_name -2th @slack_nick_name2 -3th @slack_nick_name3";
    private static final String CODENJOY_DESCRIPTION =
            "-1th @slack_nick_name -2th @slack_nick_name2 -3th @slack_nick_name3";
    private static final String INCORRECT_CODENJOY_DESCRIPTION = "-1th @slack_nick_name -2th -3th @slack_nick_name3";
    private static final String DAILY_DESCRIPTION = "daily description text";
    private static final String THANKS_DESCRIPTION = "thanks to @slack_user description text";
    private static final String INCORRECT_THANKS_DESCRIPTION = "thanks description text";

    @Inject
    private MockMvc mvc;

    @MockBean
    private GamificationService gamificationService;

    @MockBean
    private UserService userService;

    @Test
    public void onReceiveSlashCommandCodenjoyWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate("/commands/codenjoy"),
                getUriVars("wrongSlackToken", "/command",CODENJOY_DESCRIPTION))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(SORRY_MESSAGE));
    }

    @Test
    public void onReceiveSlashCommandCodenjoyReturnOkRichMessage() throws Exception {
        when(gamificationService.sendCodenjoyAchievement(any(CodenjoyAchievement.class))).thenReturn("ok");
        when(userService.findUuidUserBySlack("@slack.name")).thenReturn("uuid");
        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate("/commands/codenjoy"),
                getUriVars("slashCommandToken", "/codenjoy", CODENJOY_DESCRIPTION))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("ok"));
    }

    @Test
    public void onReceiveSlashCommandCodenjoyReturnIfToken2thWithoutSlackName() throws Exception {
        when(gamificationService.sendCodenjoyAchievement(any(CodenjoyAchievement.class))).thenReturn("ok");
        when(userService.findUuidUserBySlack("@slack.name")).thenReturn("uuid");
        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate("/commands/codenjoy"),
                getUriVars("slashCommandToken", "/codenjoy", INCORRECT_CODENJOY_DESCRIPTION))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(NOT_FOUND_USERNAME));
    }

    @Test
    public void onReceiveSlashCommandDailyWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate("/commands/daily"),
                getUriVars("wrongSlackToken", "/command", DAILY_DESCRIPTION))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(SORRY_MESSAGE));
    }

    @Test
    public void onReceiveSlashCommandDailyReturnOkRichMessage() throws Exception {
        when(gamificationService.sendDailyAchievement(any(DailyAchievement.class))).thenReturn("ok");
        when(userService.findUuidUserBySlack("@slack.name")).thenReturn("uuid");
        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate("/commands/daily"),
                getUriVars("slashCommandToken", "/daily", DAILY_DESCRIPTION))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("ok"));
    }

    @Test
    public void onReceiveSlashCommandDailyShouldReturnErrorMessageIfOccurException() throws Exception {
        when(gamificationService.sendDailyAchievement(any(DailyAchievement.class))).thenThrow(new RuntimeException("something went wrong"));
        when(userService.findUuidUserBySlack("@slack.name")).thenReturn("uuid");
        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate("/commands/daily"),
                getUriVars("slashCommandToken", "/daily", DAILY_DESCRIPTION))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("something went wrong"));
    }

    @Test
    public void onReceiveSlashCommandThanksWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate("/commands/thanks"),
                getUriVars("wrongSlackToken", "/command", INCORRECT_THANKS_DESCRIPTION))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(SORRY_MESSAGE));
    }
    @Test
    public void onReceiveSlashCommandThanksReturnOkRichMessage() throws Exception {
        when(gamificationService.sendThanksAchievement(any(ThanksAchievement.class))).thenReturn("ok");
        when(userService.findUuidUserBySlack("@slack.name")).thenReturn("uuid");
        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate("/commands/thanks"),
                getUriVars("slashCommandToken", "/thanks", THANKS_DESCRIPTION))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("ok"));
    }

    @Test
    public void onReceiveSlashCommandThanksWhenOccurSomeException() throws Exception {
        when(gamificationService.sendThanksAchievement(any(ThanksAchievement.class))).thenThrow(new RuntimeException("something went wrong"));
        when(userService.findUuidUserBySlack("@slack.name")).thenReturn("uuid");
        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate("/commands/thanks"),
                getUriVars("slashCommandToken", "/thanks", THANKS_DESCRIPTION))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("something went wrong"));
    }

    private String getUrlTemplate(String endpoint) {
        return endpoint + "?" +
                        "token={slashCommandToken}&" +
                        "team_id={team_id}&" +
                        "team_domain={team_domain}&" +
                        "channel_id={channel_id}&" +
                        "channel_name={channel_name}&" +
                        "user_id={user_id}&" +
                        "user_name={user_name}&" +
                        "command={command}&" +
                        "text={text}&" +
                        "response_url={response_url}&";
    }

    private Object[] getUriVars(String slackToken, String command, String description) {
        return new Object[]{slackToken,
                "any_team_id",
                "any_domain",
                "UHASHB8JB",
                "test-channel",
                "UNJSD9OKM",
                "@uname",
                command,
                description,
                "http://example.com"};
    }

    @Test
    public void onReceiveSlashCommandInterviewWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate("/commands/interview") ,
                getUriVars("wrongSlashCommandToken", "/interview", "interview description text"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Sorry! You're not lucky enough to use our slack command."));
    }

    @Test
    public void onReceiveSlashCommandInterviewReturnOkRichMessage() throws Exception {
        when(gamificationService.sendInterviewAchievement(any(InterviewAchievement.class))).thenReturn("ok");
        when(userService.findUuidUserBySlack("@slack.name")).thenReturn("uuid");
        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate("/commands/interview") ,
                getUriVars("slashCommandToken", "/interview", "interview description text"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("ok"));
    }

    @Test
    public void onReceiveSlashCommandInterviewShouldReturnErrorRichMessageIfOccurException() throws Exception {
        when(gamificationService.sendInterviewAchievement(any(InterviewAchievement.class))).thenThrow(new RuntimeException("something went wrong"));
        when(userService.findUuidUserBySlack("@slack.name")).thenReturn("uuid");
        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate("/commands/interview") ,
                getUriVars("slashCommandToken", "/interview", "interview description text"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("something went wrong"));
    }
}