package com.github.springerris.gui.impl;

import com.github.springerris.archive.vfs.VFS;
import com.github.springerris.archive.vfs.VFSEntity;
import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.component.VFSEntityList;
import com.github.springerris.gui.helper.BorderWindow;
import com.github.springerris.i18n.I18N;
import com.github.springerris.util.Listeners;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Level;

public class MainWindow extends BorderWindow {

    private String path;
    private JTextField pathField;
    private DefaultListModel<VFSEntity> entries;
    private VFSEntityList entriesList;

    public MainWindow(WindowContext ctx) {
        super(ctx, I18N.WINDOW_MAIN_TITLE.get(), 800, 500);
    }

    //

    private void onClickEntry(MouseEvent e) {
        if (e.getClickCount() < 2) return;

        VFSEntity selected = this.entriesList.getSelectedValue();
        if (selected == null) return;
        if (!selected.isDirectory()) return;

        String newPath = this.path;
        if (newPath.isEmpty()) {
            newPath = selected.name();
        } else if (newPath.charAt(newPath.length() - 1) == '/') {
            newPath += selected.name();
        } else {
            newPath += "/" + selected.name();
        }
        if (!this.ctx.archive().files().exists(newPath)) return;

        this.setPath(newPath);
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
        this.updateEntries();
    }

    private void onClickUp(ActionEvent ignored) {
        int len = this.path.length();
        if (len < 2) {
            this.setPath("");
            return;
        }

        int whereSlash = this.path.lastIndexOf('/');
        if (whereSlash == (len - 1)) whereSlash = this.path.lastIndexOf('/', len - 2);
        if (whereSlash == -1) {
            this.setPath("");
            return;
        }

        this.setPath(this.path.substring(0, whereSlash));
    }

    private void onUpdatePath(KeyEvent ignored) {
        String desired = this.pathField.getText();
        if (desired.isEmpty()) {
            this.setPath("");
            return;
        }
        if (!this.ctx.archive().files().exists(desired)) return;
        this.setPath(desired);
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

    //

    private void setPath(String path) {
        this.path = path;
        this.pathField.setText(path);
        this.updateEntries();
    }

    private void updateEntries() {
        VFS root = this.ctx.archive().files();
        if (!this.path.isEmpty()) root = root.sub(this.path);

        this.entries.clear();

        VFSEntity[] list;
        try {
            list = root.list();
        } catch (IOException e) {
            this.ctx.logger().log(Level.SEVERE, "Unexpected IO exception", e);
            return;
        }
        Arrays.sort(list);

        for (VFSEntity ent : list) {
            this.entries.addElement(ent);
        }
    }

    //

    @Override
    protected void setupContent() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        this.setupTools(header);
        this.setupBanner(header);
        this.addElement(header, BorderLayout.PAGE_START);

        DefaultListModel<VFSEntity> entriesModel = new DefaultListModel<>();
        VFSEntityList entriesList = new VFSEntityList(entriesModel);
        entriesList.addMouseListener(Listeners.mouseClicked(this::onClickEntry));
        JScrollPane entriesPane = new JScrollPane(entriesList);

        this.addElement(entriesPane);

        this.path = "";
        this.entries = entriesModel;
        this.entriesList = entriesList;
        this.updateEntries();
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

    protected void setupBanner(Container header) {
        JPanel banner = new JPanel();
        banner.setLayout(new BorderLayout());

        JButton up = new JButton("..");
        up.setIcon(UIManager.getIcon("FileChooser.upFolderIcon"));
        up.addActionListener(this::onClickUp);
        banner.add(up, BorderLayout.LINE_START);

        JTextField field = new JTextField("");
        banner.add(field, BorderLayout.CENTER);
        field.addKeyListener(Listeners.keyReleased(this::onUpdatePath));
        this.pathField = field;

        header.add(banner);
    }

}
