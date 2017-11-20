package com.stenkrossstudios.protocolprocessing;
import java.util.List;

import org.opencv.core.Mat;

import com.aspose.omr.OmrImage;
import com.stenkrossstudios.protocolprocessing.image.ImageManager;
import com.stenkrossstudios.protocolprocessing.utils.FileUtil;

public class App 
{
	public static void main(String[] args) 
	{
		System.out.println("Starting to process images");
		long start = System.currentTimeMillis();
	
		ImageManager imageManager = new ImageManager();
		
		List<OmrImage> inputImages = FileUtil.readOmrFromFolder("input/");
		
		System.out.println("Starting to deskew images...");

		List<Mat> deskewedImages = imageManager.deskewImages(inputImages);
		System.out.println("Image deskewing done");
		
		System.out.println("Starting to modify images...");
		List<Mat> modifiedImages = imageManager.modifyImages(deskewedImages);
		System.out.println("Image modifying done");
		
		System.out.println("Saving images...");
		FileUtil.writeToFolder("output/", modifiedImages);
		
		long end = System.currentTimeMillis() - start;
		
		System.out.println("Images processed and saved. Time taken: " + end + " ms");
	}
}
