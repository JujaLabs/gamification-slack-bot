package ua.com.juja.microservices.gamification.slackbot.service;


import ua.com.juja.slack.command.handler.UserBySlackUserId;
import ua.com.juja.slack.command.handler.model.UserDTO;

import java.util.List;
import java.util.Set;

/**
 * @author Artem
 * @author Nikolay Horushko
 */
public interface UserService extends UserBySlackUserId{

    List<UserDTO> receiveUsersBySlackUserId(List<String> slackNames);

    Set<UserDTO> receiveUsersByUuids(Set<String> uuids);
}
