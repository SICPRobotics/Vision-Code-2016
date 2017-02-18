package org.usfirst.frc.team5822.robot;

import java.time.Instant;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.tables.ITableListener;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot 
{
	
	NetworkTable piTable;
	double distance = 0; 
	double center = 0; 
	double lag = 0; 
	double start; 
	
	ITableListener_WB piListen = new ITableListener_WB(); 
	Timer teleTimer; 
	boolean first; 
	String turnFromTable;
	double centerOff; 
	double distanceFromTable; 
	
	SICPRobotDrive myRobot; 	
	
	PIDController gearPID; 
	GearCamPIDOutput GearcamOut; 
	GearCamPIDSource camSource;
	double kPg, kIg, kDg; 
	
	PIDController hgPID; 
	GearCamPIDOutput hgcamOut; 
	GearCamPIDSource hgcamSource;
	double kPhg, kIhg, kDhg; 
	
	XboxController xbox;
	
	boolean gearVision, hGVision; 
	boolean onePressed, twoPressed; 
	boolean noVision; 
	
	@Override
	public void robotInit() 
	{
		piTable.setServerMode();
		piTable = NetworkTable.getTable("piTable"); 
		NetworkTable.setUpdateRate(0.01);
		myRobot = new SICPRobotDrive (0,1,2,3); 
		kPg = .02; 
		kIg = 0; 
		kDg = 0; 
		GearcamOut = new GearCamPIDOutput();
		camSource = new GearCamPIDSource();
		gearPID = new PIDController(kPg, kIg, kDg, camSource, GearcamOut, 0.001); 
		
		hgcamOut = new GearCamPIDOutput();
		hgcamSource = new GearCamPIDSource();
		hgPID = new PIDController(kPhg, kIhg, kDhg, hgcamSource, hgcamOut, 0.001); 
		
		xbox = new XboxController(1);
		noVision = true; 
					
	}

	
	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() 
	{
		
	}
	
	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopInit() 
	{ 
		piListen.setCount(0);
		piTable.addTableListener(piListen, true);
		
		//TODO: this line enables the code that lets it line up off the gear 
		//camPID.enable();
		myRobot.setSafetyEnabled(false);
		
	}
	@Override
	public void teleopPeriodic() 
	{
		
		if (xbox.getRawButton(1))
			onePressed = true; 
		if (xbox.getRawButton(2))
			twoPressed = true; 
		
		if(onePressed && xbox.getRawButton(1)!=true)
		{
			onePressed = false; 
			noVision = !noVision; 
			hGVision = !hGVision;
			gearVision = false; 
			if(hGVision)
				hgPID.enable();
			else
				hgPID.disable();
		}
		
		else if (twoPressed && xbox.getRawButton(2)!=true)
		{
			twoPressed = false; 
			noVision = !noVision ;
			gearVision = !gearVision; 
			hGVision = false; 
			if (gearVision)
				gearPID.enable();
			else
				gearPID.disable();
		}
		
		else if (noVision) 
		{
			hGVision = false; 
			gearVision = false; 
			if(hgPID.isEnabled())
				hgPID.disable();
			if(gearPID.isEnabled())
				gearPID.disable();
		
		}
		
		piTable.putBoolean("HGVision Enabled", hGVision); 
		piTable.putBoolean("Gear Vision Enabled", gearVision); 
		
		if(hGVision)
		{
			System.out.println("HG VISION ENABLED!");
		}
		
		else if(gearVision)
		{
			System.out.println("GEAR VISION ENABLED!"); 
		}
		
		else
			System.out.println("NO VISION ENABLED");
		
		System.out.println(piTable.getBoolean("High Goal Vision from Pi", false));
		System.out.println(piTable.getBoolean("Gear Vision from Pi", false));
		
		if (piTable.getBoolean("High Goal Vision from Pi", false))
		{
			System.out.println("CENTER OF HG: " + piTable.getNumber("Center HG", 0)); 
			System.out.println("DISTANCE TO HG: " + piTable.getNumber("Distance HG", 0)); 
			System.out.println("WIDTH OF HG: " + piTable.getNumber("Width HG", 0)); 
			
		}
		
		else if (piTable.getBoolean("Gear Vision from Pi", false))
		{
			System.out.println("CENTER OF GEAR: " + piTable.getNumber("Center Gear", 0)); 
			System.out.println("DISTANCE TO GEAR: " + piTable.getNumber("Distance Gear", 0)); 
			System.out.println("WIDTH OF GEAR: " + piTable.getNumber("Width Gear", 0)); 
			
		}
		
	
		
		//distanceFromTable = piTable.getNumber("Distance", 0);
		//System.out.println("Distance: " + distanceFromTable);
		
		
	}
	

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
	
	public class HighGoalCamPIDSource implements PIDSource
	{

		@Override
		public void setPIDSourceType(PIDSourceType pidSource) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public PIDSourceType getPIDSourceType() {
			// TODO Auto-generated method stub
			return PIDSourceType.kDisplacement;
		}

		@Override
		public double pidGet() 
		{
			//System.out.println("Center From PID Source: " + piTable.getNumber("Center",0));
			return piTable.getNumber("Center HG", 0); 
		}		
		
		
	}
	
	public class HGCamPIDOutput implements PIDOutput
	{
		double center; 
		double tolerance; 
		
		@Override
		public void pidWrite(double output) 
		{		
			center = Math.abs(piTable.getNumber("Center HG",0));
			tolerance = (1000/piTable.getNumber("Width HG", 1)); 
			
			if (-tolerance<=center && center<=tolerance)
				myRobot.setLeftRightMotorOutputs(0, 0);
		
			else 
				myRobot.setLeftRightMotorOutputs(-.15-(.15*output), -.15+(.15*output));
						
		
		}
		
	}
	
	public class GearCamPIDSource implements PIDSource
	{

		@Override
		public void setPIDSourceType(PIDSourceType pidSource) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public PIDSourceType getPIDSourceType() {
			// TODO Auto-generated method stub
			return PIDSourceType.kDisplacement;
		}

		@Override
		public double pidGet() 
		{
			//System.out.println("Center From PID Source: " + piTable.getNumber("Center",0));
			return piTable.getNumber("Center Gear", 0); 
		}		
		
		
	}
	
	public class GearCamPIDOutput implements PIDOutput
	{
		double center; 
		double distance; 
		double tolerance; 
		
		@Override
		public void pidWrite(double output) 
		{		
			center = Math.abs(piTable.getNumber("Center G",0));
			distance = piTable.getNumber("Distance G",0);
			tolerance = 25+(2*piTable.getNumber("Width G",0)); 
			
			/*System.out.println("TOLERANCE: " +tolerance);*/
			/*
			if (distance>40)
				myRobot.setLeftRightMotorOutputs(-0.3, -0.3);
			
			else if (distance<10)
				myRobot.setLeftRightMotorOutputs(0.15, 0.15);
			else 
				myRobot.setLeftRightMotorOutputs(0, 0);	*/	
						
			//System.out.println("Center from PIDOutput: " + center + "; Distance from PIDOutput: " + distance + "; Output from PIDOutput: " +output);			
			
			if (10<distance && distance<20)
				myRobot.setLeftRightMotorOutputs(0, 0);
			
			else if (distance>20)
			{
				myRobot.setLeftRightMotorOutputs(-.15-(.15*output), -.15+(.15*output));
				System.out.println("FORWARD!");
			}
			
			else if (distance<10)
			{
				myRobot.setLeftRightMotorOutputs(.15, .15);
				System.out.println("BACKWARD!");
			}
			
						
			/*else
			{
				myRobot.setLeftRightMotorOutputs(-.15-(.25)*output, -.15+(.25)*output);
				System.out.println("NOT CENTERED: " +output);
			}*/
			
			
		}
		
	}
}

