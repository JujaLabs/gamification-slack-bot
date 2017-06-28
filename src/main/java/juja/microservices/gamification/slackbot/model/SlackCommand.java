package juja.microservices.gamification.slackbot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Nikol on 6/26/2017.
 */
@AllArgsConstructor
@Getter
public class SlackCommand {
    private String from;
    private String preparedText;
}
