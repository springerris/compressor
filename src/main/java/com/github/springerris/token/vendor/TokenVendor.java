package com.github.springerris.token.vendor;

import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Task responsible for prompting the user to authenticate with a service and communicating with the
 * backend to create a token.
 */
@FunctionalInterface
public interface TokenVendor {

    @Blocking
    @UnknownNullability String vend() throws Exception;

}
