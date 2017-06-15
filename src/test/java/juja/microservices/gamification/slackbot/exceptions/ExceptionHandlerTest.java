package juja.microservices.gamification.slackbot.exceptions;

import juja.microservices.gamification.slackbot.controller.GamificationSlackCommandController;
import juja.microservices.gamification.slackbot.model.DailyAchievement;
import juja.microservices.gamification.slackbot.service.GamificationService;
import juja.microservices.gamification.slackbot.service.UserService;
import juja.microservices.gamification.slackbot.service.impl.SlackNameHandlerService;
import juja.microservices.utils.SlackUrlUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.inject.Inject;
import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Danil Kuznetsov
 */
@RunWith(SpringRunner.class)
@WebMvcTest(GamificationSlackCommandController.class)
public class ExceptionHandlerTest {

    @Inject
    private MockMvc mvc;

    @MockBean
    private GamificationService gamificationService;

    @MockBean
    private UserService userService;

    @MockBean
    private SlackNameHandlerService slackNameHandlerService;

    @Test
    public void shouldHandleGamificationAPIError() throws Exception {

        final String DAILY_COMMAND_TEXT = "daily description text";

        ApiError apiError = new ApiError(
                400, "GMF-F5-D2",
                "You cannot give more than one thanks for day to one person",
                "The reason of the exception is 'Thanks' achievement",
                "Something went wrong",
                Collections.EMPTY_LIST
        );

        when(userService.findUuidUserBySlack("@slack.name")).thenReturn("uuid");
        when(gamificationService.sendDailyAchievement(any(DailyAchievement.class)))
                .thenThrow(new GamificationExchangeException(apiError, new RuntimeException("exception")));

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/daily"),
                SlackUrlUtils.getUriVars("slashCommandToken", "/daily", DAILY_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("You cannot give more than one thanks for day to one person"));
    }

    @Test
    public void shouldHandleUserAPIError() throws Exception {

        final String DAILY_COMMAND_TEXT = "daily description text";

        ApiError apiError = new ApiError(
                400, "USF-F1-D1",
                "User not found",
                "User not found",
                "Something went wrong",
                Collections.EMPTY_LIST
        );

        when(userService.findUuidUserBySlack(any(String.class))).
                thenThrow(new UserExchangeException(apiError, new RuntimeException("exception")));


        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/daily"),
                SlackUrlUtils.getUriVars("slashCommandToken", "/daily", DAILY_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("User not found"));
    }

    @Test
    public void shouldHandleWrongCommandException() throws Exception {

        final String COMMAND_TEXT = "@#uuid1#@ -2th @#uuid2#@ -3th @#uuid3#@";

        when(userService.findUuidUserBySlack(any(String.class))).
                thenThrow(new WrongCommandFormatException("Wrong command exception"));

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/codenjoy"),
                SlackUrlUtils.getUriVars("slashCommandToken", "/daily", COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Wrong command exception"));
    }

    @Test
    public void shouldHandleAllOtherException() throws Exception {

        final String COMMAND_TEXT = "@#uuid1#@ -2th @#uuid2#@ -3th @#uuid3#@";

        when(userService.findUuidUserBySlack(any(String.class))).
                thenThrow(new RuntimeException("Runtime exception"));

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/codenjoy"),
                SlackUrlUtils.getUriVars("slashCommandToken", "/daily", COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Runtime exception"));
    }
}