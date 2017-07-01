package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.model.DTO.UserDTO;

import java.util.List;

/**
 * @author Artem
 */
public interface UserService {

    String findUuidUserBySlack(String slackNickname);

    List<UserDTO> findUsersBySlackNames(List<String> slackNames);
}
