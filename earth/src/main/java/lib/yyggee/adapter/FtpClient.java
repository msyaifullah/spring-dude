package lib.yyggee.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FtpClient {

  @Value("${lib.yyggee.sftp.host:localhost}")
  private String host;

  @Value("${lib.yyggee.sftp.username:changeMe123}")
  private String username;

  @Value("${lib.yyggee.sftp.password:changeMe123}")
  private String password;

  public FTPClient ftpClient = null;

  /** Link initialization file */
  public void initFtpClient() {
    ftpClient = new FTPClient();
    // cancel server to obtain Ip address and host their own submission match, otherwise it will
    // report an exception when inconsistent.
    ftpClient.setRemoteVerificationEnabled(false);
    // Set before connecting the coding type is utf-8
    ftpClient.setControlEncoding("utf-8");
    try {
      int port = 21;
      // set the transmission timeout time is 120 seconds
      ftpClient.setDataTimeout(1000 * 120);
      ftpClient.connect(host, port); // ftp server connection
      ftpClient.login(username, password); // Log in ftp server
      int replyCode = ftpClient.getReplyCode(); // successful login server

      if (!FTPReply.isPositiveCompletion(replyCode)) {
        log.warn("[initFtpClient]: login server failed");
      }
      log.warn(
          "[initFtpClient]: Use account:"
              + username
              + "Password:"
              + password
              + "sign in the ftp server:"
              + host
              + ":"
              + port);
      log.warn(
          "[initFtpClient]: successful login server, passive mode the host:"
              + ftpClient.getPassiveHost()
              + ":"
              + ftpClient.getPassivePort());
      log.warn(
          "[initFtpClient]: logging into the server, the active mode of the host:"
              + ftpClient.getRemoteAddress()
              + ":"
              + ftpClient.getRemotePort());
      log.warn(
          "[initFtpClient]: logging into the server localhost:"
              + ftpClient.getLocalAddress()
              + ":"
              + ftpClient.getLocalPort());
      log.warn(
          "[initFtpClient]: logging into the server, return code:"
              + ftpClient.getReplyCode()
              + ", displays the status of"
              + ftpClient.getStatus());

    } catch (MalformedURLException e) {
      log.error("Error MalformedURLException falling block", e);
    } catch (IOException e) {
      log.error("Error IOException falling block", e);
    }
  }

  /**
   * upload files
   *
   * @return @Param pathname ftp service Save Address @Param fileName to ftp upload file name @Param
   *     inputStream input file stream
   */
  public boolean uploadFile(String pathname, String fileName, InputStream inputStream) {

    try {
      log.warn("[uploadFile]:" + "Start upload files");
      initFtpClient();
      ftpClient.setFileType(
          FTP.BINARY_FILE_TYPE); // set the transmission mode to binary file transfer type
      ftpClient.makeDirectory(pathname); // Set directory
      ftpClient.changeWorkingDirectory(pathname); // set the working path

      ftpClient.enterLocalPassiveMode(); // set the passive mode (FTP client in the docker container
      // required passive mode)
      ftpClient.storeFile(fileName, inputStream); // uploaded

      log.warn("[uploadFile]:" + "upload success");
      return true;
    } catch (Exception e) {
      log.warn("[uploadFile]:" + "Failed to upload file");
      e.printStackTrace();
      return false;
    } finally {
      if (null != inputStream) {
        try {
          inputStream.close(); // Close the file stream
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (ftpClient.isConnected()) {
        try {

          ftpClient.logout(); // exit FTP
          ftpClient.disconnect(); // disconnect
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
