package com.github.springerris.util;

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

public class SSHHandler {
    private String user;
    private String pwd;
    private String host;
    private SSHClient client;

    public SSHHandler(String user, String pwd, String remoteHost, int port)  {

        this.client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        try {
            client.connect(remoteHost, port);
        }
        catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(new JFrame(), "Не удалось подключится к удалённому хосту", "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }
        try {
            client.authPassword(user, pwd);
        } catch (UserAuthException | TransportException e) {
            JOptionPane.showMessageDialog(new JFrame(), "Логин и пароль не подошли под данный хост", "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);

        }
        JOptionPane.showMessageDialog(new JFrame(), "Подключение УСПЕШНО!!!", "Информация",
                JOptionPane.INFORMATION_MESSAGE);

        try {
            SFTPClient sftpClient = client.newSFTPClient();
            List<RemoteResourceInfo> files = sftpClient.ls("./");
            for (RemoteResourceInfo f : files) {
                JOptionPane.showMessageDialog(new JFrame(), f.getPath(), "Информация",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


