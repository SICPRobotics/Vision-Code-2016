package org.usfirst.frc.team5822.robot;

import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

public class ITableListener_WB implements ITableListener 
{
	int count = 0; 
	
	public ITableListener_WB()
	{
		count = 0; 
	}
	
	public void setCount(int val)
	{
		count = val; 
	}

	@Override
	public void valueChanged(ITable source, String key, Object value, boolean isNew) 
	{
		//System.out.println("Key: " + key + "; Vale: " + value);
		if (key.equals("Count"))
		{
			count++; 
					
			/*double distance = source.getNumber("Distance", 0); 
			double center = source.getNumber("Center", 0);
			double frames = source.getNumber("Count", -10);
			//System.out.println("Distance: " + distance + "; Center: " + center + "; Count: " + frames + "; ListenCount: " + count);
*/			
		}
		// TODO Auto-generated method stub
		
	}
	
	public int count()
	{
		return count; 
	}

}
