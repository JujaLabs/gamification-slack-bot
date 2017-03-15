package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.dao.InterviewRepository;
import juja.microservices.gamification.slackbot.model.Interview;

/**
 * Created by Artem
 */
public class DefaultInterviewService implements InterviewService {

    private final InterviewRepository interviewRepository;

    public DefaultInterviewService(InterviewRepository interviewRepository) {
        this.interviewRepository = interviewRepository;
    }

    @Override
    public String saveInterviewAchievement(Interview interview) {
        return interviewRepository.saveInterviewAchievement(interview);
    }
}
