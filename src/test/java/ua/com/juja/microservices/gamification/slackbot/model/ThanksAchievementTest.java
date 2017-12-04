package ua.com.juja.microservices.gamification.slackbot.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.User;
import ua.com.juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import ua.com.juja.slack.command.handler.model.SlackParsedCommand;
import ua.com.juja.slack.command.handler.model.UserDTO;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.ThanksAchievement;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

public class ThanksAchievementTest {
    private ObjectMapper objectMapper;
    private UserDTO fromUser;
    private UserDTO user1;
    private UserDTO user2;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        objectMapper = new ObjectMapper();
        fromUser = new UserDTO("uuid0", "@UFDR97JLA");
        user1 = new UserDTO("uuid1", "U1DR97JLA");
        user2 = new UserDTO("uuid2", "U2DR97JLA");
    }

    @Test
    public void createAchievement() throws Exception {
        //given
        String text = "Thanks <@U1DR97JLA|slackName1> for help";
        List<UserDTO> users = Arrays.asList(user1);
        //when
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(fromUser, text, users);
        ThanksAchievement thanks = new ThanksAchievement(slackParsedCommand);
        //then
        assertEquals("{\"description\":\"Thanks for help\",\"from\":\"uuid0\",\"to\":\"uuid1\"}", objectMapper.writeValueAsString(thanks));
    }

    @Test
    public void createAchievementThrowExceptionIfWithoutSlackName() throws Exception {
        //given
        String text = "Thanks without slack name";
        //then
        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString("We didn't find slack name in your command. 'Thanks without slack name'" +
                " You must write user's slack name for 'thanks'."));
        //when
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(fromUser, text, Collections.emptyList());
        new ThanksAchievement(slackParsedCommand);
    }

    @Test
    public void createAchievementThrowExceptionIfMoreThanOneSlackName() throws Exception {
        //given
        String text = "Thanks <@U1DR97JLA|slackName1> text <@U2DR97JLA|slackName2>";
        List<UserDTO> users = Arrays.asList(user1, user2);

        //then
        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString("We found 2 slack names in your command: 'Thanks <@U1DR97JLA|slackName1> text <@U2DR97JLA|slackName2>'" +
                "  You can't send thanks more than one user."));
        //when
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(fromUser, text, users);
        new ThanksAchievement(slackParsedCommand);
    }
}