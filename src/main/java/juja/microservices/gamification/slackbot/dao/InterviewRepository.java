package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.model.Interview;

/**
 * Created by Artem
 */
public interface InterviewRepository {

    String saveInterviewAchievement(Interview interview);
}
