package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.model.DailyAchievement;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

/**
 * @author Danil Kuznetsov
 */


public class RestGamificationRepository implements GamificationRepository {

    private final RestTemplate restTemplate;

    private final String URL_SEND_DAILY = "/achieve/daily";

    @Inject
    public RestGamificationRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String saveDailyAchievement(DailyAchievement daily) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DailyAchievement> request = new HttpEntity<>(daily, headers);
        ResponseEntity<String> response = restTemplate.exchange(URL_SEND_DAILY, HttpMethod.POST, request, String.class);
        return response.getBody();
    }
}
