package juja.microservices.gamification.slackbot.service.impl;

import juja.microservices.gamification.slackbot.exceptions.UserExchangeException;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import juja.microservices.gamification.slackbot.model.SlackParsedCommand;
import juja.microservices.gamification.slackbot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${parsedUuid.startMarker}")
    private String parsedUuidStartMarker;
    @Value("${parsedUuid.finishMarker}")
    private String parsedUuidFinishMarker;
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
        return new SlackParsedCommand(from, text, receiveAllSlackNames(text), getUsersMap(from, text));
    }

    private Map<String, UserDTO> getUsersMap(String from, String text){
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


















    public String replaceSlackNamesToUuids(String text) {
        logger.debug("Received text for processing: [{}]", text);

        if (text == null) {
            return "";
        }
        Pattern pattern = Pattern.compile(SLACK_NAME_PATTERN);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String slackName = matcher.group();
            try {
                logger.debug("Started conversion SlackName: [{}] to UUID", slackName);
                String uuid = userService.findUuidUserBySlack(slackName.toLowerCase());
                text = text.replaceAll(slackName, parsedUuidStartMarker + uuid + parsedUuidFinishMarker);
                logger.debug("Replaced SlackName: [{}] in text : [{}]", slackName, text);
                logger.debug("Finished conversion SlackName: [{}] to UUID : [{}]", slackName, uuid);
            } catch (UserExchangeException ex) {
                logger.warn("SlackName: [{}] is not convert to UUID and not be replace. Detail message: [{}]",
                        slackName, ex.detailMessage());
            }
        }

        logger.info("Replaced all finding SlackName to UUID. Result text: [{}]",text);
        return text;
    }
}

