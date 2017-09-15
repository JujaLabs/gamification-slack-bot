package juja.microservices.gamification.slackbot.model.achievements;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Getter
@ToString
public class TeamAchievement {
    @JsonProperty("from")
    private String fromUuid;
    @JsonProperty("members")
    private Set<String> members;

    public TeamAchievement(String fromUuid, Set<String> members) {
        this.fromUuid = fromUuid;
        this.members = members;
    }
}