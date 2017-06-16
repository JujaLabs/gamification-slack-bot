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
import java.util.Arrays;

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

        logger.debug("Received slash command condenjoy achievement: user: {} command: {} token: {} ",
                fromUser, text, token);

        if (!token.equals(slackToken)) {
            logger.warn("Received invalid slack token: '{}' in command 'codenjoy' for user: {} ", token, fromUser);
            return getRichMessageInvalidSlackCommand();
        }

        String response = "ERROR. Something went wrong and we didn't award the users :(";

        logger.debug("Started convert slackname to uuid and create achievement request");
        String fromUserUuid = userService.findUuidUserBySlack(fromUser);
        String preparedTextWithUuid = slackNameHandlerService.replaceSlackNamesToUuids(text);
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(fromUserUuid, preparedTextWithUuid);
        logger.debug("Finished convert slackname to uuid and create achievement request");

        logger.debug("Sent codenjoy achievement request to gamifcation service. Achievement: {}",
                codenjoy.toString());
        String[] result = gamificationService.sendCodenjoyAchievement(codenjoy);
        logger.debug("Received response from gamification service: {}", Arrays.toString(result));

        if (result.length == 3) {
            response = String.format("Thanks, we awarded the users.");
            //todo add slacknames
        }

        logger.info("Codenjoy command processed : user: {} text: {} and sent response into slack: {}",
                fromUser, text, response);

        return new RichMessage(response);
    }

    @RequestMapping(value = "/commands/daily",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RichMessage onReceiveSlashCommandDaily(@RequestParam("token") String token,
                                                  @RequestParam("user_name") String fromUser,
                                                  @RequestParam("text") String text) {
        logger.debug("Received slash command daily achievement: user: {} command: {} token: {} ",
                fromUser, text, token);

        if (!token.equals(slackToken)) {
            logger.warn("Received invalid slack token: '{}' in command 'daily' for user: {} ", token, fromUser);
            return getRichMessageInvalidSlackCommand();
        }

        String response = "ERROR. Something went wrong and daily report didn't save.";

        logger.debug("Started convert slackname to uuid and create achievement request");
        String fromUserUuid = userService.findUuidUserBySlack(fromUser);
        DailyAchievement daily = new DailyAchievement(fromUserUuid, text);
        logger.debug("Finished convert slackname to uuid and create achievement request");

        logger.debug("Send daily achievement request to gamifcation service. Achievement: {}", daily.toString());
        String[] result = gamificationService.sendDailyAchievement(daily);
        logger.debug("Received response from gamification service: {}", Arrays.toString(result));

        if (result.length == 1) {
            response = "Thanks, your daily report saved.";
        }

        logger.info("Daily command processed : user: {} text: {} and sent response into slack: {}",
                fromUser, text, response);

        return new RichMessage(response);
    }

    @RequestMapping(value = "/commands/thanks",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RichMessage onReceiveSlashCommandThanks(@RequestParam("token") String token,
                                                   @RequestParam("user_name") String fromUser,
                                                   @RequestParam("text") String text) {

        logger.debug("Received slash command thanks achievement: user: {} command: {} token: {} ",
                fromUser, text, token);
        if (!token.equals(slackToken)) {
            logger.warn("Received invalid slack token: '{}' in command 'thanks' for user: {} ", token, fromUser);
            return getRichMessageInvalidSlackCommand();
        }
        String response = "Error. Something went wrong and we didn't save the thanks.";

        logger.debug("Started convert slackname to uuid and create achievement request");
        String fromUserUuid = userService.findUuidUserBySlack(fromUser);
        String preparedTextWithUuid = slackNameHandlerService.replaceSlackNamesToUuids(text);
        ThanksAchievement thanks = new ThanksAchievement(fromUserUuid, preparedTextWithUuid);
        logger.debug("Finished convert slackname to uuid and create achievement request");

        logger.debug("Sent thanks achievement request to gamifcation service. Achievement: {}",thanks.toString());
        String[] result = gamificationService.sendThanksAchievement(thanks);
        logger.debug("Received response from gamification service: {}", Arrays.toString(result));

        if (result.length == 1) {
            response = "Thanks, your 'thanks' saved.";
        }// todo add slackname
        if (result.length == 2) {
            response = "Thanks, your 'thanks' saved. Also you received +1 for your activity.";
        } // todo add slackname

        logger.info("Thanks command processed : user: {} text: {} and sent response into slack: {}",
                fromUser, text, response);

        return new RichMessage(response);
    }

    @RequestMapping(value = "/commands/interview",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RichMessage onReceiveSlashCommandInterview(@RequestParam("token") String token,
                                                      @RequestParam("user_name") String fromUser,
                                                      @RequestParam("text") String text) {

        logger.debug("Received slash command interview achievement: user: {} command: {} token: {} ",
                fromUser, text, token);

        if (!token.equals(slackToken)) {
            logger.warn("Received invalid slack token: '{}' in command 'interview' for user: {} ", token, fromUser);
            return getRichMessageInvalidSlackCommand();
        }
        String response = "ERROR. Something went wrong and we didn't save your interview";

        logger.debug("Started convert slackname to uuid and create achievement request");
        String fromUserUuid = userService.findUuidUserBySlack(fromUser);
        InterviewAchievement interview = new InterviewAchievement(fromUserUuid, text);
        logger.debug("Finished convert slackname to uuid and create achievement request");

        logger.debug("Send interview achivement request  to gamifcation service. Achievement: {}", interview.toString());
        String[] result = gamificationService.sendInterviewAchievement(interview);
        logger.debug("Received response from gamification service: {}", Arrays.toString(result));

        if (result.length == 1) {
            response = "Thanks. Your interview saved.";
            logger.debug("Sent response to slack: {}", response);
        }

        logger.info("Interview command processed : user: {} text: {} and sent response into slack: {}",
                fromUser, text, response);
        return new RichMessage(response);
    }

    private RichMessage getRichMessageInvalidSlackCommand() {
        return new RichMessage("Sorry! You're not lucky enough to use our slack command.");
    }
}