package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.dao.GamificationRepository;
import juja.microservices.gamification.slackbot.model.CodenjoyAchievment;
import juja.microservices.gamification.slackbot.model.DailyAchievement;
import juja.microservices.gamification.slackbot.model.ThanksAchievement;

import javax.inject.Inject;

/**
 * @author Danil Kuznetsov
 */
public class DefaultGamificationService implements GamificationService {

    private final GamificationRepository gamificationRepository;

    @Inject
    public DefaultGamificationService(GamificationRepository gamificationRepository) {
        this.gamificationRepository = gamificationRepository;
    }

    @Override
    public String sendDailyAchievement(DailyAchievement daily) {
        return gamificationRepository.saveDailyAchievement(daily);
    }

    @Override
    public String sendCodenjoyAchievement(CodenjoyAchievment codenjoy) {
        return gamificationRepository.saveCodenjoyAchievement(codenjoy);
    }

    @Override
    public String sendThanksAchievement(ThanksAchievement thanks) {
        return gamificationRepository.saveThanksAchievement(thanks);
    }
}
