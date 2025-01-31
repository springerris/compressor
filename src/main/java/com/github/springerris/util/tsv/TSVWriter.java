package com.github.springerris.util.tsv;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

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
