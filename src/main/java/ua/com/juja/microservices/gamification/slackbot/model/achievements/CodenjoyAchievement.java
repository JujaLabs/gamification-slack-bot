package ua.com.juja.microservices.gamification.slackbot.model.achievements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ua.com.juja.slack.command.handler.model.UserDTO;
import ua.com.juja.slack.command.handler.model.SlackParsedCommand;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Nikolay Horushko
 */
@Getter
@ToString
@JsonIgnoreProperties({"tokens", "okSlackResponse", "firstPlaceUser",
        "secondPlaceUser", "thirdPlaceUser", "firstPlaceToken", "secondPlaceToken", "thirdPlaceToken"})
@EqualsAndHashCode(exclude = {"firstPlaceUser","secondPlaceUser","thirdPlaceUser"})
public class CodenjoyAchievement implements ResponseWithSlackName {

    private final String firstPlaceToken = "-1th";
    private final String secondPlaceToken = "-2th";
    private final String thirdPlaceToken = "-3th";

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


    private Set<String> tokens;

    public CodenjoyAchievement(String fromUuid, String firstPlaceUuid, String secondPlaceUuid, String thirdPlaceUuid) {
        this.fromUuid = fromUuid;
        this.firstPlaceUuid = firstPlaceUuid;
        this.secondPlaceUuid = secondPlaceUuid;
        this.thirdPlaceUuid = thirdPlaceUuid;
    }

    public CodenjoyAchievement(SlackParsedCommand parsedCommand) {
        tokens = new HashSet<>();
        tokens.addAll(Arrays.asList(firstPlaceToken, secondPlaceToken, thirdPlaceToken));
        this.fromUuid = parsedCommand.getFromUser().getUuid();
        Map<String, UserDTO> usersWithTokens = parsedCommand.getUsersWithTokens(tokens);
        this.firstPlaceUser = usersWithTokens.get(firstPlaceToken);
        this.firstPlaceUuid = firstPlaceUser.getUuid();

        this.secondPlaceUser = usersWithTokens.get(secondPlaceToken);
        this.secondPlaceUuid = secondPlaceUser.getUuid();

        this.thirdPlaceUser = usersWithTokens.get(thirdPlaceToken);
        this.thirdPlaceUuid = thirdPlaceUser.getUuid();
    }

    @Override
    public String injectSlackId(String messageFormat) {
        return String.format(messageFormat, firstPlaceUser.getSlackId(),
                secondPlaceUser.getSlackId(), thirdPlaceUser.getSlackId());
    }
}