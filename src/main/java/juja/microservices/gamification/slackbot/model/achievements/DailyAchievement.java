package juja.microservices.gamification.slackbot.model.achievements;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DailyAchievement {
    @JsonProperty
    private String from;
    @JsonProperty
    private String description;

    public DailyAchievement (String from, String description) {
        this.from = from;
        this.description = description;
    }
}
