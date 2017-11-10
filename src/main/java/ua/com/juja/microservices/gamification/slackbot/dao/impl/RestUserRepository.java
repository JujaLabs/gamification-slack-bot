package ua.com.juja.microservices.gamification.slackbot.dao.impl;

import feign.FeignException;
import org.springframework.context.annotation.Profile;
import ua.com.juja.microservices.gamification.slackbot.dao.UserRepository;
import ua.com.juja.microservices.gamification.slackbot.exceptions.UserExchangeException;
import ua.com.juja.microservices.gamification.slackbot.model.DTO.SlackNameRequest;
import ua.com.juja.microservices.gamification.slackbot.model.DTO.UuidRequest;
import ua.com.juja.microservices.gamification.slackbot.dao.feign.UsersClient;
import ua.com.juja.microservices.gamification.slackbot.exceptions.ApiError;
import ua.com.juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import ua.com.juja.microservices.gamification.slackbot.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

/**
 * @author Artem
 * @author Ivan Shapovalov
 */
@Repository
@Profile({"production", "default"})
public class RestUserRepository implements UserRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private UsersClient usersClient;

    @Override
    public List<UserDTO> findUsersBySlackNames(List<String> slackNames) {
        logger.debug("Received SlackNames : [{}]", slackNames);
        SlackNameRequest slackNameRequest = new SlackNameRequest(slackNames);
        List<UserDTO> users;
        try {
            users = usersClient.findUsersBySlackNames(slackNameRequest);
            logger.debug("Finished request to Users service. Users [{}]", users.toString());
        } catch (FeignException ex) {
            ApiError error = Utils.convertToApiError(ex.getMessage());
            logger.warn("Users service returned an error: [{}]", error);
            throw new UserExchangeException(error, ex);
        }
        logger.info("Got UserDTO:{} by users: {}", users, slackNames);
        return users;
    }

    @Override
    public Set<UserDTO> findUsersByUuids(Set<String> uuids) {
        logger.debug("Received uuids : [{}]", uuids);
        UuidRequest uuidRequest = new UuidRequest(uuids);
        Set<UserDTO> users;
        try {
            users = usersClient.findUsersByUuids(uuidRequest);
            logger.debug("Finished request to Users service. Users [{}]", users.toString());
        } catch (FeignException ex) {
            ApiError error = Utils.convertToApiError(ex.getMessage());
            logger.warn("Users service returned an error: [{}]", error);
            throw new UserExchangeException(error, ex);
        }
        logger.info("Got UserDTO:{} by uuids: {}", users, uuids);
        return users;
    }
}