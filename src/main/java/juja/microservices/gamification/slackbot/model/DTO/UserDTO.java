package juja.microservices.gamification.slackbot.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Nikolay Horushko
 * @author Danil Kuznetsov kuznetsov.danil.v@gmail.com
 */
@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UserDTO {
    @JsonProperty
    String uuid;
    @JsonProperty("slackId")
    String slackUser;
}
