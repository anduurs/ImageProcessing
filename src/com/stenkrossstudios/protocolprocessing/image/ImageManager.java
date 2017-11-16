package com.stenkrossstudios.protocolprocessing.image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import com.aspose.omr.OmrImage;
import com.stenkrossstudios.protocolprocessing.utils.ImageUtil;

public class ImageManager 
{
	private List<Layout> m_Layouts;
	
	public ImageManager() 
	{
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		m_Layouts  = new ArrayList<>();
		
		Mat logo1  = Imgcodecs.imread("res/templates/nitrologga1.png");
		Mat logo2  = Imgcodecs.imread("res/templates/nitrologga2.png");
		Mat logo3  = Imgcodecs.imread("res/templates/nitrologga3.png");
		Mat logo4  = Imgcodecs.imread("res/templates/aflogga.png");
		
		Mat date1  = Imgcodecs.imread("res/templates/datum.png");
		Mat date2  = Imgcodecs.imread("res/templates/datum2.png");
		Mat date3  = Imgcodecs.imread("res/templates/datum3.png");
		Mat date4  = Imgcodecs.imread("res/templates/datum4.png");
		
		Mat sign1  = Imgcodecs.imread("res/templates/sign.png");
		Mat sign2  = Imgcodecs.imread("res/templates/sign2.png");
		Mat sign3  = Imgcodecs.imread("res/templates/sign3.png");
		Mat sign4  = Imgcodecs.imread("res/templates/sign4.png");
		
		Mat litt1  = Imgcodecs.imread("res/templates/litt.png");
		Mat litt2  = Imgcodecs.imread("res/templates/litt2.png");
		Mat litt3  = Imgcodecs.imread("res/templates/litt3.png");
		Mat litt4  = Imgcodecs.imread("res/templates/litt4.png");
		
		Mat vaderlek3  = Imgcodecs.imread("res/templates/vaderlek3.png");
		Mat vaderlek4  = Imgcodecs.imread("res/templates/vaderlek4.png");
		
		BufferedImage newLogo = null;
		BufferedImage emptyLogo = null;
		
		BufferedImage newDate  = null;
		BufferedImage newDate2 = null;
		BufferedImage newDate3 = null;
		BufferedImage newDate4 = null;
		
		BufferedImage newSign  = null;
		BufferedImage newSign2 = null;
		BufferedImage newSign3 = null;
		BufferedImage newSign4 = null;
		
		BufferedImage newLitt  = null;
		BufferedImage newLitt2 = null;
		BufferedImage newLitt3 = null;
		BufferedImage newLitt4 = null;
		
		BufferedImage newVaderlek3 = null;
		BufferedImage newVaderlek4 = null;
		
		try 
		{
			newLogo = ImageIO.read(Class.class.getResourceAsStream("/replacements/newlogo2.png"));
			emptyLogo = ImageIO.read(Class.class.getResourceAsStream("/replacements/emptylogo.png"));
			
			newDate = ImageIO.read(Class.class.getResourceAsStream("/replacements/datum.png"));
			newSign = ImageIO.read(Class.class.getResourceAsStream("/replacements/sign.png"));
			newLitt = ImageIO.read(Class.class.getResourceAsStream("/replacements/litt.png"));
			
			newDate2 = ImageIO.read(Class.class.getResourceAsStream("/replacements/datum2.png"));
			newSign2 = ImageIO.read(Class.class.getResourceAsStream("/replacements/sign2.png"));
			newLitt2 = ImageIO.read(Class.class.getResourceAsStream("/replacements/litt2.png"));
			
			newDate3 = ImageIO.read(Class.class.getResourceAsStream("/replacements/datum3.png"));
			newSign3 = ImageIO.read(Class.class.getResourceAsStream("/replacements/sign3.png"));
			newLitt3 = ImageIO.read(Class.class.getResourceAsStream("/replacements/litt3.png"));
			
			newDate4 = ImageIO.read(Class.class.getResourceAsStream("/replacements/datum4.png"));
			newSign4 = ImageIO.read(Class.class.getResourceAsStream("/replacements/sign4.png"));
			newLitt4 = ImageIO.read(Class.class.getResourceAsStream("/replacements/litt4.png"));
			
			newVaderlek3 = ImageIO.read(Class.class.getResourceAsStream("/replacements/vaderlek3.png"));
			newVaderlek4 = ImageIO.read(Class.class.getResourceAsStream("/replacements/vaderlek4.png"));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		Layout layout1 = new Layout(logo1, 0.9);
		
		layout1.addTemplate(new Template(logo1, newLogo, 0.9));
		layout1.addTemplate(new Template(date1, newDate, 0.6));
		layout1.addTemplate(new Template(sign1, newSign, 0.6));
		layout1.addTemplate(new Template(litt1, newLitt, 0.6));
		
		Layout layout2 = new Layout(logo2, 0.9);
		
		layout2.addTemplate(new Template(logo2, newLogo, 0.9));
		layout2.addTemplate(new Template(date2, newDate2, 0.6));
		layout2.addTemplate(new Template(sign2, newSign2, 0.6));
		layout2.addTemplate(new Template(litt2, newLitt2, 0.6));
		
		Layout layout3 = new Layout(logo3, 0.9);
		
		layout3.addTemplate(new Template(logo3, newLogo, 0.9));
		layout3.addTemplate(new Template(date3, newDate3, 0.6));
		layout3.addTemplate(new Template(sign3, newSign3, 0.6));
		layout3.addTemplate(new Template(litt3, newLitt3, 0.6));
		layout3.addTemplate(new Template(vaderlek3, newVaderlek3, 0.6));
		
		Layout layout4 = new Layout(logo4, 0.9);
		
		layout4.addTemplate(new Template(logo4, emptyLogo, 0.9));
		layout4.addTemplate(new Template(logo4, newLogo, 0.0, 135, 85));
		layout4.addTemplate(new Template(date4, newDate4, 0.6));
		layout4.addTemplate(new Template(sign4, newSign4, 0.6));
		layout4.addTemplate(new Template(litt4, newLitt4, 0.6));
		layout4.addTemplate(new Template(vaderlek4, newVaderlek4, 0.6));
		
		m_Layouts.add(layout1);
		m_Layouts.add(layout2);
		m_Layouts.add(layout3);
		m_Layouts.add(layout4);
	}
	
	public List<Mat> modifyImages(List<Mat> inputImages)
	{
		List<Mat> modifiedImages = new ArrayList<>();
		
		int i = 0;
		
		for(Mat image : inputImages)
		{
			BufferedImage sourceImage = ImageUtil.matToBufferedImage(image);
			Layout validLayout = null;

			int counter = 1;
			
			for(Layout layout : m_Layouts) 
			{
				if(layout.matchLayout(image)) 
				{
					System.out.println("Valid layout: " + counter + " , Image: " + (i+1));
					validLayout = layout;
					break;
				}
				counter++;
			}
			
			if(validLayout != null && sourceImage != null) 
			{
				if(validLayout.matchTemplatesAndReplace(sourceImage, image))
				{
					modifiedImages.add(ImageUtil.bufferedImageToMat(sourceImage));
				}
			}
			
			i++;
		}
		
		return modifiedImages;
	}

	public List<Mat> deskewImages(List<OmrImage> images) 
	{
		List<Mat> deskewdImages = new ArrayList<>();
		
		int i = 0;
		
		for(OmrImage image : images) 
		{
			deskewdImages.add(ImageUtil.bufferedImageToMat(ImageUtil.deskewImage(image).asBitmap()));
			System.out.println("Deskewing image: " + (i+1));
			i++;
		}

		return deskewdImages;
	}
}
