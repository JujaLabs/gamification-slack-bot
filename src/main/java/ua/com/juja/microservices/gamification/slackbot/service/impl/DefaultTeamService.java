package ua.com.juja.microservices.gamification.slackbot.service.impl;

import ua.com.juja.microservices.gamification.slackbot.dao.TeamRepository;
import ua.com.juja.microservices.gamification.slackbot.model.DTO.TeamDTO;
import ua.com.juja.microservices.gamification.slackbot.service.TeamService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class DefaultTeamService implements TeamService {
    @Inject
    private TeamRepository teamRepository;

    @Override
    public TeamDTO getTeamByUserUuid(String uuid) {
        return teamRepository.getTeamByUserUuid(uuid);
    }
}