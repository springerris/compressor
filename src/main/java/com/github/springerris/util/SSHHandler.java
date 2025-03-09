package com.github.springerris.util;

import com.github.springerris.archive.vfs.VFS;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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

    public SSHHandler(@NotNull String host, int port) {
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
    @ApiStatus.Obsolete
    public void connect(@NotNull String username, @NotNull String password) throws IOException {
        this.connect(Authentication.basic(username, password));
    }

    /**
     * Connects to the SFTP server.
     * @param auth Authentication scheme to use
     * @throws net.schmizz.sshj.userauth.UserAuthException Authentication parameters may be incorrect
     * @throws IOException A generic IO exception has occurred
     */
    public void connect(@NotNull Authentication auth) throws IOException {
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
    public @NotNull List<RemoteResourceInfo> list() throws IOException {
        this.assertOpen();
        return this.transferClient.ls("./");
    }

    /**
     * Returns a VFS which represents the content of the active SFTP connection.
     */
    public @NotNull VFS vfs() {
        this.assertOpen();
        return VFS.sftp(this.transferClient);
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

        @Contract("_, _ -> new")
        static @NotNull Authentication basic(final @NotNull String username, final @NotNull String password) {
            return (SSHClient c) -> c.authPassword(username, password);
        }

        @Contract("_ -> new")
        static @NotNull Authentication publicKey(final @NotNull String username) {
            return (SSHClient c) -> c.authPublickey(username);
        }

        //

        void apply(@NotNull SSHClient client) throws IOException;

    }

}


