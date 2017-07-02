package juja.microservices.gamification.slackbot.service.impl;

import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import juja.microservices.gamification.slackbot.model.SlackParsedCommand;
import juja.microservices.gamification.slackbot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Nikolay Horushko
 */

@Service
public class SlackNameHandlerService {

    private UserService userService;

    /**
     * Slack name cannot be longer than 21 characters and
     * can only contain letters, numbers, periods, hyphens, and underscores.
     * ([a-z0-9\.\_\-]){1,21}
     * quick test regExp http://regexr.com/
     */
    private final String SLACK_NAME_PATTERN = "@([a-zA-z0-9\\.\\_\\-]){1,21}";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    public SlackNameHandlerService(UserService userService) {
        this.userService = userService;
    }

    public SlackParsedCommand createSlackParsedCommand(String from, String text){
        if(!from.startsWith("@")){
            from = "@" + from;
        }
        return new SlackParsedCommand(from, text, receiveUsersMap(from, text));
    }

    private Map<String, UserDTO> receiveUsersMap(String from, String text){
        List<String> slackNames = receiveAllSlackNames(text);
        slackNames.add(from);
        List<UserDTO> users = userService.findUsersBySlackNames(slackNames);
        return users.stream()
                .collect(Collectors.toMap(user -> user.getSlack(), user -> user));
    }

    private List<String> receiveAllSlackNames(String text){
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(SLACK_NAME_PATTERN);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }
}

