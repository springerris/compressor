package com.github.springerris.gui.impl;

import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.component.DiskOperationList;
import com.github.springerris.gui.helper.BorderWindow;
import com.github.springerris.i18n.I18N;
import com.github.springerris.op.DiskOperation;
import com.github.springerris.op.DiskOperationQueue;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class SyncWindow extends BorderWindow {

    private DiskOperationQueue operations;

    public SyncWindow(@NotNull WindowContext ctx) {
        super(ctx, I18N.WINDOW_SYNC_TITLE.get(), 640, 480);
    }

    //

    @Override
    protected void setupContent() {
        try {
            this.operations = this.ctx.archive().extract();
        } catch (IOException e) {
            this.ctx.logger().log(Level.SEVERE, "Failed to setup extraction", e);
            System.exit(1);
        }

        DefaultListModel<DiskOperation> opModel = new DefaultListModel<>();
        opModel.addAll(this.operations);

        DiskOperationList opList = new DiskOperationList(opModel);
        JScrollPane opPane = new JScrollPane(opList);

        this.addElement(opPane, BorderLayout.CENTER);

        JPanel options = new JPanel();
        options.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton btn1 = new JButton(I18N.WINDOW_SYNC_CONFIRM.get());
        btn1.addActionListener(this::onClickConfirm);
        options.add(btn1);

        JButton btn2 = new JButton(I18N.WINDOW_SYNC_CANCEL.get());
        btn2.addActionListener(this::onClickCancel);
        options.add(btn2);

        this.addElement(options, BorderLayout.PAGE_END);
    }

    //

    private void onClickConfirm(ActionEvent ignored) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        Thread t = new Thread(() -> this.doExtract(future));
        t.setName("Extractor Thread");
        t.start();

        this.ctx.setActiveTask(future);
        this.transfer(AwaitingWindow.class);
    }

    private void onClickCancel(ActionEvent ignored) {
        this.transfer(MainWindow.class);
    }

    //

    private void doExtract(@NotNull CompletableFuture<Boolean> future) {
        boolean result = this.operations.execute(this.ctx.logger());
        if (!result) {
            this.showError(I18N.WINDOW_SYNC_ERROR);
        }
        future.complete(result);
    }

}
