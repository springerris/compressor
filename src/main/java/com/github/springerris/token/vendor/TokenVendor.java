package com.github.springerris.token.vendor;

/**
 * Task responsible for prompting the user to authenticate with a service and communicating with the
 * backend to create a token.
 */
@FunctionalInterface
public interface TokenVendor {

    String vend() throws Exception;

}
