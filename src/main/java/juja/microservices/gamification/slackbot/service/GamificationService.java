package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.model.CodenjoyAchievment;
import juja.microservices.gamification.slackbot.model.DailyAchievement;

/**
 * @author Danil Kuznetsov
 */
public interface GamificationService {
    String sendDailyAchievement(DailyAchievement daily);

    String sendCodenjoyAchievement(CodenjoyAchievment codenjoy);
}
