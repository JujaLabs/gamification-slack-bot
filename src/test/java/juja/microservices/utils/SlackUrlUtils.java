package juja.microservices.utils;

/**
 * @author Danil Kuznetsov
 */
public class SlackUrlUtils {
    public static String getUrlTemplate(String endpoint) {
        return endpoint + "?" +
                "token={slashCommandToken}&" +
                "team_id={team_id}&" +
                "team_domain={team_domain}&" +
                "channel_id={channel_id}&" +
                "channel_name={channel_name}&" +
                "user_id={user_id}&" +
                "user_name={user_name}&" +
                "command={command}&" +
                "text={text}&" +
                "response_url={response_url}&";
    }

    public static Object[] getUriVars(String slackToken, String command, String description) {
        return new Object[]{slackToken,
                "any_team_id",
                "any_domain",
                "UHASHB8JB",
                "test-channel",
                "UNJSD9OKM",
                "@from-user",
                command,
                description,
                "http://example.com"};
    }
}
