package com.github.springerris.gui.impl;

import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.component.VFSExplorer;
import com.github.springerris.gui.helper.BorderWindow;
import com.github.springerris.i18n.I18N;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainWindow extends BorderWindow {

    private VFSExplorer explorer;

    public MainWindow(WindowContext ctx) {
        super(ctx, I18N.WINDOW_MAIN_TITLE.get(), 800, 500);
    }

    //

    @Override
    protected int defaultCloseOperation() {
        return JFrame.EXIT_ON_CLOSE;
    }

    @Override
    protected void setupContent() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        this.setupTools(header);
        this.addElement(header, BorderLayout.PAGE_START);

        this.explorer = new VFSExplorer(this.ctx.archive().files());
        this.addElement(this.explorer, BorderLayout.CENTER);
    }

    protected void setupTools(Container header) {
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        tools.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton b1 = new JButton(I18N.WINDOW_MAIN_BUTTON_ADD.get());
        b1.setIcon(UIManager.getIcon("FileView.fileIcon"));
        b1.addActionListener(this::onClickAdd);
        tools.add(b1);

        JButton b2 = new JButton(I18N.WINDOW_MAIN_BUTTON_IMPORT.get());
        b2.setIcon(UIManager.getIcon("FileChooser.directoryIcon"));
        b2.addActionListener(this::onClickImport);
        tools.add(b2);

        JButton b3 = new JButton(I18N.WINDOW_MAIN_BUTTON_EXPORT.get());
        b3.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
        b3.addActionListener(this::onClickExport);
        tools.add(b3);

        JButton b4 = new JButton(I18N.WINDOW_MAIN_BUTTON_SYNC.get());
        b4.setIcon(UIManager.getIcon("FileChooser.hardDriveIcon"));
        b4.addActionListener(this::onClickSync);
        tools.add(b4);

        header.add(tools);
    }

    private void onClickAdd(ActionEvent ignored) {
        JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        j.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int r = j.showOpenDialog(null);
        if (r != JFileChooser.APPROVE_OPTION) return;

        Path p = j.getSelectedFile().toPath();
        if (!Files.exists(p)) {
            // Weird hack that sometimes gets us to the intended directory
            int count = p.getNameCount();
            if (count > 1 && p.getName(count - 1).equals(p.getName(count - 2)))
                p = p.getParent();
            if (!Files.exists(p)) return;
        }

        if (!this.ctx.archive().canAdd(p)) {
            showError(I18N.WINDOW_MAIN_ERROR_ADD_FILE.get());
            return;
        }

        this.ctx.archive().add(p);
        this.explorer.refresh();
    }

    private void onClickExport(ActionEvent ignored) {
        this.transfer(ExportWindow.class);
    }

    private void onClickImport(ActionEvent ignored) {
        this.transfer(ImportWindow.class);
    }

    private void onClickSync(ActionEvent ignored) {
        this.transfer(SyncWindow.class);
    }

}
