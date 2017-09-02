package juja.microservices.gamification.slackbot.controller;

import juja.microservices.gamification.slackbot.exceptions.ExceptionsHandler;
import juja.microservices.gamification.slackbot.service.GamificationService;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Nikolay Horushko
 * @author Ivan Shapovalov
 * @author Konstantin Sergey
 */
@RestController
@RequestMapping(value = "/" + "${gamification.slackbot.rest.api.version}" + "${gamification.slackbot.commandsUrl}")
public class GamificationSlackCommandController {

    private final static String INSTANT_MESSAGE = "Your command accepted. Please wait...";
    private final static String SORRY_MESSAGE = "Sorry! You're not lucky enough to use our slack command.";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${slack.slashCommandToken}")
    private String slackToken;
    private GamificationService gamificationService;
    private RestTemplate restTemplate;
    private ExceptionsHandler exceptionsHandler;

    @Inject
    public GamificationSlackCommandController(GamificationService gamificationService,
                                              RestTemplate restTemplate,
                                              ExceptionsHandler exceptionsHandler) {
        this.gamificationService = gamificationService;
        this.restTemplate = restTemplate;
        this.exceptionsHandler = exceptionsHandler;
    }

    @PostMapping(value = "${gamification.slackbot.endpoint.codenjoy}",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void onReceiveSlashCommandCodenjoy(@RequestParam("token") String token,
                                              @RequestParam("user_name") String fromUser,
                                              @RequestParam("text") String text,
                                              @RequestParam("response_url") String responseUrl,
                                              HttpServletResponse servletResponse) throws IOException {

        logger.debug("Received slash command Condenjoy achievement: from user: [{}] command: [{}] token: [{}]",
                fromUser, text, token);

        if (!token.equals(slackToken)) {
            logger.warn("Received invalid slack token: [{}] in command Codenjoy for user: [{}]", token, fromUser);
            sendInstantResponseMessage(servletResponse, SORRY_MESSAGE);
        }
        exceptionsHandler.setResponseUrl(responseUrl);
        sendInstantResponseMessage(servletResponse, INSTANT_MESSAGE);

        String responseToSlack = gamificationService.sendCodenjoyAchievement(fromUser, text);

        logger.info("Codenjoy command processed : user: [{}] text: [{}] and sent response into slack: [{}]",
                fromUser, text, responseToSlack);

        RichMessage message = new RichMessage(responseToSlack);
        sendDelayedResponseMessage(responseUrl, message);
    }

    @PostMapping(value = "${gamification.slackbot.endpoint.daily}",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void onReceiveSlashCommandDaily(@RequestParam("token") String token,
                                           @RequestParam("user_name") String fromUser,
                                           @RequestParam("text") String text,
                                           @RequestParam("response_url") String responseUrl,
                                           HttpServletResponse servletResponse) throws IOException {

        logger.debug("Received slash command Daily achievement: user: [{}] command: [{}] token: [{}]",
                fromUser, text, token);

        if (!token.equals(slackToken)) {
            logger.warn("Received invalid slack token: [{}] in command Daily for user: [{}] ", token, fromUser);
            sendInstantResponseMessage(servletResponse, SORRY_MESSAGE);
        }
        exceptionsHandler.setResponseUrl(responseUrl);
        sendInstantResponseMessage(servletResponse, INSTANT_MESSAGE);

        String responseToSlack = gamificationService.sendDailyAchievement(fromUser, text);

        logger.info("Daily command processed : user: [{}] text: [{}] and sent response into slack: [{}]",
                fromUser, text, responseToSlack);

        RichMessage message = new RichMessage(responseToSlack);
        sendDelayedResponseMessage(responseUrl, message);
    }

    @PostMapping(value = "${gamification.slackbot.endpoint.thanks}",
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

        String responseToSlack = gamificationService.sendThanksAchievement(fromUser, text);

        logger.info("Thanks command processed : user: [{}] text: [{}] and sent response into slack: [{}]",
                fromUser, text, responseToSlack);

        RichMessage message = new RichMessage(responseToSlack);
        sendDelayedResponseMessage(responseUrl, message);
    }

    @PostMapping(value = "${gamification.slackbot.endpoint.interview}",
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

        String responseToSlack = gamificationService.sendInterviewAchievement(fromUser, text);

        logger.info("Interview command processed : user: [{}] text: [{}] and sent response into slack: [{}]",
                fromUser, text, responseToSlack);

        RichMessage message = new RichMessage(responseToSlack);
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