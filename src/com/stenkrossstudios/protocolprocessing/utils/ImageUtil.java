package com.stenkrossstudios.protocolprocessing.utils;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.aspose.omr.OmrEngine;
import com.aspose.omr.OmrImage;
import com.aspose.omr.OmrTemplate;

public class ImageUtil 
{
	public static OmrEngine engine = new OmrEngine(new OmrTemplate()); 
	
	public static MinMaxLocResult matchTemplate(Mat sourceImage, Mat template) 
	{
		int result_cols = sourceImage.cols() - template.cols() + 1;
	    int result_rows = sourceImage.rows() - template.rows() + 1;
	    
	    Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
	    Imgproc.matchTemplate(sourceImage, template, result, Imgproc.TM_CCOEFF_NORMED);
	    MinMaxLocResult mmr = Core.minMaxLoc(result);
	    result.release();
	    
	    return mmr;
	}
	
	public static boolean matchFound(Mat sourceImage, Mat template, double minMatchQuality) 
	{
		MinMaxLocResult mmr = matchTemplate(sourceImage, template);
	    return mmr.maxVal > minMatchQuality;
	}
	
	public static void modifyImage(BufferedImage sourceImage, BufferedImage replacementImage, 
			int startX, int startY, int width, int height) 
	{	
		int w = startX + width;
		int h = startY + height;
		
		for(int y = 0; y < (h - startY); y++) 
		{
			for(int x = 0; x < (w - startX); x++)
		    {
				if(x + startX < sourceImage.getWidth() && y + startY < sourceImage.getHeight())
				{
					sourceImage.setRGB(x + startX, y + startY, replacementImage.getRGB(x, y));
				}
		    }
		} 	
	}
	
	public static OmrImage deskewImage(OmrImage image) 
	{
		double skewAngle = engine.getSkewDegree(image);
		
		if(skewAngle < -20.0) 
		{
			skewAngle = 0.0f;
		}
		
		return engine.rotateImage(image, skewAngle);
	}
	
	public static BufferedImage matToBufferedImage(Mat matrix)
	{        
	    MatOfByte mob = new MatOfByte();
	    Imgcodecs.imencode(".png", matrix, mob);
	    byte ba[] = mob.toArray();
	    BufferedImage bi = null;
	    
		try {
			bi = ImageIO.read(new ByteArrayInputStream(ba));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    return bi;
	}
	
	public static Mat bufferedImageToMat(BufferedImage bi) 
	{
		Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		
		mat.put(0, 0, data);
		
		return mat;
	}
}
