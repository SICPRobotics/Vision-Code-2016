import java.io.FileWriter;
import java.io.IOException;

public class JsonTests119 
{
	static FileWriter file; 
	
	public static void main (String [] args)
	{ 
		try {
			file = new FileWriter("/Users/sicp/logfiles/test1.txt");
		} catch (IOException e) {
			System.out.println("Failed to Make Log File: " + e ); 
			e.printStackTrace();
		} 
		
		try {
			file.write("THIS WORKED!!");
			file.write("Greta was here and so was Angela");
		
		} catch (IOException e){ 
			System.out.println("Failed to write to file" +e);
		}
		
		try {
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Could not close file");
		}
	}
}
