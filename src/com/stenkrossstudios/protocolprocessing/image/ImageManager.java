package com.stenkrossstudios.protocolprocessing.image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;

import com.aspose.omr.OmrImage;
import com.stenkrossstudios.protocolprocessing.utils.ImageUtil;

public class ImageManager 
{
	private static List<ImageMat> m_IdentifierTemplates;
	private static List<ImageMat> m_SearchIdentifierTemplates;
	private static List<ImageMat> m_LogoTemplates;
	
	private static List<Template> m_LogoTemplatesAf;
	private static List<Template> m_DateTemplates;
	private static List<Template> m_SignTemplates;
	private static List<Template> m_LittTemplates;
	private static List<Template> m_WeatherTemplates;
	
	private static BufferedImage newLogo = null;
	
	public static void init()
	{
		System.loadLibrary("opencv_java331");
		
		m_IdentifierTemplates = new ArrayList<>();
		m_SearchIdentifierTemplates = new ArrayList<>();
		m_LogoTemplates = new ArrayList<>();
		m_LogoTemplatesAf = new ArrayList<>();
		m_DateTemplates = new ArrayList<>();
		m_SignTemplates = new ArrayList<>();
		m_LittTemplates = new ArrayList<>();
		m_WeatherTemplates = new ArrayList<>();
		
		fillArray("res/images/templates/af/identifiers/", m_IdentifierTemplates);
		fillArray("res/images/templates/af/logos/", "/images/replacements/af/logos/", m_LogoTemplatesAf, 0.7);
		fillArray("res/images/templates/af/dates/", "/images/replacements/af/dates/", m_DateTemplates, 0.7);
		fillArray("res/images/templates/af/signs/", "/images/replacements/af/signs/", m_SignTemplates, 0.7);
		fillArray("res/images/templates/af/litts/", "/images/replacements/af/litts/", m_LittTemplates, 0.7);
		fillArray("res/images/templates/af/weathers/", "/images/replacements/af/weathers/", m_WeatherTemplates, 0.7);
		
		try {
			newLogo = ImageIO.read(Class.class.getResourceAsStream("/images/replacements/newlogo2.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void processImages(String inputFolderPath, String outputFolderPath, 
			int batch, int startingImage, String threadName)
	{
		new File(outputFolderPath + "nonvalid/").mkdir();
		new File(outputFolderPath + "failed/").mkdir();
		
		File inputFolder = new File(inputFolderPath);
		File[] listOfInputFiles = inputFolder.listFiles();
		
		int imageCounter = 1;
		
		for(File file : listOfInputFiles)
		{
			if(file.isFile() && imageCounter >= startingImage)
			{
				System.out.println(threadName + "Processing image: " + imageCounter);
				
				ImageOmr image = null;
				
				try {
					image = new ImageOmr(OmrImage.load(inputFolderPath + file.getName()), file.getName());
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				System.out.println(threadName + "Deskewing image: " + imageCounter);
				
				ImageMat inputImage = new ImageMat(ImageUtil.bufferedImageToMat(
						ImageUtil.deskewImage(image.getImage()).asBitmap()), image.getFileName());
				
				Imgcodecs.imwrite("res/images/deskewed/batch" + batch + "/" + file.getName(), inputImage.getImage());

				boolean validImage = false;
				boolean logoModified = false;
				boolean dateModified = false;
				boolean signModified = false;
				boolean littModified = false;
				boolean weatherModified = false;
				
				String idTemplateFileName = "";
				
				for(ImageMat idTemplate : m_IdentifierTemplates)
				{
					if(ImageUtil.matchFound(inputImage.getImage(), idTemplate.getImage(), 0.7))
					{
						validImage = true;
						idTemplateFileName = idTemplate.getFileName();
						break;
					}
				}
				
				if(validImage)
				{
					System.out.println(threadName + "Image: " + file.getName() + " is valid. Starting to modify....");
					
					BufferedImage modifiableInputImage = ImageUtil.matToBufferedImage(inputImage.getImage());
					
					weatherModified = modifyField(m_WeatherTemplates, modifiableInputImage, inputImage.getImage());

					if(weatherModified)
					{
						logoModified = modifyLogo(modifiableInputImage, inputImage.getImage(), 125, 60);
						dateModified = modifyField(m_DateTemplates, modifiableInputImage, inputImage.getImage());
						signModified = modifyField(m_SignTemplates, modifiableInputImage, inputImage.getImage());
						littModified = modifyField(m_LittTemplates, modifiableInputImage, inputImage.getImage());
					}
					else
					{
						BufferedImage halfImage = modifiableInputImage.getSubimage(0, 0, 
								modifiableInputImage.getWidth(),
								modifiableInputImage.getHeight() / 4);
						
						Mat halfMat = ImageUtil.bufferedImageToMat(halfImage);
						
						logoModified = modifyLogo(modifiableInputImage, halfMat, 125, 60);
						dateModified = modifyField(m_DateTemplates, modifiableInputImage, halfMat);
						signModified = modifyField(m_SignTemplates, modifiableInputImage, halfMat);
						littModified = modifyField(m_LittTemplates, modifiableInputImage, halfMat);
					}
					
					if(logoModified)
					{
						System.out.println(threadName + "Image logo was modified correctly!");
						ImageMat result = new ImageMat(ImageUtil.bufferedImageToMat(modifiableInputImage), file.getName());
						Imgcodecs.imwrite(outputFolderPath + result.getFileName(), result.getImage());
					}
					else
					{
						System.out.println(threadName + "Image: " + file.getName() + " failed to modify logo. Saving to failed folder...");
						Imgcodecs.imwrite(outputFolderPath + "failed/" + file.getName(), inputImage.getImage());
					}
				}
				else
				{
					System.out.println(threadName + "Image: " + file.getName() + " is not valid. Saving to nonvalid folder...");
					Imgcodecs.imwrite(outputFolderPath + "nonvalid/" + file.getName(), inputImage.getImage());
				}
				
				inputImage.getImage().release();
				
			}
			imageCounter++;
		}
	}
	
	private static boolean modifyField(List<Template> fieldTemplates, BufferedImage sourceImage, Mat image)
	{
		Point matchLoc = null;
		Template closestTemplate = null;
		double highestMatchQuality = 0.0; 

		for(Template template : fieldTemplates)
		{
		
			MinMaxLocResult mmr = ImageUtil.matchTemplate(image, template.getTemplateImage());
			double matchQuality = mmr.maxVal;
			
			if(matchQuality > 0.7)
			{
				if(matchQuality > highestMatchQuality)
				{
					highestMatchQuality = matchQuality;
					matchLoc = mmr.maxLoc;
					closestTemplate = template;
				}	
			}
		}
		
		
		if(closestTemplate != null)
		{
			ImageUtil.modifyImage(sourceImage, closestTemplate.getReplacementImage(), 
					(int)matchLoc.x, (int)matchLoc.y, 
					closestTemplate.getWidth(), closestTemplate.getHeight());

			return true;
		}
		
		return false;
	}
	
	private static boolean modifyLogo(BufferedImage sourceImage, Mat image, 
			int replacementImageXpos, int replacementImageYpos)
	{
		Point matchLoc = null;
		Template closestTemplate = null;
		double highestMatchQuality = 0.0; 
		
		for(Template template : m_LogoTemplatesAf)
		{
			MinMaxLocResult mmr = ImageUtil.matchTemplate(image, template.getTemplateImage());
			double matchQuality = mmr.maxVal;
			
			if(matchQuality > 0.7)
			{
				if(matchQuality > highestMatchQuality)
				{
					highestMatchQuality = matchQuality;
					matchLoc = mmr.maxLoc;
					closestTemplate = template;
				}	
			}
		}
		
		if(closestTemplate != null)
		{
			ImageUtil.modifyImage(sourceImage, closestTemplate.getReplacementImage(), 
					(int)matchLoc.x, (int)matchLoc.y, closestTemplate.getWidth(), closestTemplate.getHeight());
			
			ImageUtil.modifyImage(sourceImage, newLogo, 
					replacementImageXpos, replacementImageYpos, newLogo.getWidth(), newLogo.getHeight());

			return true;
		}
		
		return false;
	}
	
	private static boolean modifyLogo(BufferedImage sourceImage, Mat image)
	{
		Point matchLoc = null;
		ImageMat closestTemplate = null;
		double highestMatchQuality = 0.0; 
		
		for(ImageMat template : m_LogoTemplates)
		{
			MinMaxLocResult mmr = ImageUtil.matchTemplate(image, template.getImage());
			double matchQuality = mmr.maxVal;
			
			if(matchQuality > 0.7)
			{
				if(matchQuality > highestMatchQuality)
				{
					highestMatchQuality = matchQuality;
					matchLoc = mmr.maxLoc;
					closestTemplate = template;
				}	
			}
		}
		
		if(closestTemplate != null)
		{
			ImageUtil.modifyImage(sourceImage, newLogo, 
					(int)matchLoc.x, (int)matchLoc.y, newLogo.getWidth(), newLogo.getHeight());

			return true;
		}
		
		return false;
	}
	
	private static void fillArray(String templateFolderPath, 
			List<ImageMat> imageList)
	{
		File folder = new File(templateFolderPath);
		File[] listOfFiles = folder.listFiles();
		
		for(File file : listOfFiles) 
		{
			if(file.isFile())
			{
				imageList.add(new ImageMat(Imgcodecs.imread(templateFolderPath + file.getName()), file.getName()));
			}
		}
	}
	
	private static void fillArray(String templateFolderPath, String replacementFolderPath, 
			List<Template> templateList, double minMatchQuality)
	{
		File folder = new File(templateFolderPath);
		File[] listOfFiles = folder.listFiles();
		
		for(File file : listOfFiles) 
		{
			if(file.isFile())
			{
				try {
					templateList.add(new Template(Imgcodecs.imread(templateFolderPath + file.getName()), 
							ImageIO.read(Class.class.getResourceAsStream(replacementFolderPath + file.getName())), minMatchQuality));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		}
	}
	
	public static void findOriginalAndRedo(String originalFolderPath, String inputFolderPath, String outputFolderPath, int batchNumber)
	{
		String outputFolderFailed = outputFolderPath + "failed/";
		String outputFolderNonValid = outputFolderPath + "nonvalid/";
		
		new File(outputFolderPath).mkdir();
		new File(outputFolderNonValid).mkdir();
		new File(outputFolderFailed).mkdir();
		
		File originalFolder = new File(originalFolderPath);
		File[] listOfOriginalFiles = originalFolder.listFiles();
		
		File wrongFolder = new File(inputFolderPath);
		
		File[] listOfWrongFiles = wrongFolder.listFiles();

		int i = 0;
		
		for(File file : listOfOriginalFiles)
		{
			if(file.isFile())
			{
				i++;
				boolean shouldModify = false;
				
				for(File wrongFile : listOfWrongFiles)
				{
					if(file.getName().equals(wrongFile.getName()))
					{
						System.out.println("Process image: " + file.getName());
						shouldModify = true;
						break;
					}
				}
				
				if(shouldModify)
				{
					try {
						ImageOmr image = new ImageOmr(OmrImage.load(originalFolderPath + file.getName()), file.getName());
						System.out.println("Deskewing image: " + file.getName());
						
						ImageMat deskewedImage = new ImageMat(ImageUtil.bufferedImageToMat(
								ImageUtil.deskewImage(image.getImage()).asBitmap()), image.getFileName());
						
						boolean validImage = false;
						boolean logoModified = false;
						boolean dateModified = false;
						boolean signModified = false;
						boolean littModified = false;
						boolean weatherModified = false;
						
						String idTemplate = "";
						
						System.out.println("Checking if image: " + file.getName() + " is valid");
						
						for(ImageMat template : m_IdentifierTemplates)
						{
							if(ImageUtil.matchFound(deskewedImage.getImage(), template.getImage(), 0.7))
							{
								validImage = true;
								idTemplate = template.getFileName();
								break;
							}
						}
						
						if(validImage)
						{
							System.out.println("Modifying image: " + file.getName());
							
							BufferedImage sourceImage = ImageUtil.matToBufferedImage(deskewedImage.getImage());
							ImageMat result = null;
			
							logoModified = modifyLogo(sourceImage, deskewedImage.getImage());
							dateModified = modifyField(m_DateTemplates, sourceImage, deskewedImage.getImage());
							signModified = modifyField(m_SignTemplates, sourceImage, deskewedImage.getImage());
							littModified = modifyField(m_LittTemplates, sourceImage, deskewedImage.getImage());
							
							if(idTemplate.startsWith("f"))
							{
								weatherModified = modifyField(m_WeatherTemplates, sourceImage, deskewedImage.getImage());
							}
							
							if(logoModified)
							{
								System.out.println("Image modified!");
								result = new ImageMat(ImageUtil.bufferedImageToMat(sourceImage), file.getName());
								Imgcodecs.imwrite(outputFolderPath + result.getFileName(), result.getImage());
							}
							else
							{
								System.out.println("failed all");
								Imgcodecs.imwrite(outputFolderFailed + file.getName(), deskewedImage.getImage());
							}	
						}
						else
						{
							System.out.println("non valid all");
							Imgcodecs.imwrite(outputFolderNonValid + file.getName(), deskewedImage.getImage());
						}
					
						
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			}
		}
	}
	
	public static void filterOutWronglyModified(String inputFolder, String outputFolder, int batchNumber)
	{
		int i = 0;
		System.out.println("Processing batch: " + batchNumber);
		File folder = new File(inputFolder);
		File[] listOfFiles = folder.listFiles();
		
		new File(outputFolder + "nonvalid").mkdir();
		
		for(File file : listOfFiles) 
		{
			if(file.isFile())
			{
				i++;
				
				System.out.println("Processing image: " + i);
				
				Mat image = Imgcodecs.imread(inputFolder + file.getName());
				Point matchLoc = null;
				int j = 0;
				
				for(ImageMat template : m_SearchIdentifierTemplates)
				{
					MinMaxLocResult mmr = ImageUtil.matchTemplate(image, template.getImage());
					double matchQuality = mmr.maxVal;
					
					if(matchQuality > 0.7)
					{
						matchLoc = mmr.maxLoc;
						break;
					}
					
					j++;
				}
				
				if(matchLoc != null)
				{
					int allowedXPos = 0;
					
					if(j > 1)
					{
						allowedXPos = (int)matchLoc.x + 120;
					}
					else
					{
						allowedXPos = (int)matchLoc.x - 300;
					}
					
					Point matchLoc2 = null;
					Template closestTemplate = null;
					double highestMatchQuality = 0.0; 
					
					for(Template template : m_LittTemplates)
					{
						MinMaxLocResult mmr = ImageUtil.matchTemplate(image, ImageUtil.bufferedImageToMat(template.getReplacementImage()));
						double matchQuality = mmr.maxVal;
						
						if(matchQuality > 0.8)
						{
							if(matchQuality > highestMatchQuality)
							{
								highestMatchQuality = matchQuality;
								matchLoc2 = mmr.maxLoc;
								closestTemplate = template;
							}	
						}
					}
					
					if(closestTemplate != null)
					{
						if((int)matchLoc2.x < allowedXPos)
						{
							System.out.println("Saving image: " + i + " to wrongly modified folder");
							Imgcodecs.imwrite(outputFolder + file.getName(), image);
							file.delete();
						}
					}
				}
				else
				{
					System.out.println("Saving image: " + i + " to non valid folder");
					Imgcodecs.imwrite(outputFolder + "/nonvalid/" + file.getName(), image);
					file.delete();
				}
				image.release();
			}
		}
	}
}
