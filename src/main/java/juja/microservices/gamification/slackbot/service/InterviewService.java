package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.model.InterviewAchievement;

/**
 * Created by Artem
 */
public interface InterviewService {
    String saveInterviewAchievement(InterviewAchievement interviewAchievement);
}
