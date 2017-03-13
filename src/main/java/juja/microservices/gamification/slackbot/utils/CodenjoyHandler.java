package juja.microservices.gamification.slackbot.utils;

import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import juja.microservices.gamification.slackbot.model.CodenjoyAchievment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Nikol on 3/9/2017.
 */
public class CodenjoyHandler {
    /**
     * Slack
     * Usernames must be all lowercase. They cannot be longer than 21 characters and
     * can only contain letters, numbers, periods, hyphens, and underscores.
     * ([a-z0-9\.\_\-]){1,21}
     * Command example -1th @slack_nick_name -2th @slack_nick_name2 -3th @slack_nick_name3
     * quick test regExp http://regexr.com/
     */
    private final String namePattern = "([a-z0-9\\.\\_\\-]){1,21}";
    private final String commandPattern =   "-1th\\s@" + namePattern + "\\s" +
                                            "-2th\\s@" + namePattern + "\\s" +
                                            "-3th\\s@" + namePattern;
    private final String regExpForSplit = " -";
    private final char slackNameFirstChar = '@';

    public CodenjoyAchievment recieveCodenjoyAchievment(String from, String command) {
        checkCommand(command);
        return createCodenjoyAchievment(from, createNamesList(command));
    }

    private void checkCommand (String command){
        if (command == null){
            throw new WrongCommandFormatException(String.format("Wrong command text expected %s, but actual null",
                    "-1th @slack_nick_name -2th @slack_nick_name2 -3th @slack_nick_name3", command));
        }
        if(!Pattern.matches(commandPattern, command)){
            throw new WrongCommandFormatException(String.format("Wrong command text expected %s, but actual %s",
                    "-1th @slack_nick_name -2th @slack_nick_name2 -3th @slack_nick_name3", command));
        }
    }

    private CodenjoyAchievment createCodenjoyAchievment(String from, List<String> names){
        return new CodenjoyAchievment(from, names.get(0), names.get(1), names.get(2)); //todo something with the magic numbers
    }

    private List<String> createNamesList(String command) {
        List<String> parameters = Arrays.asList(command.split(regExpForSplit));
        return extractNames(parameters);
    }

    private List<String> extractNames(List<String> parameters){
        List<String> result = new ArrayList<>();
        for (String parameter : parameters) {
            parameter.trim();
            result.add(parameter.substring(parameter.indexOf(slackNameFirstChar)));
        }
        return result;
    }
}
