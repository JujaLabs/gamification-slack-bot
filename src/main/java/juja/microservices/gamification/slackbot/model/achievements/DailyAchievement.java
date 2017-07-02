package juja.microservices.gamification.slackbot.model.achievements;

import com.fasterxml.jackson.annotation.JsonProperty;
import juja.microservices.gamification.slackbot.model.SlackParsedCommand;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Nikolay Horushko
 */
@Getter
@ToString
public class DailyAchievement {
    @JsonProperty
    private String from;
    @JsonProperty
    private String description;

    public DailyAchievement(String from, String description) {
        this.from = from;
        this.description = description;
    }

    public DailyAchievement (SlackParsedCommand slackParsedCommand) {
        this.from = slackParsedCommand.getFromUser().getUuid();
        this.description = slackParsedCommand.getText();
    }
}
