package juja.microservices.gamification.slackbot.dao.impl;

import juja.microservices.gamification.slackbot.dao.UserRepository;
import juja.microservices.gamification.slackbot.exceptions.ApiError;
import juja.microservices.gamification.slackbot.exceptions.UserExchangeException;
import juja.microservices.gamification.slackbot.model.DTO.SlackNameRequest;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import juja.microservices.gamification.slackbot.model.DTO.UuidRequest;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Artem
 */

@Repository
public class RestUserRepository extends AbstractRestRepository implements UserRepository {

    private final String REST_SERVICE_NAME = "user";

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${user.baseURL}")
    private String urlBase;
    @Value("${endpoint.usersBySlackNames}")
    private String urlGetUserBySlackNames;
    @Value("${endpoint.usersByUuids}")
    private String urlGetUserByUuids;

    @Inject
    public RestUserRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<UserDTO> findUsersBySlackNames(List<String> slackNames) {
        logger.debug("Received SlackNames : [{}]", slackNames);

        for (int i = 0; i < slackNames.size(); i++) {
            if (!slackNames.get(i).startsWith("@")) {
                logger.debug("add '@' to SlackName : [{}]", slackNames.get(i));
                String slackName = slackNames.get(i);
                slackNames.set(i, "@" + slackName);
            }
        }

        SlackNameRequest slackNameRequest = new SlackNameRequest(slackNames);
        HttpEntity<SlackNameRequest> request = new HttpEntity<>(slackNameRequest, setupBaseHttpHeaders());

        List<UserDTO> result;
        try {
            logger.debug("Started request to Users service. Request is : [{}]", request.toString());
            ResponseEntity<UserDTO[]> response = restTemplate.exchange(urlBase + urlGetUserBySlackNames,
                    HttpMethod.POST, request, UserDTO[].class);
            logger.debug("Finished request to Users service. Response is: [{}]", response.toString());
            result = Arrays.asList(response.getBody());
        } catch (HttpClientErrorException ex) {
            ApiError error = convertToApiError(ex, REST_SERVICE_NAME);
            logger.warn("Users service returned an error: [{}]", error);
            throw new UserExchangeException(error, ex);
        }

        logger.info("Got UserDTO:{} by users: {}", result, slackNames);
        return result;
    }

    @Override
    public Set<UserDTO> findUsersByUuids(Set<String> uuids) {
        logger.debug("Received uuids : [{}]", uuids);

        UuidRequest uuidRequest = new UuidRequest(uuids);
        HttpEntity<UuidRequest> request = new HttpEntity<>(uuidRequest, setupBaseHttpHeaders());

        Set<UserDTO> result;
        try {
            logger.debug("Started request to Users service. Request is : [{}]", request.toString());
            ResponseEntity<UserDTO[]> response = restTemplate.exchange(urlBase + urlGetUserByUuids,
                    HttpMethod.POST, request, UserDTO[].class);
            logger.debug("Finished request to Users service. Response is: [{}]", response.toString());
            result = new LinkedHashSet<>(Arrays.asList(response.getBody()));
        } catch (HttpClientErrorException ex) {
            ApiError error = convertToApiError(ex, REST_SERVICE_NAME);
            logger.warn("Users service returned an error: [{}]", error);
            throw new UserExchangeException(error, ex);
        }

        logger.info("Got UserDTO:{} by uuids: {}", result, uuids);
        return result;
    }
}