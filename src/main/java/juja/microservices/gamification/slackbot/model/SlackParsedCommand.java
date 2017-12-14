package juja.microservices.gamification.slackbot.model;

import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nikolay Horushko
 * @author Danil Kuznetsov kuznetsov.danil.v@gmail.com
 */
@ToString(exclude = {"SLACK_USER_PATTERN", "logger"})
public class SlackParsedCommand {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String SLACK_USER_PATTERN = "\\<@(.*?)(\\||\\>)";
    private String fromSlackUser;
    private String text;
    private List<String> slackUserInText;
    private int userCountInText;
    private Map<String, UserDTO> users;

    public SlackParsedCommand(String fromSlackUser, String text, Map<String, UserDTO> users) {
        this.fromSlackUser = fromSlackUser;
        this.text = text;
        this.slackUserInText = receiveAllSlacUsers(text);
        this.users = users;
        this.userCountInText = slackUserInText.size();
    }

    public List<String> getSlackUserInText() {
        return slackUserInText;
    }

    public String getText() {
        return text;
    }

    public UserDTO getFromUser() {
        return users.get(fromSlackUser);
    }

    public UserDTO getFirstUser() {
        checkIsTextContainsSlackUser();
        UserDTO result = users.get(slackUserInText.get(0));
        logger.debug("Found the user: {} in the text: [{}]", result.toString(), text);
        return result;
    }

    public int getUserCountInText() {
        return userCountInText;
    }

    private List<String> receiveAllSlacUsers(String text) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(SLACK_USER_PATTERN);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            result.add(matcher.group(1).trim());
        }
        return result;
    }

    public String getTextWithoutSlackUsers() {
        String result = text.replaceAll(SLACK_USER_PATTERN, "");
        result = result.replaceAll("\\s+", " ").trim();
        return result;
    }

    public List<UserDTO> getAllUsers() {
        //TODO it's strange check, fix it later.
        // checkIsTextContainsSlackUser();
        List<UserDTO> result = new LinkedList(users.values());
        result.remove(result.stream().filter(res -> res.getSlackUser().equals(fromSlackUser)).findFirst().get());
        logger.debug("Found {} users in the text: [{}]", result.size(), text);
        return result;
    }

    public void checkIsTextContainsSlackUser() {
        if (userCountInText == 0) {
            logger.warn("The text: [{}] doesn't contain slack user.");
            throw new WrongCommandFormatException(String.format("The text '%s' doesn't contains slackUser", text));
        }
    }

    public Map<String, UserDTO> getUsersWithTokens(String[] tokens) {
        logger.debug("Received tokens: [{}] for searching. in the text: [{}]", tokens, text);
        List<Token> sortedTokenList = receiveTokensWithPositionInText(tokens);
        Map<String, UserDTO> result = new HashMap<>();

        for (int i = 0; i < sortedTokenList.size(); i++) {
            Token currentToken = sortedTokenList.get(i);
            Pattern slackUserPattern = Pattern.compile(SLACK_USER_PATTERN);
            Matcher matcher = slackUserPattern.matcher(text.substring(text.indexOf(currentToken.getToken())));
            if (matcher.find()) {
                String foundedSlackUser = matcher.group(1).trim();
                int indexFoundedSlackUser = text.indexOf(foundedSlackUser);
                for (int j = i + 1; j < sortedTokenList.size(); j++) {
                    if (indexFoundedSlackUser > sortedTokenList.get(j).getPositionInText()) {
                        logger.warn("The text: [{}] doesn't contain slack user for token: [{}]",
                                text, currentToken.getToken());
                        throw new WrongCommandFormatException(String.format("The text '%s' doesn't contain slackUser " +
                                "for token '%s'", text, currentToken.getToken()));
                    }
                }
                logger.debug("Found user: {} for token: {}", users.get(foundedSlackUser), currentToken.getToken());
                result.put(currentToken.getToken(), users.get(foundedSlackUser));
            } else {
                logger.warn("The text: [{}] doesn't contain slack User for token: [{}]",
                        text, sortedTokenList.get(i).getToken());
                throw new WrongCommandFormatException(String.format("The text '%s' " +
                        "doesn't contain slackUser for token '%s'", text, sortedTokenList.get(i).getToken()));
            }
        }
        return result;
    }

    private List<Token> receiveTokensWithPositionInText(String[] tokens) {
        Set<Token> result = new TreeSet<>();
        for (String token : tokens) {
            if (!text.contains(token)) {
                throw new WrongCommandFormatException(String.format("Token '%s' didn't find in the string '%s'",
                        token, text));
            }
            int tokenCounts = text.split(token).length - 1;
            if (tokenCounts > 1) {
                throw new WrongCommandFormatException(String.format("The text '%s' contains %d tokens '%s', " +
                        "but expected 1", text, tokenCounts, token));
            }
            result.add(new Token(token, text.indexOf(token)));
        }
        return new ArrayList<>(result);
    }

    @AllArgsConstructor
    @Getter
    class Token implements Comparable {
        private String token;
        private int positionInText;

        @Override
        public int compareTo(Object object) {
            Token thatToken = (Token) object;
            return positionInText - thatToken.getPositionInText();
        }
    }
}
