import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class IndexingServer
{
	//Variable declaration
	private static int intIndexServerPort = 0;
	private static Socket sockIndex = null;
	private static ServerSocket listenerIndex;

	public static void main(String[] args) throws Exception
	{
		//Reading parameters' data from Properties file starts from here
		Properties p2pProp = new Properties();
		try
		{
			p2pProp.load(new FileInputStream("./p2pprop.properties"));
			intIndexServerPort = Integer.parseInt(p2pProp.getProperty("intIndexServerPort"));
		}

		catch (IOException e)
		{
			System.out.println (e.getStackTrace());
			System.exit(1);
		}
		//Reading parameters' data from Properties file ends over here

		if (intIndexServerPort != 0)
		{
			//Starting the listener for Index Server
			try
			{
				listenerIndex = new ServerSocket(intIndexServerPort);
				System.out.println("The Index server is running.");
				while (true)
				{
					sockIndex = listenerIndex.accept();
					System.out.println("Indexing server accepted connection from peer " + sockIndex);
					Thread t = new Thread (new RegisterSearch(sockIndex));
					t.start();
				}
			}

			catch (Exception e)
			{
				System.err.println("Index Server Port " + intIndexServerPort + " is already utilized for listening to the Index Server connections, please check another port");
				System.exit(1);
			}

			finally
			{
				if (listenerIndex!= null)
				{
					listenerIndex.close();
				}
				if (sockIndex!= null)
				{
					sockIndex.close();
				}
			}
		}
		else
		{
			//Unable to start the Index Server due to unavailability of all or some of the mandatory parameters
			String strErrorMsg = "Unable to proceed due to unavailability of \n";
			if (intIndexServerPort == 0)
			{
				strErrorMsg += "Index Server Port variable\n";
			}
			System.out.println (strErrorMsg);
			System.exit(1);
		}
	}
}