package juja.microservices.gamification.slackbot.service.impl;

import juja.microservices.gamification.slackbot.dao.UserRepository;
import juja.microservices.gamification.slackbot.service.UserService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * @author Artem
 */

@Service
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    @Inject
    public DefaultUserService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    @Override
    public String findUuidUserBySlack(String slackNickname) {
        return userRepository.findUuidUserBySlack(slackNickname);
    }
}
