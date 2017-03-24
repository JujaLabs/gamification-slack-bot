package juja.microservices.gamification.slackbot.utils;

import juja.microservices.gamification.slackbot.model.Achievement;
import juja.microservices.gamification.slackbot.model.Command;

/**
 * Created by Nikol on 3/19/2017.
 */
public interface AchievementParcer {
    //todo read the constans from property file
    String PARCED_UUID_PATTERN = "@#([a-zA-z0-9\\.\\_\\-]){1,21}#@"; //todo uuid format???
    String PARCED_UUID_START_MARKER = "@#";
    String PARCED_UUID_FINISH_MARKER = "#@";
    Achievement createAchievementFromCommand(Command command);
}
