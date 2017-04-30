package juja.microservices.gamification.slackbot.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * @author Nikolay Horushko
 */
public class CodenjoyAchievementTest {
    private ObjectMapper objectMapper;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void createAchievementTest() throws JsonProcessingException {
        //given
        String fromUserUuid = "uuid";
        String text = "-1th @#uuid1#@ -2th @#uuid2#@ -3th @#uuid3#@";
        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(fromUserUuid, text);
        //then
        assertEquals("{\"from\":\"uuid\",\"firstPlace\":\"uuid1\",\"secondPlace\":\"uuid2\",\"thirdPlace\":\"uuid3\"}",
                objectMapper.writeValueAsString(codenjoy));
    }

    @Test
    public void ifWrongTokensOrder() throws Exception {
        //given
        String fromUserUuid = "uuid";
        String text = "-2th @#uuid2#@ -1th @#uuid1#@ -3th @#uuid3#@";
        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(fromUserUuid, text);
        //then
        assertEquals("{\"from\":\"uuid\",\"firstPlace\":\"uuid1\",\"secondPlace\":\"uuid2\",\"thirdPlace\":\"uuid3\"}",
                objectMapper.writeValueAsString(codenjoy));
    }

    @Test
    public void ifWrongTokensOrder2() throws Exception {
        //given
        String fromUserUuid = "uuid";
        String text = "-2th @#uuid2#@ -3th @#uuid3#@ -1th @#uuid1#@";
        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(fromUserUuid, text);
        //then
        assertEquals("{\"from\":\"uuid\",\"firstPlace\":\"uuid1\",\"secondPlace\":\"uuid2\",\"thirdPlace\":\"uuid3\"}",
                objectMapper.writeValueAsString(codenjoy));
    }

    @Test
    public void ifWithoutSpaces() throws Exception {
        //given
        String fromUserUuid = "uuid";
        String text = "-2th@#uuid2#@-3th@#uuid3#@-1th@#uuid1#@";
        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(fromUserUuid, text);
        //then
        assertEquals("{\"from\":\"uuid\",\"firstPlace\":\"uuid1\",\"secondPlace\":\"uuid2\",\"thirdPlace\":\"uuid3\"}",
                objectMapper.writeValueAsString(codenjoy));
    }

    @Test
    public void ifTextInTheCommand() throws Exception {
        //given
        String fromUserUuid = "uuid";
        String text = "text -2th @#uuid2#@ text -3th@#uuid3#@ text-1th@#uuid1#@";
        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(fromUserUuid, text);
        //then
        assertEquals("{\"from\":\"uuid\",\"firstPlace\":\"uuid1\",\"secondPlace\":\"uuid2\",\"thirdPlace\":\"uuid3\"}",
                objectMapper.writeValueAsString(codenjoy));
    }

    @Test
    public void ifTextInTheCommand2() throws Exception {
        //given
        String fromUserUuid = "uuid";
        String text = "-1thText @#uuid1#@ -2th text @#uuid2#@ -3th @#uuid3#@";
        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(fromUserUuid, text);
        //then
        assertEquals("{\"from\":\"uuid\",\"firstPlace\":\"uuid1\",\"secondPlace\":\"uuid2\",\"thirdPlace\":\"uuid3\"}",
                objectMapper.writeValueAsString(codenjoy));
    }

    @Test(expected = WrongCommandFormatException.class)
    public void ifNotUuidForToken() throws Exception {
        //given
        String fromUserUuid = "uuid";
        String text = "-1thText @#uuid1#@ -2th -3th @#uuid3#@";
        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(fromUserUuid, text);
    }

    @Test(expected = WrongCommandFormatException.class)
    public void ifIsNotToken() throws Exception {
        //given
        String fromUserUuid = "uuid";
        String text = "@#uuid1#@ -2th @#uuid2#@ -3th @#uuid3#@";
        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(fromUserUuid, text);
    }

    @Test(expected = WrongCommandFormatException.class)
    public void ifUseTwoSameWinnerMarkers() throws Exception {
        //given
        String fromUserUuid = "uuid";
        String text = "-1th @#uuid1#@ -2th @#uuid4#@ -2th @#uuid2#@ -3th @#uuid3#@";
        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(fromUserUuid, text);
    }
}