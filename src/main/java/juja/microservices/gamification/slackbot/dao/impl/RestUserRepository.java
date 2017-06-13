package juja.microservices.gamification.slackbot.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import juja.microservices.gamification.slackbot.dao.UserRepository;
import juja.microservices.gamification.slackbot.exceptions.ApiError;
import juja.microservices.gamification.slackbot.exceptions.UserExchangeException;
import juja.microservices.gamification.slackbot.model.DTO.SlackNameRequest;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Artem
 */

@Repository
public class RestUserRepository implements UserRepository {

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
        if (!slackName.startsWith("@")) {
            slackName = "@" + slackName;
        }
        List<String> slackNames = new ArrayList<>();
        slackNames.add(slackName);

        SlackNameRequest slackNameRequest = new SlackNameRequest(slackNames);
        HttpEntity<SlackNameRequest> request = new HttpEntity<>(slackNameRequest, setupBaseHttpHeaders());
        logger.debug("find uuid by slack name request: {}", request.toString());

        String result;
        try {
            ResponseEntity<UserDTO[]> response = restTemplate.exchange(urlBase + urlGetUser,
                    HttpMethod.POST, request, UserDTO[].class);
            logger.debug("User repository response: {}", response.toString());
            result = response.getBody()[0].getUuid();
        } catch (HttpClientErrorException ex) {
            logger.warn("Exception in findUuidUserBySlack: {}", ex.getMessage());
            throw new UserExchangeException(convertToApiError(ex), ex);
        }
        logger.info("Founded UUID:{} by user: {}", result, slackName);
        return result;
    }

    private HttpHeaders setupBaseHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }

    private ApiError convertToApiError(HttpClientErrorException ex) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), ApiError.class);
        } catch (IOException e) {
            return new ApiError(
                    500, "BotError",
                    "Cannot parse api error message",
                    "Cannot parse api error message",
                    e.getMessage(),
                    Collections.EMPTY_LIST
            );
        }
    }
}