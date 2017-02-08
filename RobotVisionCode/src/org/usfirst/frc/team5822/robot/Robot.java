package org.usfirst.frc.team5822.robot;

import java.time.Instant;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Timer;
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
	
	PIDController camPID; 
	CamPIDOutput camOut; 
	CamPIDSource camSource;
	double kP, kI, kD; 
	
	
	@Override
	public void robotInit() 
	{
		piTable.setServerMode();
		piTable = NetworkTable.getTable("piTable"); 
		NetworkTable.setUpdateRate(0.01);
		myRobot = new SICPRobotDrive (0,1,2,3); 
		kP = .02; 
		kI = 0; 
		kD = 0; 
		camOut = new CamPIDOutput();
		camSource = new CamPIDSource();
		camPID = new PIDController(kP, kI, kD, camSource, camOut, 0.001); 
	
				
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
		camPID.enable();
		myRobot.setSafetyEnabled(false);
		
	}
	@Override
	public void teleopPeriodic() 
	{
		
		distanceFromTable = piTable.getNumber("Distance", 0);
		//System.out.println("Distance: " + distanceFromTable);
		
		
	}
	

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
	
	public class CamPIDSource implements PIDSource
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
			return piTable.getNumber("Center", 0); 
		}		
		
		
	}
	
	public class CamPIDOutput implements PIDOutput
	{
		double center; 
		double distance; 
		double tolerance; 
		
		@Override
		public void pidWrite(double output) 
		{		
			center = Math.abs(piTable.getNumber("Center",0));
			distance = piTable.getNumber("Distance",0);
			tolerance = 25+(2*piTable.getNumber("Width",0)); 
			System.out.println("TOLERANCE: " +tolerance);
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

