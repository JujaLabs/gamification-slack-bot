package juja.microservices.gamification.slackbot.utils;

import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import juja.microservices.gamification.slackbot.model.Achievement;
import juja.microservices.gamification.slackbot.model.Command;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.junit.Assert.*;

/**
 * Created by Nikol on 3/20/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CodenjoyAchievementParcerTest {
    @Inject
    private CodenjoyAchievementParcer codenjoyParcer;

    @Test
    public void createAchievementFromCommand() throws Exception {
        //given
        Command command = new Command("/codenjoy",
                "fromuuid",
                "-1th @#uuid1#@ -2th @#uuid2#@ -3th @#uuid3#@");
        //when
        Achievement achievement = codenjoyParcer.createAchievementFromCommand(command);
        //then
        assertEquals("{\"from\":\"fromuuid\",\"firstPlace\":\"uuid1\",\"secondPlace\":\"uuid2\",\"thirdPlace\":\"uuid3\"}", achievement.toJson().toString());
    }

    @Test(expected = WrongCommandFormatException.class)
    public void ifWrongCommandName() throws Exception {
        //given
        Command command = new Command("/wrongCommandName",
                "fromuuid",
                "-1th @#uuid1#@ -2th @#uuid2#@ -3th @#uuid3#@");
        //when
        codenjoyParcer.createAchievementFromCommand(command);
    }

    @Test
    public void ifWrongTokensOrder() throws Exception {
        //given
        Command command = new Command("/codenjoy",
                "fromuuid",
                "-2th @#uuid2#@ -1th @#uuid1#@ -3th @#uuid3#@");
        //when
        Achievement achievement = codenjoyParcer.createAchievementFromCommand(command);
        //then
        assertEquals("{\"from\":\"fromuuid\",\"firstPlace\":\"uuid1\",\"secondPlace\":\"uuid2\",\"thirdPlace\":\"uuid3\"}", achievement.toJson().toString());
    }

    @Test
    public void ifWrongTokensOrder2() throws Exception {
        //given
        Command command = new Command("/codenjoy",
                "fromuuid",
                "-2th @#uuid2#@ -3th @#uuid3#@ -1th @#uuid1#@");
        //when
        Achievement achievement = codenjoyParcer.createAchievementFromCommand(command);
        //then
        assertEquals("{\"from\":\"fromuuid\",\"firstPlace\":\"uuid1\",\"secondPlace\":\"uuid2\",\"thirdPlace\":\"uuid3\"}", achievement.toJson().toString());
    }

    @Test
    public void ifWithoutSpacesAndWrongTokensOrder() throws Exception {
        //given
        Command command = new Command("/codenjoy",
                "fromuuid",
                "-2th@#uuid2#@-3th@#uuid3#@-1th@#uuid1#@");
        //when
        Achievement achievement = codenjoyParcer.createAchievementFromCommand(command);
        //then
        assertEquals("{\"from\":\"fromuuid\",\"firstPlace\":\"uuid1\",\"secondPlace\":\"uuid2\",\"thirdPlace\":\"uuid3\"}", achievement.toJson().toString());
    }

    @Test
    public void ifTextInTheCommand() throws Exception {
        //given
        Command command = new Command("/codenjoy",
                "fromuuid",
                "text -2th @#uuid2#@ text -3th@#uuid3#@ text-1th@#uuid1#@");
        //when
        Achievement achievement = codenjoyParcer.createAchievementFromCommand(command);
        //then
        assertEquals("{\"from\":\"fromuuid\",\"firstPlace\":\"uuid1\",\"secondPlace\":\"uuid2\",\"thirdPlace\":\"uuid3\"}", achievement.toJson().toString());
    }

    @Test
    public void ifTextInTheCommand2() throws Exception {
        //given
        Command command = new Command("/codenjoy",
                "fromuuid",
                "-1thText @#uuid1#@ -2th text @#uuid2#@ -3th @#uuid3#@");
        //when
        Achievement achievement = codenjoyParcer.createAchievementFromCommand(command);
        //then
        assertEquals("{\"from\":\"fromuuid\",\"firstPlace\":\"uuid1\",\"secondPlace\":\"uuid2\",\"thirdPlace\":\"uuid3\"}", achievement.toJson().toString());
    }

    @Test(expected = WrongCommandFormatException.class)
    public void ifNotUuidForToken() throws Exception {
        //given
        Command command = new Command("/codenjoy",
                "fromuuid",
                "-1th @#uuid1#@ -2th -3th @#uuid3#@");
        //when
        codenjoyParcer.createAchievementFromCommand(command);
    }
}