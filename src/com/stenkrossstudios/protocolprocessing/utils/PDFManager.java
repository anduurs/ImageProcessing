package com.stenkrossstudios.protocolprocessing.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

public class PDFManager {
	
	public static void convertPDFsToImages(String inputFolderPath, String outputFolderPath)
	{
		File folder = new File(inputFolderPath);
		File[] listOfFiles = folder.listFiles();
		
		int i = 1;
		
		for(File file : listOfFiles)
		{
			try {
				PDDocument document = PDDocument.load(new File(inputFolderPath + file.getName()));
				PDFRenderer pdfRenderer = new PDFRenderer(document);
				
				System.out.println("PDF: " + i++);
				
				for (int page = 0; page < document.getNumberOfPages(); ++page)
				{
					BufferedImage image = pdfRenderer.renderImageWithDPI(page, 200, ImageType.RGB);
					ImageIOUtil.writeImage(image, outputFolderPath + file.getName() + "-" + (page+1) + ".png", 200);
				}
				
			} catch (InvalidPasswordException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void convertImagesToPdfs(String inputFolderPath, String outputFolderPath, int batchNumber)
	{
		File inputFolder = new File(inputFolderPath);
		File[] listOfInputFiles = inputFolder.listFiles();
		
		String previousID = "";
		PDDocument document = null;

		int i = 0;
		System.out.println("Converting batch: " + batchNumber + " to pdfs");
		
		for(File file : listOfInputFiles) 
		{
			if(file.isFile())
			{
				System.out.println("Converting image: " + (i+1) + " to a page in pdf");
				if(i == 0)
				{
					
					previousID = getPdfFileName(file.getName(), batchNumber);
					System.out.println("Processing pdf id: " + previousID);
					document = new PDDocument();
					addPageToPdf(file, inputFolderPath, document);
				}
				else
				{
					String id = getPdfFileName(file.getName(), batchNumber);
					
					if(id.equals(previousID))
					{
						System.out.println("Processing pdf id: " + previousID);
						addPageToPdf(file, inputFolderPath, document);
					}
					else
					{
						try {
							System.out.println("Pdf id: " + previousID +" completed");
							document.save(outputFolderPath + previousID + ".pdf");
							document.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						previousID = getPdfFileName(file.getName(), batchNumber);
						System.out.println("Processing pdf id: " + previousID);
						document = new PDDocument();
						addPageToPdf(file, inputFolderPath, document);
					}
				}
			}
			i++;
		}
		
		try {
			System.out.println("Pdf id: " + previousID +" completed");
			document.save(outputFolderPath + previousID + ".pdf");
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void addPageToPdf(File file, String inputFolder, 
			PDDocument document)
	{
		try {
			InputStream in = new FileInputStream(file);
			BufferedImage image = ImageIO.read(in);
			PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
			document.addPage(page); 

			PDImageXObject pdfImage = PDImageXObject.createFromFile(inputFolder + "/"+ file.getName(), document);
			PDPageContentStream contentStream = new PDPageContentStream(document, page);
			contentStream.drawImage(pdfImage, 0, 0);

			contentStream.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String getPdfFileName(String fileName, int batchNumber)
	{
		if(batchNumber < 3)
		{
			String[] parts = fileName.split("-");
			StringBuilder sb = new StringBuilder();
			
			for(int i = 0; i < parts.length - 1; i++)
			{
				if(i == parts.length - 2)
					sb.append(parts[i]);
				else
					sb.append(parts[i]).append("-");
			}
			
			return sb.toString();
		}
		else
		{
			String[] parts = fileName.split(Pattern.quote("."));
			return parts[0];
		}
	}
}
