package juja.microservices.gamification.slackbot.controller;

import juja.microservices.gamification.slackbot.model.CodenjoyAchievement;
import juja.microservices.gamification.slackbot.service.GamificationService;

import juja.microservices.gamification.slackbot.service.SlackNameHandlerService;
import juja.microservices.gamification.slackbot.service.UserService;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;


/**
 * Created by Nikol on 3/9/2017.
 */
@RestController
public class GamificationSlackCommandController {
    private final String slackToken = "slashCommandToken"; // todo read slackToken from properties
    private final String URL_RECEIVE_CODENJOY = "/commands/codenjoy"; // todo read url from properties
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

    @RequestMapping(value = URL_RECEIVE_CODENJOY,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RichMessage onReceiveSlashCommandCodenjoy(@RequestParam("token") String token,
                                                     @RequestParam("user_name") String fromUser,
                                                     @RequestParam("command") String commandName,
                                                     @RequestParam("text") String text) {
        if (!token.equals(slackToken)) {
            return new RichMessage("Sorry! You're not lucky enough to use our slack command.");
        }
        String response;
        try {
            String fromUserUuid = userService.findUuidUserBySlack(fromUser);
            String preparedTextWithUuid = slackNameHandlerService.replaceSlackNamesToUuids(text);
            CodenjoyAchievement codenjoy = new CodenjoyAchievement(fromUserUuid, preparedTextWithUuid);
            response = gamificationService.sendCodenjoyAchievement(codenjoy);
        }catch (Exception ex){
            return new RichMessage(ex.getMessage());
        }
        return new RichMessage(response);
    }
}
