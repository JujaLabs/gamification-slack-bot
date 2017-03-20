package juja.microservices.gamification.slackbot.utils;

import juja.microservices.gamification.slackbot.model.Achievement;
import juja.microservices.gamification.slackbot.model.Command;

/**
 * Created by Nikol on 3/19/2017.
 */
public interface AchievementParcer {
    Achievement createAchievementFromCommand(Command command);
}
