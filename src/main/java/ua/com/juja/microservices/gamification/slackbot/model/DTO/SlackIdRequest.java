package ua.com.juja.microservices.gamification.slackbot.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author Nikolay Horushko
 */
@Getter
@AllArgsConstructor
public class SlackIdRequest {
    List<String> slackIds;
}
