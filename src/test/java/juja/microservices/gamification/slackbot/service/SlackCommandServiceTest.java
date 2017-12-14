package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import juja.microservices.gamification.slackbot.model.SlackParsedCommand;
import juja.microservices.gamification.slackbot.service.impl.SlackCommandService;
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

import static juja.microservices.utils.SlackUtils.convertSlackUserInSlackFormat;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Nikolay Horushko
 * @author Danil Kuznetsov kuznetsov.danil.v@gmail.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SlackCommandServiceTest {
    @Inject
    private SlackCommandService slackCommandService;

    @MockBean
    private UserService userService;

    private UserDTO userFrom;
    private UserDTO user1;
    private UserDTO user2;

    @Before
    public void setup() {
        userFrom = new UserDTO("AAA000", "slackFrom");
        user1 = new UserDTO("AAA111", "slack1");
        user2 = new UserDTO("AAA222", "slack2");
    }

    @Test
    public void createSlackCommandWithOneSlackUserInText() throws Exception {
        //given
        String bodySlackCommand = "text " + convertSlackUserInSlackFormat(user1.getSlackUser()) + " TexT text.";

        List<String> requestedUsersFromUserService = Arrays.asList(user1.getSlackUser(), userFrom.getSlackUser());
        List<UserDTO> foundUsersInUserService = Arrays.asList(userFrom, user1);

        when(userService.findUsersBySlackUsers(requestedUsersFromUserService)).thenReturn(foundUsersInUserService);

        //when
        SlackParsedCommand command = slackCommandService.createSlackCommand(userFrom.getSlackUser(), bodySlackCommand);

        //then
        assertEquals(userFrom, command.getFromUser());
        assertEquals(bodySlackCommand, command.getText());

        assertThat(command.getAllUsers(), hasItem(user1));

        verify(userService).findUsersBySlackUsers(requestedUsersFromUserService);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void createSlackCommandWithTwoSlackUsersInText() throws Exception {
        //given
        String bodySlackCommand = String.format("text %s TexT %s text.",
                convertSlackUserInSlackFormat(user1.getSlackUser()),
                convertSlackUserInSlackFormat(user2.getSlackUser())
        );

        List<String> requestedUsersFromUserService = Arrays.asList(user1.getSlackUser(),
                user2.getSlackUser(), userFrom.getSlackUser());

        List<UserDTO> foundUsersInUserService = Arrays.asList(userFrom, user1, user2);

        when(userService.findUsersBySlackUsers(requestedUsersFromUserService)).thenReturn(foundUsersInUserService);

        //when
        SlackParsedCommand command = slackCommandService.createSlackCommand(userFrom.getSlackUser(), bodySlackCommand);

        //then
        assertEquals(userFrom, command.getFromUser());
        assertEquals(bodySlackCommand, command.getText());

        assertThat(command.getAllUsers(), hasItem(user1));
        assertThat(command.getAllUsers(), hasItem(user2));

        verify(userService).findUsersBySlackUsers(requestedUsersFromUserService);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void createSlackCommandWithoutSlackUsersInText() throws Exception {
        //given
        String bodySlackCommand = "text without slack users TexT text.";

        List<String> requestedUsersFromUserService = Collections.singletonList(userFrom.getSlackUser());
        List<UserDTO> foundUsersInUserService = Collections.singletonList(userFrom);

        when(userService.findUsersBySlackUsers(requestedUsersFromUserService)).thenReturn(foundUsersInUserService);

        //when
        SlackParsedCommand command = slackCommandService.createSlackCommand(userFrom.getSlackUser(), bodySlackCommand);

        //then
        assertEquals(userFrom, command.getFromUser());
        assertEquals(bodySlackCommand, command.getText());

        assertTrue(command.getAllUsers().isEmpty());

        verify(userService).findUsersBySlackUsers(requestedUsersFromUserService);
        verifyNoMoreInteractions(userService);
    }

}