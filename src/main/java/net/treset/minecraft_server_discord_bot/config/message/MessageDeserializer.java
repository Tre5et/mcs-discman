package net.treset.minecraft_server_discord_bot.config.message;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

public class MessageDeserializer extends StdDeserializer<Message<?>> {
    public MessageDeserializer() {
        super(Message.class);
    }

    @Override
    public Message<?> deserialize(JsonParser parser, DeserializationContext context) {
        JsonToken token = parser.currentToken();
        if (token == null) {
            token = parser.nextToken();
        }

        if (token == JsonToken.VALUE_STRING) {
            return new Message<>(parser.getValueAsString());
        }

        throw DatabindException.from(parser, "Message must be a YAML string value.");
    }
}

class MessageDefaultDeserializer extends StdDeserializer<Message.Default> {
    public MessageDefaultDeserializer() {
        super(Message.Default.class);
    }

    @Override
    public Message.Default deserialize(JsonParser parser, DeserializationContext context) {
        JsonToken token = parser.currentToken();
        if (token == null) {
            token = parser.nextToken();
        }

        if (token == JsonToken.VALUE_STRING) {
            return new Message.Default(parser.getValueAsString());
        }

        throw DatabindException.from(parser, "Message.Default must be a YAML string value.");
    }
}




