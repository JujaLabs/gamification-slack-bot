package juja.microservices.gamification.slackbot.dao.impl;

import juja.microservices.gamification.slackbot.dao.TeamRepository;
import juja.microservices.gamification.slackbot.exceptions.ApiError;
import juja.microservices.gamification.slackbot.exceptions.TeamExchangeException;
import juja.microservices.gamification.slackbot.model.DTO.TeamDTO;
import juja.microservices.gamification.slackbot.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

@Repository
public class RestTeamRepository implements TeamRepository {

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${teams.endpoint.teamByUserUuid}")
    private String getTeamByUserUuidUrl;

    @Inject
    public RestTeamRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public TeamDTO getTeamByUserUuid(String uuid) {
        String urlTemplate = getTeamByUserUuidUrl + "/" + uuid;
        TeamDTO result;
        logger.debug("Started request to Teams service. Get team by uuid {}.", uuid);
        try {
            ResponseEntity<TeamDTO> response = this.restTemplate.getForEntity(urlTemplate, TeamDTO.class);
            result = response.getBody();
        } catch (HttpClientErrorException ex) {
            ApiError error = Utils.convertToApiError(ex);
            logger.warn("Teams service returned an error: [{}]", error);
            throw new TeamExchangeException(error, ex);
        }
        logger.debug("Received active team : {}", result);
        return result;
    }
}