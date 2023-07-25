package io.github.lihewei7.easyftp.core;

import org.springframework.util.Assert;

/**
 * @author: lihewei
 */
public final class FtpTemplate {

    private final FtpPool ftpPool;

    public FtpTemplate(FtpPool ftpPool) {
        this.ftpPool = ftpPool;
    }

    /**
     * ftp template methods with return values include basic FTP operations,
     * such as obtaining and returning connections.
     */
    public <T> T execute(FtpCallback<T> action) throws Exception {
        Assert.notNull(action, "Callback object must not be null");
        String hostName = ftpPool.isUniqueHost() ? null : HostsManage.getHostName();
        FtpClient ftpClient = null;
        try {
            ftpClient = ftpPool.borrowObject(hostName);
            return action.doInSftp(ftpClient.getFtp());
        }finally {
            HostsManage.clear();
            if (ftpClient != null) {
                if (ftpClient.reset()) {
                    ftpPool.returnObject(hostName, ftpClient);
                } else {
                    ftpPool.invalidateObject(hostName, ftpClient);
                }
            }
        }
    }

    /**
     * The ftp template method with no return value includes basic FTP operations,
     * such as obtaining and returning connections.
     */
    public void executeWithoutResult(FtpCallbackWithoutResult action) throws Exception {
        Assert.notNull(action, "Callback object must not be null");
        this.execute(ftpClient -> {
            action.doInSftp(ftpClient);
            return null;
        });
    }

    public void download(String from, String to) throws Exception {
        this.executeWithoutResult(ftpClient -> new FtpWrapper(ftpClient).download(from, to));
    }

    public void upload(String from, String to) throws Exception {
        this.executeWithoutResult(ftpClient -> new FtpWrapper(ftpClient).upload(from, to));
    }

    public boolean exists(String path) throws Exception {
        return this.execute(ftpClient -> new FtpWrapper(ftpClient).exists(path));
    }

    public String list(String path) throws Exception {
        return this.execute(channelSftp -> new FtpWrapper(channelSftp).list(path));
    }
}
