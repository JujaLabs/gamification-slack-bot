package juja.microservices.gamification.slackbot.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Created by Nikol on 3/4/2017.
 */
@Getter
public class CodenjoyRequest {
    private String from;
    private String firstPlace;
    private String secondPlace;
    private String thirdPlace;

    @JsonCreator
    public CodenjoyRequest(@JsonProperty("from") String from,
                           @JsonProperty("firstPlace") String firstPlace,
                           @JsonProperty("secondPlace") String secondPlace,
                           @JsonProperty("thirdPlace") String thirdPlace) {
        this.from = from;
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("from: ")
                .append(from).append("\n")
                .append("firstPlace: ")
                .append(firstPlace).append("\n")
                .append("secondPlace: ")
                .append(secondPlace).append("\n")
                .append("thirdPlace: ")
                .append(thirdPlace).append("\n");
        return stringBuilder.toString();
    }

}