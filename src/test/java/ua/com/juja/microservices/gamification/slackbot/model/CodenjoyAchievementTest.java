package ua.com.juja.microservices.gamification.slackbot.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ua.com.juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import ua.com.juja.slack.command.handler.model.UserDTO;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.CodenjoyAchievement;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

/**
 * @author Nikolay Horushko
 */
public class CodenjoyAchievementTest {
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
        users.put("@slack2", new UserDTO("uuid2", "@slack2"));
        users.put("@slack3", new UserDTO("uuid3", "@slack3"));
        fromSlackName = "from";
    }

    @Test
    public void createAchievementTest() throws JsonProcessingException {
        //given
        String text = "-1th @slack1 -2th @slack2 -3th @slack3";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(fromSlackName, text, users);
        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(slackParsedCommand);
        //then
        assertEquals("{\"from\":\"uuid0\",\"firstPlace\":\"uuid1\",\"secondPlace\":\"uuid2\",\"thirdPlace\":\"uuid3\"}",
                objectMapper.writeValueAsString(codenjoy));
    }

    @Test
    public void ifWrongTokensOrder() throws Exception {
        //given
        String text = "-2th @slack2 -1th @slack1 -3th @slack3";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(fromSlackName, text, users);
        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(slackParsedCommand);
        //then
        assertEquals("{\"from\":\"uuid0\",\"firstPlace\":\"uuid1\",\"secondPlace\":\"uuid2\",\"thirdPlace\":\"uuid3\"}",
                objectMapper.writeValueAsString(codenjoy));
    }


    @Test
    public void ifWithoutSpaces() throws Exception {
        //given
        String text = "-2th @slack2 -3th @slack3 -1th @slack1";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(fromSlackName, text, users);
        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(slackParsedCommand);
        //then
        assertEquals("{\"from\":\"uuid0\",\"firstPlace\":\"uuid1\",\"secondPlace\":\"uuid2\",\"thirdPlace\":\"uuid3\"}",
                objectMapper.writeValueAsString(codenjoy));
    }

    @Test
    public void ifTextInTheCommand() throws Exception {
        //given
        String text = "text -2th @slack2 text text-3th @slack3 -1th @slack1";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(fromSlackName, text, users);
        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(slackParsedCommand);
        //then
        assertEquals("{\"from\":\"uuid0\",\"firstPlace\":\"uuid1\",\"secondPlace\":\"uuid2\",\"thirdPlace\":\"uuid3\"}",
                objectMapper.writeValueAsString(codenjoy));
    }

    @Test
    public void ifNotSlackNameForToken() throws Exception {
        //given
        String text = "-2th @slack2 -3th -1th @slack1";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(fromSlackName, text, users);
        //then
        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString("-2th @slack2 -3th -1th @slack1' doesn't contain slackName for token " +
                "'-3th'"));
        //when
        new CodenjoyAchievement(slackParsedCommand);
    }

    @Test
    public void ifIsNotToken() throws Exception {
        //given
        String text = "-2th@slack2 @slack3 -1th@slack1";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(fromSlackName, text, users);
        //then
        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString("Token '-3th' didn't find in the string '-2th@slack2 @slack3 -1th@slack1'"));
        //when
        new CodenjoyAchievement(slackParsedCommand);
    }

    @Test
    public void ifUseTwoSameWinnerMarkers() throws Exception {
        //given
        String text = "-2th@slack2 -3th@slack3 -3th @slack4 -1th@slack1";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(fromSlackName, text, users);
        //then
        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString("The text '-2th@slack2 -3th@slack3 -3th @slack4 -1th@slack1' " +
                "contains 2 tokens '-3th', but expected 1"));
        //when
        new CodenjoyAchievement(slackParsedCommand);
    }
}