package juja.microservices.gamification.slackbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Artem
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
}


