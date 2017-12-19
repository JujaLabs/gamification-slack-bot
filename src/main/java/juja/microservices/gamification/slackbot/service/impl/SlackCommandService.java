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

import static juja.microservices.gamification.slackbot.model.SlackParsedCommand.SLACK_USER_PATTERN;

/**
 * @author Nikolay Horushko
 * @author Danil Kuznetsov kuznetsov.danil.v@gmail.com
 */
@Service
public class SlackCommandService {

    private UserService userService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    public SlackCommandService(UserService userService) {
        this.userService = userService;
    }

    public SlackParsedCommand createSlackCommand(String fromSlackUser, String text) {
        return new SlackParsedCommand(fromSlackUser, text, receiveUsersMap(fromSlackUser, text));
    }

    private Map<String, UserDTO> receiveUsersMap(String fromSlackUser, String text) {
        List<String> slackUsers = findAllSlackUsersIn(text);
        slackUsers.add(fromSlackUser);
        logger.debug("Send slack users: {} to user service", slackUsers);
        List<UserDTO> users = userService.findUsersBySlackUsers(slackUsers);
        return users.stream()
                .collect(Collectors.toMap(UserDTO::getSlackUser, user -> user));
    }

    private List<String> findAllSlackUsersIn(String text) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(SLACK_USER_PATTERN);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            result.add(matcher.group(1).trim());
        }
        logger.debug("Received slack users: {} from text:", result.toString(), text);
        return result;
    }
}

