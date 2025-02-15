package com.github.springerris.token;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Holds access tokens for third-party integrations.
 * This is global state so that tokens are never requested multiple times.
 */
public final class TokenStore {

    private final Logger logger;
    private final Map<TokenType, Optional<String>> map = new EnumMap<>(TokenType.class);

    public TokenStore(Logger logger) {
        this.logger = logger;
    }

    //

    public String get(TokenType type) {
        Optional<String> value = this.map.get(type);
        if (value != null) return value.orElse(null);

        String ret = null;
        try {
            ret = type.vendor().vend();
        } catch (Exception e) {
            this.logger.log(Level.WARNING, "Failed to vend token (" + type.name() + ")", e);
        }
        this.map.put(type, Optional.ofNullable(ret));
        return ret;
    }

}
