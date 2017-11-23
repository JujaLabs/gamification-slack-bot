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
    @JsonProperty("from")
    private String fromUuid;
    @JsonProperty
    private String description;

    public InterviewAchievement(String fromUuid, String description) {
        this.fromUuid = fromUuid;
        this.description = description;
    }

    public InterviewAchievement(SlackParsedCommand slackParsedCommand) {
        this.fromUuid = slackParsedCommand.getFromUser().getUuid();
        this.description = slackParsedCommand.getText();
    }
}


