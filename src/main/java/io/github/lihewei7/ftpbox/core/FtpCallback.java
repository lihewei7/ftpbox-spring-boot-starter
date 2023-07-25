package io.github.lihewei7.ftpbox.core;

import com.enterprisedt.net.ftp.FTPClient;

@FunctionalInterface
public interface FtpCallback<T> {

    T doInFtp(FTPClient ftp) throws Exception;
}
