package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.model.InterviewAchievement;

/**
 * Created by Artem
 */
public interface InterviewRepository {

    String saveInterviewAchievement(InterviewAchievement interviewAchievement);
}
