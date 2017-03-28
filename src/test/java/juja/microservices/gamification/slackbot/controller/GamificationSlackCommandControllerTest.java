package juja.microservices.gamification.slackbot.controller;

import juja.microservices.gamification.slackbot.model.CodenjoyAchievement;
import juja.microservices.gamification.slackbot.model.User;
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
    @Inject
    private MockMvc mvc;

    @MockBean
    private GamificationService gamificationService;

    @MockBean
    private UserService userService;

    @Test
    public void onReceiveSlashCommand_When_IncorrectToken_Should_ReturnSorryRichMessage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/commands/codenjoy?" +
                        "token={slashCommandToken}&" +
                        "team_id={team_id}&" +
                        "team_domain={team_domain}&" +
                        "channel_id={channel_id}&" +
                        "channel_name={channel_name}&" +
                        "user_id={user_id}&" +
                        "user_name={user_name}&" +
                        "command={command}&" +
                        "text={text}&" +
                        "response_url={response_url}&",
                "wrongSlackToken",
                "any_team_id",
                "any_domain",
                "UHASHB8JB",
                "test-channel",
                "UNJSD9OKM",
                "@uname",
                "/command",
                "/codenjoy -1th @slack_nick_name -2th @slack_nick_name2 -3th @slack_nick_name3",
                "http://example.com")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Sorry! You're not lucky enough to use our slack command."));
    }

    @Test
    public void onReceiveSlashCommandReturnOkRichMessage() throws Exception {
        when(gamificationService.sendCodenjoyAchievement(any(CodenjoyAchievement.class))).thenReturn("ok");
        when(userService.findUserBySlack(any(String.class))).thenReturn(new User("uuid", "gmail", "slack", "skype", "lin", "face", "tw"));
        mvc.perform(MockMvcRequestBuilders.post("/commands/codenjoy?" +
                        "token={slashCommandToken}&" +
                        "team_id={team_id}&" +
                        "team_domain={team_domain}&" +
                        "channel_id={channel_id}&" +
                        "channel_name={channel_name}&" +
                        "user_id={user_id}&" +
                        "user_name={user_name}&" +
                        "command={command}&" +
                        "text={text}&" +
                        "response_url={response_url}&",
                "slashCommandToken",
                "any_team_id",
                "any_domain",
                "UHASHB8JB",
                "test-channel",
                "UNJSD9OKM",
                "@uname",
                "/codenjoy",
                "-1th @slack_nick_name -2th @slack_nick_name2 -3th @slack_nick_name3",
                "http://example.com")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("ok"));
    }

    @Test
    public void onReceiveSlashCommandReturnIfToken2thWithoutSlackName() throws Exception {
        when(gamificationService.sendCodenjoyAchievement(any(CodenjoyAchievement.class))).thenReturn("ok");
        when(userService.findUserBySlack(any(String.class))).thenReturn(new User("uuid", "gmail", "slack", "skype", "lin", "face", "tw"));
        mvc.perform(MockMvcRequestBuilders.post("/commands/codenjoy?" +
                        "token={slashCommandToken}&" +
                        "team_id={team_id}&" +
                        "team_domain={team_domain}&" +
                        "channel_id={channel_id}&" +
                        "channel_name={channel_name}&" +
                        "user_id={user_id}&" +
                        "user_name={user_name}&" +
                        "command={command}&" +
                        "text={text}&" +
                        "response_url={response_url}&",
                "slashCommandToken",
                "any_team_id",
                "any_domain",
                "UHASHB8JB",
                "test-channel",
                "UNJSD9OKM",
                "@uname",
                "/codenjoy",
                "-1th @slack_nick_name -2th -3th @slack_nick_name3",
                "http://example.com")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Not found username for token '-2th'. Example for this command /codenjoy -1th @slack_nick_name -2th @slack_nick_name2 -3th @slack_nick_name3"));
    }
}