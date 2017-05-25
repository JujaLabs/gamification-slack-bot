package juja.microservices.gamification.slackbot.dao;


/**
 * @author Artem
 */
public interface UserRepository {

    String findUuidUserBySlack(String slackNickname);

}
