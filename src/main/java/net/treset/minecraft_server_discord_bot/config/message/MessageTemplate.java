package net.treset.minecraft_server_discord_bot.config.message;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class MessageTemplate<S> {
    private final Map<String, Function<S, String>> extractors;

    public MessageTemplate(Map<String, Function<S, String>> extractors) {
        this.extractors = Map.copyOf(extractors);
    }

    public boolean hasKey(String key) {
        return extractors.containsKey(key);
    }

    public Set<String> keys() {
        return extractors.keySet();
    }

    public String value(S source, String key) {
        Function<S, String> fn = extractors.get(key);
        if (fn == null) throw new IllegalArgumentException("Unknown key: " + key);
        return fn.apply(source);
    }

    public <T> MessageTemplate<T> extend(Map<String, Function<T, String>> own,  Function<T, S> baseMapper) {
        Map<String, Function<T, String>> merged = new HashMap<>();
        extractors.forEach((k, v) -> merged.put(k, t -> v.apply(baseMapper.apply(t))));
        merged.putAll(own);
        return new MessageTemplate<>(merged);
    }
}