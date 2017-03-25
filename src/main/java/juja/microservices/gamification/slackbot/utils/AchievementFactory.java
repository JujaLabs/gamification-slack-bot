package juja.microservices.gamification.slackbot.utils;

import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import juja.microservices.gamification.slackbot.model.Achievement;
import juja.microservices.gamification.slackbot.model.Command;
import juja.microservices.gamification.slackbot.service.SlackNameHandlerService;

import javax.inject.Inject;

/**
 * Created by Nikol on 3/19/2017.
 */
public class AchievementFactory {
    private SlackNameHandlerService slackNameHandlerService;
    private CodenjoyAchievementParcer codenjoyAchievementParcer;

    @Inject
    public AchievementFactory(SlackNameHandlerService slackNameHandlerService,
                              CodenjoyAchievementParcer codenjoyAchievementParcer) {
        this.slackNameHandlerService = slackNameHandlerService;
        this.codenjoyAchievementParcer = codenjoyAchievementParcer;
    }

    public Achievement createAchievement (Command command){
        command = prepare(command);
        if(command.getName().equals("/codenjoy")){
            return codenjoyAchievementParcer.createAchievementFromCommand(command);
        }
        throw new WrongCommandFormatException(String.format("Command %s is not supported", command.getName()));
    }

    private Command prepare(Command command){
        return slackNameHandlerService.handle(command);
    }
}
