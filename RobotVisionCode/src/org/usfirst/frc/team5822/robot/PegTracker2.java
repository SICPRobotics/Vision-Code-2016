package org.usfirst.frc.team5822.robot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.TreeSet;

import java.util.Iterator;

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

public class PegTracker2

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
		Rect testRect;
		Rect bestRect; 
		Rect boundingRect; 
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		VideoCapture capture = new VideoCapture();
		//this link continually changes, find a way to set a static ip address 
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
		double targetRatio = 0; 
		double centerPixel = 0; 
		double bestArea = 0; 
		double boundMaxX, boundMinX, boundMaxY, boundMinY; 
		TreeSet<Integer> myTree = new TreeSet<Integer>(); 
		
		System.out.println("Going to the while"); 
		
		while (true)  
		{
			//reset variables
			contours.clear(); 
			finalContours.clear(); 
			double testArea = 0; 
			bestArea = 0; 
			bestContour = null; 
			bestRect = null; 
			
			//find all contours that are within HSV range
			capture.read(bgr);
			Imgproc.cvtColor(bgr, hsvConvert, Imgproc.COLOR_BGR2HSV); //using RGB does not work. MUST use BGR. We are still unsure of the reasons. 
			Core.inRange(hsvConvert, new Scalar (47, 0, 19), new Scalar (108, 255, 255), hsv); //those two Scalar values are the the max and min HSV values respectively. Those were determined in GRIP. 
			Imgproc.findContours(hsv, contours, mhierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE); //first enum lets you control things about hierarchy, I chose option with no hierarchy 
			Imgproc.drawContours(bgr, contours,0,new Scalar (0,255,0),1); 
			
			int idex=0; 
			int size = contours.size();
			
			//find the largest contour with geometric similarity to target 
			for (idex=0; idex < contours.size(); idex++)
			{
				test = contours.get(idex); 
				testArea = Imgproc.contourArea(test); 
				testRect = Imgproc.boundingRect(test);
				targetRatio = testRect.height/testRect.width; 
				if (targetRatio <= 3 && targetRatio >= 2 && testArea>bestArea)
				{
					bestContour = test; 
					bestArea = testArea; 
				}
			}
			
						
			if (bestContour!=null)
			{ 
				bestRect = Imgproc.boundingRect(bestContour); 
				
				//build a bounding box that covers the area we expect to find the other target in 
				boundMaxX = bestRect.x + (bestRect.width*8); 
				boundMaxY = bestRect.y + (bestRect.height *.25); 
				boundMinX =  bestRect.x - (bestRect.width*7); 
				boundMinY = bestRect.y - (bestRect.height *1.25);  
				
				boundingRect = new Rect ((int)boundMinX, (int)boundMaxY, (int)(boundMaxX-boundMinX), (int)(boundMaxY-boundMinY));
				//TODO: draw out this rectangle on the image and save it 
				
				for (idex=0; idex<contours.size(); idex++)
				{
					test = contours.get(idex); 
					testRect = Imgproc.boundingRect(test); 
					if((testRect.x + testRect.width) < boundMaxX && testRect.x > boundMinX && testRect.y < boundMaxY && (testRect.y - testRect.height) > boundMinY)
					{
						finalContours.add(test); 
						Imgproc.rectangle(bgr, new Point (testRect.x, testRect.y),  new Point (testRect.x+testRect.width, testRect.y+testRect.height), new Scalar (0,0,255), 3);
					}
				}

				if (finalContours.isEmpty())
				{
					if (bestRect.x>160)
					{
						centerPixel = findCenter(bestRect, false); 
					}

					else
						centerPixel = findCenter(bestRect, true); 
				}

				else 
				{
					for (int foo=0; foo<finalContours.size(); foo++)
					{
						testRect = Imgproc.boundingRect(finalContours.get(foo));
						myTree.add(testRect.x+(testRect.width/2)); 
					}

					Iterator<Integer> myIt = myTree.iterator(); 
					int median = 0; 

					for(int i =0; i<=finalContours.size()/2; i++)
					{
						median = (int) myIt.next();
					}

					if (median > bestRect.x+bestRect.width/2)
						centerPixel = findCenter(bestRect, false); 
					else 
						centerPixel = findCenter(bestRect, true); 

				}
			}
			else 
			{
				centerPixel = 0; 
				distance = 0; 
			}
			
			piVals.putNumber("Center", centerPixel -160); 
			piVals.putNumber("Distance", findDistance(bestRect)); 
		}	
			
		
	}	
	
	public static double findCenter (Rect rect, boolean right)
	{
		if (right)
			return (rect.x) - (3.25*(rect.width/2)); 
		else
			return (rect.x+rect.width) + (3.25*(rect.width/2)); 
	}
	
	public static double findDistance (Rect rect)
	{
		return ((0.1666666667*320)/(2*rect.width*0.38901939)); // d = TftFOVpx/2TpxtanO, calculated angle was 21.257
	}

	/*public static double [] findCenterXYDistance (java.util.List<org.opencv.core.Point> test)
	{ 
		double maxX = -10000;
		double minX = 10000;  
		double maxY = -10000;
		double minY = 10000; 
		double [] vals = new double [5]; 
		
		for (int idex2=0; idex2 < test.size(); idex2++)
		{
			if (test.get(idex2).x > maxX)
				maxX = test.get(idex2).x; 
			else if (test.get(idex2).x < minX)
				minX = test.get(idex2).x; 
			
			if (test.get(idex2).y > maxY)
				maxY = test.get(idex2).y; 
			else if (test.get(idex2).y < minY)
				minY = test.get(idex2).y; 


		}
			
		vals [0] = maxX; 
		vals [1] = maxX-minX; 
		vals [2] = maxY; 
		vals [3] = maxY-minY; 

		//the first number in the equation is the width of the object
		vals [4] = ((0.1666666667*320)/(2*(maxX-minX)*0.38901939)); // d = TftFOVpx/2TpxtanO, calculated angle was 21.257
		return vals; 
		
	
*/
	//public static void printArray (double [] arr)
	//{
	//	System.out.println("MaxX: " +arr[0]+ "; Width: " + arr[1] + "; MaxY: " + arr[2] + "; Height: " + arr[3]); 
	//}

}
