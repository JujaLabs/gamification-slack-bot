package ua.com.juja.microservices.gamification.slackbot.service.impl;

import ua.com.juja.microservices.gamification.slackbot.dao.GamificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.com.juja.microservices.gamification.slackbot.model.DTO.TeamDTO;
import ua.com.juja.slack.command.handler.model.UserDTO;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.CodenjoyAchievement;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.DailyAchievement;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.InterviewAchievement;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.TeamAchievement;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.ThanksAchievement;
import ua.com.juja.microservices.gamification.slackbot.service.GamificationService;
import ua.com.juja.microservices.gamification.slackbot.service.TeamService;
import ua.com.juja.microservices.gamification.slackbot.service.UserService;
import ua.com.juja.slack.command.handler.service.SlackCommandHandlerService;
import ua.com.juja.slack.command.handler.model.SlackParsedCommand;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Danil Kuznetsov
 * @author Nikolay Horushko
 */
@Service
public class DefaultGamificationService implements GamificationService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final GamificationRepository gamificationRepository;
    private final TeamService teamService;
    private final UserService userService;
    private final SlackCommandHandlerService slackCommandHandlerService;


    @Inject
    public DefaultGamificationService(GamificationRepository gamificationRepository, TeamService teamService,
                                      UserService userService, SlackCommandHandlerService slackCommandHandlerService) {
        this.gamificationRepository = gamificationRepository;
        this.teamService = teamService;
        this.userService = userService;
        this.slackCommandHandlerService = slackCommandHandlerService;
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
            return codenjoy.injectSlackId("Thanks, we awarded the users. " +
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
            response = thanks.injectSlackId("Thanks, your 'thanks' for %s saved.");
        }
        if (ids.length == 2) {
            response = thanks.injectSlackId("Thanks, your 'thanks' for %s saved. " +
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

    @Override
    public String sendTeamAchievement(String fromUser, String text) {

        logger.debug("Start sending and saving Team achievement. fromUser: [{}] text: [{}]", fromUser, text);

        logger.debug("Start create Team achievement from slack parsed command");
        String fromUuid = createSlackParsedCommand(fromUser, text).getFromUser().getUuid();
        TeamDTO teamDTO = teamService.getTeamByUserUuid(fromUuid);

        Set<String> teamMembers = teamDTO.getMembers();
        TeamAchievement team = new TeamAchievement(fromUuid, teamMembers);
        logger.debug("Team achievement was created. team: {}", team.toString());

        Set<UserDTO> users = userService.receiveUsersByUuids(teamMembers);
        Set<String> slackNames = new LinkedHashSet<>();
        users.forEach(user -> slackNames.add(user.getSlackId()));
        logger.debug("Slack names for team {} were received: {}", team.toString(), slackNames);

        String[] ids = gamificationRepository.saveTeamAchievement(team);
        logger.info("Team achievement was saved with ids: {}", Arrays.toString(ids));

        int teamSize = teamMembers.size();
        if (ids.length == teamSize) {
            return "Thanks, your team report saved. Members: " + slackNames;
        } else {
            logger.debug("Expected {} saved achievements, but gamification service saved: {} ", teamSize, ids.length);
            return "Something went wrong during saving your team report";
        }
    }

    private SlackParsedCommand createSlackParsedCommand(String fromUser, String text) {

        logger.debug("Start create slackParsedCommand");
        SlackParsedCommand slackParsedCommand = slackCommandHandlerService.createSlackParsedCommand(fromUser, text);
        logger.debug("Finish create slackParsedCommand: {}", slackParsedCommand.toString());
        return slackParsedCommand;
    }
}