package com.github.springerris.token;

import com.github.springerris.token.vendor.TokenVendor;
import com.github.springerris.token.vendor.YanDiskTokenVendor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A token which may be used in the {@link TokenStore}.
 */
public enum TokenType {
    YANDEX_DISK(new YanDiskTokenVendor());

    private final TokenVendor vendor;
    TokenType(TokenVendor vendor) {
        this.vendor = vendor;
    }

    @Contract(pure = true)
    public @NotNull TokenVendor vendor() {
        return this.vendor;
    }

}
