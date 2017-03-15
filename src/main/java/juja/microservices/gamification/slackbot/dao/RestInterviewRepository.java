package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.exceptions.GamificationExchangeException;
import juja.microservices.gamification.slackbot.model.InterviewAchievement;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Artem
 */
public class RestInterviewRepository implements InterviewRepository {

    private final RestTemplate restTemplate;

    private final String URL_SEND_INTERVIEW = "/achieve/interview";

    public RestInterviewRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders setupBaseHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }


    @Override
    public String saveInterviewAchievement(InterviewAchievement interviewAchievement) {
        HttpEntity<InterviewAchievement> request = new HttpEntity<>(interviewAchievement, setupBaseHttpHeaders());
        String result = "";
        try {
            ResponseEntity<String> response = restTemplate.exchange(URL_SEND_INTERVIEW, HttpMethod.POST, request, String.class);
            result = response.getBody();
        } catch (HttpClientErrorException ex) {
            throw new GamificationExchangeException("InterviewAchievement Exchange Error: ", ex);
        }
        return result;
    }
}
