package ua.com.juja.microservices.gamification.slackbot.dao;

import ua.com.juja.microservices.gamification.slackbot.model.DTO.TeamDTO;

public interface TeamRepository {

    TeamDTO getTeamByUserUuid(String uuid);

}