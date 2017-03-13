package juja.microservices.gamification.slackbot.utils;

import juja.microservices.gamification.slackbot.dao.DummyUserRepository;
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
    DummyUserRepository userRepository;

    public CodenjoyHandler(DummyUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CodenjoyAchievment recieveCodenjoyAchievment(String from, String command) {
        CodenjoyParcer parcer = new CodenjoyParcer();
        List<String> slackNames = parcer.recieveSlackNames(command);
        return createCodenjoyAchievment(from, recieveUserNamesBySlackNames(slackNames));
    }


    private List<String> recieveUserNamesBySlackNames(List<String> slackNames) {
        List<String> result = new ArrayList<>();
        for (String slackName : slackNames) {
            result.add(userRepository.getUserNameBySlackName(slackName));
        }
        return result;
    }

    private CodenjoyAchievment createCodenjoyAchievment(String from, List<String> userNames) {
        return new CodenjoyAchievment(from, userNames.get(0), userNames.get(1), userNames.get(2));
    }

    private class CodenjoyParcer {
        /**
         * Slack
         * Usernames must be all lowercase. They cannot be longer than 21 characters and
         * can only contain letters, numbers, periods, hyphens, and underscores.
         * ([a-z0-9\.\_\-]){1,21}
         * Command example -1th @slack_nick_name -2th @slack_nick_name2 -3th @slack_nick_name3
         * quick test regExp http://regexr.com/
         */
        private final String namePattern = "([a-z0-9\\.\\_\\-]){1,21}";
        private final String commandPattern = "-1th\\s@" + namePattern + "\\s" +
                "-2th\\s@" + namePattern + "\\s" +
                "-3th\\s@" + namePattern;
        private final String regExpForSplit = " -";
        private final char slackNameFirstChar = '@';

        public List<String> recieveSlackNames(String command) {
            checkCommand(command);
            return createSlackNamesList(command);
        }

        private void checkCommand(String command) {
            if (command == null) {
                throw new WrongCommandFormatException(String.format("Wrong command text expected %s, but actual null",
                        "-1th @slack_nick_name -2th @slack_nick_name2 -3th @slack_nick_name3", command));
            }
            if (!Pattern.matches(commandPattern, command)) {
                throw new WrongCommandFormatException(String.format("Wrong command text expected %s, but actual %s",
                        "-1th @slack_nick_name -2th @slack_nick_name2 -3th @slack_nick_name3", command));
            }
        }

        private List<String> createSlackNamesList(String command) {
            List<String> parameters = Arrays.asList(command.split(regExpForSplit));
            return extractSlackNames(parameters);
        }

        private List<String> extractSlackNames(List<String> parameters) {
            List<String> result = new ArrayList<>();
            for (String parameter : parameters) {
                parameter.trim();
                result.add(parameter.substring(parameter.indexOf(slackNameFirstChar)));
            }
            return result;
        }
    }
}
