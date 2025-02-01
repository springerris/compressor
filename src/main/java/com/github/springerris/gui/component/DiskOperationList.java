package com.github.springerris.gui.component;

import com.github.springerris.op.DiskOperation;

import javax.swing.*;
import java.awt.*;

public class DiskOperationList extends JList<DiskOperation> {

    public DiskOperationList(ListModel<DiskOperation> dataModel) {
        super(dataModel);
        this.setCellRenderer(new CellRenderer());
    }

    //

    private static final class CellRenderer implements ListCellRenderer<DiskOperation> {

        private static final ListCellRenderer<Object> DEFAULT = new DefaultListCellRenderer();

        @Override
        public Component getListCellRendererComponent(
                JList<? extends DiskOperation> jList,
                DiskOperation op,
                int i,
                boolean b,
                boolean b1
        ) {
            Component c = DEFAULT.getListCellRendererComponent(
                    jList,
                    op.description(),
                    i, b, b1
            );
            c.setForeground(this.getColor(op.type()));
            if (c instanceof JLabel jl) {
                jl.setIcon(UIManager.getIcon("Menu.arrowIcon"));
            }
            return c;
        }

        private Color getColor(DiskOperation.Type type) {
            return switch (type) {
                case CREATE -> new Color(0x368C33);
                case MODIFY -> new Color(0x33628C);
                case DELETE -> new Color(0xA92E22);
            };
        }

    }

}
