package juja.microservices.gamification.slackbot.model;

import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nikolay Horushko
 */
@ToString(exclude={"SLACK_NAME_PATTERN","logger"})
public class SlackParsedCommand {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String SLACK_NAME_PATTERN = "@([a-zA-z0-9\\.\\_\\-]){1,21}";
    private String fromSlackName;
    private String text;
    private List<String> slackNamesInText;
    private int userCountInText;
    private Map<String, UserDTO> users;

    public SlackParsedCommand(String fromSlackName, String text, Map<String, UserDTO> users) {

        if (!fromSlackName.startsWith("@")) {
            logger.debug("add '@' to slack name [{}]", fromSlackName);
            fromSlackName = "@" + fromSlackName;
        }
        this.fromSlackName = fromSlackName;
        this.text = text;
        this.slackNamesInText = receiveAllSlackNames(text);
        this.users = users;
        this.userCountInText = slackNamesInText.size();
    }

    public List<String> getSlackNamesInText() {
        return slackNamesInText;
    }

    public String getText() {
        return text;
    }

    public UserDTO getFromUser() {
        return users.get(fromSlackName);
    }

    public UserDTO getFirstUser() {
        checkIsTextContainsSlackName();
        UserDTO result = users.get(slackNamesInText.get(0));
        logger.debug("Found the user: {} in the text: [{}]", result.toString(), text);
        return result;
    }

    public int getUserCountInText() {
        return userCountInText;
    }

    private List<String> receiveAllSlackNames(String text){
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(SLACK_NAME_PATTERN);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }

    public List<UserDTO> getAllUsers() {
        checkIsTextContainsSlackName();
        List<UserDTO> result = new LinkedList(users.values());
        result.remove(result.stream().filter(res -> res.getSlack().equals(fromSlackName)).findFirst().get());
        logger.debug("Found {} users in the text: [{}]", result.size(), text);
        return result;
    }

    public void checkIsTextContainsSlackName() {
        if (userCountInText == 0) {
            logger.warn("The text: [{}] doesn't contain slack name.");
            throw new WrongCommandFormatException(String.format("The text '%s' doesn't contains slackName", text));
        }
    }

    public Map<String, UserDTO> getUsersWithTokens(String[] tokens) {
        logger.debug("Recieve tokens: [{}] for searching. in the text: [{}]", tokens, text);
        List<Token> sortedTokenList = receiveTokensWithPositionInText(tokens);
        Map<String, UserDTO> result = new HashMap<>();
        for (int i = 0; i < sortedTokenList.size(); i++) {
            Token currentToken = sortedTokenList.get(i);
            Pattern slackNamePattern = Pattern.compile(SLACK_NAME_PATTERN);
            Matcher matcher = slackNamePattern.matcher(text.substring(text.indexOf(currentToken.getToken())));
            if (matcher.find()) {
                String foundedSlackName = matcher.group();
                int indexFoundedSlackName = text.indexOf(foundedSlackName);
                for (int j = i + 1; j < sortedTokenList.size(); j++) {
                    if (indexFoundedSlackName > sortedTokenList.get(j).getPositionInText()) {
                        logger.warn("The text: [{}] doesn't contain slack name for token: [{}]",
                                text, currentToken.getToken());
                        throw new WrongCommandFormatException(String.format("The text '%s' doesn't contain slackName " +
                                "for token '%s'", text, currentToken.getToken()));
                    }
                }
                logger.debug("Found user: {} for token:", users.get(foundedSlackName), currentToken.getToken());
                result.put(currentToken.getToken(), users.get(foundedSlackName));
            } else {
                logger.warn("The text: [{}] doesn't contain slack name for token: [{}]",
                        text, sortedTokenList.get(i).getToken());
                throw new WrongCommandFormatException(String.format("The text '%s' " +
                        "doesn't contain slackName for token '%s'", text, sortedTokenList.get(i).getToken()));
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
