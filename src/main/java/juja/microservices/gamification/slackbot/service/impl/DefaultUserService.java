package juja.microservices.gamification.slackbot.service.impl;

import juja.microservices.gamification.slackbot.dao.UserRepository;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import juja.microservices.gamification.slackbot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Artem
 */

@Service
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    public DefaultUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDTO> findUsersBySlackNames(List<String> slackNames) {
        logger.debug("Received SlackName: [{}] for conversion", slackNames.toString());
        List<UserDTO> users = userRepository.findUsersBySlackNames(slackNames);
        logger.info("Found users: [{}] by SlackName: [{}]", users.toString(), slackNames.toString());
        return users;
    }
}
