package org.usfirst.frc.team5822.robot;


import java.util.ArrayList;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.core.Point; 

import edu.wpi.first.wpilibj.networktables.NetworkTable; 

public class HighGoalTracker1

{
	
	public static void main( String[] args)
	{
		
		NetworkTable.setClientMode();
		NetworkTable.setNetworkIdentity("Raspberry Pi");
		NetworkTable.setTeam(5822); 
		NetworkTable.initialize();
		NetworkTable.setUpdateRate(0.01); 
		NetworkTable piVals = NetworkTable.getTable("piTable"); 
		
		MatOfPoint bestContour = null; 
		Rect testRect = null;
		Rect testRect2; 
		Rect bestRect; 
			
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		VideoCapture capture = new VideoCapture();
		//this link continually changes, find a way to set a static ip address 
		capture.open("http://10.58.22.72/mjpg/video.mjpg");
		
		Mat bgr = new Mat(); 
		Mat hsvConvert = new Mat(); 
		Mat hsv = new Mat();
		MatOfPoint test = new MatOfPoint(); 
		MatOfPoint test2 = new MatOfPoint(); 
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		ArrayList<MatOfPoint> finalContours = new ArrayList<MatOfPoint>();
		Mat mhierarchy = new Mat(); //have no clue why hierarchy is needed. Still need to figure this one out. 
		double distance;
		double targetRatio = 0; 
		double targetRatio2 = 0; 
		double centerPixel = 0; 
		double bestArea = 0; 
		double boundMaxX, boundMinX, boundMaxY, boundMinY; 
		int oneCount = 0; 
		System.out.println("Going to the while"); 
		int bestCIndex = 0;
		boolean foundPair;  
		
		while (true)  
		{
			System.out.println("TRIAL NUMBER " + oneCount); 

			//reset variables
			contours.clear(); 
			finalContours.clear();
			foundPair = false; 
			
			double testArea = 0;
			double testArea2 = 0; 
			bestArea = 0; 
			bestContour = null; 
			bestRect = null; 
					
			//find all contours that are within HSV range
			capture.read(bgr);
			//Imgcodecs.imwrite("/home/pi/dev/Vision/PiCode/PiPics/RawImage" + oneCount+".jpg", bgr);
			Imgproc.cvtColor(bgr, hsvConvert, Imgproc.COLOR_BGR2HSV); //using RGB does not work. MUST use BGR. We are still unsure of the reasons. 
			Core.inRange(hsvConvert, new Scalar (41, 82, 29), new Scalar (82, 255, 198), hsv); //those two Scalar values are the the max and min HSV values respectively. Those were determined in GRIP. 
			Imgproc.findContours(hsv, contours, mhierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE); //first enum lets you control things about hierarchy, I chose option with no hierarchy 
			//Imgproc.drawContours(bgr, contours,0,new Scalar (255,0,0),3); 
			Imgcodecs.imwrite("/home/pi/dev/Vision/PiCode/HighGoalPics/rawImg" + oneCount+".jpg", bgr);
			
			int idex=0; 
					
			//find the largest contour with geometric similarity to target 
			for (idex=0; idex < contours.size(); idex++)
			{
				test = contours.get(idex); 
				testArea = Imgproc.contourArea(test); 
				testRect = Imgproc.boundingRect(test);
				targetRatio = testRect.height/testRect.width; 
				
				if (targetRatio <= 1 && testArea>50)
				{
					System.out.println("ONE CONTOUR PASSED!"); 
					Imgproc.rectangle(bgr, new Point(testRect.x, testRect.y), new Point(testRect.x+testRect.width, testRect.y+testRect.height), new Scalar(255,0,0), 2);
					
					for (int inc = 0; inc < contours.size(); inc++) 
					{
						if (inc == idex)
							continue; 
						test2 = contours.get(inc); 
						testArea2 = Imgproc.contourArea(test2); 
						testRect2 = Imgproc.boundingRect(test2);
						targetRatio2 = testRect2.height/testRect2.width; 
						double tripleH = testRect2.height*2.5; 
						double threeHalveH = testRect2.height*1.5; 
						
						//TODO: I made this restraints less stingy 
						if (targetRatio2 <=1 
                                                    && testArea2>50 
						    && testRect.height < tripleH 
						    && testRect.height > threeHalveH 
						    && testRect2.y < (testRect.y+(3*testRect.height))
						    && testRect2.y > (testRect.y+testRect.height) 
						)

						{
														
							System.out.println("TWO CONTOURS PASSED!"); 
							System.out.println("WIDTH OF TOP CONTOUR: " + testRect.width);
							distance = findDistance(testRect);
							System.out.println("DISTANCE TO CONTOURS: " + distance); 
							//System.out.println("Height of TestRect: " + testRect.height); 
							//System.out.println("Height of TestRect2: " + testRect2.height); 
							//System.out.println("Height of TestRect Y Coordinate: " + testRect.y); 
							//System.out.println("Height of TestRect2 Y Coordinate: " + testRect2.y); 
							bestContour = test; 
							bestArea = testArea; 
							bestCIndex = idex;
							bestRect = testRect; 
							
							Imgproc.rectangle(bgr, new Point(testRect.x, testRect.y), new Point(testRect.x+testRect.width, testRect.y+testRect.height), new Scalar(0,255,0), 7);
							Imgproc.rectangle(bgr, new Point(testRect2.x, testRect2.y), new Point(testRect2.x+testRect2.width, testRect2.y+testRect2.height), new Scalar(0,0,255), 3);
							foundPair = true; 
							break; 
						}
						
						 
						
						if (foundPair)
							break; 
					}	 
				if (foundPair)
					break; 
					
				}
			}
			
								
			if (bestContour!=null)
			{ 
				contours.remove(bestCIndex); 
				centerPixel = bestRect.x+(bestRect.width/2); 
				distance = findDistance(bestRect); 
				
				piVals.putNumber("Center", centerPixel); 
				piVals.putNumber("Distance", distance); 
				piVals.putNumber("Width", bestRect.width); 
				
			}
			
			else
			{
				piVals.putNumber("Center", 0); 
				piVals.putNumber("Distance", 0); 
				piVals.putNumber("Width", 0); 
			}
										
			Imgcodecs.imwrite("/home/pi/dev/Vision/PiCode/HighGoalPics/outputFrom" + oneCount+".jpg", bgr);
			oneCount++; 
		}	
		
		
	}	
	
	
	public static double findDistance (Rect rect)
	{
		return (1000/rect.width)*4.2241;
	}

}
