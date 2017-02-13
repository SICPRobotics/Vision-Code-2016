package org.usfirst.frc.team5822.robot;

import org.opencv.core.Mat;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	UsbCamera cam0; 
	UsbCamera cam1; 
	Mat image;
	Mat image1; 
	CvSink cvSink; 
	CvSource cvSource;
	CvSink cvSink1; 
	CvSource cvSource1;
	
	boolean centeredOnGearPeg = false; 
	boolean gearVisionEnabled = false; 
	boolean highGoalVisionEnabled = false; 
	boolean flyWheelSpinning = false; 
	boolean shooting = false; 
	boolean climberEnabled = false; 
	
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	
	SendableChooser<String> chooseAlliance = new SendableChooser<>(); 
	SendableChooser<String> chooseShoot = new SendableChooser<>(); 
	SendableChooser<String> chooseGear = new SendableChooser<>(); 
	SendableChooser<String> chooseOrder = new SendableChooser<>(); 
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
	
		
		chooseShoot.addObject("Cross Baseline Only", "crossOnly");
		chooseShoot.addObject("Shoot Only", "shootOnly");
		chooseShoot.addObject("Gear Only", "gearOnly");
		chooseShoot.addObject("Gear and Shoot", "gearAndShoot");
		
		chooseGear.addObject("Peg Position 1", "peg1");
		chooseGear.addObject("Peg Poistion 2", "peg2");
		chooseGear.addObject("Peg Position 3", "peg3");
		
		chooseAlliance.addObject("Red Alliance", "red");
		chooseAlliance.addObject("Blue Alliance", "blue");
		
		chooseOrder.addObject("Peg First", "pegFirst");
		chooseOrder.addObject("Shoot First", "shootFirst");
		chooseOrder.addObject("Neither", "neither");
			
		SmartDashboard.putData("Auto Gear", chooseGear);
		SmartDashboard.putData("Auto Shoot", chooseShoot);
		SmartDashboard.putData("Auto Alliance", chooseAlliance);
		SmartDashboard.putData("Auto Order", chooseOrder);
			
		
		SmartDashboard.putBoolean("Centered on Gear Peg", centeredOnGearPeg); 
		SmartDashboard.putBoolean("Gear Vision Enabled", gearVisionEnabled); 
		SmartDashboard.putBoolean("Fly Wheel Spinning", flyWheelSpinning); 
		SmartDashboard.putBoolean("Shooting", shooting); 
		SmartDashboard.putBoolean("Climber Enabled", climberEnabled); 
		SmartDashboard.putBoolean("High Goal Vision Enabled", highGoalVisionEnabled); 
		
		
	  			
		
		Thread t = new Thread(() -> {
			cam0 = new UsbCamera ("USB Camera 0", 0);
			cam0.setResolution(320,240);
			cam0.setFPS(20);
			
			cam1 = new UsbCamera ("USB Camera 1", 1);
			cam1.setResolution(320,240);
			cam1.setFPS(20);
			
			cvSink = CameraServer.getInstance().getVideo(cam0);
			cvSink.setEnabled(true);
			cvSource = CameraServer.getInstance().putVideo("Current View", 320, 240);
			image = new Mat();	 
			
			cvSink1 = CameraServer.getInstance().getVideo(cam1);
			cvSink1.setEnabled(true);
			cvSource1 = CameraServer.getInstance().putVideo("Current View 1", 320, 240);
			image1 = new Mat();	
			
			while(!Thread.interrupted()) 
			{
				cvSink.grabFrame(image);
				cvSource.putFrame(image);
			
				cvSink1.grabFrame(image1);
				cvSource1.putFrame(image1);
			}
		}
		);
        t.start();
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
	 // 
	  * 
	  */
	@Override
	public void autonomousInit() 
	{
		
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() 
	{
		gearVisionEnabled = true; 
		shooting = false; 
		SmartDashboard.putBoolean("Centered on Gear Peg", centeredOnGearPeg); 
		SmartDashboard.putBoolean("Gear Vision Enabled", gearVisionEnabled); 
		SmartDashboard.putBoolean("Fly Wheel Spinning", flyWheelSpinning); 
		SmartDashboard.putBoolean("Shooting", shooting); 
		SmartDashboard.putBoolean("Climber Enabled", climberEnabled); 
		SmartDashboard.putBoolean("High Goal Vision Enabled", highGoalVisionEnabled);
	
	}
	

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() 
	{
				 
		shooting = true; 
		gearVisionEnabled = false; 
		SmartDashboard.putBoolean("Centered on Gear Peg", centeredOnGearPeg); 
		SmartDashboard.putBoolean("Gear Vision Enabled", gearVisionEnabled); 
		SmartDashboard.putBoolean("Fly Wheel Spinning", flyWheelSpinning); 
		SmartDashboard.putBoolean("Shooting", shooting); 
		SmartDashboard.putBoolean("Climber Enabled", climberEnabled); 
		SmartDashboard.putBoolean("High Goal Vision Enabled", highGoalVisionEnabled);
		
		
		
	}
	
	@Override 
	public void teleopInit()
	{
	
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}

