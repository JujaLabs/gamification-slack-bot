package juja.microservices.gamification.slackbot.service.impl;

import juja.microservices.gamification.slackbot.dao.UserRepository;
import juja.microservices.gamification.slackbot.model.User;
import juja.microservices.gamification.slackbot.service.UserService;

import javax.inject.Inject;

/**
 * @author Artem
 */

public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    @Inject
    public DefaultUserService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    @Override
    public User findUserBySlack(String slackNickname) {
        return userRepository.findUserBySlack(slackNickname);
    }

    @Override
    public String findUuidUserBySlack(String slackNickname) {
        return userRepository.findUuidUserBySlack(slackNickname);
    }
}
