package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.model.DailyAchievement;

/**
 * @author Danil Kuznetsov
 */
public interface GamificationService {
    String sendDailyAchievement(DailyAchievement daily);
}
