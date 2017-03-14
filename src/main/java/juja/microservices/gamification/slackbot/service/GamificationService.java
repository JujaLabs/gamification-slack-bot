package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.model.DailyAchievement;
import juja.microservices.gamification.slackbot.model.Interview;
import juja.microservices.gamification.slackbot.model.User;

/**
 * @author Danil Kuznetsov
 */
public interface GamificationService {
    String sendDailyAchievement(DailyAchievement daily);
    User searchUser();
    String saveInterviewAchievement(Interview interview);
}
