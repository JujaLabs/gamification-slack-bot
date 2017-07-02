package juja.microservices.gamification.slackbot.model.achievements;

import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import juja.microservices.gamification.slackbot.model.SlackParsedCommand;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Vitalii Viazovoi
 * @author Nikolay Horushko
 */
@Getter
@ToString
public class ThanksAchievement {
    private String from;
    private String to;
    private String description;

    public ThanksAchievement(String from, String to, String description) {
        this.from = from;
        this.to = to;
        this.description = description;
    }

    public ThanksAchievement(SlackParsedCommand slackParsedCommand) {
        this.from = slackParsedCommand.getFromUser().getUuid();
        this.to = receiveToUser(slackParsedCommand).getUuid();
        this.description = slackParsedCommand.getText();
    }

    private UserDTO receiveToUser(SlackParsedCommand slackParsedCommand){
        if(slackParsedCommand.getUserCountInText() > 1){
            throw new WrongCommandFormatException(String.format("We found %d slack names in your command: '%s' " +
                    " You can't send thanks more than one user.", slackParsedCommand.getUserCountInText(),
                    slackParsedCommand.getText()));
        }
        if (slackParsedCommand.getUserCountInText() == 0){
            throw new WrongCommandFormatException(String.format("We didn't find slack name in your command. '%s'" +
                    " You must write user's slack name for 'thanks'.", slackParsedCommand.getText()));
        }
        return slackParsedCommand.getFirstUser();
    }
}