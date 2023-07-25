package io.github.lihewei7.ftpbox.core;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;

public class FtpWrapper {

    private static final Log logger = LogFactory.getLog(FtpWrapper.class);

    private final FTPClient ftpClient;
//    private final FileTransferClient ftpClient;

    public FtpWrapper(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    /**
     * Download files from the ftp server to the specified local directory
     *
     * @param from Path of the remote file
     * @param to   Path after downloading the file to a local directory
     * @see FtpTemplate#download(String, String)
     */
    public void download(String from, String to) throws Exception {
        Assert.hasLength(from, "from must not be null");
        Assert.hasLength(to, "to must not be null");
        try {
            String remotePath = from.substring(0, from.lastIndexOf(File.separator) + 1),
                    localPath = to,
                    remoteFile = from.substring(from.lastIndexOf(File.separator) + 1);
            logger.info("remotePath=" + remotePath + "|remoteFile=" + remoteFile + "|localPath=" + localPath);
            localPath = localPath.endsWith("/") ? localPath : localPath + "/";
            File path = new File(localPath);
            if (!path.exists()) {
                if (path.mkdirs())
                    logger.info("localPath=" + localPath + " Directory does not exist,Created successfully! ");
            }
            this.changeDirectory(remotePath);
            ftpClient.get(localPath, remoteFile);

        } catch (Exception e) {
            throw new Exception("download error host=  " + ftpClient.getRemoteHost() + ":" + ftpClient.getRemotePort() + " !" + e.getMessage());
        }
    }

    /**
     * File upload: Upload local files to ftp
     *
     * @param from Local source file path
     * @param to   Remote path after the file is uploaded
     * @throws Exception
     * @see FtpTemplate#upload(String, String)
     */
    public void upload(String from, String to) throws Exception {
        Assert.hasLength(from, "from must not be null");
        Assert.hasLength(to, "to must not be null");
        if (!new File(from).exists()) {
            throw new Exception("upload error release the connection of " + ftpClient.getRemoteHost() + ":" + ftpClient.getRemotePort() + " !");
        }
        String dir = to.substring(0, to.lastIndexOf(File.separator) + 1);
        try {
            if (!"".equals(dir)) {
                this.mkdir(dir);
            }
        } catch (Exception e) {
            try {
                logger.info("cdAndMkdir dir:" + dir);
                this.cdAndMkdir(dir);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new Exception("ftp.chdir error dirpath:" + dir, e);
            }
        }
        ftpClient.put(from, to.substring(to.lastIndexOf(File.separator) + 1), false);
    }

    /**
     * Create and enter the path.
     *
     * @param path ftp Remote path
     * @throws Exception
     */
    public final void cdAndMkdir(String path) throws Exception {
        Assert.hasLength(path, "path must not be null");
        try {
            cd(path);
        } catch (Exception e) {
            if (path.startsWith(File.separator)) {
                cd(File.separator);
            }
            String[] dirs = path.split(File.separator);
            for (String dir : dirs) {
                if ("".equals(dir)) {
                    continue;
                } else {
                    ftpClient.mkdir(dir);
                }
                cd(dir);
            }
        }
    }

    public void cd(String path) throws Exception {
        try {
            ftpClient.chdir(path);
        } catch (IOException | FTPException e) {
            throw new Exception("failed to change remote directory " + path + "'." + e.getMessage(), e.getCause());
        }
    }

    private void changeDirectory(String path) throws Exception {
        //注意ftp当前的目录大部分情况下都不是根目录，因此最好调整目录
        ftpClient.chdir("/");
        //对要创建的目录进行解析
        path = path.substring(0, path.lastIndexOf("/"));
        String[] tempDir = path.split("/");

        try {
            for (int i = 0; i < tempDir.length; i++) {
                //注意Split函数会对"/webjoin/lihw/"拆出第一个String为空串，这里过滤掉
                if (tempDir[i].length() > 0) {
                    try {
                        ftpClient.chdir(tempDir[i]);
                    } catch (Exception e) {
                        logger.error(String.format("dir=%s 切换目录失败，重试一次", tempDir[i]));
                        ftpClient.chdir(tempDir[i]);
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("changeDirectory error for dir '" + path + "'." + e.getMessage(), e.getCause());
        }
    }

    /**
     * Create Routes
     *
     * @param path the remote path.
     * @throws IOException, FTPException
     */
    public void mkdir(String path) throws Exception {
        //注意ftp当前的目录大部分情况下都不是根目录，因此最好调整目录
        ftpClient.chdir("/");
        //对要创建的目录进行解析
        path = path.substring(0, path.lastIndexOf("/"));
        String[] tempDir = path.split("/");

        try {
            for (int i = 0; i < tempDir.length; i++) {
                //注意Split函数会对"/webjoin/lihw/"拆出第一个String为空串，这里过滤掉
                if (tempDir[i].length() > 0) {
                    try {
                        ftpClient.chdir(tempDir[i]);
                    } catch (Exception e) {
                        ftpClient.mkdir(tempDir[i]);
                        ftpClient.chdir(tempDir[i]);
                    }
                }
            }
        } catch (Throwable e) {
            throw new Exception("Create Routes error for dir '" + path + "'." + e.getMessage(), e.getCause());
        }
    }

    /**
     * Check whether the path exists
     *
     * @param path Address of the path to be verified
     * @return Destination path existence：true/false
     * @throws Exception
     * @see FtpTemplate#exists(String)
     */
    public boolean exists(String path) throws Exception {
        try {
            this.ftpClient.existsDirectory(path);
            return true;
        } catch (IOException | FTPException e) {
            throw new Exception("cannot check status for path '" + path + "'." + e.getMessage(), e.getCause());
        }
    }

    /**
     * @see FtpTemplate#list(String)
     */
    public String list(String path) throws Exception {
        Assert.hasLength(path, "path must not be null");
        try {
            return ftpClient.list(path);
        } catch (Exception e) {
            throw new Exception("list Directory error for path '" + path + "'." + e.getMessage(), e.getCause());
        }
    }
}
