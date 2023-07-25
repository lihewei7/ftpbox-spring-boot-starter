package io.github.lihewei7.ftpbox.core;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPTransferType;
import io.github.lihewei7.ftpbox.config.FtpProperties;

import java.io.IOException;

/**
 * @explain: FTP Client.
 * @author: lihewei
 */
public class FtpClient {

    private String originalDir;
    private FTPClient ftp;

    public FTPClient getFtp() {
        return ftp;
    }

    public FtpClient(FtpProperties ftpProperties) {
        try {
            ftp = new FTPClient();
            if (!ftp.connected()) {
                ftp.setRemoteHost(ftpProperties.getHost());
                ftp.setRemotePort(ftpProperties.getPort());
                ftp.setTimeout(10000);
                ftp.setControlEncoding("GBK");
                ftp.connect();
                ftp.login(ftpProperties.getUsername(), ftpProperties.getPassword());
                ftp.setType(FTPTransferType.BINARY);
                originalDir = ftp.pwd();
            }
        } catch (Exception e) {
            disconnect();
            throw new IllegalStateException("failed to create ftp Client", e);
        }
    }

    /**
     * ftp disconnect.
     */
    public synchronized void disconnect() {
        if (ftp.connected()) {
            try {
                ftp.quit();
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalStateException("failed to disconnect ftp", e);
            }
        }
    }


    /**
     * todo test connection. ???
     */
    protected boolean test() {
        try {
            if (ftp.connected() && originalDir.equals(ftp.pwd())) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * Reset the connection and restore the initial ftp path.
     */
    protected boolean reset() {
        try {
//            channelSftp.cd(originalDir);
            ftp.chdir("/");
            //对要创建的目录进行解析
            originalDir = originalDir.substring(0, originalDir.lastIndexOf("/"));
            String[] tempDir = originalDir.split("/");

            for (int i = 0; i < tempDir.length; i++) {
                //注意Split函数会对"/webjoin/lihw/"拆出第一个String为空串，这里过滤掉
                if (tempDir[i].length() > 0) {
                    ftp.chdir(tempDir[i]);
                }
            }
            return true;
        } catch (IOException | FTPException e) {
            return false;
        }
    }
}
