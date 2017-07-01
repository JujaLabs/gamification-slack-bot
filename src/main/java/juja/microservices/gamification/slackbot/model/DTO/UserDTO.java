package juja.microservices.gamification.slackbot.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Nikolay Horushko
 */
@Getter
@AllArgsConstructor
@ToString
public class UserDTO {
    String uuid;
    String slack;
}
