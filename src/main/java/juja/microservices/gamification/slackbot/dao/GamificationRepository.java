package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.model.DailyAchievement;
import juja.microservices.gamification.slackbot.model.Interview;
import juja.microservices.gamification.slackbot.model.User;

/**
 * @author Danil Kuznetsov
 */

public interface GamificationRepository {
    String saveDailyAchievement(DailyAchievement daily);
    User findUserBySlackGmail();
    String saveInterviewAchievement(Interview interview);
}
