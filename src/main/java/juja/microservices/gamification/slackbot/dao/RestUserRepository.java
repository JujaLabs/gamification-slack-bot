package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.exceptions.GamificationExchangeException;
import juja.microservices.gamification.slackbot.model.User;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

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


    public User findUserBySlack(String slackNickname) {
        HashMap<String, String> urlVariables = new HashMap<>(1);
        urlVariables.put("slackNickname", slackNickname);
        String urlTemplate = URL_GET_USER + "/slackNickname={slackNickname}";
        User result;
        try {
            ResponseEntity<User> response = this.restTemplate.getForEntity(urlTemplate, User.class, urlVariables);
            result = response.getBody();
        } catch (HttpClientErrorException ex) {
            throw new GamificationExchangeException("User Exchange Error: ", ex);
        }
        return result;
    }

}
