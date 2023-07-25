package io.github.lihewei7.easyftp.core;

import com.enterprisedt.net.ftp.FTPClient;

@FunctionalInterface
public interface FtpCallbackWithoutResult {

  void doInSftp(FTPClient ftp) throws Exception;
}
