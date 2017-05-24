package juja.microservices.gamification.slackbot.dao.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import juja.microservices.gamification.slackbot.dao.UserRepository;
import juja.microservices.gamification.slackbot.exceptions.GamificationExchangeException;
import juja.microservices.gamification.slackbot.exceptions.UserNotFoundException;
import juja.microservices.gamification.slackbot.model.DTO.SlackNameRequest;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

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
    public String findUuidUserBySlack(String slackName) {
        List slackNames = new ArrayList();
        slackNames.add(slackName);
        SlackNameRequest slackNameRequest = new SlackNameRequest(slackNames);
        HttpEntity<SlackNameRequest> request = new HttpEntity<>(slackNameRequest, setupBaseHttpHeaders());
        String result;
        try {
            ResponseEntity<UserDTO[]> response = restTemplate.exchange(urlBase + urlGetUser,
                    HttpMethod.POST, request, UserDTO[].class);
            result = response.getBody()[0].getUuid();
        } catch (HttpClientErrorException ex) {
            if (ex.getRawStatusCode() == 400 && checkInternalErrorCode(ex.getResponseBodyAsString(), 0)) {
                throw new UserNotFoundException(String.format("User with slack name '%s' not found.", slackName));
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
        return actualValue.equals(expectedValue);
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

    private HttpHeaders setupBaseHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }
}
