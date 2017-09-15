package juja.microservices.gamification.slackbot.model.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@ToString
public class TeamDTO {

    @NotEmpty
    private final Set<String> members;

    @JsonCreator
    public TeamDTO(@JsonProperty("members") @JsonDeserialize(as = LinkedHashSet.class) Set<String> members) {
        this.members = members;
    }
}