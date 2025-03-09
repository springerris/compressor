package com.github.springerris.util.ssh;

import net.schmizz.sshj.common.SSHException;
import net.schmizz.sshj.sftp.RemoteFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public class RemoteFileInputStream extends InputStream {

    private final RemoteFile handle;
    private long offset;

    public RemoteFileInputStream(@NotNull RemoteFile handle) {
        this.handle = handle;
        this.offset = 0L;
    }

    //

    @Override
    public int read(byte @NotNull [] b, int off, int len) throws IOException {
        int read = this.handle.read(this.offset, b, off, len);
        if (read == -1) return -1;
        this.offset += read;
        return read;
    }

    @Override
    public int read() throws IOException {
        byte[] b = new byte[1];
        int n;
        while ((n = this.handle.read(this.offset, b, 0, 1)) != 1) {
            if (n == -1) return -1;
        }
        this.offset++;
        return b[0] & 0xFF;
    }

    @Override
    public void close() throws IOException {
        try {
            this.handle.close();
        } catch (SSHException ignored) {
            // Library likes to throw bullshit errors on close
        }
    }

}
