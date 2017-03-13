package juja.microservices.gamification.slackbot.controller;

import juja.microservices.gamification.slackbot.dao.DummyUserRepository;
import juja.microservices.gamification.slackbot.model.CodenjoyAchievment;
import juja.microservices.gamification.slackbot.service.GamificationService;
import juja.microservices.gamification.slackbot.utils.CodenjoyHandler;
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
    private final String URL_SEND_CODENJOY = "/commands/codenjoy"; // todo read url from properties

    private GamificationService gamificationService;

    public SlackCommandController(GamificationService gamificationService) {
        this.gamificationService = gamificationService;
    }

    @RequestMapping(value = URL_SEND_CODENJOY,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RichMessage onReceiveSlashCommand(@RequestParam("token") String token,
                                             @RequestParam("user_id") String userId,
                                             @RequestParam("user_name") String userName,
                                             @RequestParam("command") String command,
                                             @RequestParam("text") String text,
                                             @RequestParam("response_url") String responseUrl) {
        if (!token.equals(slackToken)) {
            return new RichMessage("Sorry! You're not lucky enough to use our slack command.");
        }
        CodenjoyHandler codenjoyHandler = new CodenjoyHandler(new DummyUserRepository());
        CodenjoyAchievment codenjoy = codenjoyHandler.recieveCodenjoyAchievment(userName, text);
        String response = gamificationService.sendCodenjoyAchievement(codenjoy);
        return new RichMessage(response);
    }
}
