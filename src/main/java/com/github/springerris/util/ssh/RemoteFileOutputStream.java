package com.github.springerris.util.ssh;

import net.schmizz.sshj.sftp.RemoteFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

public class RemoteFileOutputStream extends OutputStream {

    private final RemoteFile handle;
    private long offset;

    public RemoteFileOutputStream(@NotNull RemoteFile handle) {
        this.handle = handle;
        this.offset = 0L;
    }

    //

    @Override
    public void write(int i) throws IOException {
        this.handle.write(this.offset, new byte[] { (byte) i }, 0, 1);
        this.offset++;
    }

    @Override
    public void write(byte @NotNull [] b, int off, int len) throws IOException {
        this.handle.write(this.offset, b, off, len);
        this.offset += len;
    }

    @Override
    public void close() throws IOException {
        this.handle.close();
    }

}
