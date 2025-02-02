package com.github.springerris.archive.vfs;

import java.io.IOException;
import java.util.Arrays;

/**
 * Implements {@link VFS} and provides a neatly formatted tree as a {@link #toString()} implementation.
 */
public abstract class AbstractVFS implements VFS {

    protected String treeTitle() {
        return this.getClass().getSimpleName();
    }

    private void toString0(StringBuilder sb, int pad) {
        char[] padChars = new char[pad];
        Arrays.fill(padChars, '│');

        VFSEntity[] list;
        try {
            list = this.list();
        } catch (IOException e) {
            sb.append(padChars)
                    .append("├ * FAILED TO LIST *")
                    .append('\n');
            return;
        }

        int len = list.length;
        if (len == 0) {
            sb.append(padChars)
                    .append("├─ * EMPTY *")
                    .append('\n');
            return;
        }

        VFSEntity ent;
        VFS sub;
        for (int i=0; i < len; i++) {
            ent = list[i];

            String prefix;
            if (ent.isDirectory()) {
                prefix = "├┬ ";
            } else {
                prefix = (i == (len - 1)) ? "└─ " : "├─ ";
            }

            sb.append(padChars)
                    .append(prefix)
                    .append(ent.name())
                    .append('\n');

            if (!ent.isDirectory()) continue;

            sub = this.sub(ent.name());
            if (sub instanceof AbstractVFS aSub) {
                aSub.toString0(sb, pad + 1);
            } else {
                sb.append(padChars)
                        .append("│├─ * UNKNOWN *")
                        .append('\n');
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.treeTitle()).append('\n');
        this.toString0(sb, 0);
        return sb.toString();
    }

}
