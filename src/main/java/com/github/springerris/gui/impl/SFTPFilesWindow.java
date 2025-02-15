package com.github.springerris.gui.impl;

import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.helper.BorderWindow;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class SFTPFilesWindow extends BorderWindow {

    private boolean isDownloading = false;
    private DefaultListModel<String> list;

    public SFTPFilesWindow(@NotNull WindowContext ctx) {
        // TODO: Decide title & dimensions
        super(ctx, "Title", 500, 500);
    }

    @Override
    protected void setupContent() {
        // This code was copied from the YanFilesWindow class, as they have similar functions. However, it has to be
        // commented out as it references symbols that only makes sense for Yandex.
        /*
        list = new DefaultListModel<>();
        JList<String> fileList = new JList<>(list);
        ScrollPane sp = new ScrollPane();
        sp.add(fileList);
        this.addElement(sp, BorderLayout.CENTER);

        fileList.addListSelectionListener(arg0 -> {
            if (!arg0.getValueIsAdjusting()) {
                System.out.println(list.get(fileList.getSelectedIndex()));
                zipNode = yanFiles.get(fileList.getSelectedIndex());
                JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


                int r = jfc.showOpenDialog(null);
                if (r != JFileChooser.APPROVE_OPTION) return;
                File dir = jfc.getSelectedFile();
                this.setVisible(false);
                System.out.println(dir);
                System.out.println(Paths.get(dir.toString(),this.zipNode.name()).toString());
                File newZip = new File(Paths.get(dir.toString(),this.zipNode.name()).toString());
                try {
                    yh.download(zipNode,newZip);
                    ctx.loadArchive(newZip.toPath(),this::passwordPrompt);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        */
    }

}
