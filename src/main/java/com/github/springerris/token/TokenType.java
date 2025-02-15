package com.github.springerris.token;

import com.github.springerris.token.vendor.TokenVendor;
import com.github.springerris.token.vendor.YanDiskTokenVendor;

/**
 * A token which may be used in the {@link TokenStore}.
 */
public enum TokenType {
    YANDEX_DISK(new YanDiskTokenVendor());

    private final TokenVendor vendor;
    TokenType(TokenVendor vendor) {
        this.vendor = vendor;
    }

    public TokenVendor vendor() {
        return this.vendor;
    }
}
