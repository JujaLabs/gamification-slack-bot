package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.exceptions.GamificationExchangeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import javax.inject.Inject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nikolay on 3/16/2017.
 */
@PropertySource("application.properties")
public class SlackNameHandlerService {
    private UserService userService;
    @Value("${parcedUuid.startMarker}")
    private String parcedUuidStartMarker;
    @Value("${parcedUuid.finishMarker}")
    private String parcedUuidFinishMarker;
    /**
     * Slack name cannot be longer than 21 characters and
     * can only contain letters, numbers, periods, hyphens, and underscores.
     * ([a-z0-9\.\_\-]){1,21}
     * quick test regExp http://regexr.com/
     */
    private final String SLACK_NAME_PATTERN = "@([a-zA-z0-9\\.\\_\\-]){1,21}";

    @Inject
    public SlackNameHandlerService(UserService userService) {
        this.userService = userService;
    }

    public String replaceSlackNamesToUuids(String text) {
        if (text == null) {
            return "";
        }
        Pattern pattern = Pattern.compile(SLACK_NAME_PATTERN);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            try {
                String slackName = matcher.group();
                String uuid = userService.findUuidUserBySlack(slackName.toLowerCase());
                text = text.replaceAll(slackName, parcedUuidStartMarker + uuid + parcedUuidFinishMarker);
            }catch (GamificationExchangeException ex){
                //
            }
        }
        return text;
    }
}

