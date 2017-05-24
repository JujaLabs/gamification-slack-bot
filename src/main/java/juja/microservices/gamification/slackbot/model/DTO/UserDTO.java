package juja.microservices.gamification.slackbot.model.DTO;

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
