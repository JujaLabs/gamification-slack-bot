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
public class SlackCommandService {

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
    public SlackCommandService(UserService userService) {
        this.userService = userService;
    }

    public SlackParsedCommand createSlackParsedCommand(String fromSlackName, String text) {
        if (!fromSlackName.startsWith("@")) {
            logger.debug("add '@' to slack name: [{}]", fromSlackName);
            fromSlackName = "@" + fromSlackName;
        }
        return new SlackParsedCommand(fromSlackName, text, receiveUsersMap(fromSlackName, text));
    }

    private Map<String, UserDTO> receiveUsersMap(String fromSlackName, String text) {
        List<String> slackNames = receiveAllSlackNames(text);
        slackNames.add(fromSlackName);
        logger.debug("added \"fromSlackName\" slack name to request: [{}]", fromSlackName);
        logger.debug("send slack names: {} to user service", slackNames);
        List<UserDTO> users = userService.findUsersBySlackNames(slackNames);
        return users.stream()
                .collect(Collectors.toMap(UserDTO::getSlack, user -> user));
    }

    private List<String> receiveAllSlackNames(String text) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(SLACK_NAME_PATTERN);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            result.add(matcher.group().trim());
        }
        logger.debug("Recieved slack names: {} from text:", result.toString(), text);
        return result;
    }
}

