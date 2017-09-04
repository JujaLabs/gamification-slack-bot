package juja.microservices.gamification.slackbot.model.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Set;

@Getter
public class TeamDTO {

    @NotEmpty
    private final Set<String> members;

    @JsonCreator
    public TeamDTO(@JsonProperty("members") Set<String> members) {
        this.members = members;
    }
}
