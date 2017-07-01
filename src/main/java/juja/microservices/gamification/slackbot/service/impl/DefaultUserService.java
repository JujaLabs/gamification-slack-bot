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
    public String findUuidUserBySlack(String slackName) {
        logger.debug("Received SlackName: [{}] for conversion", slackName);
        String uuid = userRepository.findUuidUserBySlack(slackName);
        logger.info("Found uuid: [{}] by SlackName: [{}]", uuid,slackName);
        return uuid;
    }

    @Override
    public List<UserDTO> findUsersBySlackNames(List<String> slackNames) {
        return userRepository.findUsersBySlackNames(slackNames);
    }
}
