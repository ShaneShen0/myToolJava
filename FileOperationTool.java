package com.unary.glv.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unary.glv.view.GLVUI;

/**
 * 名称：FileOperationTool
 * 类型：JAVA
 * 描述：文件操作工具类提供文件操作功能
 * 最近修改时间：2019/8/5
 * @author ShaneSheng
 * @since 2019/8/5
 * @version 1.0
 */
public class FileOperationTool {
	private static Logger logger = LogManager.getLogger(FileOperationTool.class); 
	/**
	 * 方法描述： 获得指定文件路径下文件夹的名字
	 * @param path String 查询的路径
	 * @return folderNames ArrayList<String> 所有文件夹名字的列表
	 */
	public static ArrayList<String> getFolderNames(String path){
		ArrayList<String> folderNames = new ArrayList<String>();
		File file = new File(path);
		File[] fileArray = file.listFiles();
		for(int i=0;i<fileArray.length;i++) {
			
			 if (fileArray[i].isDirectory()) {
				folderNames.add(fileArray[i].getName());
			}
		}
		return folderNames;
	}
	/**
	 * 方法描述：复制源路径下文件中的所有文件及子文件夹到目标路径，并保证同结构同名称
	 * @param originalPath String 源路径
	 * @param targetPath String 目的路径
	 * @param fileName String 文件名
	 * @return boolean 是否拷贝成功
	 */
	public static boolean copyAllFilesAndDirectories(String originalPath, String targetPath, String fileName){
		/*根据源路径和文件名得到源文件路径*/
		String originalFilePath = originalPath+File.separator+fileName;
		/*根据目标路径和文件名得到源文件路径，保证源路径下文件名字与目标文件相同*/
		String targetFilePath = targetPath+File.separator+fileName;
		/*将源文件路径下的所有文件拷贝到目标路径*/
		/*1.判断目标文件夹是否存在*/
		File targetFile = new File(targetFilePath);
		if (!targetFile.exists()) {
			/*如果目标目录不存在，则创建目标目录*/
			targetFile.mkdir();
		}
		/*获得源目录下的所有文件*/
		File originalFile = new File(originalFilePath);
		String[] fileChildrenNameList = originalFile.list();
		if (fileChildrenNameList.length == 0) {
			/*如果文件夹下没有子文件和子文件夹，则直接返回true*/
			return true;
		}
		else {
			/*否则复制所有子文件夹及子文件*/
			for (int i = 0; i < fileChildrenNameList.length; i++) {
				if ((new File(originalFilePath+File.separator+fileChildrenNameList[i])).isDirectory()) {
					/*如果该File是一个文件夹,则递归执行本方法*/
					if (copyAllFilesAndDirectories(originalFilePath, targetFilePath,fileChildrenNameList[i])) {
						logger.info("All files and directories in "+ originalFilePath+"copy successfully");
						
					}
					else {
						return false;
					}
				}
				else {
					/*如果该File是一个文件*/
					if (copyFileSpecified(originalFilePath, targetFilePath,fileChildrenNameList[i])) {
						logger.info("File copying from " + originalFilePath+File.separator+fileChildrenNameList[i]+" to" +targetFilePath+File.separator+fileChildrenNameList[i] + " succeeded");
						
					}
					else {
						return false;
					}
				}
				
			}
		}
		
		return true;
		
	}
	/**
	 * 方法描述：拷贝指定源路径下指定文件到目的路径
	 * @param originalPath String 文件拷贝源路径
	 * @param targetPath String 文件拷贝目的路径
	 * @param fileName String 要拷贝的文件名
	 * @return boolean 是否拷贝成功
	 */
	public static boolean copyFileSpecified(String originalPath, String targetPath,String fileName) {
		File originalFile = new File(originalPath + File.separator + fileName);
		File targetFile = new File(targetPath + File.separator + fileName);
		if (!targetFile.exists()) {
			/*如果目标文件不存在，则先新建目标文件*/
			try {
				targetFile.createNewFile();
			} catch (IOException e) {
				logger.error("File "+ targetPath + File.separator + fileName + "creation failed");
				return false;
				
				
			}
			logger.info("File" + targetPath+File.separator+fileName+"successfully ceated ");
			
		}
		/*******开始复制*******/
		byte[] buffer = new byte[1024];
		
		try {
			FileInputStream in = new FileInputStream(originalFile);
			FileOutputStream out = new FileOutputStream(targetFile);
			while (in.read(buffer)!=-1) {
				out.write(buffer);
				
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			logger.error("Original file "+ originalPath + File.separator + fileName + "is not found");
			return false;
		} catch (IOException e) {
			logger.error("File copying from " + originalPath+File.separator+fileName+" to" +targetPath+File.separator+fileName + " failed");
			return false;
		}
		
		return true;
 	}
	/**
	 * 方法描述：按行读取文本内容
	 * @param path 文本路径 
	 * @return List<String> 读取到的文本内容
	 */
	public static List<String> readTextLineByLine(String path) {
		List<String> contentsList = new ArrayList<String>();
		try {
			FileReader reader = new FileReader(new File(path));
			BufferedReader bReader = new BufferedReader(reader);
			String str;
			while((str = bReader.readLine()) != null ) {
				contentsList.add(str);
			}
			bReader.close();
			reader.close();
		} catch (FileNotFoundException e) {
			logger.error("Filter configuration file "+path+" was not found");
			
		} catch (IOException e) {
			logger.error("Filter configuration "+path+" read failed");
		}
		return contentsList;
	}
}
