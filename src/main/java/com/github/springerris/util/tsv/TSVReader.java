package com.github.springerris.util.tsv;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility to read the TSV (tab-separated values) format. Currently used for the .ROOTS metadata in Archive.
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
 * reader.readHeader();
 *
 * reader.readRow(); // 3
 * reader.readValue("a"); // foo1
 * reader.readValue(1);   // bar1
 * reader.readValue("c"); // baz1
 *
 * reader.readRow(); // 3
 * reader.readValue(0);   // foo2
 * reader.readValue("b"); // bar2
 * reader.readValue(2);   // baz2
 *
 * reader.readRow(); // -1
 * }</pre>
 */
public class TSVReader extends BufferedReader {

    private final int expectedColumns;
    private List<String> header = null;
    private List<String> row = null;

    public TSVReader(Reader in, int expectedColumns) {
        super(in);
        if (expectedColumns < 0)
            throw new IllegalArgumentException("expectedColumns may not be negative");
        this.expectedColumns = expectedColumns;
    }

    //

    private List<String> readRowData() throws IOException {
        String line = this.readLine();
        if (line == null) return null;

        List<String> ret = new ArrayList<>(this.expectedColumns);

        int start = 0;
        for (int i=0; i < line.length(); i++) {
            if (line.charAt(i) != '\t') continue;
            ret.add(line.substring(start, i));
            start = i + 1;
        }
        if (start != (line.length() - 1))
            ret.add(line.substring(start));

        return ret;
    }

    public void readHeader() throws IOException {
        List<String> data = this.readRowData();
        if (data == null) throw new EOFException("Expected header, got EOF");
        this.header = data;
    }

    public int readRow() throws IOException {
        List<String> data = this.readRowData();
        this.row = data;
        return (data == null) ? -1 : data.size();
    }

    public String readValue(int index) throws IndexOutOfBoundsException, IllegalStateException {
        if (this.row == null)
            throw new IllegalStateException("Call to readValue must follow successful call to readRow");
        if (index < 0 || index >= this.row.size())
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for column count " + this.row.size());
        return this.row.get(index);
    }

    public String readValue(String key) throws IllegalArgumentException, IllegalStateException {
        if (this.header == null)
            throw new IllegalArgumentException("Cannot read value by key without reading a header first");

        if (this.row == null)
            throw new IllegalStateException("Call to readValue must follow successful call to readRow");

        int index = this.header.indexOf(key);
        if (index == -1)
            throw new IllegalArgumentException("Header does not contain key \"" + key + "\"");

        if (index >= this.row.size()) return "";
        return this.row.get(index);
    }

}
