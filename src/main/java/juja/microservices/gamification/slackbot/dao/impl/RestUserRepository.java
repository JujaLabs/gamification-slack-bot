package juja.microservices.gamification.slackbot.dao.impl;

import juja.microservices.gamification.slackbot.dao.UserRepository;
import juja.microservices.gamification.slackbot.exceptions.ApiError;
import juja.microservices.gamification.slackbot.exceptions.UserExchangeException;
import juja.microservices.gamification.slackbot.model.DTO.SlackNameRequest;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Artem
 */

@Repository
public class RestUserRepository extends AbstractRestRepository implements UserRepository {

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
        logger.debug("Received SlackName : [{}]", slackName);

        if (!slackName.startsWith("@")) {
            slackName = "@" + slackName;
        }

        List<String> slackNames = new ArrayList<>();
        slackNames.add(slackName);

        SlackNameRequest slackNameRequest = new SlackNameRequest(slackNames);
        HttpEntity<SlackNameRequest> request = new HttpEntity<>(slackNameRequest, setupBaseHttpHeaders());

        String result;
        try {
            logger.debug("Started request to Users service. Request is : [{}]", request.toString());
            ResponseEntity<UserDTO[]> response = restTemplate.exchange(urlBase + urlGetUser,
                    HttpMethod.POST, request, UserDTO[].class);
            result = response.getBody()[0].getUuid();
            logger.debug("Finished request to Users service. Response is: [{}]", response.toString());
        } catch (HttpClientErrorException ex) {
            ApiError error = convertToApiError(ex);
            logger.warn("Users service returned an error: [{}]", error);
            throw new UserExchangeException(error, ex);
        }
        logger.info("Got UUID:{} by user: {}", result, slackName);
        return result;
    }

}