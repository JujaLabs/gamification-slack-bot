package juja.microservices.gamification.slackbot.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Created by Nikol on 5/24/2017.
 */
@Getter
@AllArgsConstructor
public class SlackNameRequest {
    List<String> slackNames;
}
