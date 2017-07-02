package juja.microservices.gamification.slackbot.model.achievements;

import com.fasterxml.jackson.annotation.JsonProperty;
import juja.microservices.gamification.slackbot.model.SlackParsedCommand;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Artem
 * @author Nikolay Horushko
 */

@Getter
@ToString
public class InterviewAchievement {
    @JsonProperty
    private String from;
    @JsonProperty
    private String description;

    public InterviewAchievement(String from, String description) {
        this.from = from;
        this.description = description;
    }

    public InterviewAchievement(SlackParsedCommand slackParsedCommand) {
        this.from = slackParsedCommand.getFromUser().getUuid();
        this.description = slackParsedCommand.getText();
    }
}


