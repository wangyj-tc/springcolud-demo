package com.suxintec.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FtpUtil {
	/**
	 * 获取ftp连接
	 * 
	 * @param ipAddr
	 *            IP地址
	 * @param port
	 *            端口
	 * @param userName
	 *            用户名
	 * @param pwd
	 *            密码
	 * @param path
	 *            资源路径
	 * @return
	 * @throws Exception
	 */
	public static FTPClient connectFtp(String ipAddr, Integer port, String userName, String pwd) throws Exception {
		FTPClient ftp = new FTPClient();
		int reply;
		ftp.connect(ipAddr, port);
		ftp.login(userName, pwd);
		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
		ftp.setConnectTimeout(200000);
		reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			return ftp;
		}
		return ftp;
	}
	/**
	 * 测试ftp连接
	 * 
	 * @param ipAddr
	 *            IP地址
	 * @param port
	 *            端口
	 * @param userName
	 *            用户名
	 * @param pwd
	 *            密码
	 * @param path
	 *            资源路径
	 * @return
	 * @throws Exception
	 */
	public static boolean testFtp(String ipAddr, Integer port, String userName, String pwd) {
		FTPClient ftp = new FTPClient();
		try {
			ftp.connect(ipAddr, port);
			return ftp.login(userName, pwd);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			closeFtp(ftp);
		}
		return false;
	}
	/**
	 * @param ftp ftp
	 * 
	 * @param filename
	 *            上传到FTP服务器上的文件名
	 * @param input
	 *            输入流
	 * @param pathname
	 * 			  相对路径
	 * 
	 * @return 成功返回true，否则返回false
	 */
	public static boolean uploadFile(FTPClient ftp, String filename, InputStream input,String pathname) throws IOException {
		boolean success = false;
		try {
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.changeWorkingDirectory(pathname);
			ftp.storeFile(filename, input);
			input.close();
			success = true;
		} catch (IOException e) {
			throw e;
		} finally {
			closeFtp(ftp);
		}
		return success;
	}

	/**
	 * @param FTPClient
	 *            ftp
	 * 
	 * @param filename
	 *            上传到FTP服务器上的文件名外部关闭FTP连接
	 * @param input
	 *            输入流
	 * @return 成功返回true，否则返回false
	 * @throws Exception
	 */
	public static boolean uploadFileOpen(FTPClient ftpClient, String filename, InputStream input, String path)
			throws Exception {
		boolean success = false;
		try {
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
//			ftpClient.makeDirectory(path);
			ftpClient.changeWorkingDirectory(path);
			ftpClient.storeFile(filename, input);
			input.close();
			success = true;
		} catch (IOException e) {
			throw e;
		} finally {
			closeFtp(ftpClient);
		}
		return success;
	}

	/**
	 * @param FTPClient
	 *            ftp
	 * 
	 * @param fileName
	 *            要下载的文件名
	 * @param localPath
	 *            下载后保存到本地的路径
	 * @return
	 */
	public static boolean downFile(FTPClient ftp, String fileName, String localPath) throws IOException {
		boolean success = false;
		OutputStream is = null;
		try {
			File dir = new File(localPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File localFile = new File(localPath + "/" + fileName);
			localFile.lastModified();
			is = new FileOutputStream(localFile);
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.retrieveFile(fileName, is);
			is.close();
			success = true;
		} catch (IOException e) {
			throw e;
		}finally{
			IOUtils.closeQuietly(is);
		}
		return success;
	}

	/**
	 * ftp 删除文件
	 * 
	 * @param ftp
	 * @param fileName
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static boolean deleteFile(FTPClient ftp, String fileName) throws IOException {
		boolean success = false;
		ftp.enterLocalPassiveMode();
		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
		FTPFile[] files = ftp.listFiles(fileName);
		if (files.length == 1) // 文件是否存在
		{
			boolean status = ftp.deleteFile(fileName);
			success = status ? true : false;
		}
		return success;
	}

	/**
	 * 关闭ftp连接
	 */
	public static void closeFtp(FTPClient ftp) {
		if (ftp != null && ftp.isConnected()) {
			try {
				ftp.logout();
				ftp.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 下载文件
	 * 
	 * @param host
	 * @param user
	 * @param password
	 * @param localDir
	 * @param remoteDir
	 * @param remoteFilename
	 * @return
	 */
	public static boolean downloadFTPFile(String host,String user,String password,String localDir, String remoteDir, String remoteFilename) {
		FTPClient ftpClient = new FTPClient();
		FileOutputStream local = null;
		try {
			// 连接
			ftpClient.connect(host);
			// 登录
			if (ftpClient.login(user, password)) {
				ftpClient.setBufferSize(1024);
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); // 设置文件类型（二进制）
				// 定位到根目录
				ftpClient.changeWorkingDirectory("/");
				File file = new File(localDir);
				if(!file.exists()){
					if(!file.mkdirs()){
						return false;
					}
				}
				local = new FileOutputStream(localDir.trim() + File.separator + remoteFilename);
				if (StringUtils.isNotEmpty(remoteDir)) {
					return ftpClient.retrieveFile(remoteDir.trim() + "/" + remoteFilename, local);
				} else {
					return ftpClient.retrieveFile(remoteFilename, local);
				}
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}  finally {
			// 关闭流
			IOUtils.closeQuietly(local);
			// 登出
			closeFtp(ftpClient);
		}
	}
	
	/**
	 * 读取FTP上的文件
	 * @param host
	 * @param user
	 * @param password
	 * @param remoteDir
	 * @param remoteFilename
	 * @return
	 */
	public static String readFTPFile(String host,String user,String password , String remoteDir, String remoteFilename){
		FTPClient ftpClient = new FTPClient();
		String result = null;
		InputStream in = null;
		StringBuffer resultBuffer = new StringBuffer();
		try {
			// 连接
			ftpClient.connect(host);
			// 登录
			if (ftpClient.login(user, password)) {
				ftpClient.setBufferSize(1024);
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); // 设置文件类型（二进制）
				ftpClient.setControlEncoding("GBK");
				ftpClient.enterLocalPassiveMode();
				// 定位到根目录
				ftpClient.changeWorkingDirectory("/");
				
				in = ftpClient.retrieveFileStream(remoteDir.trim() + "/" + remoteFilename); 
				
				if (in != null) {
					BufferedReader br = new BufferedReader(new InputStreamReader(in,"GBK"));
					String data = null;
					while ((data = br.readLine()) != null) { 
						resultBuffer.append(data + "\n");  
					}
				}
				result = resultBuffer.toString();
			
			} else {
				return result;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return result;
		} finally {
			// 登出
			closeFtp(ftpClient);
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param host
	 * @param user
	 * @param password
	 * @param remoteDir
	 * @param remoteFilename
	 * @return
	 */
	public static BufferedReader readBufferedReaderFTPFile(String host,int post,String user,String password , String remoteDir, String remoteFilename){
		FTPClient ftpClient = new FTPClient();
		InputStream in = null;
		BufferedReader br = null;
		try {
			// 连接
			ftpClient.connect(host, post);
			// 登录
			if (ftpClient.login(user, password)) {
				ftpClient.setBufferSize(1024);
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); // 设置文件类型（二进制）
				ftpClient.setControlEncoding("GBK");
				ftpClient.enterLocalPassiveMode();
				// 定位到根目录
				ftpClient.changeWorkingDirectory("/");
				
				in = ftpClient.retrieveFileStream(remoteDir.trim() + "/" + remoteFilename); 
				
				if (in != null) {
					br = new BufferedReader(new InputStreamReader(in,"GBK"));
				}
			
			} else {
				return br;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return br;
		} finally {
			// 登出
			closeFtp(ftpClient);
		}
		
		return br;
	}
	
	/**
	 * 
	 * @param host
	 * @param user
	 * @param password
	 * @param remoteDir
	 * @param remoteFilename
	 * @return
	 */
	public static InputStream readInputStreamFTPFile(String host,int post,String user,String password , String remoteDir, String remoteFilename){
		FTPClient ftpClient = new FTPClient();
		InputStream in = null;
		try {
			// 连接
			ftpClient.connect(host,post);
			// 登录
			if (ftpClient.login(user, password)) {
				ftpClient.setBufferSize(1024);
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); // 设置文件类型（二进制）
				ftpClient.setControlEncoding("GBK");
				ftpClient.enterLocalPassiveMode();
				// 定位到根目录
				ftpClient.changeWorkingDirectory("/");
				
				in = ftpClient.retrieveFileStream(remoteDir.trim() + "/" + remoteFilename); 
				
				/*if (in != null) {
					br = new BufferedReader(new InputStreamReader(in,"GBK"));
				}*/
			
			} else {
				return in;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return in;
		} finally {
			// 登出
			closeFtp(ftpClient);
		}
		
		return in;
	}

}
