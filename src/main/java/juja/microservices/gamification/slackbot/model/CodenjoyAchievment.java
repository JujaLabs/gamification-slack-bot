package juja.microservices.gamification.slackbot.model;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by Nikol on 3/4/2017.
 */
@Getter
@ToString
public class CodenjoyAchievment {
    private String from;
    private String firstPlace;
    private String secondPlace;
    private String thirdPlace;

    public CodenjoyAchievment(String from,
                              String firstPlace,
                              String secondPlace,
                              String thirdPlace) {
        this.from = from;
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }
}