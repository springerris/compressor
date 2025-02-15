package com.github.springerris.gui.component;

import com.github.springerris.op.DiskOperation;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * A {@link JList} specialized to view {@link DiskOperation} objects.
 * This differs from the default implementation in that the label will be colored correctly
 * to reflect it's {@link DiskOperation.Type type}, and an arrow icon will be added.
 */
public class DiskOperationList extends JList<DiskOperation> {

    public DiskOperationList(@NotNull ListModel<DiskOperation> dataModel) {
        super(dataModel);
        this.setCellRenderer(new CellRenderer());
    }

    //

    private static final class CellRenderer implements ListCellRenderer<DiskOperation> {

        private static final ListCellRenderer<Object> DEFAULT = new DefaultListCellRenderer();

        @Override
        public @NotNull Component getListCellRendererComponent(
                @NotNull JList<? extends DiskOperation> jList,
                @NotNull DiskOperation op,
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

        private @NotNull Color getColor(@NotNull DiskOperation.Type type) {
            return switch (type) {
                case CREATE -> new Color(0x368C33);
                case MODIFY -> new Color(0x33628C);
                case DELETE -> new Color(0xA92E22);
            };
        }

    }

}
