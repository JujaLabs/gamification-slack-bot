package juja.microservices.gamification.slackbot.dao.impl;

import feign.FeignException;
import juja.microservices.gamification.slackbot.dao.GamificationRepository;
import juja.microservices.gamification.slackbot.dao.feign.GamificationClient;
import juja.microservices.gamification.slackbot.exceptions.ApiError;
import juja.microservices.gamification.slackbot.exceptions.GamificationExchangeException;
import juja.microservices.gamification.slackbot.model.achievements.CodenjoyAchievement;
import juja.microservices.gamification.slackbot.model.achievements.DailyAchievement;
import juja.microservices.gamification.slackbot.model.achievements.InterviewAchievement;
import juja.microservices.gamification.slackbot.model.achievements.TeamAchievement;
import juja.microservices.gamification.slackbot.model.achievements.ThanksAchievement;
import juja.microservices.gamification.slackbot.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * @author Danil Kuznetsov
 * @author Ivan Shapovalov
 */
@Repository
public class RestGamificationRepository implements GamificationRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    private GamificationClient gamificationClient;

    @Override
    public String[] saveDailyAchievement(DailyAchievement daily) {
        logger.debug("Received Daily achievement: [{}]", daily.toString());
        String[] achievementIds;
        try {
            achievementIds = gamificationClient.saveDailyAchievement(daily);
            logger.debug("Finished request to Gamification service. Ids is: [{}]", Arrays.toString(achievementIds));
        } catch (FeignException ex) {
            ApiError error = Utils.convertToApiError(ex.getMessage());
            logger.warn("Gamification service returned an error: [{}]", error);
            throw new GamificationExchangeException(error, ex);
        }
        logger.info("Saved Daily achievement: [{}]", Arrays.toString(achievementIds));
        return achievementIds;
    }

    @Override
    public String[] saveCodenjoyAchievement(CodenjoyAchievement codenjoy) {
        logger.debug("Received Codenjoy achievement: [{}]", codenjoy.toString());
        String[] achievementIds;
        try {
            achievementIds = gamificationClient.saveCodenjoyAchievement(codenjoy);
            logger.debug("Finished request to Gamification service. Ids is: [{}]", Arrays.toString(achievementIds));
        } catch (FeignException ex) {
            ApiError error = Utils.convertToApiError(ex.getMessage());
            logger.warn("Gamification service returned an error: [{}]", error);
            throw new GamificationExchangeException(error, ex);
        }
        logger.info("Saved Codenjoy achievements: [{}]", Arrays.toString(achievementIds));
        return achievementIds;
    }

    @Override
    public String[] saveThanksAchievement(ThanksAchievement thanks) {
        logger.debug("Received Thanks achievement: [{}]", thanks.toString());
        String[] achievementIds;
        try {
            achievementIds = gamificationClient.saveThanksAchievement(thanks);
            logger.debug("Finished request to Gamification service. Ids is: [{}]", Arrays.toString(achievementIds));
        } catch (FeignException ex) {
            ApiError error = Utils.convertToApiError(ex.getMessage());
            logger.warn("Gamification service returned an error: [{}]", error);
            throw new GamificationExchangeException(error, ex);
        }
        logger.info("Saved Thanks achievements: [{}]", Arrays.toString(achievementIds));
        return achievementIds;
    }

    @Override
    public String[] saveInterviewAchievement(InterviewAchievement interview) {
        logger.debug("Received Interview achievement: [{}]", interview.toString());
        String[] achievementIds;
        try {
            achievementIds = gamificationClient.saveInterviewAchievement(interview);
            logger.debug("Finished request to Gamification service. Ids is: [{}]", Arrays.toString(achievementIds));
        } catch (FeignException ex) {
            ApiError error = Utils.convertToApiError(ex.getMessage());
            logger.warn("Gamification service returned an error: [{}]", error);
            throw new GamificationExchangeException(error, ex);
        }
        logger.info("Saved Interview achievements: [{}]", Arrays.toString(achievementIds));
        return achievementIds;
    }

    @Override
    public String[] saveTeamAchievement(TeamAchievement team) {
        logger.debug("Received Team achievement: [{}]", team.toString());
        String[] achievementIds;
        try {
            achievementIds = gamificationClient.saveTeamAchievement(team);
            logger.debug("Finished request to Gamification service. Ids is: [{}]", Arrays.toString(achievementIds));
        } catch (FeignException ex) {
            ApiError error = Utils.convertToApiError(ex.getMessage());
            logger.warn("Gamification service returned an error: [{}]", error);
            throw new GamificationExchangeException(error, ex);
        }
        logger.info("Saved Team achievements: [{}]", Arrays.toString(achievementIds));
        return achievementIds;
    }
}