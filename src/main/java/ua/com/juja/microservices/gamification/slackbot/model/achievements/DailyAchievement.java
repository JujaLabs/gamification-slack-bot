package ua.com.juja.microservices.gamification.slackbot.model.achievements;

import com.fasterxml.jackson.annotation.JsonProperty;
import ua.com.juja.slack.command.handler.model.SlackParsedCommand;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Nikolay Horushko
 */
@Getter
@ToString
@EqualsAndHashCode
public class DailyAchievement {
    @JsonProperty("from")
    private String fromUuid;
    @JsonProperty
    private String description;

    public DailyAchievement(String fromUuid, String description) {
        this.fromUuid = fromUuid;
        this.description = description;
    }

    public DailyAchievement(SlackParsedCommand slackParsedCommand) {
        this.fromUuid = slackParsedCommand.getFromUser().getUuid();
        this.description = slackParsedCommand.getText();
    }
}
