package ua.com.juja.microservices.gamification.slackbot.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import ua.com.juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import ua.com.juja.slack.command.handler.model.UserDTO;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.ThanksAchievement;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

public class ThanksAchievementTest {
    private ObjectMapper objectMapper;
    private Map<String, UserDTO> users;
    private String fromSlackName;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        objectMapper = new ObjectMapper();
        users = new HashMap<>();
        users.put("@from", new UserDTO("uuid0", "@from"));
        users.put("@slack1", new UserDTO("uuid1", "@slack1"));
        fromSlackName = "@from";
    }

    @Test
    public void createAchievement() throws Exception {
        //given
        String text = "Thanks @slack1 for help";
        //when
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(fromSlackName, text, users);
        ThanksAchievement thanks = new ThanksAchievement(slackParsedCommand);
        //then
        assertEquals("{\"description\":\"Thanks for help\",\"from\":\"uuid0\",\"to\":\"uuid1\"}", objectMapper.writeValueAsString(thanks));
    }

    @Test
    public void createAchievementThrowExceptionIfWithoutSlackName() throws Exception {
        //given
        users = new HashMap<>();
        users.put("@from", new UserDTO("uuid0", "@from"));
        String text = "Thanks without slack name";
        //then
        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString("We didn't find slack name in your command. 'Thanks without slack name'" +
                " You must write user's slack name for 'thanks'."));
        //when
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(fromSlackName, text, users);
        new ThanksAchievement(slackParsedCommand);
    }

    @Test
    public void createAchievementThrowExceptionIfMoreThanOneSlackName() throws Exception {
        //given
        users = new HashMap<>();
        users.put("@from", new UserDTO("uuid0", "@from"));
        users.put("@slack1", new UserDTO("uuid1", "@slack1"));
        users.put("@slack2", new UserDTO("uuid2", "@slack2"));
        String text = "Thanks @slack1 text @slack2";
        //then
        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString("We found 2 slack names in your command: 'Thanks @slack1 text @slack2'" +
                "  You can't send thanks more than one user."));
        //when
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(fromSlackName, text, users);
        new ThanksAchievement(slackParsedCommand);
    }


}