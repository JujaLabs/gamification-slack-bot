package juja.microservices.gamification.slackbot.model.achievements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import juja.microservices.gamification.slackbot.model.SlackParsedCommand;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

/**
 * @author Nikolay Horushko
 */
@Getter
@ToString
@JsonIgnoreProperties({"tokens"})
public class CodenjoyAchievement {
    @JsonProperty
    private String from;
    @JsonProperty
    private String firstPlace;
    @JsonProperty
    private String secondPlace;
    @JsonProperty
    private String thirdPlace;

    private String[] tokens = new String[]{"-1th", "-2th", "-3th"};

    public CodenjoyAchievement(String from, String firstPlace, String secondPlace, String thirdPlace) {
        this.from = from;
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    public CodenjoyAchievement(SlackParsedCommand parsedCommand) {
        this.from = parsedCommand.getFromUser().getUuid();
        Map<String, UserDTO> usersWithTokens = getUsersForTokens(parsedCommand, tokens);
        this.firstPlace = usersWithTokens.get(tokens[0]).getUuid();
        this.secondPlace = usersWithTokens.get(tokens[1]).getUuid();
        this.thirdPlace = usersWithTokens.get(tokens[2]).getUuid();
    }

    private Map<String, UserDTO> getUsersForTokens(SlackParsedCommand parsedCommand, String[] tokens){
        return parsedCommand.getUsersWithTokens(tokens);
    }
}