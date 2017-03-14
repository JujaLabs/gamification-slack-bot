package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.exceptions.GamificationExchangeException;
import juja.microservices.gamification.slackbot.model.DailyAchievement;
import juja.microservices.gamification.slackbot.model.Interview;
import juja.microservices.gamification.slackbot.model.User;
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
    private final String URL_GET_USER = "/users/search";
    private final String URL_SEND_INTERVIEW = "/achieve/interview";

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
    public User findUserBySlackGmail() {
        HttpEntity request = new HttpEntity<>(setupBaseHttpHeaders());
        User result;
        try {
            ResponseEntity<User> response = restTemplate.exchange(URL_GET_USER, HttpMethod.GET, request, User.class);
            result = response.getBody();
        } catch (HttpClientErrorException ex) {
            throw new GamificationExchangeException("User Exchange Error: ", ex);
        }
        return result;
    }

    @Override
    public String saveInterviewAchievement(Interview interview) {
        HttpEntity<Interview> request = new HttpEntity<>(interview, setupBaseHttpHeaders());
        String result = "";
        try {
            ResponseEntity<String> response = restTemplate.exchange(URL_SEND_INTERVIEW, HttpMethod.POST, request, String.class);
            result = response.getBody();
        } catch (HttpClientErrorException ex) {
            throw new GamificationExchangeException("Interview Exchange Error: ", ex);
        }
        return result;
    }


}
