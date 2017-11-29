package com.suxintec.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;

public class SFTPUtil {

	/**
	 * connect server via sftp
	 */
	public static ChannelSftp connect(String keyPath, String host, int port,
			String username, String password) {
		try {
			String keyFile = SFTPUtil.class.getClassLoader()
					.getResource(keyPath).getPath();
			//keyFile = "d:/id_rsa_crpl_weixin.bat";
			JSch jsch = new JSch();
			jsch.addIdentity(keyFile);

			Session session = jsch.getSession(username, host, port);

			UserInfo ui = new MyUserInfo(password);
			session.setUserInfo(ui);
			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();
			return (ChannelSftp) channel;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void disconnect(ChannelSftp sftp) {
		if (sftp != null) {
			if (sftp.isConnected()) {
				sftp.disconnect();
			}
		}
	}

	/**
	 * 下载文件
	 * @param sftp
	 *            SFTP连接
	 * @param directory
	 *            下载目录
	 * @param downloadFile
	 *            下载的文件名
	 * @return
	 * @throws SftpException
	 * @throws IOException
	 * @throws Exception
	 */
	public static InputStream download(ChannelSftp sftp,String directory, String downloadFile)
			throws SftpException, IOException {
		if (directory != null && !"".equals(directory)) {
			sftp.cd(directory);
		}
		InputStream is = sftp.get(downloadFile);
		// byte[] fileData = IOUtils.toByteArray(is);
		return is;
	}


	/**
	 * 将输入流的数据上传到sftp作为文件
	 * 
	 * @param sftp
	 *            SFTP连接
	 * @param directory
	 *            上传到该目录
	 * @param sftpFileName
	 *            sftp端文件名
	 * @param input
	 *            输入流
	 * @throws SftpException
	 * @throws Exception
	 */
	public static boolean upload(ChannelSftp sftp, String directory,
			String sftpFileName, InputStream input) throws SftpException {
		try {
			sftp.cd(directory);
		} catch (SftpException e) {
			createDir(directory, sftp);
			sftp.cd(directory);
		}
		sftp.put(input, sftpFileName);
		return true;
	}


	public static void createDir(String createpath, ChannelSftp sftp) {
		try {
			if (isDirExist(createpath,sftp)) {
				sftp.cd(createpath);
			}
			String pathArry[] = createpath.split("/");
			StringBuffer filePath = new StringBuffer("/");
			for (String path : pathArry) {
				if (path.equals("")) {
					continue;
				}
				filePath.append(path + "/");
				if (isDirExist(filePath.toString(), sftp)) {
					sftp.cd(filePath.toString());
				} else {
					// 建立目录
					sftp.mkdir(filePath.toString());
					// 进入并设置为当前目录
					sftp.cd(filePath.toString());
				}
			}
			sftp.cd(createpath);
		} catch (SftpException e) {
			e.fillInStackTrace();
		}
	}

	public static boolean isDirExist(String directory, ChannelSftp sftp) {
		boolean isDirExistFlag = false;
		try {
			isDirExistFlag = true;
			return sftp.lstat(directory).isDir();
		} catch (Exception e) {
			if (e.getMessage().toLowerCase().equals("no such file")) {
				isDirExistFlag = false;
			}
		}
		return isDirExistFlag;
	}

	public static void main(String[] arg) {
		String keyFile = "D:/id_rsa_crpl_weixin.dat";
		String user = "crpl_weixin";
		String host = "111.202.58.171";
		String passphrase = "123456";
		int port = 20000;
		ChannelSftp sftp = null;
		try {
			connect("/signature/id_rsa_crpl_weixin.dat", host, port, user,
					passphrase);
			JSch jsch = new JSch();
			jsch.addIdentity(keyFile);

			Session session = jsch.getSession(user, host, port);

			// username and passphrase will be given via UserInfo interface.
			// UserInfo
			UserInfo ui = new MyUserInfo(passphrase);
			session.setUserInfo(ui);
			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			InputStream inputStream = download(sftp, "/upload/repayment/withhold/JD20170516", "20170516_01.txt");
			String a = IOUtils.toString(inputStream);
			System.out.println(a);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		} finally {
			if (sftp != null) {
				sftp.disconnect();
				sftp.exit();
			}
		}
	}

	public static class MyUserInfo implements UserInfo {
		private String passphrase = null;

		public MyUserInfo(String passphrase) {
			this.passphrase = passphrase;
		}

		public String getPassphrase() {
			return passphrase;
		}

		public String getPassword() {
			return null;
		}

		public boolean promptPassphrase(String s) {
			return true;
		}

		public boolean promptPassword(String s) {
			return true;
		}

		public boolean promptYesNo(String s) {
			return true;
		}

		public void showMessage(String s) {
			System.out.println(s);
		}
	}

}