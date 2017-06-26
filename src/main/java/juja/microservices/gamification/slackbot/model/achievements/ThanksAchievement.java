package juja.microservices.gamification.slackbot.model.achievements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import lombok.Getter;
import lombok.ToString;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vitalii Viazovoi
 */
@Getter
@ToString
@JsonIgnoreProperties({"parsedUuidPattern", "parsedUuidStartMarker",
        "parsedUuidFinishMarker", "PARSED_UUID_PATTERN_WITH_MARKERS", "command_EXAMPLE", "ONE_UUID"})
public class ThanksAchievement {
    private String from;
    private String to;
    private String description;

    private static final String parsedUuidPattern = "@#([a-zA-z0-9\\-]){1,36}#@";
    private static final String parsedUuidStartMarker = "@#";
    private static final String parsedUuidFinishMarker = "#@";
    private final String COMMAND_EXAMPLE = "/thanks Thanks to @slack_nick_name for help.";

    public ThanksAchievement(String from, String to, String description) {
        this.from = from;
        this.to = to;
        this.description = description;
    }

    public ThanksAchievement(String fromUserUuid, String text) {
        this.from = fromUserUuid;
        this.to = findUuid(text);
        this.description = text.replaceAll(parsedUuidStartMarker.concat(this.to).concat(parsedUuidFinishMarker),"").
                replaceAll(" +"," ");
    }

    @Override
    public String toString() {
        return "ThanksAchievement{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", description='" + description + "'}";
    }

    private String findUuid(String text) {
        Pattern pattern = Pattern.compile(parsedUuidPattern);
        Matcher matcher = pattern.matcher(text);
        String uuid = "";
        if (matcher.find()) {
            uuid = matcher.group();
            if (matcher.find()) {
                throwWrongCommandFormatException();
            }
        } else {
            throwWrongCommandFormatException();
        }
        return uuid.replaceAll(parsedUuidStartMarker,"").replaceAll(parsedUuidFinishMarker,"");
    }

    private void throwWrongCommandFormatException() {
        throw new WrongCommandFormatException(String.format("Wrong command. Example for this command %s",
                COMMAND_EXAMPLE));
    }
}