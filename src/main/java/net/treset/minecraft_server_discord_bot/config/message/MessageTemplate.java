package net.treset.minecraft_server_discord_bot.config.message;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MessageTemplate<S> {
    private final Map<String, BiFunction<S, MessageContext, String>> extractors;

    public MessageTemplate(Map<String, BiFunction<S, MessageContext, String>> extractors) {
        this.extractors = Map.copyOf(extractors);
    }

    public boolean hasKey(String key) {
        return extractors.containsKey(key);
    }

    public Set<String> keys() {
        return extractors.keySet();
    }

    public String value(S source, MessageContext context, String key) {
        BiFunction<S, MessageContext, String> fn = extractors.get(key);
        if (fn == null) throw new IllegalArgumentException("Unknown key: " + key);
        return fn.apply(source, context);
    }

    public <T> MessageTemplate<T> extend(Map<String, BiFunction<T, MessageContext, String>> own,  Function<T, S> baseMapper) {
        Map<String, BiFunction<T, MessageContext, String>> merged = new HashMap<>();
        extractors.forEach((k, v) -> merged.put(k, (t, c) -> v.apply(baseMapper.apply(t), c)));
        merged.putAll(own);
        return new MessageTemplate<>(merged);
    }

    public <T> MessageTemplate<T> extendSimple(Map<String, Function<T, String>> own, Function<T, S> baseMapper) {
        Map<String, BiFunction<T, MessageContext, String>> modified = own.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (v, c) -> e.getValue().apply(v)));
        return extend(modified, baseMapper);
    }
}