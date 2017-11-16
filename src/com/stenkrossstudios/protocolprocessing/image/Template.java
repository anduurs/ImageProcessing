package com.stenkrossstudios.protocolprocessing.image;
import java.awt.image.BufferedImage;

import org.opencv.core.Mat;

public class Template 
{
	private Mat m_TemplateImage;
	private BufferedImage m_ReplacementImage;
	
	private double m_MinMatchQuality;
	
	public int x, y;
	
	private int m_Width;
	private int m_Height;
	
	public Template(Mat templateImage, BufferedImage replacementImage, double minMatchQuality) 
	{
		this(templateImage, replacementImage, minMatchQuality, 0, 0, 
				replacementImage.getWidth(), replacementImage.getHeight());
	}
	
	public Template(Mat templateImage, BufferedImage replacementImage, 
			double minMatchQuality, int x, int y) 
	{
		this(templateImage, replacementImage, minMatchQuality, x, y, 
				replacementImage.getWidth(), replacementImage.getHeight());
	}
	
	public Template(Mat templateImage, BufferedImage replacementImage, double minMatchQuality, 
			int x, int y, int width, int height) 
	{
		m_TemplateImage = templateImage;
		m_ReplacementImage = replacementImage;
		
		m_MinMatchQuality = minMatchQuality;
		
		m_Width = width;
		m_Height = height;
		
		this.x = x;
		this.y = y;
	}

	public Mat getTemplateImage() 
	{
		return m_TemplateImage;
	}

	public BufferedImage getReplacementImage() 
	{
		return m_ReplacementImage;
	}
	
	public double getMinMatchQuality() 
	{
		return m_MinMatchQuality;
	}

	public int getWidth() 
	{
		return m_Width;
	}

	public int getHeight() 
	{
		return m_Height;
	}
}
