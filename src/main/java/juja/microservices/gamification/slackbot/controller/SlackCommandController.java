package juja.microservices.gamification.slackbot.controller;

import juja.microservices.gamification.slackbot.model.CodenjoyAchievment;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by Nikol on 3/9/2017.
 */
@RestController
public class SlackCommandController {
    private final String slackToken = "slashCommandToken"; // todo read slackToken from properties
    private final String URL_SEND_CODENJOY = "/bot/codenjoy"; // todo read url from properties

    @RequestMapping(value = URL_SEND_CODENJOY,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RichMessage onReceiveSlashCommand(@RequestParam("token") String token,
                                             @RequestParam("user_id") String userId,
                                             @RequestParam("command") String command,
                                             @RequestParam("text") String text,
                                             @RequestParam("response_url") String responseUrl) {
        // validate token
        if (!token.equals(slackToken)) {
            return new RichMessage("Sorry! You're not lucky enough to use our slack command.");
        }
        /** build response */
        //parse message
        return null;
    }






}
