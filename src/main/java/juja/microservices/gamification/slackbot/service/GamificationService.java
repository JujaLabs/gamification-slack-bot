package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.model.achievements.CodenjoyAchievement;
import juja.microservices.gamification.slackbot.model.achievements.DailyAchievement;
import juja.microservices.gamification.slackbot.model.achievements.InterviewAchievement;
import juja.microservices.gamification.slackbot.model.achievements.ThanksAchievement;

/**
 * @author Danil Kuznetsov
 */
public interface GamificationService {

    String[] sendDailyAchievement(DailyAchievement daily);

    String[] sendCodenjoyAchievement(CodenjoyAchievement codenjoy);

    String[] sendThanksAchievement(ThanksAchievement thanks);

    String[] sendInterviewAchievement(InterviewAchievement interview);
}
