package juja.microservices.gamification.slackbot.utils;

import juja.microservices.gamification.slackbot.dao.DummyUserRepository;
import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;

import java.util.regex.Pattern;

/**
 * Created by Nikol on 3/16/2017.
 */
public class NameHandler {
    private DummyUserRepository userRepository;
    private final String SLACK_NAME_START_TOKEN = "@";
    private final String USER_ID_START_TOKEN = "@#";
    private final String USER_ID_FINISH_TOKEN = "#@";
    private final String PARAM_SPLIT_TOKEN = " ";
    private final String NAME_PATTERN = "@([a-z0-9\\.\\_\\-]){1,21}";

    public NameHandler(DummyUserRepository userRepository) {
        this.userRepository = new DummyUserRepository();
    }

    public String changeSlackNamesToUuid(String text) {
        if(!text.contains(SLACK_NAME_START_TOKEN)){
            return text;
        }
        String[] params = text.split(PARAM_SPLIT_TOKEN);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            if (params[i].contains(SLACK_NAME_START_TOKEN)) {
                String slackName = extractSlackName(params[i]);
                checkSlackName(slackName);
                params[i] = String.format("%s%s%s", USER_ID_START_TOKEN,
                        userRepository.getUserNameBySlackName(slackName), USER_ID_FINISH_TOKEN);
            }
            result.append(params[i]).append(PARAM_SPLIT_TOKEN);
        }
        return result.toString();
    }

    private String extractSlackName(String param) {
        return param.substring(param.indexOf(SLACK_NAME_START_TOKEN));
    }

    /**
     * Slack
     * Usernames must be all lowercase. They cannot be longer than 21 characters and
     * can only contain letters, numbers, periods, hyphens, and underscores.
     * ([a-z0-9\.\_\-]){1,21}
     * Command example -1th @slack_nick_name -2th @slack_nick_name2 -3th @slack_nick_name3
     * quick test regExp http://regexr.com/
     */
    private void checkSlackName(String slackName){
        if(!Pattern.matches(NAME_PATTERN, slackName.toLowerCase())){
            throw new WrongCommandFormatException(String.format("Illegal slack name. They cannot be longer than 21 characters and can only contain letters, numbers, periods, hyphens, and underscores. actual : %s", slackName)); //todo change description
        }
    }
}

