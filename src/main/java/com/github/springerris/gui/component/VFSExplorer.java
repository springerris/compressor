package com.github.springerris.gui.component;

import com.github.springerris.archive.vfs.VFS;
import com.github.springerris.archive.vfs.VFSEntity;
import com.github.springerris.util.Listeners;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;

public class VFSExplorer extends JPanel {

    private final VFS vfs;
    private String path;
    private final JTextField pathField;
    private final DefaultListModel<VFSEntity> entries;
    private final VFSEntityList entriesList;

    public VFSExplorer(VFS vfs) {
        this.vfs = vfs;
        this.path = this.findDeepestRichPath();
        this.setLayout(new BorderLayout());

        // Create header
        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());

        JButton up = new JButton("..");
        up.setIcon(UIManager.getIcon("FileChooser.upFolderIcon"));
        up.addActionListener(this::onClickUp);
        header.add(up, BorderLayout.LINE_START);

        JTextField field = new JTextField(this.path);
        field.addKeyListener(Listeners.keyReleased(this::onUpdatePath));
        header.add(field, BorderLayout.CENTER);
        this.pathField = field;

        this.add(header, BorderLayout.PAGE_START);

        // Create list
        this.entries = new DefaultListModel<>();
        this.entriesList = new VFSEntityList(this.entries);
        this.entriesList.addMouseListener(Listeners.mouseClicked(this::onClickEntry));
        this.add(new JScrollPane(this.entriesList), BorderLayout.CENTER);

        // Initial populate
        this.refresh0();
    }

    //

    /** Gets the active path of the explorer */
    public String getPath() {
        return this.path;
    }

    /** Sets the active path of the explorer */
    public void setPath(String path) {
        if (path == null) path = "";
        if (this.path.equals(path)) return;
        this.path = path;
        this.pathField.setText(path);
        this.refresh0();
    }

    /** Indicates that the VFS has been updated externally and the explorer should reflect any changes */
    public void refresh() {
        if (this.path.isEmpty()) {
            this.pathField.setText(this.path = this.findDeepestRichPath());
        }
        this.refresh0();
    }

    private void refresh0() {
        VFS root = this.vfs;
        if (!this.path.isEmpty()) root = root.sub(this.path);

        this.entries.clear();

        VFSEntity[] list;
        try {
            list = root.list();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Arrays.sort(list);

        for (VFSEntity ent : list) {
            this.entries.addElement(ent);
        }
    }

    //

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
        if (!this.vfs.exists(desired)) return;
        this.setPath(desired);
    }

    private void onClickEntry(MouseEvent e) {
        boolean doubleClick = (e.getClickCount() >= 2);

        VFSEntity selected = this.entriesList.getSelectedValue();
        if (selected == null) return;

        if (selected.isDirectory()) {
            this.onClickDirectory(selected, doubleClick);
        } else if (selected.isFile()) {
            this.onClickFile(selected, doubleClick);
        }
    }

    protected void onClickDirectory(VFSEntity dir, boolean doubleClick) {
        if (!doubleClick) return;

        String newPath = this.path;
        if (newPath.isEmpty()) {
            newPath = dir.name();
        } else if (newPath.charAt(newPath.length() - 1) == '/') {
            newPath += dir.name();
        } else {
            newPath += "/" + dir.name();
        }

        if (!this.vfs.exists(newPath)) return;
        this.setPath(newPath);
    }

    protected void onClickFile(VFSEntity file, boolean doubleClick) { }

    //

    private String findDeepestRichPath() {
        StringBuilder sb = new StringBuilder();
        this.findDeepestRichPath0(sb, this.vfs);
        return sb.toString();
    }

    private void findDeepestRichPath0(StringBuilder sb, VFS head) {
        VFSEntity[] ents;
        try {
            ents = head.list();
        } catch (IOException e) {
            return;
        }

        if (ents.length != 1) return;

        VFSEntity ent = ents[0];
        if (!ent.isDirectory()) return;

        if (!sb.isEmpty()) sb.append('/');
        sb.append(ent.name());
        this.findDeepestRichPath0(sb, head.sub(ent.name()));
    }

}
