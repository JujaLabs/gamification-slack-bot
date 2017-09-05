package juja.microservices.gamification.slackbot.dao.impl;

import juja.microservices.gamification.slackbot.dao.GamificationRepository;
import juja.microservices.gamification.slackbot.exceptions.ApiError;
import juja.microservices.gamification.slackbot.exceptions.GamificationExchangeException;
import juja.microservices.gamification.slackbot.model.achievements.*;
import juja.microservices.gamification.slackbot.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * @author Danil Kuznetsov
 */

@Repository
public class RestGamificationRepository implements GamificationRepository {

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${gamification.endpoint.daily}")
    private String gamificationSendDailyUrl;
    @Value("${gamification.endpoint.codenjoy}")
    private String gamificationSendCodenjoyUrl;
    @Value("${gamification.endpoint.thanks}")
    private String gamificationSendThanksUrl;
    @Value("${gamification.endpoint.interview}")
    private String gamificationSendInterviewUrl;
    @Value("${gamification.endpoint.team}")
    private String gamificationSendTeamUrl;

    @Inject
    public RestGamificationRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String[] saveDailyAchievement(DailyAchievement daily) {
        logger.debug("Received Daily achievement: [{}]", daily.toString());

        HttpEntity<DailyAchievement> request = new HttpEntity<>(daily, Utils.setupJsonHttpHeaders());
        String[] result;
        try {
            logger.debug("Started request to Gamification service. Request is : [{}]", request.toString());
            ResponseEntity<String[]> response = restTemplate.exchange(gamificationSendDailyUrl,
                    HttpMethod.POST, request, String[].class);
            result = response.getBody();
            logger.debug("Finished request to Gamification service. Response is: [{}]", response.toString());
        } catch (HttpClientErrorException ex) {
            ApiError error = Utils.convertToApiError(ex);
            logger.warn("Gamification service returned an error: [{}]", error);
            throw new GamificationExchangeException(Utils.convertToApiError(ex), ex);
        }

        logger.info("Saved Daily achievement: [{}]", Arrays.toString(result));
        return result;
    }

    @Override
    public String[] saveCodenjoyAchievement(CodenjoyAchievement codenjoy) {
        logger.debug("Received Codenjoy achievement: [{}]", codenjoy.toString());

        HttpEntity<CodenjoyAchievement> request = new HttpEntity<>(codenjoy, Utils.setupJsonHttpHeaders());
        String[] result;

        try {
            logger.debug("Started request to Gamification service. Request is : [{}]", request.toString());
            ResponseEntity<String[]> response = restTemplate.exchange(gamificationSendCodenjoyUrl,
                    HttpMethod.POST, request, String[].class);
            result = response.getBody();
            logger.debug("Finished request to Gamification service. Response is: [{}]", response.toString());
        } catch (HttpClientErrorException ex) {
            ApiError error = Utils.convertToApiError(ex);
            logger.warn("Gamification service returned an error: [{}]", error);
            throw new GamificationExchangeException(error, ex);
        }
        logger.info("Saved Codenjoy achievements: [{}]", Arrays.toString(result));
        return result;
    }

    @Override
    public String[] saveThanksAchievement(ThanksAchievement thanks) {
        logger.debug("Received Thanks achievement: [{}]", thanks.toString());

        HttpEntity<ThanksAchievement> request = new HttpEntity<>(thanks, Utils.setupJsonHttpHeaders());
        String[] result;
        try {
            logger.debug("Started request to Gamification service. Request is : [{}]", request.toString());
            ResponseEntity<String[]> response = restTemplate.exchange(gamificationSendThanksUrl,
                    HttpMethod.POST, request, String[].class);
            result = response.getBody();
            logger.debug("Finished request to Gamification service. Response is: [{}]", response.toString());
        } catch (HttpClientErrorException ex) {
            ApiError error = Utils.convertToApiError(ex);
            logger.warn("Gamification service returned an error: [{}]", error);
            throw new GamificationExchangeException(Utils.convertToApiError(ex), ex);
        }

        logger.info("Saved Thanks achievements: [{}]", Arrays.toString(result));
        return result;
    }

    @Override
    public String[] saveInterviewAchievement(InterviewAchievement interview) {
        logger.debug("Received Interview achievement: [{}]", interview.toString());

        HttpEntity<InterviewAchievement> request = new HttpEntity<>(interview, Utils.setupJsonHttpHeaders());
        String[] result;
        try {
            logger.debug("Started request to Gamification service. Request is : [{}]", request.toString());
            ResponseEntity<String[]> response = restTemplate.exchange(gamificationSendInterviewUrl,
                    HttpMethod.POST, request, String[].class);
            result = response.getBody();
            logger.debug("Finished request to Gamification service. Response is: [{}]", response.toString());
        } catch (HttpClientErrorException ex) {
            ApiError error = Utils.convertToApiError(ex);
            logger.warn("Gamification service returned an error: [{}]", error);
            throw new GamificationExchangeException(Utils.convertToApiError(ex), ex);
        }

        logger.info("Saved Interview achievements: [{}]", Arrays.toString(result));
        return result;
    }

    @Override
    public String[] saveTeamAchievement(TeamAchievement team) {
        logger.debug("Received Team achievement: [{}]", team.toString());

        HttpEntity<TeamAchievement> request = new HttpEntity<>(team, Utils.setupJsonHttpHeaders());
        String[] result;

        try {
            logger.debug("Started request to Gamification service. Request is : [{}]", request.toString());
            ResponseEntity<String[]> response = restTemplate.exchange(gamificationSendTeamUrl,
                    HttpMethod.POST, request, String[].class);
            result = response.getBody();
            logger.debug("Finished request to Gamification service. Response is: [{}]", response.toString());
        } catch (HttpClientErrorException ex) {
            ApiError error = Utils.convertToApiError(ex);
            logger.warn("Gamification service returned an error: [{}]", error);
            throw new GamificationExchangeException(Utils.convertToApiError(ex), ex);
        }

        logger.info("Saved Team achievements: [{}]", Arrays.toString(result));
        return result;
    }


}