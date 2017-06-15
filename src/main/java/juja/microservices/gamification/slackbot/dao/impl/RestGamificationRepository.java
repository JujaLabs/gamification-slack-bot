package juja.microservices.gamification.slackbot.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import juja.microservices.gamification.slackbot.dao.GamificationRepository;
import juja.microservices.gamification.slackbot.exceptions.ApiError;
import juja.microservices.gamification.slackbot.exceptions.GamificationExchangeException;
import juja.microservices.gamification.slackbot.model.CodenjoyAchievement;
import juja.microservices.gamification.slackbot.model.DailyAchievement;
import juja.microservices.gamification.slackbot.model.InterviewAchievement;
import juja.microservices.gamification.slackbot.model.ThanksAchievement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Danil Kuznetsov
 */

@Repository
public class RestGamificationRepository implements GamificationRepository {

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

    @Inject
    public RestGamificationRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders setupBaseHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Override
    public String[] saveDailyAchievement(DailyAchievement daily) {
        HttpEntity<DailyAchievement> request = new HttpEntity<>(daily, setupBaseHttpHeaders());
        logger.debug("Daily achievement request: {}", request.toString());
        String[] result;
        try {
            ResponseEntity<String[]> response = restTemplate.exchange(urlBase + urlSendDaily,
                    HttpMethod.POST, request, String[].class);
            logger.debug("Received response from gamification: {}", response.toString());
            result = response.getBody();
        } catch (HttpClientErrorException ex) {
            logger.warn("Exception in <saveDailyAchievement()>: {}", ex.getMessage());
            throw new GamificationExchangeException(convertToApiError(ex), ex);
        }
        logger.info("Saved daily achievement, id's={}", Arrays.toString(result));
        return result;
    }

    @Override
    public String[] saveCodenjoyAchievement(CodenjoyAchievement codenjoy) {
        HttpEntity<CodenjoyAchievement> request = new HttpEntity<>(codenjoy, setupBaseHttpHeaders());
        logger.debug("Codenjoy achievement request: {}", request.toString());
        String[] result;
        try {
            ResponseEntity<String[]> response = restTemplate.exchange(urlBase + urlSendCodenjoy,
                    HttpMethod.POST, request, String[].class);
            logger.debug("Received response from gamification: {}", response.toString());
            result = response.getBody();

        } catch (HttpClientErrorException ex) {
            logger.warn("Exception in <saveCodenjoyAchievement()>: {}", ex.getMessage());
            throw new GamificationExchangeException(convertToApiError(ex), ex);
        }
        logger.info("Saved codenjoy achievements, id's={}", Arrays.toString(result));
        return result;
    }

    @Override
    public String[] saveThanksAchievement(ThanksAchievement thanks) {
        HttpEntity<ThanksAchievement> request = new HttpEntity<>(thanks, setupBaseHttpHeaders());
        logger.debug("Thanks achivement request: {}", request.toString());
        String[] result;
        try {
            ResponseEntity<String[]> response = restTemplate.exchange(urlBase + urlSendThanks,
                    HttpMethod.POST, request, String[].class);
            logger.debug("Received response from gamification: {}", response.toString());
            result = response.getBody();
        } catch (HttpClientErrorException ex) {
            logger.warn("Exception in <saveThanksAchievement()>: {}", ex.getMessage());
            throw new GamificationExchangeException(convertToApiError(ex), ex);
        }
        logger.info("Saved thanks achievements, id's={}", Arrays.toString(result));
        return result;
    }

    @Override
    public String[] saveInterviewAchievement(InterviewAchievement interview) {
        HttpEntity<InterviewAchievement> request = new HttpEntity<>(interview, setupBaseHttpHeaders());
        logger.debug("Interview achivement request: {}", request.toString());
        String[] result;
        try {
            ResponseEntity<String[]> response = restTemplate.exchange(urlBase + urlSendInterview,
                    HttpMethod.POST, request, String[].class);
            logger.debug("Received response from gamification: {}", response.toString());
            result = response.getBody();
        } catch (HttpClientErrorException ex) {
            logger.warn("Exception in <saveInterviewAchievement()>: {}", ex.getMessage());
            throw new GamificationExchangeException(convertToApiError(ex), ex);
        }
        logger.info("Saved interview achievements, id's={}", Arrays.toString(result));
        return result;
    }

    private ApiError convertToApiError(HttpClientErrorException ex) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), ApiError.class);
        } catch (IOException e) {
            return new ApiError(
                    500, "BotError",
                    "Cannot parse api error message",
                    "Cannot parse api error message",
                    e.getMessage(),
                    Collections.EMPTY_LIST
            );
        }
    }
}