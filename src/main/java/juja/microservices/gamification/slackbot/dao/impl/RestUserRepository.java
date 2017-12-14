package juja.microservices.gamification.slackbot.dao.impl;

import juja.microservices.gamification.slackbot.dao.UserRepository;
import juja.microservices.gamification.slackbot.exceptions.ApiError;
import juja.microservices.gamification.slackbot.exceptions.UserExchangeException;
import juja.microservices.gamification.slackbot.model.DTO.SlackUsersRequest;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import juja.microservices.gamification.slackbot.model.DTO.UuidRequest;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Artem
 */

@Repository
public class RestUserRepository implements UserRepository {

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${users.endpoint.findUsersBySlackIds}")
    private String urlFindUsersBySlackUsers;

    @Value("${users.endpoint.findUsersByUuids}")
    private String urlFindUsersByUuids;

    @Inject
    public RestUserRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<UserDTO> findUsersBySlackUsers(List<String> slackUsers) {
        logger.debug("Received SlackUsers : [{}]", slackUsers);

        SlackUsersRequest slackUsersRequest = new SlackUsersRequest(slackUsers);
        HttpEntity<SlackUsersRequest> request = new HttpEntity<>(slackUsersRequest, Utils.setupJsonHttpHeaders());

        List<UserDTO> result;
        try {
            logger.debug("Started request to Users service. Request is : [{}]", request.toString());
            ResponseEntity<UserDTO[]> response = restTemplate.exchange(urlFindUsersBySlackUsers,
                    HttpMethod.POST, request, UserDTO[].class);
            logger.debug("Finished request to Users service. Response is: [{}]", response.toString());
            result = Arrays.asList(response.getBody());
        } catch (HttpClientErrorException ex) {
            ApiError error = Utils.convertToApiError(ex);
            logger.warn("Users service returned an error: [{}]", error);
            throw new UserExchangeException(error, ex);
        }

        logger.info("Got UserDTO:{} by users: {}", result, slackUsers);
        return result;
    }

    @Override
    public Set<UserDTO> findUsersByUuids(Set<String> uuids) {
        logger.debug("Received uuids : [{}]", uuids);

        UuidRequest uuidRequest = new UuidRequest(uuids);
        HttpEntity<UuidRequest> request = new HttpEntity<>(uuidRequest, Utils.setupJsonHttpHeaders());

        Set<UserDTO> result;
        try {
            logger.debug("Started request to Users service. Request is : [{}]", request.toString());
            ResponseEntity<UserDTO[]> response = restTemplate.postForEntity(urlFindUsersByUuids,
                    request, UserDTO[].class);
            logger.debug("Finished request to Users service. Response is: [{}]", response.toString());
            result = new LinkedHashSet<>(Arrays.asList(response.getBody()));
        } catch (HttpClientErrorException ex) {
            ApiError error = Utils.convertToApiError(ex);
            logger.warn("Users service returned an error: [{}]", error);
            throw new UserExchangeException(error, ex);
        }

        logger.info("Got UserDTO:{} by uuids: {}", result, uuids);
        return result;
    }
}