package com.github.springerris.gui.component;

import com.github.springerris.archive.vfs.VFS;
import com.github.springerris.archive.vfs.VFSEntity;
import com.github.springerris.i18n.I18N;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tailors VFSExplorer for picking a source/target file
 */
public class VFSPicker extends VFSExplorer {

    private final boolean write;
    private Selection selection = null;
    private boolean hideDotfiles;
    private JTextField nameField;
    private JComboBox<String> extensionsBox;

    public VFSPicker(@NotNull VFS vfs, boolean write) {
        super(vfs);
        this.write = write;
    }

    //

    public @UnknownNullability Selection selection() {
        return this.selection;
    }

    public void addActionListener(@NotNull ActionListener l) {
        this.listenerList.add(ActionListener.class, l);
    }

    //

    @Override
    protected @NotNull Component createFooter() {
        JPanel footer = new JPanel();
        footer.setLayout(new BorderLayout());

        JPanel top = new JPanel();
        top.setLayout(new BorderLayout());

        JTextField nameField = new JTextField();
        top.add(nameField, BorderLayout.CENTER);
        this.nameField = nameField;

        JButton openButton = new JButton(I18N.PICKER_OPEN.get());
        openButton.setMargin(new Insets(0, 8, 0, 8));
        top.add(openButton, BorderLayout.LINE_END);
        openButton.addActionListener(this::onClickOpenButton);

        footer.add(top, BorderLayout.PAGE_START);

        JPanel bottom = new JPanel();
        bottom.setLayout(new BorderLayout());

        JComboBox<String> extensions = new JComboBox<>(new String[] {
                I18N.FILE_TYPE_ALL.get(),
                I18N.FILE_TYPE_ZIP.get(),
                I18N.FILE_TYPE_ZIP_ENCRYPTED.get()
        });
        bottom.add(extensions, BorderLayout.CENTER);
        extensions.setSelectedIndex(1);
        extensions.addActionListener((ActionEvent ignored) -> this.refresh());
        this.extensionsBox = extensions;

        JPanel dot = new JPanel();
        dot.setLayout(new FlowLayout());

        dot.add(Box.createHorizontalStrut(2));

        dot.add(new JLabel(I18N.PICKER_HIDE_DOT_FILES.get()));

        JRadioButton dotButton = new JRadioButton();
        dotButton.setSelected(this.hideDotfiles = true);
        dotButton.addActionListener((ActionEvent ignored) -> {
            this.hideDotfiles = dotButton.isSelected();
            this.refresh();
        });
        dot.add(dotButton);

        bottom.add(dot, BorderLayout.LINE_END);

        footer.add(bottom, BorderLayout.PAGE_END);

        return footer;
    }

    @Override
    protected boolean shouldExclude(@NotNull VFSEntity entity) {
        if (this.hideDotfiles && entity.name().startsWith(".")) return true;
        if (entity.isDirectory()) return false;

        int idx = this.extensionsBox.getSelectedIndex();
        if (idx == 1) {
            return !entity.name().endsWith(".zip") && !entity.name().endsWith(".zip.m64");
        } else if (idx == 2) {
            return !entity.name().endsWith(".zip.m64");
        }
        return false;
    }

    @Override
    protected void onClickDirectory(@NotNull VFSEntity dir, boolean doubleClick) {
        if (doubleClick) {
            super.onClickDirectory(dir, true);
        } else {
            this.onClickFile(dir, false);
        }
    }

    @Override
    protected void onClickFile(@NotNull VFSEntity file, boolean doubleClick) {
        this.nameField.setText(file.name());
        this.selection = new Selection.Existing(this, file);
    }

    private void onClickOpenButton(@NotNull ActionEvent ignored) {
        String input = this.nameField.getText();
        if (this.selection == null || !this.selection.name().equals(input)) {
            if (input.isBlank()) return;
            this.resolveSelection(input);
        }

        // Try adding extensions based on file type
        if (!this.selection.isExisting() && this.selection.canBeFile() && input.indexOf('.') == -1) {
            int type = this.extensionsBox.getSelectedIndex();
            if (type != 0) {
                input += (type == 1 ? ".zip" : ".zip.m64");
                this.resolveSelection(input);
            }
        }

        // Confirm overwrite
        if (this.write && this.selection.isExisting()) {
            int res = JOptionPane.showConfirmDialog(
                    this,
                    I18N.PICKER_CONFLICT.get(),
                    I18N.POPUP_INFO.get(),
                    JOptionPane.YES_NO_OPTION
            );
            if (res != JOptionPane.YES_OPTION) return;
        }

        // Fire ActionEvent
        for (ActionListener al : this.listenerList.getListeners(ActionListener.class)) {
            al.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
        }
    }

    private void resolveSelection(@NotNull String name) {
        VFSEntity ent;
        for (int i=0; i < this.entries.size(); i++) {
            ent = this.entries.get(i);
            if (ent.name().equals(name)) {
                this.selection = new Selection.Existing(this, ent);
                return;
            }
        }
        this.selection = new Selection.New(this, name);
    }

    //

    public static sealed abstract class Selection {

        protected final VFS vfs;
        protected final String directory;
        protected Selection(@NotNull VFSPicker picker) {
            this.vfs = picker.vfs();
            this.directory = picker.getPath();
        }

        public final @NotNull VFS vfs() {
            return this.vfs;
        }

        public final @NotNull String directory() {
            return this.directory;
        }

        public abstract @NotNull String name();

        public @NotNull String path() {
            String dir = this.directory();
            String name = this.name();
            if (dir.isEmpty()) return name;
            return dir.charAt(dir.length() - 1) == '/' ?
                    dir + name :
                    dir + "/" + name;
        }

        public abstract boolean isExisting();

        public abstract boolean canBeFile();

        public abstract boolean canBeDirectory();

        //

        private static final class Existing extends Selection {

            private final VFSEntity entity;
            Existing(@NotNull VFSPicker picker, @NotNull VFSEntity entity) {
                super(picker);
                this.entity = entity;
            }

            @Override
            public @NotNull String name() {
                return this.entity.name();
            }

            @Override
            public boolean isExisting() {
                return true;
            }

            @Override
            public boolean canBeFile() {
                return this.entity.isFile();
            }

            @Override
            public boolean canBeDirectory() {
                return this.entity.isDirectory();
            }

        }

        //

        private static final class New extends Selection {

            private final String name;
            New(@NotNull VFSPicker picker, @NotNull String name) {
                super(picker);
                this.name = name;
            }

            @Override
            public @NotNull String name() {
                return this.name;
            }

            @Override
            public boolean isExisting() {
                return false;
            }

            @Override
            public boolean canBeFile() {
                return true;
            }

            @Override
            public boolean canBeDirectory() {
                return true;
            }

        }

    }

}
