package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.model.*;

/**
 * @author Danil Kuznetsov
 */
public interface GamificationService {

    String sendDailyAchievement(DailyAchievement daily);

    String sendCodenjoyAchievement(CodenjoyAchievment codenjoy);

    String sendThanksAchievement(ThanksAchievement thanks);

    String saveInterviewAchievement(InterviewAchievement interview);
}
