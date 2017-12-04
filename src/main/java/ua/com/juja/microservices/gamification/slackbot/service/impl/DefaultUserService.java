package ua.com.juja.microservices.gamification.slackbot.service.impl;

import ua.com.juja.microservices.gamification.slackbot.dao.UserRepository;
import ua.com.juja.slack.command.handler.model.UserDTO;
import ua.com.juja.microservices.gamification.slackbot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

/**
 * @author Artem
 * @author Ivan Shapovalov
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
    public List<UserDTO> receiveUsersBySlackUserId(List<String> slackNames) {
        logger.debug("Received SlackName: [{}] for conversion", slackNames.toString());
        List<UserDTO> users = userRepository.findUsersBySlackNames(slackNames);
        logger.info("Found users: [{}] by SlackName: [{}]", users.toString(), slackNames.toString());
        return users;
    }

    @Override
    public Set<UserDTO> receiveUsersByUuids(Set<String> uuids) {
        logger.debug("Received uuids: [{}] for conversion", uuids.toString());
        Set<UserDTO> users = userRepository.findUsersByUuids(uuids);
        logger.info("Found users: [{}] by uuids: [{}]", users.toString(), uuids.toString());
        return users;
    }


    @Override
    public List<UserDTO> findUsersBySlackUserId(List<String> list) {
        return receiveUsersBySlackUserId(list);
    }
}
