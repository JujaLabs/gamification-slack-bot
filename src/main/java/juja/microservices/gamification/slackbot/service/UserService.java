package juja.microservices.gamification.slackbot.service;


/**
 * @author Artem
 */


public interface UserService {

    String findUuidUserBySlack(String slackNickname);
}
