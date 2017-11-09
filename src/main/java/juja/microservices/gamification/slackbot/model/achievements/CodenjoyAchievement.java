package juja.microservices.gamification.slackbot.model.achievements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import juja.microservices.gamification.slackbot.model.SlackParsedCommand;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(exclude = {"firstPlaceUser","secondPlaceUser","thirdPlaceUser"})
public class CodenjoyAchievement implements ResponseWithSlackName {
    @JsonProperty("from")
    private String fromUuid;
    @JsonProperty("firstPlace")
    private String firstPlaceUuid;
    private UserDTO firstPlaceUser;
    @JsonProperty("secondPlace")
    private String secondPlaceUuid;
    private UserDTO secondPlaceUser;
    @JsonProperty("thirdPlace")
    private String thirdPlaceUuid;
    private UserDTO thirdPlaceUser;


    private String[] tokens = new String[]{"-1th", "-2th", "-3th"};

    public CodenjoyAchievement(String fromUuid, String firstPlaceUuid, String secondPlaceUuid, String thirdPlaceUuid) {
        this.fromUuid = fromUuid;
        this.firstPlaceUuid = firstPlaceUuid;
        this.secondPlaceUuid = secondPlaceUuid;
        this.thirdPlaceUuid = thirdPlaceUuid;
    }

    public CodenjoyAchievement(SlackParsedCommand parsedCommand) {
        this.fromUuid = parsedCommand.getFromUser().getUuid();
        Map<String, UserDTO> usersWithTokens = parsedCommand.getUsersWithTokens(tokens);
        this.firstPlaceUser = usersWithTokens.get(tokens[0]);
        this.firstPlaceUuid = firstPlaceUser.getUuid();

        this.secondPlaceUser = usersWithTokens.get(tokens[1]);
        this.secondPlaceUuid = secondPlaceUser.getUuid();

        this.thirdPlaceUser = usersWithTokens.get(tokens[2]);
        this.thirdPlaceUuid = thirdPlaceUser.getUuid();
    }

    @Override
    public String injectSlackNames(String messageFormat) {
        return String.format(messageFormat, firstPlaceUser.getSlack(),
                secondPlaceUser.getSlack(), thirdPlaceUser.getSlack());
    }
}