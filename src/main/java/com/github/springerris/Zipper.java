package com.github.springerris;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {
    String filename;
    List<File> files;
    FileOutputStream stream;
    ZipOutputStream zipStream;

    Zipper(String filename) throws FileNotFoundException {
        this.filename = filename;
        this.files = new ArrayList<File>();
        stream = new FileOutputStream(filename);
        zipStream = new ZipOutputStream(stream);
    }

    public void traverseDir(String parentName,File dir) throws IOException {
        traverseDir(parentName,dir, false);
    }

    public void traverseDir(File dir) throws IOException {
        traverseDir("",dir, false);
    }

    public void traverseDir(String parentName,File dir, boolean isRoot) throws IOException {
        System.out.println(dir.getName());
        System.out.println(dir.isDirectory());
        if (dir.isDirectory()) {

            System.out.println(" Searching in " + parentName + "/" + dir.getName());
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (isRoot) {
                        this.files.add(f);
                    }
                    System.out.println("FOUND FILE: " + f.getName());
                    String path = "";
                    if (isRoot) {
                        path = dir.getName() + "/"  + f.getName();
                    }
                    else {
                        path = parentName + "/" + dir.getName() + "/"  + f.getName();
                    }
                    System.out.println("PATH IS:" + path);

                    if (f.isDirectory()) {
                        System.out.println("FOUND DIR: " + f.getName());
                        if (isRoot) {
                            traverseDir(dir.getName(), f);
                        }
                        else {
                            traverseDir(parentName + "/" + dir.getName(), f);
                        }

                    }
                    if (!f.isDirectory()) {
                        System.out.println("CREATING ENTRY AT:" + path);
                        zipStream.putNextEntry(new ZipEntry(path));
                        FileInputStream fis = new FileInputStream(f);
                        byte[] bytes = new byte[1024];
                        int length;
                        while ((length = fis.read(bytes)) >= 0) {
                            zipStream.write(bytes, 0, length);
                        }

                        fis.close();
                        zipStream.closeEntry();
                    }
                }
            }

        }
    }
}
