package com.github.springerris.token.vendor;

import io.github.wasabithumb.yandisk4j.YanDisk;
import io.github.wasabithumb.yandisk4j.auth.AuthHandler;
import io.github.wasabithumb.yandisk4j.auth.AuthResponse;
import io.github.wasabithumb.yandisk4j.auth.AuthScheme;
import io.github.wasabithumb.yandisk4j.auth.scope.AuthScope;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class YanDiskTokenVendor implements TokenVendor {

    @Override
    public @NotNull String vend() {
        final AuthHandler auth = YanDisk.auth(AuthScheme.LOCAL_CODE)
                .clientID("6b7a4a728a624228b2d93abe697ef726")
                .clientSecret("0fd69c030af349c4bec206126fd5b01d")
                .redirectURI("http://127.0.0.1:8015/")
                .scopes(AuthScope.INFO, AuthScope.READ, AuthScope.WRITE)
                .build();

        auth.openURL();

        String code = auth.awaitCode().code();
        AuthResponse response = auth.exchange(code);
        return response.accessToken();
    }

}
