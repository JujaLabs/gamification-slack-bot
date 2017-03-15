package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.model.Interview;

/**
 * Created by Artem
 */
public interface InterviewService {
    String saveInterviewAchievement(Interview interview);
}
