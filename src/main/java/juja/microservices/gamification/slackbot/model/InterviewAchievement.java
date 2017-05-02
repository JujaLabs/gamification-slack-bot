package juja.microservices.gamification.slackbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * @author Artem
 */

@Getter
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


