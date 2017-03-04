package juja.microservices.gamification.slackbot.service;
import juja.microservices.gamification.slackbot.entities.CodenjoyRequest;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Nikol on 3/4/2017.
 */

public class CodenjoyService {
    RestTemplate restTemplate;

    private static final String CODENJOY_URL = "/achieve/codenjoy";

    public CodenjoyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String sendCodenjoy(String from, String firstPlace, String secondPlace, String thirdPlace) {
        return restTemplate.postForObject(CODENJOY_URL,
                new CodenjoyRequest(from, firstPlace, secondPlace, thirdPlace),
                String.class);
    }
}
