package juja.microservices.gamification.slackbot.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nikolay on 3/16/2017.
 */
public class SlackNameHandlerService {
    private UserService userService;
    private final String USER_UUID_START_TOKEN = "@#";
    private final String USER_UUID_FINISH_TOKEN = "#@";
    /**
     * Slack name cannot be longer than 21 characters and
     * can only contain letters, numbers, periods, hyphens, and underscores.
     * ([a-z0-9\.\_\-]){1,21}
     * Command example -1th @slack_nick_name -2th @slack_nick_name2 -3th @slack_nick_name3
     * quick test regExp http://regexr.com/
     */
    private final String SLACK_NAME_PATTERN = "@([a-zA-z0-9\\.\\_\\-]){1,21}";

    public SlackNameHandlerService(UserService userService) {
        this.userService = userService;
    }

    public String handle(String text) {
        if (text == null) {
            return "";
        }
        List<String> slackNames = extractSlackNamesFromText(text);
        return replaceSlackNamesToUuid(text, slackNames);
    }

    private List<String> extractSlackNamesFromText(String input) {
        Pattern pattern = Pattern.compile(SLACK_NAME_PATTERN);
        Matcher matcher = pattern.matcher(input);
        List<String> slackNames = new ArrayList<>();
        while (matcher.find()) {
            slackNames.add(matcher.group());
        }
        return slackNames;
    }

    private String replaceSlackNamesToUuid(String input, List<String> slackNames) {
        for (String slackName : slackNames) {
            String uuid = userService.findUserBySlack(slackName.toLowerCase()).getUuid();
            input = input.replaceAll(slackName, USER_UUID_START_TOKEN + uuid + USER_UUID_FINISH_TOKEN);
        }
        return input;
    }
}

