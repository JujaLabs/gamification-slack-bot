package juja.microservices.gamification.slackbot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nikol on 3/16/2017.
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Command {
    String name;
    String fromUser;
    String text;
}
