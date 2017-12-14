package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.model.DTO.UserDTO;

import java.util.List;
import java.util.Set;

/**
 * @author Artem
 * @author Nikolay Horushko
 * @author Danil Kuznetsov kuznetsov.danil.v@gmail.com
 */
public interface UserService {

    List<UserDTO> findUsersBySlackUsers(List<String> slackUsers);

    Set<UserDTO> findUsersByUuids(Set<String> uuids);
}
