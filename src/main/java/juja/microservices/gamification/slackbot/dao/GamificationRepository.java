package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.model.CodenjoyAchievment;
import juja.microservices.gamification.slackbot.model.DailyAchievement;
import juja.microservices.gamification.slackbot.model.InterviewAchievement;
import juja.microservices.gamification.slackbot.model.ThanksAchievement;

/**
 * @author Danil Kuznetsov
 */

public interface GamificationRepository {
    String saveDailyAchievement(DailyAchievement daily);

    String saveCodenjoyAchievement(CodenjoyAchievment codenjoy);

    String saveThanksAchievement(ThanksAchievement thanks);

    String saveInterviewAchievement(InterviewAchievement interview);
}
