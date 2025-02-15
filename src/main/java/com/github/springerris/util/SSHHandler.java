package com.github.springerris.util;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public class SSHHandler implements Closeable {

    private static final int F_SHELL    = 1;
    private static final int F_TRANSFER = 2;

    //

    private final String host;
    private final int port;
    private int flags = 0;
    private SSHClient shellClient;
    private SFTPClient transferClient;

    public SSHHandler(String host, int port) {
        this.host = host;
        this.port = port;
    }

    //

    /**
     * Connects to the SFTP server.
     * @param username Username
     * @param password Password
     * @throws net.schmizz.sshj.userauth.UserAuthException Authentication parameters may be incorrect
     * @throws IOException A generic IO exception has occurred
     * @see #connect(Authentication)
     */
    @Deprecated
    public void connect(String username, String password) throws IOException {
        this.connect(Authentication.basic(username, password));
    }

    /**
     * Connects to the SFTP server.
     * @param auth Authentication scheme to use
     * @throws net.schmizz.sshj.userauth.UserAuthException Authentication parameters may be incorrect
     * @throws IOException A generic IO exception has occurred
     */
    public void connect(Authentication auth) throws IOException {
        if (!this.getFlag(F_SHELL) || !this.shellClient.isConnected()) {
            this.connectShell(auth);
            this.setFlag(F_SHELL, true);
        }
        if (!this.getFlag(F_TRANSFER)) {
            this.connectTransfer();
            this.setFlag(F_TRANSFER, true);
        }
    }

    private void connectShell(Authentication auth) throws IOException {
        if (this.getFlag(F_SHELL) && this.shellClient.isConnected()) return;

        this.shellClient = new SSHClient();
        this.shellClient.addHostKeyVerifier(new PromiscuousVerifier());
        this.shellClient.connect(this.host, this.port);

        boolean close = true;
        try {
            auth.apply(this.shellClient);
            close = false;
        } finally {
            if (close) this.shellClient.close();
        }

        this.setFlag(F_SHELL, true);
    }

    private void connectTransfer() throws IOException {
        if (this.getFlag(F_TRANSFER)) return;
        this.transferClient = this.shellClient.newSFTPClient();
        this.setFlag(F_TRANSFER, true);
    }

    //

    /**
     * Performs a directory listing.
     */
    public List<RemoteResourceInfo> list() throws IOException {
        this.assertOpen();
        return this.transferClient.ls("./");
    }

    //

    @Override
    public void close() throws IOException {
        IOException suppressed = null;
        boolean error = false;

        if (this.getFlag(F_TRANSFER)) {
            try {
                this.transferClient.close();
            } catch (IOException e) {
                suppressed = e;
                error = true;
            }
            this.setFlag(F_TRANSFER, false);
        }

        if (this.getFlag(F_SHELL)) {
            try {
                this.shellClient.close();
            } catch (IOException e) {
                if (error) {
                    suppressed.addSuppressed(e);
                } else {
                    suppressed = e;
                    error = true;
                }
            }
            this.setFlag(F_SHELL, false);
        }

        if (error)
            throw suppressed;
    }

    //

    private void assertOpen() {
        if (!this.getFlag(F_SHELL | F_TRANSFER))
            throw new IllegalStateException("SSHHandler cannot be used; did not connect successfully or already closed");
    }

    private synchronized boolean getFlag(int flag) {
        return (this.flags & flag) != 0;
    }

    private synchronized void setFlag(int flag, boolean value) {
        if (value) {
            this.flags |= flag;
        } else {
            this.flags &= (~flag);
        }
    }

    //

    @FunctionalInterface
    public interface Authentication {

        static Authentication basic(final String username, final String password) {
            return (SSHClient c) -> c.authPassword(username, password);
        }

        static Authentication publicKey(final String username) {
            return (SSHClient c) -> c.authPublickey(username);
        }

        //

        void apply(SSHClient client) throws IOException;

    }

}


