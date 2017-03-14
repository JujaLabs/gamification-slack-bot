package juja.microservices.gamification.slackbot.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Artem
 */

@Getter
@Setter
@ToString
public class Interview {

    private String from;
    private String description;

    public Interview(String from, String description) {
        this.from = from;
        this.description = description;
    }
}


