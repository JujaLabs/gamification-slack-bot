package ua.com.juja.microservices.gamification.slackbot.service;

import ua.com.juja.microservices.gamification.slackbot.model.SlackParsedCommand;
import ua.com.juja.slack.command.handler.model.UserDTO;
import ua.com.juja.microservices.gamification.slackbot.service.impl.SlackNameHandlerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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

    @Before
    public void setup() {
        userFrom = new UserDTO("AAA000", "@slackFrom");
        user1 = new UserDTO("AAA111", "@slack1");
        user2 = new UserDTO("AAA222", "@slack2");
    }

    @Test
    public void getSlackParcedCommandOneSlackInText() throws Exception {
        //given
        String text = "text " + user1.getSlackUserId() + " TexT text.";
        List<String> requestToUserService = Arrays.asList(user1.getSlackUserId(), userFrom.getSlackUserId());
        List<UserDTO> responseFromUserService = Arrays.asList(userFrom, user1);
        when(userService.findUsersBySlackNames(requestToUserService)).thenReturn(responseFromUserService);
        //when
        SlackParsedCommand slackParsedCommand = slackNameHandlerService.createSlackParsedCommand(userFrom.getSlackUserId(), text);
        //then
        assertEquals("SlackParsedCommand(fromSlackName=@slackFrom, text=text @slack1 TexT text., " +
                "slackNamesInText=[@slack1], userCountInText=1, " +
                "users={@slackFrom=UserDTO(uuid=AAA000, slack=@slackFrom), " +
                "@slack1=UserDTO(uuid=AAA111, slack=@slack1)})", slackParsedCommand.toString());
        verify(userService).findUsersBySlackNames(requestToUserService);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void getSlackParcedCommandOneSlackInTextWhenUserFromWithoutAt() throws Exception {
        //given
        String text = "text " + user1.getSlackUserId() + " TexT text.";
        UserDTO userFromWithoutAt = new UserDTO("AAA000", "slackFrom");
        List<String> requestToUserService = Arrays.asList(user1.getSlackUserId(), userFrom.getSlackUserId());
        List<UserDTO> responseFromUserService = Arrays.asList(userFrom, user1);
        when(userService.findUsersBySlackNames(requestToUserService)).thenReturn(responseFromUserService);
        //when
        SlackParsedCommand slackParsedCommand = slackNameHandlerService.createSlackParsedCommand(userFromWithoutAt.getSlackUserId()
                , text);
        //then
        assertEquals("SlackParsedCommand(fromSlackName=@slackFrom, text=text @slack1 TexT text., " +
                "slackNamesInText=[@slack1], userCountInText=1, " +
                "users={@slackFrom=UserDTO(uuid=AAA000, slack=@slackFrom), " +
                "@slack1=UserDTO(uuid=AAA111, slack=@slack1)})", slackParsedCommand.toString());
        verify(userService).findUsersBySlackNames(requestToUserService);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void getSlackParcedCommandTwoSlackInText() throws Exception {
        //given
        String text = "text " + user1.getSlackUserId() + " TexT " + user2.getSlackUserId() + " text.";
        List<String> requestToUserService = Arrays.asList(user1.getSlackUserId(), user2.getSlackUserId(), userFrom.getSlackUserId());
        List<UserDTO> responseFromUserService = Arrays.asList(userFrom, user1, user2);
        when(userService.findUsersBySlackNames(requestToUserService)).thenReturn(responseFromUserService);
        //when
        SlackParsedCommand slackParsedCommand = slackNameHandlerService.createSlackParsedCommand(userFrom.getSlackUserId(), text);
        //then
        assertEquals("SlackParsedCommand(fromSlackName=@slackFrom, text=text @slack1 TexT @slack2 text., " +
                "slackNamesInText=[@slack1, @slack2], userCountInText=2, " +
                "users={@slackFrom=UserDTO(uuid=AAA000, slack=@slackFrom), @slack2=UserDTO(uuid=AAA222, slack=@slack2), " +
                "@slack1=UserDTO(uuid=AAA111, slack=@slack1)})", slackParsedCommand.toString());
        verify(userService).findUsersBySlackNames(requestToUserService);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void getSlackParcedCommandWithoutSlackInText() throws Exception {
        //given
        String text = "text without slack name TexT text.";
        List<String> requestToUserService = Collections.singletonList(userFrom.getSlackUserId());
        List<UserDTO> responseFromUserService = Collections.singletonList(userFrom);
        when(userService.findUsersBySlackNames(requestToUserService)).thenReturn(responseFromUserService);
        //when
        SlackParsedCommand slackParsedCommand = slackNameHandlerService.createSlackParsedCommand(userFrom.getSlackUserId(), text);
        //then
        assertEquals("SlackParsedCommand(fromSlackName=@slackFrom, text=text without slack name TexT text., " +
                "slackNamesInText=[], userCountInText=0, " +
                "users={@slackFrom=UserDTO(uuid=AAA000, slack=@slackFrom)})", slackParsedCommand.toString());
        verify(userService).findUsersBySlackNames(requestToUserService);
        verifyNoMoreInteractions(userService);
    }

}