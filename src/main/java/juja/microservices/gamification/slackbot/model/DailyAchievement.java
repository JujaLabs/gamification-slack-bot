package juja.microservices.gamification.slackbot.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Danil Kuznetsov
 */
@Getter
@Setter
@ToString
public class DailyAchievement {
    private String from;
    private String description;

    public DailyAchievement(String from, String description) {
        this.from = from;
        this.description = description;
    }
}
