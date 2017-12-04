package com.stenkrossstudios.protocolprocessing;

import com.stenkrossstudios.protocolprocessing.image.ImageManager;
import com.stenkrossstudios.protocolprocessing.utils.PDFManager;

public class App 
{
	public static void main(String[] args) 
	{

	}
	
	public static void fullProcess()
	{
		ImageManager.init();
		
		int startBatch = 1;
		int totalBatchCount = 22;
		
		for(int batch = startBatch; batch <= totalBatchCount; batch++)
		{
			PDFManager.convertPDFsToImages("res/pdfs/input/batch" + batch + "/", 
					"res/images/input/batch" + batch + "/");
			
			ImageManager.processImages("res/images/input/batch" + batch + "/", 
					"res/images/output/batch" + batch + "/", batch , 1, "Thread1");
			
			PDFManager.convertImagesToPdfs("res/images/output/batch" + batch + "/", 
					"res/pdfs/output/batch" + batch + "/", batch);
		}
	}
}
