package juja.microservices.gamification.slackbot.controller;

import juja.microservices.gamification.slackbot.model.CodenjoyAchievement;
import juja.microservices.gamification.slackbot.model.DailyAchievement;
import juja.microservices.gamification.slackbot.model.InterviewAchievement;
import juja.microservices.gamification.slackbot.model.ThanksAchievement;
import juja.microservices.gamification.slackbot.service.GamificationService;
import juja.microservices.gamification.slackbot.service.UserService;
import juja.microservices.gamification.slackbot.service.impl.SlackNameHandlerService;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
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
            return getRichMessageInvalidSlackCommand();
        }
        String response = "ERROR. Something went wrong and we didn't award the users :(";

        try {
            String fromUserUuid = userService.findUuidUserBySlack(fromUser);
            String preparedTextWithUuid = slackNameHandlerService.replaceSlackNamesToUuids(text);
            CodenjoyAchievement codenjoy = new CodenjoyAchievement(fromUserUuid, preparedTextWithUuid);
            String[] result = gamificationService.sendCodenjoyAchievement(codenjoy);
            if (result.length == 3) {
                response = String.format("Thansks, we awarded the users.");
                //todo add slacknames
            }
        } catch (Exception ex) {
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
            return getRichMessageInvalidSlackCommand();
        }
        String response = "ERROR. Something went wrong and daily report didn't save.";
        try {
            String fromUserUuid = userService.findUuidUserBySlack(fromUser);
            DailyAchievement daily = new DailyAchievement(fromUserUuid, text);
            String[] result = gamificationService.sendDailyAchievement(daily);
            if (result.length == 1) {
                response = "Thanks, your daily report saved.";
            }
        } catch (Exception ex) {
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
            return getRichMessageInvalidSlackCommand();
        }
        String response = "Error. Something went wrong and we didn't save the thanks.";
        try {
            String fromUserUuid = userService.findUuidUserBySlack(fromUser);
            String preparedTextWithUuid = slackNameHandlerService.replaceSlackNamesToUuids(text);
            ThanksAchievement thanks = new ThanksAchievement(fromUserUuid, preparedTextWithUuid);
            String[] result = gamificationService.sendThanksAchievement(thanks);
            if (result.length == 1) {
                response = "Thanks, your 'thanks' saved.";
            }// todo add slackname
            if (result.length == 2) {
                response = "Thanks, your 'thanks' saved. Also you received +1 for your activity .";
            } // todo add slackname
        } catch (Exception ex) {
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
            return getRichMessageInvalidSlackCommand();
        }
        String response = "ERROR. Something went wrong and we didn't save your interview";
        try {
            String fromUserUuid = userService.findUuidUserBySlack(fromUser);
            String preparedTextWithUuid = slackNameHandlerService.replaceSlackNamesToUuids(text);
            InterviewAchievement interview = new InterviewAchievement(fromUserUuid, preparedTextWithUuid);
            String[] result = gamificationService.sendInterviewAchievement(interview);
            if(result.length == 1){
                response = "Thanks. Your interview saved";
            }
        } catch (Exception ex) {
            return new RichMessage(ex.getMessage());
        }
        return new RichMessage(response);
    }

    private RichMessage getRichMessageInvalidSlackCommand() {
        return new RichMessage("Sorry! You're not lucky enough to use our slack command.");
    }
}
