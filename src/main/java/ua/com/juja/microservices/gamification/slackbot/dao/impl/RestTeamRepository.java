package ua.com.juja.microservices.gamification.slackbot.dao.impl;

import feign.FeignException;
import ua.com.juja.microservices.gamification.slackbot.dao.TeamRepository;
import ua.com.juja.microservices.gamification.slackbot.dao.feign.TeamsClient;
import ua.com.juja.microservices.gamification.slackbot.exceptions.ApiError;
import ua.com.juja.microservices.gamification.slackbot.exceptions.TeamExchangeException;
import ua.com.juja.microservices.gamification.slackbot.model.DTO.TeamDTO;
import ua.com.juja.microservices.gamification.slackbot.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

/**
 * @author Petr Kramar
 * @author Ivan Shapovalov
 */
@Repository
public class RestTeamRepository implements TeamRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private TeamsClient teamsClient;

    @Override
    public TeamDTO getTeamByUserUuid(String uuid) {
        TeamDTO result;
        logger.debug("Started request to Teams service. Get team by uuid {}.", uuid);
        try {
            result = teamsClient.getTeamByUserUuid(uuid);
        } catch (FeignException ex) {
            ApiError error = Utils.convertToApiError(ex.getMessage());
            logger.warn("Teams service returned an error: [{}]", error);
            throw new TeamExchangeException(error, ex);
        }
        logger.debug("Received active team : {}", result);
        return result;
    }
}