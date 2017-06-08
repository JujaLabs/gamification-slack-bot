package juja.microservices.gamification.slackbot.controller;

import juja.microservices.gamification.slackbot.model.CodenjoyAchievement;
import juja.microservices.gamification.slackbot.model.DailyAchievement;
import juja.microservices.gamification.slackbot.model.InterviewAchievement;
import juja.microservices.gamification.slackbot.model.ThanksAchievement;
import juja.microservices.gamification.slackbot.service.GamificationService;
import juja.microservices.gamification.slackbot.service.UserService;
import juja.microservices.gamification.slackbot.service.impl.SlackNameHandlerService;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * @author Nikolay Horushko
 */
@RestController
public class GamificationSlackCommandController {
    @Value("${slack.slashCommandToken}")
    private String slackToken;

    private final SlackNameHandlerService slackNameHandlerService;
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private GamificationService gamificationService;

    @Inject
    public GamificationSlackCommandController(GamificationService gamificationService,
                                              UserService userService,
                                              SlackNameHandlerService slackNameHandlerService) {
        this.gamificationService = gamificationService;
        this.slackNameHandlerService = slackNameHandlerService;
        this.userService = userService;
    }

    @RequestMapping(value = "/commands/codenjoy",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RichMessage onReceiveSlashCommandCodenjoy(@RequestParam("token") String token,
                                                     @RequestParam("user_name") String fromUser,
                                                     @RequestParam("text") String text) {
        if (!token.equals(slackToken)) {
            logger.warn("Received invalid slack token: '{}' in command 'codenjoy'", token);
            return getRichMessageInvalidSlackCommand();
        }
        String response;
        try {
            String fromUserUuid = userService.findUuidUserBySlack(fromUser);
            String preparedTextWithUuid = slackNameHandlerService.replaceSlackNamesToUuids(text);
            CodenjoyAchievement codenjoy = new CodenjoyAchievement(fromUserUuid, preparedTextWithUuid);
            logger.debug("Send codenjoy achievement request. fromUserUuid: {}; prepared text: {}",
                    fromUserUuid, preparedTextWithUuid);
            response = gamificationService.sendCodenjoyAchievement(codenjoy);
            logger.debug("Received response from gamification service: {}", response);
        } catch (Exception ex) {
            logger.warn("Exception in command 'codenjoy': {}", ex.getMessage());
            return new RichMessage(ex.getMessage());
        }
        return new RichMessage(response);
    }

    @RequestMapping(value = "/commands/daily",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RichMessage onReceiveSlashCommandDaily(@RequestParam("token") String token,
                                                  @RequestParam("user_name") String fromUser,
                                                  @RequestParam("text") String text) {
        if (!token.equals(slackToken)) {
            logger.warn("Received invalid slack token: '{}' in command 'daily'", token);
            return getRichMessageInvalidSlackCommand();
        }
        String response;
        try {
            String fromUserUuid = userService.findUuidUserBySlack(fromUser);
            DailyAchievement daily = new DailyAchievement(fromUserUuid, text);
            logger.debug("Send daily achievement request. fromUserUuid: {}; text: {}", fromUserUuid, text);
            response = gamificationService.sendDailyAchievement(daily);
            logger.debug("Received response from gamification service: {}", response);
        } catch (Exception ex) {
            logger.warn("Exception in command 'daily': {}", ex.getMessage());
            return new RichMessage(ex.getMessage());
        }
        return new RichMessage(response);
    }

    @RequestMapping(value = "/commands/thanks",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RichMessage onReceiveSlashCommandThanks(@RequestParam("token") String token,
                                                   @RequestParam("user_name") String fromUser,
                                                   @RequestParam("text") String text) {
        if (!token.equals(slackToken)) {
            logger.warn("Received invalid slack token: '{}' in command 'thanks'", token);
            return getRichMessageInvalidSlackCommand();
        }
        String response;
        try {
            String fromUserUuid = userService.findUuidUserBySlack(fromUser);
            String preparedTextWithUuid = slackNameHandlerService.replaceSlackNamesToUuids(text);
            ThanksAchievement thanks = new ThanksAchievement(fromUserUuid, preparedTextWithUuid);
            logger.debug("Send thanks achivement request. fromUserUuid: {}; prepared text: {}",
                    fromUserUuid, preparedTextWithUuid);
            response = gamificationService.sendThanksAchievement(thanks);
            logger.debug("Received response from gamification service: {}", response);
        } catch (Exception ex) {
            logger.warn("Exception in command 'thanks': {}", ex.getMessage());
            return new RichMessage(ex.getMessage());
        }
        return new RichMessage(response);
    }

    @RequestMapping(value = "/commands/interview",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RichMessage onReceiveSlashCommandInterview(@RequestParam("token") String token,
                                                      @RequestParam("user_name") String fromUser,
                                                      @RequestParam("text") String text) {
        if (!token.equals(slackToken)) {
            logger.warn("Received invalid slack token: '{}' in command 'interview'", token);
            return getRichMessageInvalidSlackCommand();
        }
        String response;
        try {
            String fromUserUuid = userService.findUuidUserBySlack(fromUser);
            String preparedTextWithUuid = slackNameHandlerService.replaceSlackNamesToUuids(text);
            InterviewAchievement interview = new InterviewAchievement(fromUserUuid, preparedTextWithUuid);
            logger.debug("Send interview achivement request. fromUserUuid: {}; prepared text: {}",
                    fromUserUuid, preparedTextWithUuid);
            response = gamificationService.sendInterviewAchievement(interview);
            logger.debug("Received response from gamification service: {}", response);
        } catch (Exception ex) {
            logger.warn("Exception in command 'interview': {}", ex.getMessage());
            return new RichMessage(ex.getMessage());
        }
        return new RichMessage(response);
    }

    private RichMessage getRichMessageInvalidSlackCommand() {
        return new RichMessage("Sorry! You're not lucky enough to use our slack command.");
    }
}
