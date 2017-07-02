package juja.microservices.gamification.slackbot.model.achievements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
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
@JsonIgnoreProperties({"tokens", "okSlackResponse", "firstPlaceUser",
        "secondPlaceUser", "thirdPlaceUser"})
public class CodenjoyAchievement implements ResponseWithSlackName {
    @JsonProperty
    private String from;
    @JsonProperty
    private String firstPlace;
    @JsonProperty
    private String secondPlace;
    @JsonProperty
    private String thirdPlace;

    private UserDTO firstPlaceUser;
    private UserDTO secondPlaceUser;
    private UserDTO thirdPlaceUser;

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
        this.firstPlaceUser = usersWithTokens.get(tokens[0]);
        this.firstPlace = firstPlaceUser.getUuid();

        this.secondPlaceUser = usersWithTokens.get(tokens[1]);
        this.secondPlace = secondPlaceUser.getUuid();

        this.thirdPlaceUser = usersWithTokens.get(tokens[2]);
        this.thirdPlace = thirdPlaceUser.getUuid();
    }

    private Map<String, UserDTO> getUsersForTokens(SlackParsedCommand parsedCommand, String[] tokens){
        return parsedCommand.getUsersWithTokens(tokens);
    }

    @Override
    public String injectSlackNames(String messageFormat) {
        return String.format(messageFormat, firstPlaceUser.getSlack(),
                secondPlaceUser.getSlack(), thirdPlaceUser.getSlack());
    }
}