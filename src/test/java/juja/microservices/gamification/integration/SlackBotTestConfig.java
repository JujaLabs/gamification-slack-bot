package juja.microservices.gamification.integration;

import juja.microservices.gamification.slackbot.controller.GamificationSlackCommandController;
import juja.microservices.gamification.slackbot.dao.impl.RestGamificationRepository;
import juja.microservices.gamification.slackbot.dao.impl.RestUserRepository;
import juja.microservices.gamification.slackbot.service.GamificationService;
import juja.microservices.gamification.slackbot.service.UserService;
import juja.microservices.gamification.slackbot.service.impl.DefaultGamificationService;
import juja.microservices.gamification.slackbot.service.impl.DefaultUserService;
import juja.microservices.gamification.slackbot.service.impl.SlackNameHandlerService;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


/**
 * Created by Nikol on 5/31/2017.
 */

@SpringBootConfiguration
public class SlackBotTestConfig {

    @Bean
    public GamificationSlackCommandController gamificationSlackCommandController(){
        return new GamificationSlackCommandController(gamificationService(), userService(), slackNameHandlerService());
    }

    @Bean
    public SlackNameHandlerService slackNameHandlerService(){
        return new SlackNameHandlerService(userService());
    }

    @Bean
    public UserService userService(){
        return new DefaultUserService(restUserRepository());
    }

    @Bean
    public RestUserRepository restUserRepository(){
        return new RestUserRepository(restTemplate());
    }

    @Bean

    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public GamificationService gamificationService(){
        return new DefaultGamificationService(restGamificationRepository());
    }

    @Bean
    public RestGamificationRepository restGamificationRepository(){
        return new RestGamificationRepository(restTemplate());
    }
}