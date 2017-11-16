package com.stenkrossstudios.protocolprocessing.image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core.MinMaxLocResult;

import com.stenkrossstudios.protocolprocessing.utils.ImageUtil;

import org.opencv.core.Mat;
import org.opencv.core.Point;

public class Layout 
{
	private List<Template> m_Templates;
	
	private Mat m_UniqueImageIdentifier;
	private double m_MinMatchQuality;
	
	public Layout(Mat uniqueIdentifier, double minMatchQuality) 
	{
		m_UniqueImageIdentifier = uniqueIdentifier;
		m_MinMatchQuality = minMatchQuality;
		
		m_Templates = new ArrayList<>();
	}
	
	public void addTemplate(Template template) 
	{
		m_Templates.add(template);
	}

	public Mat getUniqueImageIdentifier() 
	{
		return m_UniqueImageIdentifier;
	}
	
	public boolean matchLayout(Mat image) 
	{
		return ImageUtil.matchFound(image, m_UniqueImageIdentifier, m_MinMatchQuality);
	}
	
	public boolean matchTemplatesAndReplace(BufferedImage modifiableSourceImage, Mat readableSourceImage) 
	{
		boolean templateMatchingSuccesful = true;
		
		for(Template template : m_Templates) 
		{
			if(template.getMinMatchQuality() == 0.0) 
			{
			    ImageUtil.modifyImage(modifiableSourceImage, template.getReplacementImage(), 
			    		template.x, template.y, 
			    		template.getWidth(), template.getHeight());
			}
			else 
			{
				MinMaxLocResult mmr = ImageUtil.matchTemplate(readableSourceImage, template.getTemplateImage());
				
				if(mmr.maxVal > template.getMinMatchQuality()) 
				{
					Point matchLoc = mmr.maxLoc;

				    ImageUtil.modifyImage(modifiableSourceImage, template.getReplacementImage(), 
				    		(int)matchLoc.x, (int)matchLoc.y, 
				    		template.getWidth(), template.getHeight());
				}
				else 
				{
					templateMatchingSuccesful = false;
					break;
				}
			}
		}
		
		return templateMatchingSuccesful;
	}


}
