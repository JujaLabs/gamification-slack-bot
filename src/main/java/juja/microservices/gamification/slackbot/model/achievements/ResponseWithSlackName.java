package juja.microservices.gamification.slackbot.model.achievements;

/**
 * Created by Nikol on 7/2/2017.
 */
public interface ResponseWithSlackName {
    String injectSlackNames(String messageFormat);
}
