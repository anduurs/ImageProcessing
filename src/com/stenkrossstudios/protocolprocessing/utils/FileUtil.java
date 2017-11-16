package com.stenkrossstudios.protocolprocessing.utils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import com.aspose.omr.OmrImage;

public class FileUtil {
	
	public static List<Mat> readMatFromFolder(String folderPath)
	{
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
	
	public static List<OmrImage> readOmrFromFolder(String folderPath)
	{
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
	
	public static List<String> readFileNamesFromFolder(String folderPath)
	{
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
	
	public static void writeToFolder(String folderPath, List<Mat> images)
	{
		int i = 1;
		
		for(Mat image : images)
		{
			Imgcodecs.imwrite(folderPath + "output" + i + ".png", image);
			i++;
		}
	}

}
