package io.github.lihewei7.easyftp.core;

import com.enterprisedt.net.ftp.FTPClient;

@FunctionalInterface
public interface FtpCallback<T> {

    T doInSftp(FTPClient ftp) throws Exception;
}
