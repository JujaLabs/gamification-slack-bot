package ua.com.juja.microservices.gamification.slackbot.model.achievements;

/**
 * @author Nikolay Horushko
 */
public interface ResponseWithSlackName {
    String injectSlackNames(String messageFormat);
}
