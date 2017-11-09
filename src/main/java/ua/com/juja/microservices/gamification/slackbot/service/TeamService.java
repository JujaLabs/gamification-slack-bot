package ua.com.juja.microservices.gamification.slackbot.service;

import ua.com.juja.microservices.gamification.slackbot.model.DTO.TeamDTO;

public interface TeamService {
    TeamDTO getTeamByUserUuid(String uuid);
}