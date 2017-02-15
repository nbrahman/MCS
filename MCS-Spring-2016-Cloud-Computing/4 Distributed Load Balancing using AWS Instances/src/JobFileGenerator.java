import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class JobFileGenerator
{
	public static void main(String[] args)
	{
		BufferedWriter writer = null;
		try
		{
			//create a temporary file
			File logFile = new File(args[2]);

			writer = new BufferedWriter(new FileWriter(logFile));
			for (int i=0; i<Integer.parseInt(args[0]);i++)
			{
				writer.write(args[1]+"\n");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				// Close the writer regardless of what happens...
				writer.close();
			}
			catch (Exception e)
			{
			}
		}
	}
}
