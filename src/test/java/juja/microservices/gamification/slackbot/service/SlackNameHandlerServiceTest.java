package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import juja.microservices.gamification.slackbot.model.SlackParsedCommand;
import juja.microservices.gamification.slackbot.service.impl.SlackNameHandlerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author Nikolay Horushko
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SlackNameHandlerServiceTest {
    @Inject
    private SlackNameHandlerService slackNameHandlerService;
    @MockBean
    private UserService userService;
    private UserDTO userFrom;
    private UserDTO user1;
    private UserDTO user2;
    private String userFromSlackName = "slackFrom";

    @Before
    public void setup() {
        userFrom = new UserDTO("AAA000", "@slackFrom");
        user1 = new UserDTO("AAA111", "@slack1");
        user2 = new UserDTO("AAA222", "@slack2");
    }

    @Test
    public void getSlackParcedCommandOneSlackInText() throws Exception {
        //given
        String text = "text " + user1.getSlack() + " TexT text.";
        List<String> requestToUserService = Arrays.asList(new String[]{user1.getSlack(), userFrom.getSlack()});
        List<UserDTO> responseFromUserService = Arrays.asList(new UserDTO[]{userFrom, user1});
        when(userService.findUsersBySlackNames(requestToUserService)).thenReturn(responseFromUserService);
        //when
        SlackParsedCommand slackParsedCommand = slackNameHandlerService.createSlackParsedCommand(userFromSlackName, text);
        //then
        assertEquals("SlackParsedCommand(fromSlackName=@slackFrom, text=text @slack1 TexT text., " +
                "slackNamesInText=[@slack1], userCountInText=1, " +
                "users={@slackFrom=UserDTO(uuid=AAA000, slack=@slackFrom), " +
                "@slack1=UserDTO(uuid=AAA111, slack=@slack1)})", slackParsedCommand.toString());
    }

    @Test
    public void getSlackParcedCommandTwoSlackInText() throws Exception {
        //given
        String text = "text " + user1.getSlack() + " TexT " + user2.getSlack() + " text.";
        List<String> requestToUserService = Arrays.asList(new String[]{user1.getSlack(), user2.getSlack(), userFrom.getSlack()});
        List<UserDTO> responseFromUserService = Arrays.asList(new UserDTO[]{userFrom, user1, user2});
        when(userService.findUsersBySlackNames(requestToUserService)).thenReturn(responseFromUserService);
        //when
        SlackParsedCommand slackParsedCommand = slackNameHandlerService.createSlackParsedCommand(userFromSlackName, text);
        //then
        assertEquals("SlackParsedCommand(fromSlackName=@slackFrom, text=text @slack1 TexT @slack2 text., " +
                "slackNamesInText=[@slack1, @slack2], userCountInText=2, " +
                "users={@slackFrom=UserDTO(uuid=AAA000, slack=@slackFrom), @slack2=UserDTO(uuid=AAA222, slack=@slack2), " +
                "@slack1=UserDTO(uuid=AAA111, slack=@slack1)})", slackParsedCommand.toString());
    }

    @Test
    public void getSlackParcedCommandWithoutSlackInText() throws Exception {
        //given
        String text = "text without slack name TexT text.";
        List<String> requestToUserService = Arrays.asList(new String[]{userFrom.getSlack()});
        List<UserDTO> responseFromUserService = Arrays.asList(new UserDTO[]{userFrom});
        when(userService.findUsersBySlackNames(requestToUserService)).thenReturn(responseFromUserService);
        //when
        SlackParsedCommand slackParsedCommand = slackNameHandlerService.createSlackParsedCommand(userFromSlackName, text);
        //then
        assertEquals("SlackParsedCommand(fromSlackName=@slackFrom, text=text without slack name TexT text., " +
                "slackNamesInText=[], userCountInText=0, " +
                "users={@slackFrom=UserDTO(uuid=AAA000, slack=@slackFrom)})", slackParsedCommand.toString());
    }

}