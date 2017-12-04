package com.stenkrossstudios.protocolprocessing.image;

import com.aspose.omr.OmrImage;

public class ImageOmr 
{
	private OmrImage m_Image;
	private String m_FileName;
	
	public ImageOmr(OmrImage image, String fileName)
	{
		m_Image = image;
		m_FileName = fileName;
	}
	
	public OmrImage getImage()
	{
		return m_Image;
	}
	
	public String getFileName()
	{
		return m_FileName;
	}
}
