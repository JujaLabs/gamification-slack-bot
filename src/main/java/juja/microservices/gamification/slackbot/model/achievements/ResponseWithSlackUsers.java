package juja.microservices.gamification.slackbot.model.achievements;

/**
 * @author Nikolay Horushko
 */
public interface ResponseWithSlackUsers {
    String injectSlackUsers(String messageFormat);
}
