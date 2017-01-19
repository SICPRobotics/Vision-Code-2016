package org.usfirst.frc.team5822.robot;



import java.time.Instant;
import java.util.ArrayList;
import org.opencv.core.Core;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import edu.wpi.first.wpilibj.networktables.NetworkTable; 

public class PiWNetworkTables 

{
	public static final int piPort = 27002;
   	public static final int rioPort = 27001; 
   	
   	//is it ok that this vRioariable is static?
   	
	
	public static void main( String[] args)
	{
		
		NetworkTable piVals = NetworkTable.getTable("piTable"); 
		
		double startPro = 0; 
		long startMills = Instant.now().toEpochMilli(); 
		//double start = System.currentTimeMillis();
		
	//	String address = "roboRIO-5822-FRC.local";
		
				
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		VideoCapture capture = new VideoCapture();
		
		//this link has changed, check with code on the pi right now 
		capture.open("http://10.58.22.72/mjpg/video.mjpg");
		
		Mat bgr = new Mat(); 
		Mat hsvConvert = new Mat(); 
		Mat hsv = new Mat();
		MatOfPoint test = new MatOfPoint(); 
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		ArrayList<MatOfPoint> finalContours = new ArrayList<MatOfPoint>();
		MatOfPoint biggest = null; 
		Mat mhierarchy = new Mat(); //have no clue why hierarchy is needed. Still need to figure this one out. 
		double centerX; 
		double distance;
		double [] nums = new double [4]; 
		nums = null; 
		//int greta = 0; 
		
		while (true)  
		{
			
			startPro = piVals.getNumber("RioTime",0); 
			contours.clear(); 
			finalContours.clear(); 

			capture.read(bgr);
			//Imgcodecs.imwrite("greenCart.jpg",bgr);	
			Imgproc.cvtColor(bgr, hsvConvert, Imgproc.COLOR_BGR2HSV); //using RGB does not work. MUST use BGR. We are still unsure of the reasons. 
			Core.inRange(hsvConvert, new Scalar (13, 39, 135), new Scalar (92, 118, 255), hsv); //those two Scalar values are the the max and min HSV values respectively. Those were determined in GRIP. 
			Imgproc.findContours(hsv, contours, mhierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE); //first enum lets you control things about hierarchy, I chose option with no hierarchy 

			int idex=0; 
			int size = contours.size();
			
			//changed to a for loop 
			for (idex=0; idex < contours.size(); idex++)
			{
				test = contours.get(idex); 
				if (Imgproc.contourArea(test)>100)
				{
					finalContours.add(test); 
				}
				idex++; 
			}
			
			
			if (finalContours.size()>0)
			{
				nums = findCenterXDistance(finalContours.get(0).toList());
				//System.out.println("CENTER X COORIDINATE OF CONTOUR " +0+ ": " + nums[2]);
				System.out.println("DISTANCE TO CONTOUR " +0+ ": " + (12*nums[3]));
				piVals.putNumber("Center", nums[2]);
				piVals.putNumber("Distance", (12*nums[3])); 
				piVals.putNumber("Start Time", startPro); 
				
			}
			
			
		
			//for (int count = 0; count < finalContours.size(); count++)
			//{ 
				
			//	nums = findCenterXDistance(finalContours.get(count).toList());
				//System.out.println("CENTER X COORIDINATE OF CONTOUR " +count+ ": " + nums[2]);
			//	System.out.println("DISTANCE TO CONTOUR " +count+ ": " + (12*nums[3]));
			//	robot.Send(rioMillis, true, nums[2], nums[3]);  
								
			//}		
				
		}
	}	

	public static double [] findCenterXDistance (java.util.List<org.opencv.core.Point> test)
	{ 
		double maxX = -10000;
		double minX = 10000;  
		double [] vals = new double [4]; 
		for (int idex2=0; idex2 < test.size(); idex2++)
		{
			if (test.get(idex2).x > maxX)
				maxX = test.get(idex2).x; 
			if (test.get(idex2).x < minX)
				minX = test.get(idex2).x; 
		}
			
		vals [0] = maxX; 
		vals [1] = minX; 
		vals [2] = (maxX+minX)/2; 
		vals [3] = ((.895833333*320)/(2*(maxX-minX)*0.38901939)); // d = TftFOVpx/2TpxtanO, calculated angle was 21.257
		return vals; 
		
				

	}	

	

	public static double findMaxX (java.util.List<org.opencv.core.Point> test)
	{ 
		double maxX = -10000; 
		for (int idex2=0; idex2 < test.size(); idex2++)
		{
			if (test.get(idex2).x > maxX)
				maxX = test.get(idex2).x; 
		}
			
		return maxX; 
		
	}
	
	public static double findMinX (java.util.List<org.opencv.core.Point> test)
	{ 
		double minX = 10000; 
		for (int idex2=0; idex2 < test.size(); idex2++)
		{
			if (test.get(idex2).x < minX)
				minX = test.get(idex2).x; 
		}
			
		return minX;
		
	}
}
