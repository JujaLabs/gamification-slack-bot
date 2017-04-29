package juja.microservices.gamification.slackbot.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Vitalii Viazovoi
 */
@Getter
public class ThanksAchievement {
    private String from;
    private String to;
    private String description;

    public ThanksAchievement(String from, String to, String description) {
        this.from = from;
        this.to = to;
        this.description = description;
    }
}