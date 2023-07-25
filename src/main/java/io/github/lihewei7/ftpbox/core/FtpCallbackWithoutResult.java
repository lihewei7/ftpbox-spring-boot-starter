package io.github.lihewei7.ftpbox.core;

import com.enterprisedt.net.ftp.FTPClient;

@FunctionalInterface
public interface FtpCallbackWithoutResult {

  void doInSftp(FTPClient ftp) throws Exception;
}
