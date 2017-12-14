package juja.microservices.gamification.slackbot.model.achievements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import juja.microservices.gamification.slackbot.model.SlackParsedCommand;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Vitalii Viazovoi
 * @author Nikolay Horushko
 * @author Danil Kuznetsov kuznetsov.danil.v@gmail.com
 */
@Getter
@ToString
public class ThanksAchievement implements ResponseWithSlackUsers {
    @JsonProperty("from")
    private String fromUuid;
    @JsonProperty("to")
    private String toUuid;
    @JsonIgnore
    private UserDTO toUser;
    @JsonProperty
    private String description;

    public ThanksAchievement(String fromUuid, String to, String description) {
        this.fromUuid = fromUuid;
        this.toUuid = to;
        this.description = description;
    }

    public ThanksAchievement(SlackParsedCommand slackParsedCommand) {
        this.fromUuid = slackParsedCommand.getFromUser().getUuid();
        this.toUser = receiveToUser(slackParsedCommand);
        this.toUuid = toUser.getUuid();
        this.description = slackParsedCommand.getTextWithoutSlackUsers();
    }

    private UserDTO receiveToUser(SlackParsedCommand slackParsedCommand) {
        if (slackParsedCommand.getUserCountInText() > 1) {
            throw new WrongCommandFormatException(String.format("We found %d slack user in your command: '%s' " +
                            " You can't send thanks more than one user.", slackParsedCommand.getUserCountInText(),
                    slackParsedCommand.getText()));
        }
        if (slackParsedCommand.getUserCountInText() == 0) {
            throw new WrongCommandFormatException(String.format("We didn't find slack user in your command. '%s'" +
                    " You must write user's slack user for 'thanks'.", slackParsedCommand.getText()));
        }
        return slackParsedCommand.getFirstUser();
    }

    @Override
    public String injectSlackUsers(String messageFormat) {
        return String.format(messageFormat, toUser.getSlackUser());
    }
}