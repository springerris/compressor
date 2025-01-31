package com.github.springerris.util;

import com.github.springerris.gui.Window;
import com.github.springerris.gui.WindowContext;
import com.github.springerris.i18n.I18N;
import io.github.wasabithumb.yandisk4j.YanDisk;
import io.github.wasabithumb.yandisk4j.auth.AuthHandler;
import io.github.wasabithumb.yandisk4j.auth.AuthResponse;
import io.github.wasabithumb.yandisk4j.auth.AuthScheme;
import io.github.wasabithumb.yandisk4j.auth.scope.AuthScope;
import io.github.wasabithumb.yandisk4j.node.accessor.NodeUploader;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;

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

        // for SCREEN_CODE
        // String code = JOptionPane.showInputDialog("ENTER CODE");
        String code = auth.awaitCode().code();

        AuthResponse response = auth.exchange(code);
        return this.token = response.accessToken();

    }

    public void upload() {
        YanDisk yd = YanDisk.yanDisk(this.getToken());
        String zipName = JOptionPane.showInputDialog(I18N.SEND_YANDEX_PICK_NAME.get());
        if (zipName.isBlank()) {
            // TODO: something will go here
            return;
        }

        String password = "";
        int isProtected = JOptionPane.showConfirmDialog(this.window,
                I18N.STAGE_PASSWORD_PROMPT_CONFIRM.get(),
                I18N.STAGE_PASSWORD_PROMPT_TITLE.get(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (isProtected == JOptionPane.YES_OPTION) {
            while (password.isBlank()) {
                password = JOptionPane.showInputDialog(I18N.STAGE_PASSWORD_PROMPT_ENTER.get());
            }
        }

        NodeUploader nu = yd.upload("disk:/" + zipName + ".zip");
        try (OutputStream os = nu.open()) {
            if (!password.isBlank()) {
                System.out.println("OOPS! no encryption");
            }
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

}
