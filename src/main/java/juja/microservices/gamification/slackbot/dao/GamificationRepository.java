package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.model.achievements.*;

/**
 * @author Danil Kuznetsov
 */

public interface GamificationRepository {

    String[] saveDailyAchievement(DailyAchievement daily);

    String[] saveCodenjoyAchievement(CodenjoyAchievement codenjoy);

    String[] saveThanksAchievement(ThanksAchievement thanks);

    String[] saveInterviewAchievement(InterviewAchievement interview);

    String[] saveTeamAchievement(TeamAchievement team);
}