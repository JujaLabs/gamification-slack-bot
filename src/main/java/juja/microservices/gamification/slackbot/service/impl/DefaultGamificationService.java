package juja.microservices.gamification.slackbot.service.impl;

import juja.microservices.gamification.slackbot.dao.GamificationRepository;
import juja.microservices.gamification.slackbot.model.SlackParsedCommand;
import juja.microservices.gamification.slackbot.model.achievements.CodenjoyAchievement;
import juja.microservices.gamification.slackbot.model.achievements.DailyAchievement;
import juja.microservices.gamification.slackbot.model.achievements.InterviewAchievement;
import juja.microservices.gamification.slackbot.model.achievements.ThanksAchievement;
import juja.microservices.gamification.slackbot.service.GamificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * @author Danil Kuznetsov
 * @author Nikolay Horushko
 */

@Service
public class DefaultGamificationService implements GamificationService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final GamificationRepository gamificationRepository;
    private final SlackNameHandlerService slackNameHandlerService;

    @Inject
    public DefaultGamificationService(GamificationRepository gamificationRepository,
                                      SlackNameHandlerService slackNameHandlerService) {
        this.gamificationRepository = gamificationRepository;
        this.slackNameHandlerService = slackNameHandlerService;
    }

    @Override
    public String sendDailyAchievement(String fromUser, String text) {

        logger.debug("Start create Daily achievement from slack parsed command");
        DailyAchievement daily = new DailyAchievement(createSlackParsedCommand(fromUser, text));
        logger.debug("Daily achievement was created. Daily: {}", daily.toString());

        String[] ids = gamificationRepository.saveDailyAchievement(daily);
        logger.info("Daily achievement was saved with id: {}", Arrays.toString(ids));

        if (ids.length == 1) {
            return "Thanks, your daily report saved.";
        } else {
            logger.debug("Expected 1 saved achievements, but gamification service saved: {} ", ids.length);
            return "Something went wrong and we didn't save your daily report";
        }
    }

    @Override
    public String sendCodenjoyAchievement(String fromUser, String text) {

        logger.debug("Start create Codenjoy achievement from slack parsed command");
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(createSlackParsedCommand(fromUser, text));
        logger.debug("Codenjoy achievement was created. codenjoy: {}", codenjoy.toString());

        String[] ids = gamificationRepository.saveCodenjoyAchievement(codenjoy);
        logger.info("Codenjoy achievement was saved with id: {}", Arrays.toString(ids));

        if (ids.length == 3) {
            return codenjoy.injectSlackNames("Thanks, we awarded the users. " +
                    "First place: %s, Second place: %s, Third place: %s");
        } else {
            logger.debug("Expected 3 saved achievements, but gamification service saved: {} ", ids.length);
            return "Something went wrong and we didn't award the users";
        }
    }

    @Override
    public String sendThanksAchievement(String fromUser, String text) {

        logger.debug("Start create Thanks achievement from slack parsed command");
        ThanksAchievement thanks = new ThanksAchievement(createSlackParsedCommand(fromUser, text));
        logger.debug("Thanks achievement was created. thanks: {}", thanks.toString());

        String[] ids = gamificationRepository.saveThanksAchievement(thanks);
        logger.info("Thanks achievement was saved with id: {}", Arrays.toString(ids));

        String response = "Something went wrong and we didn't save the thanks.";

        if (ids.length == 1) {
            response = thanks.injectSlackNames("Thanks, your 'thanks' for %s saved.");
        }
        if (ids.length == 2) {
            response = thanks.injectSlackNames("Thanks, your 'thanks' for %s saved. " +
                    "Also you received +1 for your activity.");
        }

        return response;
    }

    @Override
    public String sendInterviewAchievement(String fromUser, String text) {

        logger.debug("Start create Interview achievement from slack parsed command");
        InterviewAchievement interview = new InterviewAchievement(createSlackParsedCommand(fromUser, text));
        logger.debug("Interview achievement was created. interview: {}", interview.toString());

        String[] ids = gamificationRepository.saveInterviewAchievement(interview);
        logger.info("Interview achievement was saved with id: {}", Arrays.toString(ids));

        if (ids.length == 1) {
            return "Thanks. Your interview saved.";
        } else {
            return "Something went wrong and we didn't save your interview";
        }
    }

    private SlackParsedCommand createSlackParsedCommand(String fromUser, String text) {

        logger.debug("Start create slackParsedCommand");
        SlackParsedCommand slackParsedCommand = slackNameHandlerService.createSlackParsedCommand(fromUser, text);
        logger.debug("Finish create slackParsedCommand: {}", slackParsedCommand.toString());
        return slackParsedCommand;
    }
}
