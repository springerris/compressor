package com.github.springerris.gui.component;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

public class PathList extends JList<Path> {

    public PathList(ListModel<Path> dataModel) {
        super(dataModel);
        this.setCellRenderer(new CellRenderer());
    }

    //

    private static final class CellRenderer implements ListCellRenderer<Path> {

        private static final ListCellRenderer<Object> DEFAULT = new DefaultListCellRenderer();

        @Override
        public Component getListCellRendererComponent(JList<? extends Path> jList, Path path, int i, boolean b, boolean b1) {
            return DEFAULT.getListCellRendererComponent(
                    jList,
                    path.getFileName().toString(),
                    i, b, b1
            );
        }

    }

}
