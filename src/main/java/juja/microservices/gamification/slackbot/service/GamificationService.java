package juja.microservices.gamification.slackbot.service;

/**
 * @author Danil Kuznetsov
 */
public interface GamificationService {

    String sendDailyAchievement(String fromSlackUser, String text);

    String sendCodenjoyAchievement(String fromSlackUser, String text);

    String sendThanksAchievement(String fromSlackUser, String text);

    String sendInterviewAchievement(String fromSlackUser, String text);

    String sendTeamAchievement(String fromSlackUser, String text);
}
