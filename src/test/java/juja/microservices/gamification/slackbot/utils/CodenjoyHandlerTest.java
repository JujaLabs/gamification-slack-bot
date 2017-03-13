package juja.microservices.gamification.slackbot.utils;

import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import juja.microservices.gamification.slackbot.model.CodenjoyAchievment;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Nikol on 3/9/2017.
 */
public class CodenjoyHandlerTest {
    private CodenjoyHandler codenjoyHandler;

    @Before
    public void setup(){
        codenjoyHandler = new CodenjoyHandler();
    }

    @Test
    public void receiveCodenjoyAchievmentTest(){
        //given
        final String commandText = "-1th @slack_nick_name -2th @slack_nick_name2 -3th @slack_nick_name3";
        final CodenjoyAchievment expectedCodenjoy = new CodenjoyAchievment("from",
                "@slack_nick_name",
                "@slack_nick_name2",
                "@slack_nick_name3");
        //when
        CodenjoyAchievment codenjoy = codenjoyHandler.recieveCodenjoyAchievment("from", commandText);
        //then
        assertEquals(expectedCodenjoy.toString(), codenjoy.toString());
    }

    @Test(expected = WrongCommandFormatException.class)
    public void receiveCodenjoyAchievmentIfCommandNullTest(){
        //given
        final String commandText = null;
        //when
        codenjoyHandler.recieveCodenjoyAchievment("from", commandText);
    }

    @Test(expected = WrongCommandFormatException.class)
    public void receiveCodenjoyAchievmentIfWrongParametersCountTest(){
        //given
        final String commandText = "-1th @slack_nick_name -3th @slack_nick_name3";
        //when
        codenjoyHandler.recieveCodenjoyAchievment("from", commandText);
    }

    @Test(expected = WrongCommandFormatException.class)
    public void receiveCodenjoyAchievmentIfWrongParametersOrderTest(){
        //given
        final String commandText = "-2th @slack_nick_name2 -1th @slack_nick_name -3th @slack_nick_name3";
        //when
        codenjoyHandler.recieveCodenjoyAchievment("from", commandText);
    }

    @Test(expected = WrongCommandFormatException.class)
    public void receiveCodenjoyAchievmentIfWrongCommandSlackNameWithoutDog(){
        //given
        final String commandText = "-1th @slack_nick_name -2th slack_nick_name2 -3th @slack_nick_name3";
        //when
        codenjoyHandler.recieveCodenjoyAchievment("from", commandText);
    }

    @Test(expected = WrongCommandFormatException.class)
    public void receiveCodenjoyAchievmentIfWrongCommandWithForbiddenSymbol(){
        //given
        final String commandText = "-1th @slack_nick*name -2th slack_nick_name2 -3th @slack_nick_name3";
        //when
        codenjoyHandler.recieveCodenjoyAchievment("from", commandText);
    }

    @Test(expected = WrongCommandFormatException.class)
    public void receiveCodenjoyAchievmentIfEmptyCommand(){
        //given
        final String commandText = "";
        //when
        codenjoyHandler.recieveCodenjoyAchievment("from", commandText);
    }


}