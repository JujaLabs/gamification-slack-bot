package juja.microservices.gamification.slackbot.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * Created by Artem on 08.03.2017.
 */

@Getter
@Setter
@ToString
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(uuid, user.uuid) &&
                Objects.equals(gmail, user.gmail) &&
                Objects.equals(slack, user.slack) &&
                Objects.equals(skype, user.skype) &&
                Objects.equals(linkedin, user.linkedin) &&
                Objects.equals(facebook, user.facebook) &&
                Objects.equals(twitter, user.twitter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, gmail, slack, skype, linkedin, facebook, twitter);
    }
}
