package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.model.CodenjoyAchievment;
import juja.microservices.gamification.slackbot.model.DailyAchievement;
import juja.microservices.gamification.slackbot.model.ThanksAchievement;

/**
 * @author Danil Kuznetsov
 */
public interface GamificationService {
    String sendDailyAchievement(DailyAchievement daily);

    String sendCodenjoyAchievement(CodenjoyAchievment codenjoy);

    String sendThanksAchievement(ThanksAchievement thanks);
}
