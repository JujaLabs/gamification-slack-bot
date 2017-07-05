package juja.microservices.gamification.slackbot.service.impl;

import juja.microservices.gamification.slackbot.dao.GamificationRepository;
import juja.microservices.gamification.slackbot.model.CodenjoyAchievement;
import juja.microservices.gamification.slackbot.model.DailyAchievement;
import juja.microservices.gamification.slackbot.model.InterviewAchievement;
import juja.microservices.gamification.slackbot.model.ThanksAchievement;
import juja.microservices.gamification.slackbot.service.GamificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * @author Danil Kuznetsov
 */

@Service
public class DefaultGamificationService implements GamificationService {

    private final GamificationRepository gamificationRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    public DefaultGamificationService(GamificationRepository gamificationRepository) {
        this.gamificationRepository = gamificationRepository;
    }

    @Override
    public String[] sendDailyAchievement(DailyAchievement daily) {
        logger.debug("Received Daily achievement: [{}]", daily.toString());
        String[] ids = gamificationRepository.saveDailyAchievement(daily);
        logger.info("Added Daily achievement: [{}]", Arrays.toString(ids));
        return ids;
    }

    @Override
    public String[] sendCodenjoyAchievement(CodenjoyAchievement codenjoy) {
        logger.debug("Received Codenjoy achievement: [{}]", codenjoy.toString());
        String[] ids = gamificationRepository.saveCodenjoyAchievement(codenjoy);
        logger.info("Added Codenjoy achievement id: [{}]", Arrays.toString(ids));
        return ids;
    }

    @Override
    public String[] sendThanksAchievement(ThanksAchievement thanks) {
        logger.debug("Received Thanks achievement: [{}]", thanks.toString());
        String [] ids = gamificationRepository.saveThanksAchievement(thanks);
        logger.info("Added Thanks achievement id: [{}]", Arrays.toString(ids));
        return ids;
    }

    @Override
    public String[] sendInterviewAchievement(InterviewAchievement interview) {
        logger.debug("Received Interview achievement: [{}]", interview.toString());
        String[] ids = gamificationRepository.saveInterviewAchievement(interview);
        logger.info("Added Interview achievement id: [{}]", Arrays.toString(ids));
        return ids;
    }
}
