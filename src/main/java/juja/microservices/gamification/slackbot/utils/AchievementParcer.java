package juja.microservices.gamification.slackbot.utils;

import juja.microservices.gamification.slackbot.model.Achievement;
import juja.microservices.gamification.slackbot.model.Command;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by Nikol on 3/19/2017.
 */
public abstract class AchievementParcer {
    @Value("${parcedUuid.pattern}")
    protected String parcedUuidPattern;
    @Value("${parcedUuid.startMarker}")
    protected String parcedUuidStartMarker;
    @Value("${parcedUuid.finishMarker}")
    protected String parcedUuidFinishMarker;
    public abstract Achievement createAchievementFromCommand(Command command);
}
