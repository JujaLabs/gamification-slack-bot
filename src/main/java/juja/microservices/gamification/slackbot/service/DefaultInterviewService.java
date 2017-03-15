package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.dao.InterviewRepository;
import juja.microservices.gamification.slackbot.model.InterviewAchievement;

/**
 * Created by Artem
 */
public class DefaultInterviewService implements InterviewService {

    private final InterviewRepository interviewRepository;

    public DefaultInterviewService(InterviewRepository interviewRepository) {
        this.interviewRepository = interviewRepository;
    }

    @Override
    public String saveInterviewAchievement(InterviewAchievement interviewAchievement) {
        return interviewRepository.saveInterviewAchievement(interviewAchievement);
    }
}
