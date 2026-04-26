package net.treset.minecraft_server_discord_bot.config.message;

import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.ValidatableConfig;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = MessageDeserializer.class)
public class Message<C> extends ValidatableConfig {
    private final String formattableMessage;
    private final List<String> formatKeys;
    private String failure = null;
    private MessageTemplate<C> template;

    public Message(String message) {
        if(message == null || !message.contains("{")) {
            formattableMessage = message;
            formatKeys = List.of();
            return;
        }

        StringBuilder messageFormat = new StringBuilder();
        formatKeys = new ArrayList<>();
        int i = 0;
        while(i >= 0) {
            int startIndex = message.indexOf('{', i);
            if(startIndex < 0) {
                messageFormat.append(message, i, message.length());
                break;
            }
            if(startIndex > 0 && message.charAt(startIndex-1) == '\\') continue;
            int endIndex = message.indexOf('}', startIndex);
            if(endIndex < 0) {
                formattableMessage = message;
                failure = "Can't parse message '" + message + "'; format key not closed.";
                return;
            }
            messageFormat.append(message, i, startIndex);
            messageFormat.append("%s");
            formatKeys.add(message.substring(startIndex+1, endIndex));
            i = endIndex + 1;
        }
        formattableMessage = messageFormat.toString();
    }

    public String get(C source, MessageContext context) {
        Object[] resolvedKeys = formatKeys.stream().map(k -> template.value(source, context, k)).toArray(String[]::new);
        return String.format(formattableMessage, resolvedKeys);
    }

    public String get(C source) {
        return get(source, MessageContext.IN_GAME);
    }

    @Override
    public List<String> prefix() {
        return List.of("message");
    }

    @Override
    public void validate(Config config) throws ConfigException {
        throw new ConfigException("Cannot validate message without context");
    }

    public void validate(MessageTemplate<C> template) throws ConfigException {
        this.template = template;
        if(this.failure != null) {
            throw new ConfigException(this.failure);
        }
        for(String key : formatKeys) {
            if(!template.hasKey(key)) {
                throw new ConfigException("Message contains invalid key '" + key + "'. Allowed are '" + String.join(", ", template.keys()) + "'.");
            }
        }
    }

    @JsonDeserialize(using = MessageDefaultDeserializer.class)
    public static class Default extends Message<Object> {
        public Default(String message) {
            super(message);
        }

        public String get(MessageContext context) {
            return super.get(null, context);
        }

        public void validate() throws ConfigException {
            super.validate(MessageTemplates.DATE_TIME);
        }
    }
}
