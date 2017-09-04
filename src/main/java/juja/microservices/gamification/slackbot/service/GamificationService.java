package juja.microservices.gamification.slackbot.service;

/**
 * @author Danil Kuznetsov
 */
public interface GamificationService {

    String sendDailyAchievement(String fromUser, String text);

    String sendCodenjoyAchievement(String fromUser, String text);

    String sendThanksAchievement(String fromUser, String text);

    String sendInterviewAchievement(String fromUser, String text);

    String sendTeamAchievement(String fromUser, String text);
}
