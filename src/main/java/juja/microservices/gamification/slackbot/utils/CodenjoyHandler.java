package juja.microservices.gamification.slackbot.utils;

import juja.microservices.gamification.slackbot.model.CodenjoyAchievment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikol on 3/9/2017.
 */
public class CodenjoyHandler {
    private final int expectedArrayLength = 4;
    private final String regExpForSplit = "-";
    private final String[] tokens = {"/codenjoy", "1th", "2th", "3th"};

    //     /codenjoy -1th @slack_nick_name -2th @slack_nick_name2 -3th @slack_nick_name3
    public CodenjoyAchievment recieveCodenjoyAchievment(String from, String text) {
        String[] parametersArray = createParametersArray(text);
        checkParametersArray(parametersArray);
        return createCodenjoyAchievment(from, parametersArray);
    }

    private void checkParametersArray(String[] array){
        checkArrayLength(array);
        checkOrder(array);
    }

    private void checkArrayLength(String[] array) {
        if (array.length != expectedArrayLength) {
            throw new RuntimeException("wrong command parameters count");
        }
    }

    private void checkOrder(String[] array) {
        for (int i = 0; i < array.length; i++) {
            if(!array[i].contains(tokens[i])){
                throw new RuntimeException(String.format("wrong parameter %s", array[i]));
            }
        }
    }

    private String[] trimArrayElements(String[] array) {
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].trim();
        }
        return result;
    }

    private CodenjoyAchievment createCodenjoyAchievment(String from, String[] array){
        List<String> nicknames = extractNickNames(array);
        return new CodenjoyAchievment(from, nicknames.get(0), nicknames.get(1), nicknames.get(2));
    }

    private String[] createParametersArray(String text) {
        return trimArrayElements(text.split(regExpForSplit));
    }

    private List<String> extractNickNames(String[] array){
        List<String> result = new ArrayList<>();
        for (int i = 1; i < array.length; i++) {
            result.add(array[i].substring(array[i].indexOf('@')));
        }
        return result;
    }
}
