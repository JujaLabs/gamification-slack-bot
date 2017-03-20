package juja.microservices.gamification.slackbot.service;


import juja.microservices.gamification.slackbot.model.User;

/**
 * Created by Artem
 */


public interface UserService {
    User findUserBySlack(String slackNickname);

    String findUuidUserBySlack(String slackNickname);
}
