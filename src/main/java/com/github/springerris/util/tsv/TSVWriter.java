package com.github.springerris.util.tsv;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

/**
 * Utility to write the TSV (tab-separated values) format. Currently used for the .ROOTS metadata in Archive.
 *
 * <h3>Example TSV</h3>
 * <pre>{@code
 * a    b   c
 * foo1    bar1    baz1
 * foo2    bar2    baz2
 * }</pre>
 *
 * <h3>Example Code</h3>
 * <pre>{@code
 * writer.writeRow("a", "b", "c");
 * writer.writeRow("foo1", "bar1", "baz1");
 * writer.writeRow("foo2", "bar2", "baz2");
 * }</pre>
 */
public class TSVWriter extends BufferedWriter {

    public TSVWriter(Writer out) {
        super(out);
    }

    //

    public void writeRow(List<String> row) throws IOException {
        for (int i=0; i < row.size(); i++) {
            if (i != 0) this.write('\t');
            this.write(row.get(i));
        }
        this.write('\n');
    }

    public void writeRow(String... row) throws IOException {
        this.writeRow(Arrays.asList(row));
    }

}
