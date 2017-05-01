package juja.microservices.gamification.slackbot.dao.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import juja.microservices.gamification.slackbot.dao.UserRepository;
import juja.microservices.gamification.slackbot.exceptions.GamificationExchangeException;
import juja.microservices.gamification.slackbot.exceptions.UserNotFoundException;
import juja.microservices.gamification.slackbot.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Artem
 */

@Repository
public class RestUserRepository implements UserRepository {

    private final RestTemplate restTemplate;

    @Value("${user.baseURL}")
    private String urlBase;
    @Value("${endpoint.userSearch}")
    private String urlGetUser;


    @Inject
    public RestUserRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public User findUserBySlack(String slackNickname) {
        HashMap<String, String> urlVariables = new HashMap<>(1);
        urlVariables.put("slackNickname", slackNickname);
        String urlTemplate = urlBase + urlGetUser + "/slackNickname={slackNickname}";
        User result;
        try {
            ResponseEntity<User> response = this.restTemplate.getForEntity(urlTemplate, User.class, urlVariables);
            result = response.getBody();
        } catch (HttpClientErrorException ex) {
            if (ex.getRawStatusCode() == 400 && checkInternalErrorCode(ex.getResponseBodyAsString(), 0)) {
                throw new UserNotFoundException(String.format("User with slack name '%s' not found.", slackNickname));
            }
            throw new GamificationExchangeException("User Exchange Error: ", ex);
        }
        return result;
    }

    private boolean checkInternalErrorCode(String jsonInString, int expectedValue) {
        if (!jsonInString.contains("internalErrorCode")) {
            return false;
        }
        Map<String, Object> map = jsonStringToHashMap(jsonInString);
        Integer actualValue = (Integer) map.get("internalErrorCode");
        if (actualValue.equals(expectedValue)) {
            return true;
        } else return false;
    }

    private Map<String, Object> jsonStringToHashMap(String jsonInString) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> result;
        try {
            result = mapper.readValue(jsonInString, new TypeReference<HashMap<String, Object>>() {
            });
        } catch (JsonParseException | JsonMappingException exception){
            throw new GamificationExchangeException(String.format("The string '%s' can't parse or " +
                    "mapping to Map<String, Object>", jsonInString), exception);
        } catch (IOException exception) {
            throw new GamificationExchangeException("A low-level I/O problem", exception);
        }
        return result;
    }

    @Override
    public String findUuidUserBySlack(String slackNickname) {
        return findUserBySlack(slackNickname).getUuid();
    }
}
