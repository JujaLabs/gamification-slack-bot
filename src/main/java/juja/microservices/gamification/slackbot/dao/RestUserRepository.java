package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.exceptions.GamificationExchangeException;
import juja.microservices.gamification.slackbot.model.User;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

/**
 * Created by Artem
 */


public class RestUserRepository implements UserRepository {

    private final RestTemplate restTemplate;

    private final String URL_GET_USER = "/users/search";


    @Inject
    public RestUserRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    private HttpHeaders setupBaseHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
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


}
