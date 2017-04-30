package juja.microservices.gamification.slackbot.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by Artem
 */

@Getter
@EqualsAndHashCode
public class User {

    private String uuid;
    private String gmail;
    private String slack;
    private String skype;
    private String linkedin;
    private String facebook;
    private String twitter;

    @JsonCreator
    public User(@JsonProperty("uuid")String uuid,
                @JsonProperty("gmail")String gmail,
                @JsonProperty("slack")String slack,
                @JsonProperty("skype")String skype,
                @JsonProperty("linkedin")String linkedin,
                @JsonProperty("facebook")String facebook,
                @JsonProperty("twitter") String twitter) {
        this.uuid = uuid;
        this.gmail = gmail;
        this.slack = slack;
        this.skype = skype;
        this.linkedin = linkedin;
        this.facebook = facebook;
        this.twitter = twitter;
    }
}
