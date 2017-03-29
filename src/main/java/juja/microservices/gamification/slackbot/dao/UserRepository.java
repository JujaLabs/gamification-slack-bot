package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.model.User;

/**
 * Created by Artem
 */
public interface UserRepository {

    User findUserBySlack(String slackNickname);

    String findUuidUserBySlack(String slackNickname);

}
