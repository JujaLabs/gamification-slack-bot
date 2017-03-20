package juja.microservices.gamification.slackbot.utils;

import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import juja.microservices.gamification.slackbot.model.Achievement;
import juja.microservices.gamification.slackbot.model.Command;
import juja.microservices.gamification.slackbot.service.SlackNameHandlerService;

/**
 * Created by Nikol on 3/19/2017.
 */
public class AchievementFactory {
    private SlackNameHandlerService slackNameHandlerService;

    public AchievementFactory(SlackNameHandlerService slackNameHandlerService) {
        this.slackNameHandlerService = slackNameHandlerService;
    }

    public Achievement createAchievement (Command command){
        command = prepare(command);
        if(command.getName().equals("/codenjoy")){
            return new CodenjoyAchievementParcer().createAchievementFromCommand(command);
        }
        throw new WrongCommandFormatException(String.format("Command %s is not supported", command.getName()));
    }

    private Command prepare(Command command){
        return slackNameHandlerService.handle(command);
    }
}
