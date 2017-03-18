package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.exceptions.GamificationExchangeException;
import juja.microservices.gamification.slackbot.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.HashMap;

/**
 * Created by Artem
 */

@PropertySource("application.properties")
public class RestUserRepository implements UserRepository {

    private final RestTemplate restTemplate;

    @Value("${user.baseURL}")
    private String BASE_URL;
    @Value("${endpoint.userSearch}")
    private String URL_GET_USER;


    @Inject
    public RestUserRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public User findUserBySlack(String slackNickname) {
        HashMap<String, String> urlVariables = new HashMap<>(1);
        urlVariables.put("slackNickname", slackNickname);
        String urlTemplate = BASE_URL+ URL_GET_USER + "/slackNickname={slackNickname}";
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
