package com.stenkrossstudios.protocolprocessing.image;

import org.opencv.core.Mat;

public class ImageMat 
{
	private Mat m_Image;
	private String m_FileName;
	public double maxValue;
	
	public ImageMat(Mat image, String fileName)
	{
		m_Image = image;
		m_FileName = fileName;
		maxValue = 0.0;
	}
	
	public Mat getImage()
	{
		return m_Image;
	}
	
	public String getFileName()
	{
		return m_FileName;
	}
}
