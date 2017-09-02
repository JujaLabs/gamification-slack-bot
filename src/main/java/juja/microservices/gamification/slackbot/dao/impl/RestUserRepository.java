package juja.microservices.gamification.slackbot.dao.impl;

import juja.microservices.gamification.slackbot.dao.UserRepository;
import juja.microservices.gamification.slackbot.exceptions.ApiError;
import juja.microservices.gamification.slackbot.exceptions.UserExchangeException;
import juja.microservices.gamification.slackbot.model.DTO.SlackNameRequest;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import juja.microservices.gamification.slackbot.util.Utils;
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
import java.util.Arrays;
import java.util.List;

/**
 * @author Artem
 */

@Repository
public class RestUserRepository implements UserRepository {

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${users.rest.api.version}")
    private String usersRestApiVersion;
    @Value("${users.baseURL}")
    private String usersBaseUrl;
    @Value("${users.endpoint.usersBySlackNames}")
    private String usersFindUsersBySlackNamesUrl;


    @Inject
    public RestUserRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<UserDTO> findUsersBySlackNames(List<String> slackNames) {
        logger.debug("Received SlackNames : [{}]", slackNames);
        String userServiceURL = usersBaseUrl + usersRestApiVersion + usersFindUsersBySlackNamesUrl;

        for (int i = 0; i < slackNames.size(); i++) {
            if (!slackNames.get(i).startsWith("@")) {
                logger.debug("add '@' to SlackName : [{}]", slackNames.get(i));
                String slackName = slackNames.get(i);
                slackNames.set(i, "@" + slackName);
            }
        }

        SlackNameRequest slackNameRequest = new SlackNameRequest(slackNames);
        HttpEntity<SlackNameRequest> request = new HttpEntity<>(slackNameRequest, Utils.setupJsonHttpHeaders());

        List<UserDTO> result;
        try {
            logger.debug("Started request to Users service. Request is : [{}]", request.toString());
            ResponseEntity<UserDTO[]> response = restTemplate.exchange(userServiceURL,
                    HttpMethod.POST, request, UserDTO[].class);
            logger.debug("Finished request to Users service. Response is: [{}]", response.toString());
            result = Arrays.asList(response.getBody());
        } catch (HttpClientErrorException ex) {
            ApiError error = Utils.convertToApiError(ex);
            logger.warn("Users service returned an error: [{}]", error);
            throw new UserExchangeException(error, ex);
        }

        logger.info("Got UserDTO:{} by users: {}", result, slackNames);
        return result;
    }
}