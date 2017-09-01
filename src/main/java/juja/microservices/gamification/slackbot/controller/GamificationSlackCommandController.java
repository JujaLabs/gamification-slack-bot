package juja.microservices.gamification.slackbot.controller;

import juja.microservices.gamification.slackbot.exceptions.ExceptionsHandler;
import juja.microservices.gamification.slackbot.model.SlackParsedCommand;
import juja.microservices.gamification.slackbot.model.achievements.CodenjoyAchievement;
import juja.microservices.gamification.slackbot.model.achievements.DailyAchievement;
import juja.microservices.gamification.slackbot.model.achievements.InterviewAchievement;
import juja.microservices.gamification.slackbot.model.achievements.ThanksAchievement;
import juja.microservices.gamification.slackbot.service.GamificationService;
import juja.microservices.gamification.slackbot.service.impl.SlackNameHandlerService;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * @author Nikolay Horushko
 * @author Konstantin Sergey
 */
@RestController
public class GamificationSlackCommandController {
    private final static String INSTANT_MESSAGE = "Your command accepted. Please wait...";
    private final static String SORRY_MESSAGE = "Sorry! You're not lucky enough to use our slack command.";
    private final static String CODENJOY_ERROR_MESSAGE = "ERROR. Something went wrong and we didn't award the users.";
    private final static String CODENJOY_THANKS_MESSAGE = "Thanks, we awarded the users. \" +\n" +
            "                    \"First place: %s, Second place: %s, Third place: %s.";
    private final static String DAILY_ERROR_MESSAGE = "ERROR. Something went wrong and daily report was not saved.";
    private final static String DAILY_THANKS_MESSAGE = "Thanks, your daily report saved.";
    private final static String THANKS_ERROR_MESSAGE = "Error. Something went wrong and we didn't save your 'thanks'.";
    private final static String THANKS_ONE_THANKS_MESSAGE = "Thanks, your 'thanks' for %s saved.";
    private final static String THANKS_TWO_THANKS_MESSAGE = "Thanks, your 'thanks' for %s saved. \" +\n" +
            "                    \"Also you received +1 for your activity.";
    private final static String INTERVIEW_ERROR_MESSAGE = "ERROR. Something went wrong and we didn't save your interview.";
    private final static String INTERVIEW_THANKS_MESSAGE = "Thanks. Your interview saved.";

    @Value("${slack.slashCommandToken}")
    private String slackToken;

    private final RestTemplate restTemplate;
    private final ExceptionsHandler exceptionsHandler;
    private final SlackNameHandlerService slackNameHandlerService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private GamificationService gamificationService;

    @Inject
    public GamificationSlackCommandController(GamificationService gamificationService,
                                              SlackNameHandlerService slackNameHandlerService,
                                              RestTemplate restTemplate,
                                              ExceptionsHandler exceptionsHandler) {
        this.gamificationService = gamificationService;
        this.slackNameHandlerService = slackNameHandlerService;
        this.restTemplate = restTemplate;
        this.exceptionsHandler = exceptionsHandler;
    }

    @RequestMapping(value = "/commands/codenjoy", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void onReceiveSlashCommandCodenjoy(@RequestParam("token") String token,
                                              @RequestParam("user_name") String fromUser,
                                              @RequestParam("text") String text,
                                              @RequestParam("response_url") String responseUrl,
                                              HttpServletResponse servletResponse) throws IOException {
        logger.debug("Received slash command Condenjoy achievement: from user: [{}] command: [{}] token: [{}]," +
                " responseUrl : [{}]", fromUser, text, token, responseUrl);

        if (!token.equals(slackToken)) {
            logger.warn("Received invalid slack token: [{}] in command Codenjoy for user: [{}]", token, fromUser);
            sendInstantResponseMessage(servletResponse, SORRY_MESSAGE);
        }
        exceptionsHandler.setResponseUrl(responseUrl);
        sendInstantResponseMessage(servletResponse, INSTANT_MESSAGE);

        String response = CODENJOY_ERROR_MESSAGE;

        logger.debug("Started create slackParsedCommand and create achievement request");
        SlackParsedCommand slackParsedCommand = slackNameHandlerService.createSlackParsedCommand(fromUser, text);
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(slackParsedCommand);
        logger.debug("Finished create slackParsedCommand and create achievement request");

        logger.debug("Sent codenjoy achievement request to Gamifcation service. Achievement: [{}]",
                codenjoy.toString());
        String[] result = gamificationService.sendCodenjoyAchievement(codenjoy);
        logger.debug("Received response from Gamification service: [{}]", Arrays.toString(result));

        if (result.length == 3) {
            response = codenjoy.injectSlackNames(CODENJOY_THANKS_MESSAGE);
        }

        logger.info("Codenjoy command processed : user: [{}] text: [{}] and sent response into slack: [{}]",
                fromUser, text, response);

        RichMessage message = new RichMessage(response);
        sendDelayedResponseMessage(responseUrl, message);
    }

    @RequestMapping(value = "/commands/daily", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void onReceiveSlashCommandDaily(@RequestParam("token") String token,
                                           @RequestParam("user_name") String fromUser,
                                           @RequestParam("text") String text,
                                           @RequestParam("response_url") String responseUrl,
                                           HttpServletResponse servletResponse) throws IOException {
        logger.debug("Received slash command Daily achievement: user: [{}] command: [{}] token: [{}]" +
                " responseUrl : [{}]", fromUser, text, token, responseUrl);

        if (!token.equals(slackToken)) {
            logger.warn("Received invalid slack token: [{}] in command Daily for user: [{}] ", token, fromUser);
            sendInstantResponseMessage(servletResponse, SORRY_MESSAGE);
        }
        exceptionsHandler.setResponseUrl(responseUrl);
        sendInstantResponseMessage(servletResponse, INSTANT_MESSAGE);

        String response = DAILY_ERROR_MESSAGE;

        logger.debug("Started create slackParsedCommand and create achievement request");
        SlackParsedCommand slackParsedCommand = slackNameHandlerService.createSlackParsedCommand(fromUser, text);
        DailyAchievement daily = new DailyAchievement(slackParsedCommand);
        logger.debug("Finished create slackParsedCommand and create achievement request");

        logger.debug("Send daily achievement request to Gamifcation service. Achievement: [{}]", daily.toString());
        String[] result = gamificationService.sendDailyAchievement(daily);
        logger.debug("Received response from Gamification service: [{}]", Arrays.toString(result));

        if (result.length == 1) {
            response = DAILY_THANKS_MESSAGE;
        }

        logger.info("Daily command processed : user: [{}] text: [{}] and sent response into slack: [{}]",
                fromUser, text, response);

        RichMessage message = new RichMessage(response);
        sendDelayedResponseMessage(responseUrl, message);
    }

    @RequestMapping(value = "/commands/thanks", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void onReceiveSlashCommandThanks(@RequestParam("token") String token,
                                            @RequestParam("user_name") String fromUser,
                                            @RequestParam("text") String text,
                                            @RequestParam("response_url") String responseUrl,
                                            HttpServletResponse servletResponse) throws IOException {
        logger.debug("Received slash command Thanks achievement: user: [{}] command: [{}] token: [{}]",
                fromUser, text, token);
        if (!token.equals(slackToken)) {
            logger.warn("Received invalid slack token: [{}] in command thanks for user: [{}]", token, fromUser);
            sendInstantResponseMessage(servletResponse, SORRY_MESSAGE);
        }
        exceptionsHandler.setResponseUrl(responseUrl);
        sendInstantResponseMessage(servletResponse, INSTANT_MESSAGE);

        String response = THANKS_ERROR_MESSAGE;

        logger.debug("Started create slackParsedCommand and create achievement request");
        SlackParsedCommand slackParsedCommand = slackNameHandlerService.createSlackParsedCommand(fromUser, text);
        ThanksAchievement thanks = new ThanksAchievement(slackParsedCommand);
        logger.debug("Finished create slackParsedCommand and create achievement request");

        logger.debug("Sent thanks achievement request to Gamifcation service. Achievement: [{}]", thanks.toString());
        String[] result = gamificationService.sendThanksAchievement(thanks);
        logger.debug("Received response from Gamification service: [{}]", Arrays.toString(result));

        if (result.length == 1) {
            response = thanks.injectSlackNames(THANKS_ONE_THANKS_MESSAGE);
        }
        if (result.length == 2) {
            response = thanks.injectSlackNames(THANKS_TWO_THANKS_MESSAGE);
        }

        logger.info("Thanks command processed : user: [{}] text: [{}] and sent response into slack: [{}]",
                fromUser, text, response);

        RichMessage message = new RichMessage(response);
        sendDelayedResponseMessage(responseUrl, message);
    }

    @RequestMapping(value = "/commands/interview",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void onReceiveSlashCommandInterview(@RequestParam("token") String token,
                                               @RequestParam("user_name") String fromUser,
                                               @RequestParam("text") String text,
                                               @RequestParam("response_url") String responseUrl,
                                               HttpServletResponse servletResponse) throws IOException {
        logger.debug("Received slash command Interview achievement: user: [{}] command: [{}] token: [{}]",
                fromUser, text, token);

        if (!token.equals(slackToken)) {
            logger.warn("Received invalid slack token: [{}] in command interview for user: [{}]", token, fromUser);
            sendInstantResponseMessage(servletResponse, SORRY_MESSAGE);
        }
        exceptionsHandler.setResponseUrl(responseUrl);
        sendInstantResponseMessage(servletResponse, INSTANT_MESSAGE);

        String response = INTERVIEW_ERROR_MESSAGE;

        logger.debug("Started create slackParsedCommand and create achievement request");
        SlackParsedCommand slackParsedCommand = slackNameHandlerService.createSlackParsedCommand(fromUser, text);
        InterviewAchievement interview = new InterviewAchievement(slackParsedCommand);
        logger.debug("Finished create slackParsedCommand and create achievement request");

        logger.debug("Send interview achivement request  to Gamifcation service. Achievement: [{}]", interview.toString());
        String[] result = gamificationService.sendInterviewAchievement(interview);
        logger.debug("Received response from Gamification service: [{}]", Arrays.toString(result));

        if (result.length == 1) {
            response = INTERVIEW_THANKS_MESSAGE;
        }

        logger.info("Interview command processed : user: [{}] text: [{}] and sent response into slack: [{}]",
                fromUser, text, response);

        RichMessage message = new RichMessage(response);
        sendDelayedResponseMessage(responseUrl, message);
    }

    private void sendInstantResponseMessage(HttpServletResponse response, String message) throws IOException {
        logger.debug("Before sending instant response message '{}' ", message);
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter printWriter = response.getWriter();
        printWriter.print(message);
        printWriter.flush();
        printWriter.close();
        logger.info("Sent instant response message to slack '{}' ", message);
    }

    private void sendDelayedResponseMessage(String responseUrl, RichMessage message) {
        logger.debug("Before sending delayed response message '{}' to slack url '{}' ", message, responseUrl);
        String response = restTemplate.postForObject(responseUrl, message, String.class);
        logger.debug("After sending delayed response message. Response is '{}'", response);
    }
}