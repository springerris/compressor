package com.github.springerris;

import io.github.wasabithumb.yandisk4j.YanDisk;
import io.github.wasabithumb.yandisk4j.auth.AuthHandler;
import io.github.wasabithumb.yandisk4j.auth.AuthResponse;
import io.github.wasabithumb.yandisk4j.auth.AuthScheme;
import io.github.wasabithumb.yandisk4j.auth.scope.AuthScope;
import io.github.wasabithumb.yandisk4j.node.accessor.NodeUploader;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

public class YanHandler {
    private String token = "";
    public WindowContext ctx;
    public Window wd;
    YanHandler(WindowContext ctx, Window wd) {
        this.ctx = ctx;
        this.wd = wd;
    }

    public synchronized String getToken() {
        if (!token.isBlank()) return token;

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
            System.out.println("OAuth token: " + response.accessToken());
            return response.accessToken();

    }

    public void upload() {

    YanDisk yd = YanDisk.yanDisk(token);
    String zipName = JOptionPane.showInputDialog("Введите название файла для архива");
            if (!zipName.isBlank()) {
        String password = "";
        int isProtected = JOptionPane.showConfirmDialog(this.wd,"Добавить пароль для доступа к архиву?", "Выбор пароля",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if(isProtected == JOptionPane.YES_OPTION){
            while(password.isBlank())  {
                password = JOptionPane.showInputDialog("Введите пароль для архива:");
            }
        }
        NodeUploader nu = yd.upload("disk:/" + zipName + ".zip");
        try (OutputStream ostr = nu.open();
             ZipOutputStream zos = new ZipOutputStream(ostr)
        ) {
            if (!password.isBlank()) {
                System.out.println("OOPS! no encryption");
            }
            ctx.zipper().write(zos);
        } catch (IOException ex) {
            this.wd.showError("Ошибка работы с сервисом Yandex Disk. Проверьте: \n 1) Есть ли у вас доступ к интернету \n 2) Доступен ли сервис Yandex на данный момент");
            throw new RuntimeException(ex);

        }
    } else {
        // something will go here
    }
    }
}
