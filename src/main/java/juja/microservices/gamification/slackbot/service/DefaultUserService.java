package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.dao.UserRepository;
import juja.microservices.gamification.slackbot.model.User;

import javax.inject.Inject;

/**
 * Created by Artem
 */

public class DefaultUserService implements  UserService {

    private final UserRepository userRepository;

    @Inject
    public DefaultUserService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    @Override
    public User searchUser() {
        return userRepository.findUserBySlackGmail();
    }
}
