package com.github.springerris.util;

import com.github.springerris.gui.Window;
import com.github.springerris.gui.WindowContext;
import com.github.springerris.i18n.I18N;
import io.github.wasabithumb.yandisk4j.YanDisk;
import io.github.wasabithumb.yandisk4j.auth.AuthHandler;
import io.github.wasabithumb.yandisk4j.auth.AuthResponse;
import io.github.wasabithumb.yandisk4j.auth.AuthScheme;
import io.github.wasabithumb.yandisk4j.auth.scope.AuthScope;
import io.github.wasabithumb.yandisk4j.node.Node;
import io.github.wasabithumb.yandisk4j.node.accessor.NodeDownloader;
import io.github.wasabithumb.yandisk4j.node.accessor.NodeUploader;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class YanHandler {

    private String token = "";
    public WindowContext ctx;
    public Window window;

    public YanHandler(WindowContext ctx, Window window) {
        this.ctx = ctx;
        this.window = window;
    }

    public synchronized String getToken() {
        if (!this.token.isBlank()) return this.token;

        final AuthHandler auth = YanDisk.auth(AuthScheme.LOCAL_CODE)
                .clientID("6b7a4a728a624228b2d93abe697ef726")
                .clientSecret("0fd69c030af349c4bec206126fd5b01d")
                .redirectURI("http://127.0.0.1:8015/")
                .scopes(AuthScope.INFO, AuthScope.READ, AuthScope.WRITE)
                .build();

        auth.openURL();

        String code = auth.awaitCode().code();
        AuthResponse response = auth.exchange(code);
        return this.token = response.accessToken();
    }

    public void upload(String password) {
        YanDisk yd = YanDisk.yanDisk(this.getToken());
        String zipName = JOptionPane.showInputDialog(I18N.SEND_PICK_NAME.get());
        if (zipName == null || zipName.isBlank()) {
            // TODO: something will go here
            return;
        }

        yd.mkdir("disk:/.archives", true);

        String ext = (password == null) ? ".zip" : ".zip.m64";
        NodeUploader nu = yd.upload("disk:/.archives/" + zipName + ext);
        try (OutputStream os = nu.open()) {
            this.ctx.archive().write(os, password);
        } catch (IOException ex) {
            this.ctx.logger().log(Level.SEVERE, "Ошибка работы с сервисом Yandex Disk", ex);
            this.window.showError("""
                    Ошибка работы с сервисом Yandex Disk. Проверьте: \
                    1) Есть ли у вас доступ к интернету              \
                    2) Доступен ли сервис Yandex на данный момент"""
            );
            System.exit(1);
        }
    }

    // TODO: the rest
    public void download(Node n, File f) throws IOException {
        YanDisk yd = YanDisk.yanDisk(this.getToken());
        NodeDownloader nd = yd.download(n.path());
        if (f.exists()) {
            int result = JOptionPane.showConfirmDialog(
                    null,
                    I18N.RECEIVE_CONFLICT.get(),
                    I18N.POPUP_INFO.get(),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result != JOptionPane.YES_OPTION) return;
        }
        Files.copy(nd.open(), f.toPath(), REPLACE_EXISTING);
    }

    public List<Node> listFiles() {
        YanDisk yd = YanDisk.yanDisk(this.getToken());
        yd.mkdir("disk:/.archives", true);
        return yd.list("disk:/.archives", 100, 0);
    }

}
