package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.model.DTO.UserDTO;

import java.util.List;
import java.util.Set;

/**
 * @author Artem
 * @author Danil Kuznetsov kuznetsov.danil.v@gmail.com
 */
public interface UserRepository {

    List<UserDTO> findUsersBySlackUsers(List<String> slackUsers);

    Set<UserDTO> findUsersByUuids(Set<String> uuids);
}