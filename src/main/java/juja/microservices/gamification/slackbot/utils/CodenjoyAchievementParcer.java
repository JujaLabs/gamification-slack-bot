package juja.microservices.gamification.slackbot.utils;

import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import juja.microservices.gamification.slackbot.model.*;
import juja.microservices.gamification.slackbot.model.Achievement;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nikol on 3/9/2017.
 */
public class CodenjoyAchievementParcer extends AchievementParcer {
    private final String COMMAND_EXAMPLE = "/codenjoy -1th @slack_nick_name -2th @slack_nick_name2 -3th @slack_nick_name3";
    private final String[] COMMAND_TOKENS = {"-1th", "-2th", "-3th"};
    private final String COMMAND_NAME = "/codenjoy";

    public Achievement createAchievementFromCommand(Command command) {
        checkCommand(command);
        List<String> winners = receiveUserNames(command.getText());
        return new CodenjoyAchievement(command.getFromUser(), winners.get(0), winners.get(1), winners.get(2));
    }

    private List<String> receiveUserNames(String text) {
        return extractUserNames(text);
    }

    private void checkCommand(Command command) {
        if(!command.getName().equals(COMMAND_NAME)){
            throw new WrongCommandFormatException(String.format("Expect command name '%s', but given '%s'", COMMAND_NAME, command.getName()));
        }
        for (String token : COMMAND_TOKENS) {
            if (!command.getText().contains(token)) {
                throw new WrongCommandFormatException(String.format("token '%s' not found. Example for this command \"%s\"", token, COMMAND_EXAMPLE));
            }
            int count;
            if ((count = command.getText().split(token).length) > 2) {
                throw new WrongCommandFormatException(String.format("token '%s' used %d times, but expected 1. Example for this command \"%s\"", token, count - 1, COMMAND_EXAMPLE));
            }
        }
    }

    private List<String> extractUserNames(String text) {
        List<String> result = new ArrayList<>();
        //bit.ly/2mFsE92
        String[] splitedText = text.split("(?=-[123]th)");
        for (String token : COMMAND_TOKENS) {
            result.add(findUuid(token, splitedText));
        }
        return result;
    }

    private String findUuid(String token, String[] splitedText) {
        for (String s : splitedText) {
            if (s.contains(token)) {
                Pattern uuidPattern = Pattern.compile(parcedUuidPattern);
                Matcher matcher = uuidPattern.matcher(s.substring(s.indexOf(token)));
                if (matcher.find()) {
                    return cleanTheUuidOfMarkers(matcher.group());
                }
            }
        }
        throw new WrongCommandFormatException(String.format("Not found username for token '%s'. Example for this command %s", token, COMMAND_EXAMPLE));
    }

    private String cleanTheUuidOfMarkers(String uuidWithMarkers){
        return uuidWithMarkers.replaceAll(parcedUuidStartMarker, "")
                .replaceAll(parcedUuidFinishMarker, "");
    }
}
