package juja.microservices.gamification.slackbot;

import juja.microservices.gamification.slackbot.controller.GamificationSlackCommandController;
import juja.microservices.gamification.slackbot.dao.*;
import juja.microservices.gamification.slackbot.service.*;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Danil Kuznetsov
 */

@SpringBootApplication
public class GamificationSlackBotApplication {


    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
        restTemplate.setMessageConverters(getHttpMessageConverters());
        return restTemplate;
    }


    @Bean
    public GamificationRepository gamificationDao() {
        return new RestGamificationRepository(restTemplate());
    }

    @Bean
    public GamificationService gamificationService() {
        return new DefaultGamificationService(gamificationDao());
    }

    @Bean
    public UserRepository userDao(){return new RestUserRepository(restTemplate());}

    @Bean
    public UserService userService(){return new DefaultUserService(userDao());}

    @Bean
    public SlackNameHandlerService slackNameHandlerService(){
        return new SlackNameHandlerService(userService());
    }

    @Bean
    public GamificationSlackCommandController gamificationSlackCommandController (){
        return new GamificationSlackCommandController(gamificationService(), userService(), slackNameHandlerService());
    }

    private ClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    private HttpClient httpClient() {
        return HttpClients.createDefault();
    }

    private List<HttpMessageConverter<?>> getHttpMessageConverters() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        return converters;
    }

    public static void main(String[] args) {
        SpringApplication.run(GamificationSlackBotApplication.class);
    }
}
