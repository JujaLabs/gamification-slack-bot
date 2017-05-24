package juja.microservices.gamification.slackbot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Nikolay Horushko
 */
@Getter
@AllArgsConstructor
public class UserDTO {
    String uuid;
    String slack;
}
