package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.model.DTO.TeamDTO;

public interface TeamRepository {

    TeamDTO getTeamByUserUuid(String uuid);

}