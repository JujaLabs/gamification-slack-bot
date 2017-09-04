package juja.microservices.gamification.slackbot.dao.impl;

import juja.microservices.gamification.slackbot.dao.TeamRepository;
import juja.microservices.gamification.slackbot.exceptions.ApiError;
import juja.microservices.gamification.slackbot.exceptions.TeamExchangeException;
import juja.microservices.gamification.slackbot.model.DTO.TeamDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

@Repository
public class RestTeamRepository  extends AbstractRestRepository implements TeamRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String REST_SERVICE_NAME = "team";

    @Inject
    private RestTemplate restTemplate;

    @Value("${teams.baseURL}")
    private String urlBase;
    @Value("${endpoint.teamByUserUuid}")
    private String urlGetTeamByUserUuid;

    @Override
    public TeamDTO getTeamByUserUuid(String uuid) {
        String urlTemplate = urlBase + urlGetTeamByUserUuid + uuid;
        TeamDTO result;
        logger.debug("Send request to team repository");
        try {
            ResponseEntity<TeamDTO> response = this.restTemplate.getForEntity(urlTemplate, TeamDTO.class);
            result = response.getBody();
        } catch (HttpClientErrorException ex) {
            ApiError error = convertToApiError(ex, REST_SERVICE_NAME);
            logger.warn("Teams service returned an error: [{}]", error);
            throw new TeamExchangeException(error, ex);
        }
        logger.debug("Received active team : {}", result);
        return result;
    }
}