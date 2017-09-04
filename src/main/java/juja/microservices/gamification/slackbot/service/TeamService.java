package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.model.DTO.TeamDTO;

public interface TeamService {
    TeamDTO getTeamByUserUuid(String uuid);
}