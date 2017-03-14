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


    private HttpHeaders setupBaseHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Override
    public User findUserBySlack(String slackNickname) {
      /* HttpEntity request = new HttpEntity<>(setupBaseHttpHeaders());*/
        HttpHeaders headers = new HttpHeaders();
        headers.add("slackNickname", URL_GET_USER);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        Map<String, String> params = new HashMap<>();
        params.put("slackNickname", slackNickname);
        User result;
        try {
            ResponseEntity<User> response = restTemplate.exchange(URL_GET_USER+"/slackNickname={slackNickname}", HttpMethod.GET, requestEntity, User.class, params);
            result = response.getBody();
        } catch (HttpClientErrorException ex) {
            throw new GamificationExchangeException("User Exchange Error: ", ex);
        }
        return result;
    }

 /*   public User findUserBySlack(String slackNickname) {
        final HashMap<String, String> urlVariables = new HashMap<>(1);
        urlVariables.put("slackNickname", slackNickname);
        final String urlTemplate = URL_GET_USER + "/slackNickname={slackNickname}";
        final ResponseEntity<User> responseEntity = this.restTemplate.getForEntity(urlTemplate, User.class, urlVariables);

        System.out.println("Response Status : " + responseEntity.getStatusCode());

        final HttpHeaders headers = responseEntity.getHeaders();
        System.out.println("headers in response are : " + headers);
        return responseEntity.getBody();
    }*/

}
