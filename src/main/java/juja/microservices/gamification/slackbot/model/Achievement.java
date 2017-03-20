package juja.microservices.gamification.slackbot.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by Nikol on 3/19/2017.
 */
public interface Achievement {
    ObjectNode toJson();
}
