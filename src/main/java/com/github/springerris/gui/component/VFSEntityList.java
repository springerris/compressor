package com.github.springerris.gui.component;

import com.github.springerris.archive.vfs.VFSEntity;

import javax.swing.*;
import java.awt.*;

public class VFSEntityList extends JList<VFSEntity> {

    public VFSEntityList(ListModel<VFSEntity> dataModel) {
        super(dataModel);
        this.setCellRenderer(new CellRenderer());
    }

    //

    private static final class CellRenderer implements ListCellRenderer<VFSEntity> {

        private static final ListCellRenderer<Object> DEFAULT = new DefaultListCellRenderer();

        @Override
        public Component getListCellRendererComponent(
                JList<? extends VFSEntity> jList,
                VFSEntity ent,
                int i,
                boolean b,
                boolean b1
        ) {
            Component c = DEFAULT.getListCellRendererComponent(
                    jList,
                    ent.name(),
                    i, b, b1
            );
            if (c instanceof JLabel jl) {
                jl.setIcon(UIManager.getIcon(
                        ent.isDirectory() ?
                                "FileChooser.directoryIcon" :
                                "FileChooser.fileIcon"
                ));
            }
            return c;
        }

    }

}
