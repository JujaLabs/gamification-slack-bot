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
    private String BASE_URL;
    @Value("${endpoint.daily}")
    private String URL_SEND_DAILY;
    @Value("${endpoint.codenjoy}")
    private String URL_SEND_CODENJOY;
    @Value("${endpoint.thanks}")
    private String URL_SEND_THANKS;
    @Value("${endpoint.interview}")
    private String URL_SEND_INTERVIEW;

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
            ResponseEntity<String> response = restTemplate.exchange(BASE_URL+URL_SEND_DAILY, HttpMethod.POST, request, String.class);
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
            ResponseEntity<String> response = restTemplate.exchange(BASE_URL+URL_SEND_CODENJOY, HttpMethod.POST, request, String.class);
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
            ResponseEntity<String> response = restTemplate.exchange(BASE_URL+URL_SEND_THANKS, HttpMethod.POST, request, String.class);
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
            ResponseEntity<String> response = restTemplate.exchange(BASE_URL+URL_SEND_INTERVIEW, HttpMethod.POST, request, String.class);
            result = response.getBody();
        } catch (HttpClientErrorException ex) {
            throw new GamificationExchangeException("Gamification Exchange Error: ", ex);
        }
        return result;
    }
}
