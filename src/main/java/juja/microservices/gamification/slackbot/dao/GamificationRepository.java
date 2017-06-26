package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.model.achievements.CodenjoyAchievement;
import juja.microservices.gamification.slackbot.model.achievements.DailyAchievement;
import juja.microservices.gamification.slackbot.model.achievements.InterviewAchievement;
import juja.microservices.gamification.slackbot.model.achievements.ThanksAchievement;

/**
 * @author Danil Kuznetsov
 */

public interface GamificationRepository {

    String[] saveDailyAchievement(DailyAchievement daily);

    String[] saveCodenjoyAchievement(CodenjoyAchievement codenjoy);

    String[] saveThanksAchievement(ThanksAchievement thanks);

    String[] saveInterviewAchievement(InterviewAchievement interview);

}