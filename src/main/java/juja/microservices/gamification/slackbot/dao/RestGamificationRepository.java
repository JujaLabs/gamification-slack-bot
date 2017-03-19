package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.exceptions.GamificationExchangeException;
import juja.microservices.gamification.slackbot.model.CodenjoyAchievment;
import juja.microservices.gamification.slackbot.model.DailyAchievement;
import juja.microservices.gamification.slackbot.model.InterviewAchievement;
import juja.microservices.gamification.slackbot.model.ThanksAchievement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

/**
 * @author Danil Kuznetsov
 */

@PropertySource("application.properties")
public class RestGamificationRepository implements GamificationRepository {

    private final RestTemplate restTemplate;

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
    public String saveDailyAchievement(DailyAchievement daily) {

        HttpEntity<DailyAchievement> request = new HttpEntity<>(daily, setupBaseHttpHeaders());
        String result = "";
        try {
            ResponseEntity<String> response = restTemplate.exchange(urlBase + urlSendDaily, HttpMethod.POST, request, String.class);
            result = response.getBody();
        } catch (HttpClientErrorException ex) {
            throw new GamificationExchangeException("Gamification Exchange Error: ", ex);
        }
        return result;
    }

    @Override
    public String saveCodenjoyAchievement(CodenjoyAchievment codenjoy) {

        HttpEntity<CodenjoyAchievment> request = new HttpEntity<>(codenjoy, setupBaseHttpHeaders());
        String result = "";
        try {
            ResponseEntity<String> response = restTemplate.exchange(urlBase + urlSendCodenjoy, HttpMethod.POST, request, String.class);
            result = response.getBody();
        } catch (HttpClientErrorException ex) {
            throw new GamificationExchangeException("Gamification Exchange Error: ", ex);
        }
        return result;
    }

    @Override
    public String saveThanksAchievement(ThanksAchievement thanks) {

        HttpEntity<ThanksAchievement> request = new HttpEntity<>(thanks, setupBaseHttpHeaders());
        String result = "";
        try {
            ResponseEntity<String> response = restTemplate.exchange(urlBase + urlSendThanks, HttpMethod.POST, request, String.class);
            result = response.getBody();
        } catch (HttpClientErrorException ex) {
            throw new GamificationExchangeException("Gamification Exchange Error: ", ex);
        }
        return result;
    }

    @Override
    public String saveInterviewAchievement(InterviewAchievement interview) {
        HttpEntity<InterviewAchievement> request = new HttpEntity<>(interview, setupBaseHttpHeaders());
        String result = "";
        try {
            ResponseEntity<String> response = restTemplate.exchange(urlBase + urlSendInterview, HttpMethod.POST, request, String.class);
            result = response.getBody();
        } catch (HttpClientErrorException ex) {
            throw new GamificationExchangeException("Gamification Exchange Error: ", ex);
        }
        return result;
    }
}
