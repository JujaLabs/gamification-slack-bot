package juja.microservices.gamification.slackbot.dao.impl;

import juja.microservices.gamification.slackbot.dao.GamificationRepository;
import juja.microservices.gamification.slackbot.exceptions.ApiError;
import juja.microservices.gamification.slackbot.exceptions.GamificationExchangeException;
import juja.microservices.gamification.slackbot.model.achievements.*;
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
public class RestGamificationRepository extends AbstractRestRepository implements GamificationRepository {

    private final String REST_SERVICE_NAME = "gamification";

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${gamification.baseURL}")
    private String urlBase;
    @Value("${endpoint.daily}")
    private String urlSendDaily;
    @Value("${endpoint.codenjoy}")
    private String urlSendCodenjoy;
    @Value("${endpoint.thanks}")
    private String urlSendThanks;
    @Value("${endpoint.interview}")
    private String urlSendInterview;
    @Value("${endpoint.team}")
    private String urlSendTeam;

    @Inject
    public RestGamificationRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String[] saveDailyAchievement(DailyAchievement daily) {
        logger.debug("Received Daily achievement: [{}]", daily.toString());

        HttpEntity<DailyAchievement> request = new HttpEntity<>(daily, setupBaseHttpHeaders());
        String[] result;

        try {
            logger.debug("Started request to Gamification service. Request is : [{}]", request.toString());
            ResponseEntity<String[]> response = restTemplate.exchange(urlBase + urlSendDaily,
                    HttpMethod.POST, request, String[].class);
            result = response.getBody();
            logger.debug("Finished request to Gamification service. Response is: [{}]", response.toString());
        } catch (HttpClientErrorException ex) {
            ApiError error = convertToApiError(ex, REST_SERVICE_NAME);
            logger.warn("Gamification service returned an error: [{}]", error);
            throw new GamificationExchangeException(convertToApiError(ex, REST_SERVICE_NAME), ex);
        }

        logger.info("Saved Daily achievement: [{}]", Arrays.toString(result));
        return result;
    }

    @Override
    public String[] saveCodenjoyAchievement(CodenjoyAchievement codenjoy) {
        logger.debug("Received Codenjoy achievement: [{}]", codenjoy.toString());

        HttpEntity<CodenjoyAchievement> request = new HttpEntity<>(codenjoy, setupBaseHttpHeaders());
        String[] result;

        try {
            logger.debug("Started request to Gamification service. Request is : [{}]", request.toString());
            ResponseEntity<String[]> response = restTemplate.exchange(urlBase + urlSendCodenjoy,
                    HttpMethod.POST, request, String[].class);
            result = response.getBody();
            logger.debug("Finished request to Gamification service. Response is: [{}]", response.toString());
        } catch (HttpClientErrorException ex) {
            ApiError error = convertToApiError(ex, REST_SERVICE_NAME);
            logger.warn("Gamification service returned an error: [{}]", error);
            throw new GamificationExchangeException(error, ex);
        }
        logger.info("Saved Codenjoy achievements: [{}]", Arrays.toString(result));
        return result;
    }

    @Override
    public String[] saveThanksAchievement(ThanksAchievement thanks) {
        logger.debug("Received Thanks achievement: [{}]", thanks.toString());

        HttpEntity<ThanksAchievement> request = new HttpEntity<>(thanks, setupBaseHttpHeaders());
        String[] result;

        try {
            logger.debug("Started request to Gamification service. Request is : [{}]", request.toString());
            ResponseEntity<String[]> response = restTemplate.exchange(urlBase + urlSendThanks,
                    HttpMethod.POST, request, String[].class);
            result = response.getBody();
            logger.debug("Finished request to Gamification service. Response is: [{}]", response.toString());
        } catch (HttpClientErrorException ex) {
            ApiError error = convertToApiError(ex, REST_SERVICE_NAME);
            logger.warn("Gamification service returned an error: [{}]", error);
            throw new GamificationExchangeException(convertToApiError(ex, REST_SERVICE_NAME), ex);
        }

        logger.info("Saved Thanks achievements: [{}]", Arrays.toString(result));
        return result;
    }

    @Override
    public String[] saveInterviewAchievement(InterviewAchievement interview) {
        logger.debug("Received Interview achievement: [{}]", interview.toString());

        HttpEntity<InterviewAchievement> request = new HttpEntity<>(interview, setupBaseHttpHeaders());
        String[] result;

        try {
            logger.debug("Started request to Gamification service. Request is : [{}]", request.toString());
            ResponseEntity<String[]> response = restTemplate.exchange(urlBase + urlSendInterview,
                    HttpMethod.POST, request, String[].class);
            result = response.getBody();
            logger.debug("Finished request to Gamification service. Response is: [{}]", response.toString());
        } catch (HttpClientErrorException ex) {
            ApiError error = convertToApiError(ex, REST_SERVICE_NAME);
            logger.warn("Gamification service returned an error: [{}]", error);
            throw new GamificationExchangeException(convertToApiError(ex, REST_SERVICE_NAME), ex);
        }

        logger.info("Saved Interview achievements: [{}]", Arrays.toString(result));
        return result;
    }

    @Override
    public String[] saveTeamAchievement(TeamAchievement team) {
        logger.debug("Received Team achievement: [{}]", team.toString());

        HttpEntity<TeamAchievement> request = new HttpEntity<>(team, setupBaseHttpHeaders());
        String[] result;

        try {
            logger.debug("Started request to Gamification service. Request is : [{}]", request.toString());
            ResponseEntity<String[]> response = restTemplate.exchange(urlBase + urlSendTeam,
                    HttpMethod.POST, request, String[].class);
            result = response.getBody();
            logger.debug("Finished request to Gamification service. Response is: [{}]", response.toString());
        } catch (HttpClientErrorException ex) {
            ApiError error = convertToApiError(ex, REST_SERVICE_NAME);
            logger.warn("Gamification service returned an error: [{}]", error);
            throw new GamificationExchangeException(convertToApiError(ex, REST_SERVICE_NAME), ex);
        }

        logger.info("Saved Team achievements: [{}]", Arrays.toString(result));
        return result;
    }


}