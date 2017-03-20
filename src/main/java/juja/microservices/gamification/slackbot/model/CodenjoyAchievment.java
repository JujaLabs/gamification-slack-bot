package juja.microservices.gamification.slackbot.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by Nikol on 3/4/2017.
 */
@Getter
@ToString
public class CodenjoyAchievment implements Achievement {
    private String from;
    private String firstPlace;
    private String secondPlace;
    private String thirdPlace;

    public CodenjoyAchievment(String from,
                              String firstPlace,
                              String secondPlace,
                              String thirdPlace) {
        this.from = from;
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    @Override
    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("from", from);
        objectNode.put("firstPlace", firstPlace);
        objectNode.put("secondPlace", secondPlace);
        objectNode.put("thirdPlace", thirdPlace);
        return objectNode;
    }
}