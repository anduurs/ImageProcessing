package com.stenkrossstudios.protocolprocessing.utils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import com.aspose.omr.OmrImage;
import com.stenkrossstudios.protocolprocessing.App;

public class FileUtil {
	
	public static List<Mat> readMatFromFolder(String folderName)
	{
		URL url = App.class.getClassLoader().getResource("./" + folderName);
		String folderPath = url.getPath();
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		List<Mat> images = new ArrayList<>();
		
		for(File file : listOfFiles) 
		{
			if(file.isFile())
			{
				images.add(Imgcodecs.imread(folderPath + file.getName()));
			}
		}
		
		return images;
	}
	
	public static List<OmrImage> readOmrFromFolder(String folderName)
	{
		URL url = App.class.getClassLoader().getResource("./" + folderName);
		String folderPath = url.getPath();
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		List<OmrImage> images = new ArrayList<>();
		
		for(File file : listOfFiles) 
		{
			if(file.isFile())
			{
				try 
				{
					images.add(OmrImage.load(folderPath + file.getName()));
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
			}
		}
		
		return images;
	}
	
	public static List<String> readFileNamesFromFolder(String folderName)
	{
		URL url = App.class.getClassLoader().getResource("./" + folderName);
		String folderPath = url.getPath();
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		List<String> fileNames = new ArrayList<>();
		
		for(File file : listOfFiles) 
		{
			if(file.isFile())
			{
				fileNames.add(file.getName());
			}
		}
		
		return fileNames;
	}
	
	public static void writeToFolder(String folderName, List<Mat> images)
	{
		URL url = App.class.getClassLoader().getResource("./" + folderName);
		String folderPath = url.getPath();
		
		int i = 1;
		
		for(Mat image : images)
		{
			Imgcodecs.imwrite(folderPath + "image" + i + ".png", image);
			i++;
		}
	}

}
