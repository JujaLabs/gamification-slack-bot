package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import juja.microservices.gamification.slackbot.model.Command;
import juja.microservices.gamification.slackbot.model.User;
import juja.microservices.gamification.slackbot.service.SlackNameHandlerService;
import juja.microservices.gamification.slackbot.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Nikol on 3/18/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SlackNameHandlerServiceTest {
    @Inject
    private SlackNameHandlerService slackNameHandlerService;
    @MockBean
    private UserService userService;
    private User defaultUser;

    @Before
    public void setup() {
        defaultUser = new User("uuid", "gmail", "slack", "skype", "linkedin", "facebook", "twitter");
    }

    @Test
    public void changeSlackNamesToUuidWhenTextWithoutSlackNames() throws Exception {
        //given
        Command command = new Command("commandName", "@from.user", "textWithoutSlackNames");
        when(userService.findUserBySlack(command.getFromUser())).thenReturn(defaultUser);
        //when
        Command preparedCommand = slackNameHandlerService.handle(command);
        //then
        assertEquals("Command(name=commandName, fromUser=uuid, text=textWithoutSlackNames)", preparedCommand.toString());
    }


    @Test
    public void changeSlackNamesToUuid() throws Exception {
        //given
        Command command = new Command("commandName", "@from.user", "text @from.user TexT text.");
        when(userService.findUserBySlack(command.getFromUser())).thenReturn(defaultUser);
        //when
        Command preparedCommand = slackNameHandlerService.handle(command);
        //then
        assertEquals("Command(name=commandName, fromUser=uuid, text=text @#uuid#@ TexT text.)", preparedCommand.toString());
    }

    @Test
    public void changeSlackNamesToUuidWhenTwoSlackNamesInText() throws Exception {
        //given
        Command command = new Command("commandName", "@from.user", "text @from.user TexT @from.user text.");
        when(userService.findUserBySlack(command.getFromUser())).thenReturn(defaultUser);
        //when
        Command preparedCommand = slackNameHandlerService.handle(command);
        //then
        assertEquals("Command(name=commandName, fromUser=uuid, text=text @#uuid#@ TexT @#uuid#@ text.)", preparedCommand.toString());
    }

    @Test
    public void changeSlackNamesToUuidIfSlackNameWithoutSpace() throws Exception {
        //given
        Command command = new Command("commandName", "@from.user", "text@from.user TexT text.");
        when(userService.findUserBySlack(command.getFromUser())).thenReturn(defaultUser);
        //when
        Command preparedCommand = slackNameHandlerService.handle(command);
        //then
        assertEquals("Command(name=commandName, fromUser=uuid, text=text@#uuid#@ TexT text.)", preparedCommand.toString());
    }

    @Test
    public void changeSlackNamesToUuidIfTwoSlackNameWithoutSpace() throws Exception {
        //given
        Command command = new Command("commandName", "@from.user", "text@from.user TexT@from.user text.");
        when(userService.findUserBySlack(command.getFromUser())).thenReturn(defaultUser);
        //when
        Command preparedCommand = slackNameHandlerService.handle(command);
        //then
        assertEquals("Command(name=commandName, fromUser=uuid, text=text@#uuid#@ TexT@#uuid#@ text.)", preparedCommand.toString());
    }

    @Test(expected = WrongCommandFormatException.class)
    public void changeSlackNamesToUuidifCommandNull() throws Exception {
        //given
        Command command = null;
        //when
        slackNameHandlerService.handle(command);
    }

    @Test(expected = WrongCommandFormatException.class)
    public void changeSlackNamesToUuidIfSlackNameUseProhibitiveSymbol() throws Exception {
        //given
        Command command = new Command("commandName", "@from.u#ser", "text@from.user TexT@from.user text.");
        ;
        //when
        slackNameHandlerService.handle(command);
    }

    @Test(expected = WrongCommandFormatException.class)
    public void changeSlackNamesToUuidIfSlackNameNull() throws Exception {
        //given
        Command command = new Command("commandName", null, "text@from.user TexT@from.user text.");
        ;
        //when
        slackNameHandlerService.handle(command);
    }

    @Test(expected = WrongCommandFormatException.class)
    public void changeSlackNamesToUuidIfSlackNameIsEmpty() throws Exception {
        //given
        Command command = new Command("commandName", "", "text@from.user TexT@from.user text.");
        //when
        slackNameHandlerService.handle(command);
    }

    @Test
    public void changeSlackNamesToUuidIfTextNull() throws Exception {
        //given
        Command command = new Command("commandName", "@from.user", null);
        when(userService.findUserBySlack(command.getFromUser())).thenReturn(defaultUser);
        //when
        Command preparedCommand = slackNameHandlerService.handle(command);
        //then
        assertEquals("Command(name=commandName, fromUser=uuid, text=)", preparedCommand.toString());
    }

    @Test
    public void changeSlackNamesToUuidIfTextEmpty() throws Exception {
        //given
        Command command = new Command("commandName", "@from.user", "");
        when(userService.findUserBySlack(command.getFromUser())).thenReturn(defaultUser);
        //when
        Command preparedCommand = slackNameHandlerService.handle(command);
        //then
        assertEquals("Command(name=commandName, fromUser=uuid, text=)", preparedCommand.toString());
    }
}