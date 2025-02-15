package com.github.springerris.util;

import com.github.springerris.i18n.I18N;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.RemoteResource;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.UserAuthException;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// TODO: Class has been cleaned up, but is still SUBOPTIMAL. This will get its own commit. Go to your room young man!
public class SSHHandler {

    private final SSHClient client;

    public SSHHandler(String user, String password, String remoteHost, int port) {
        this.client = new SSHClient();
        this.client.addHostKeyVerifier(new PromiscuousVerifier());
        try {
            this.client.connect(remoteHost, port);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Не удалось подключится к удалённому хосту",
                    I18N.POPUP_ERROR.get(),
                    JOptionPane.ERROR_MESSAGE
            );
            throw new RuntimeException(e);
        }
        try {
            this.client.authPassword(user, password);
        } catch (UserAuthException | TransportException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Логин и пароль не подошли под данный хост",
                    I18N.POPUP_ERROR.get(),
                    JOptionPane.ERROR_MESSAGE
            );
            throw new RuntimeException(e);
        }
        JOptionPane.showMessageDialog(
                null,
                "Подключение УСПЕШНО!!!",
                I18N.POPUP_INFO.get(),
                JOptionPane.INFORMATION_MESSAGE
        );
        try {
            SFTPClient sftpClient = this.client.newSFTPClient();
            List<RemoteResourceInfo> files = sftpClient.ls("./");
            for (RemoteResourceInfo f : files) {
                JOptionPane.showMessageDialog(
                        null,
                        f.getPath(),
                        I18N.POPUP_INFO.get(),
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}


