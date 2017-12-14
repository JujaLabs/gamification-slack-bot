package juja.microservices.gamification.slackbot.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * @author Nikolay Horushko
 */
@Getter
@AllArgsConstructor
@ToString
public class SlackUsersRequest {
    @JsonProperty("slackIds")
    List<String> slackUsers;
}
