package juja.microservices.gamification.slackbot.controller;


import juja.microservices.gamification.slackbot.model.Achievement;
import juja.microservices.gamification.slackbot.model.Command;
import juja.microservices.gamification.slackbot.service.SlackNameHandlerService;
import juja.microservices.gamification.slackbot.service.UserService;
import juja.microservices.gamification.slackbot.utils.AchievementFactory;
import juja.microservices.gamification.slackbot.service.GamificationService;

import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by Nikol on 3/9/2017.
 */
@RestController
public class SlackCommandController {
    private final String slackToken = "slashCommandToken"; // todo read slackToken from properties
    private final String URL_SEND_CODENJOY = "/achieve/codenjoy";
    private final String URL_RECEIVE_CODENJOY = "/commands/codenjoy"; // todo read url from properties

    private GamificationService gamificationService;
    private UserService userService;
    private AchievementFactory achievmentFactory;

    public SlackCommandController(GamificationService gamificationService, UserService userService) {
        this.gamificationService = gamificationService;
        this.userService = userService;
        this.achievmentFactory = new AchievementFactory(new SlackNameHandlerService(userService));
    }

    @RequestMapping(value = URL_RECEIVE_CODENJOY,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RichMessage onReceiveSlashCommandCodenjoy(@RequestParam("token") String token,
                                                     @RequestParam("user_name") String fromUser,
                                                     @RequestParam("command") String commandName,
                                                     @RequestParam("text") String text) {
        Command command = new Command(commandName, fromUser, text);
        return getRichMessage(token, command, URL_SEND_CODENJOY);
    }

    private RichMessage getRichMessage(String token, Command command, String urlForSend) {
        if (!token.equals(slackToken)) {
            return new RichMessage("Sorry! You're not lucky enough to use our slack command.");
        }
        String response;
        try {
            Achievement achievement = achievmentFactory.createAchievement(command);
            response = gamificationService.sendAchievement(URL_SEND_CODENJOY, achievement);
        }catch (Exception ex){
            return new RichMessage(ex.getMessage());
        }
        return new RichMessage(response);
    }
}
