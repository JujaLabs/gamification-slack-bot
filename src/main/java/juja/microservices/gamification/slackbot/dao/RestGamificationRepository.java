package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.exceptions.GamificationExchangeException;
import juja.microservices.gamification.slackbot.model.CodenjoyAchievment;
import juja.microservices.gamification.slackbot.model.DailyAchievement;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

/**
 * @author Danil Kuznetsov
 */


public class RestGamificationRepository implements GamificationRepository {

    private final RestTemplate restTemplate;

    private final String URL_SEND_DAILY = "/achieve/daily";

    private final String URL_SEND_CODENJOY = "/achieve/codenjoy";

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
            ResponseEntity<String> response = restTemplate.exchange(URL_SEND_DAILY, HttpMethod.POST, request, String.class);
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
            ResponseEntity<String> response = restTemplate.exchange(URL_SEND_CODENJOY, HttpMethod.POST, request, String.class);
            result = response.getBody();
        } catch (HttpClientErrorException ex) {
            throw new GamificationExchangeException("Gamification Exchange Error: ", ex);
        }
        return result;
    }
}
