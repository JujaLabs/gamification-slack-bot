package juja.microservices.gamification.slackbot.dao;


/**
 * Created by Nikol on 3/13/2017.
 */
public class DummyUserRepository {
    public String getUserNameBySlackName(String slackName){
        return slackName + "User";
    }
}
