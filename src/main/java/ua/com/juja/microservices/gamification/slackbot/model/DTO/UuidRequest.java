package ua.com.juja.microservices.gamification.slackbot.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class UuidRequest {
    Set<String> uuids;
}