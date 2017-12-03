package ua.com.juja.microservices.gamification.slackbot.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ua.com.juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import ua.com.juja.slack.command.handler.exception.ParseSlackCommandException;
import ua.com.juja.slack.command.handler.model.SlackParsedCommand;
import ua.com.juja.slack.command.handler.model.UserDTO;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.CodenjoyAchievement;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

/**
 * @author Nikolay Horushko
 */
public class CodenjoyAchievementTest {
    private ObjectMapper objectMapper;
    private UserDTO fromUser;
    private UserDTO user1;
    private UserDTO user2;
    private UserDTO user3;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        objectMapper = new ObjectMapper();
        fromUser = new UserDTO("uuid0", "UFDR97JLA");
        user1 = new UserDTO("uuid1", "U1DR97JLA");
        user2 = new UserDTO("uuid2", "U2DR97JLA");
        user3 = new UserDTO("uuid3", "U3DR97JLA");
    }

    @Test
    public void createAchievementTest() throws JsonProcessingException {
        //given
        String text = "-1th <@U1DR97JLA|slackName1> -2th <@U2DR97JLA|slackName2> -3th <@U3DR97JLA|slackName3>";
        List<UserDTO> users = Arrays.asList(user1, user2, user3);
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(fromUser, text, users);
        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(slackParsedCommand);
        //then
        assertEquals("{\"from\":\"uuid0\",\"firstPlace\":\"uuid1\",\"secondPlace\":\"uuid2\",\"thirdPlace\":\"uuid3\"}",
                objectMapper.writeValueAsString(codenjoy));
    }

    @Test
    public void ifWrongTokensOrder() throws Exception {
        //given
        String text = "-2th <@U2DR97JLA|slackName2> -1th <@U1DR97JLA|slackName1> -3th <@U3DR97JLA|slackName3>";
        List<UserDTO> users = Arrays.asList(user2, user1, user3);
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(fromUser, text, users);
        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(slackParsedCommand);
        //then
        assertEquals("{\"from\":\"uuid0\",\"firstPlace\":\"uuid1\",\"secondPlace\":\"uuid2\",\"thirdPlace\":\"uuid3\"}",
                objectMapper.writeValueAsString(codenjoy));
    }


    @Test
    public void ifWithoutSpaces() throws Exception {
        //given
        String text = "-2th <@U2DR97JLA|slackName2> -3th <@U3DR97JLA|slackName3> -1th <@U1DR97JLA|slackName1>";
        List<UserDTO> users = Arrays.asList(user2, user3, user1);
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(fromUser, text, users);
        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(slackParsedCommand);
        //then
        assertEquals("{\"from\":\"uuid0\",\"firstPlace\":\"uuid1\",\"secondPlace\":\"uuid2\",\"thirdPlace\":\"uuid3\"}",
                objectMapper.writeValueAsString(codenjoy));
    }

    @Test
    public void ifTextInTheCommand() throws Exception {
        //given
        String text = "text -2th <@U2DR97JLA|slackName2> text text-3th <@U3DR97JLA|slackName3> -1th <@U1DR97JLA|slackName1>";
        List<UserDTO> users = Arrays.asList(user2, user3, user1);
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(fromUser, text, users);
        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(slackParsedCommand);
        //then
        assertEquals("{\"from\":\"uuid0\",\"firstPlace\":\"uuid1\",\"secondPlace\":\"uuid2\",\"thirdPlace\":\"uuid3\"}",
                objectMapper.writeValueAsString(codenjoy));
    }

    @Test
    public void ifNotSlackNameForToken() throws Exception {
        //given
        String text = "-2th <@U2DR97JLA|slackName2> -3th -1th <@U1DR97JLA|slackName1>";
        List<UserDTO> users = Arrays.asList(user2, user1);
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(fromUser, text, users);
        //then
        thrown.expect(ParseSlackCommandException.class);
        thrown.expectMessage(containsString("-2th <@U2DR97JLA|slackName2> -3th -1th <@U1DR97JLA|slackName1>' doesn't contain slackName for token " +
                "'-3th'"));
        //when
        new CodenjoyAchievement(slackParsedCommand);
    }

    @Test
    public void ifIsNotToken() throws Exception {
        //given
        String text = "-2th<@U2DR97JLA|slackName2> <@U3DR97JLA|slackName3> -1th<@U1DR97JLA|slackName1>";
        List<UserDTO> users = Arrays.asList(user2, user3, user1);
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(fromUser, text, users);
        //then
        thrown.expect(ParseSlackCommandException.class);
        thrown.expectMessage(containsString("Token '-3th' didn't find in the string '-2th<@U2DR97JLA|slackName2> <@U3DR97JLA|slackName3> -1th<@U1DR97JLA|slackName1>'"));
        //when
        new CodenjoyAchievement(slackParsedCommand);
    }

    @Test
    public void ifUseTwoSameWinnerMarkers() throws Exception {
        //given
        String text = "-2th<@U2DR97JLA|slackName2> -3th<@U3DR97JLA|slackName3> -3th <@U4DR97JLA|slackName4> -1th<@U1DR97JLA|slackName1>";
        UserDTO user4 = new UserDTO("uuid4", "U4DR97JLA");
        List<UserDTO> users = Arrays.asList(user2, user3, user4, user1);
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(fromUser, text, users);
        //then
        thrown.expect(ParseSlackCommandException.class);
        thrown.expectMessage(containsString("contains 2 tokens '-3th', but expected 1"));
        //when
        new CodenjoyAchievement(slackParsedCommand);
    }
}