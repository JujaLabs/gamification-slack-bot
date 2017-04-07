package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.exceptions.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
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
    private String defaultUuid;

    @Before
    public void setup() {
        defaultUuid = "uuid";
    }

    @Test
    public void changeSlackNamesToUuidWhenTextWithoutSlackNames() throws Exception {
        //given
        String text = "textWithoutSlackNames";
        //when
        String preparedText = slackNameHandlerService.replaceSlackNamesToUuids(text);
        //then
        assertEquals("textWithoutSlackNames", preparedText);
    }


    @Test
    public void changeSlackNamesToUuid() throws Exception {
        //given
        String text = "text @slack.name TexT text.";
        when(userService.findUuidUserBySlack("@slack.name")).thenReturn(defaultUuid);
        //when
        String preparedText = slackNameHandlerService.replaceSlackNamesToUuids(text);
        //then
        assertEquals("text @#uuid#@ TexT text.", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidWhenTwoSlackNamesInText() throws Exception {
        //given
        String text = "text @slack.name TexT @slack.name text.";
        when(userService.findUuidUserBySlack("@slack.name")).thenReturn(defaultUuid);
        //when
        String preparedText = slackNameHandlerService.replaceSlackNamesToUuids(text);
        //then
        assertEquals("text @#uuid#@ TexT @#uuid#@ text.", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidIfSlackNameWithoutSpace() throws Exception {
        String text = "text@slack.name TexT text.";
        when(userService.findUuidUserBySlack("@slack.name")).thenReturn(defaultUuid);
        //when
        String preparedText = slackNameHandlerService.replaceSlackNamesToUuids(text);
        //then
        assertEquals("text@#uuid#@ TexT text.", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidIfTwoSlackNameWithoutSpace() throws Exception {
        String text = "text@slack.name TexT@slack.name text.";
        when(userService.findUuidUserBySlack("@slack.name")).thenReturn(defaultUuid);
        //when
        String preparedText = slackNameHandlerService.replaceSlackNamesToUuids(text);
        //then
        assertEquals("text@#uuid#@ TexT@#uuid#@ text.", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidIfSlackNameInUpperCase() throws Exception {
        //given
        String text = "text @SLACK.NAME TexT text.";
        when(userService.findUuidUserBySlack("@slack.name")).thenReturn(defaultUuid);
        //when
        String preparedText = slackNameHandlerService.replaceSlackNamesToUuids(text);
        //then
        assertEquals("text @#uuid#@ TexT text.", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidIfSomeCharSlackNameInUpperCase() throws Exception {
        //given
        String text = "text @SlACK.NaME TexT text.";
        when(userService.findUuidUserBySlack("@slack.name")).thenReturn(defaultUuid);
        //when
        String preparedText = slackNameHandlerService.replaceSlackNamesToUuids(text);
        //then
        assertEquals("text @#uuid#@ TexT text.", preparedText);
    }


    @Test
    public void changeSlackNamesToUuidIfTextNull() throws Exception {
        String text = null;
        //when
        String preparedText = slackNameHandlerService.replaceSlackNamesToUuids(text);
        //then
        assertEquals("", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidIfTextEmpty() throws Exception {
        String text = "";
        //when
        String preparedText = slackNameHandlerService.replaceSlackNamesToUuids(text);
        //then
        assertEquals("", preparedText);
    }

    @Test
    public void ifTextContainsTextSeemsLikeSlackName() throws Exception {
        //given
        String text = "text @SLACK.NAME TexT @notSlackName text.";
        when(userService.findUuidUserBySlack("@slack.name")).thenReturn(defaultUuid);
        when(userService.findUuidUserBySlack("@notslackname")).thenThrow(UserNotFoundException.class);
        //when
        String preparedText = slackNameHandlerService.replaceSlackNamesToUuids(text);
        //then
        assertEquals("text @#uuid#@ TexT @notSlackName text.", preparedText);
    }
}