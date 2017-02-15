import java.io.FileInputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Properties;
import java.net.InetAddress;
import java.io.DataOutputStream;

public class IndexingServer6
{
	//Variable declaration
	private static int intIndexingServerPort = 0;
	private static Socket sockIndexing = null;
	private static ServerSocket lstnrIndexing;
	private static String strServerLogFileName = "";
	private static int intClientID = 0;
	private static String strDelimiter = "%@$#";

	public static void main(String[] args) throws Exception
	{
		String [] arrIPAddress = new String [50];
		int i = 0;
		int intBufferSize=0;
		String strKeyName = "";
		//Reading parameters' data from Properties file starts from here
		Properties dcp2pProp = new Properties();
		try
		{
			Enumeration <NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements())
			{
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration <InetAddress> ee = n.getInetAddresses();
				while (ee.hasMoreElements())
				{
					arrIPAddress [i] = ee.nextElement().toString();
					//System.out.println(arrIPAddress [i]);
					i++;
				}
			}

			dcp2pProp.load(new FileInputStream("./dcp2p.properties"));
			for (String key : dcp2pProp.stringPropertyNames())
			{
				//System.out.println(key.substring(0, 8).toUpperCase()+"\n");
				if ((key.substring(key.length() - 2).toUpperCase().equals("IP")) && (key.substring(0, 8).toUpperCase().equals("STRINDEX")))
				{
					String value = dcp2pProp.getProperty(key);
					for (i=0; i < arrIPAddress.length; i++)
					{
						if (arrIPAddress[i] != null)
						{
							if (arrIPAddress[i].equals("/" + value))
							{
								//System.out.println("Inside final if\n");
								strKeyName = key;
							}
						}
					}
				}
			}
			//System.out.println(strKeyName);
			String strPropertyName = strKeyName.replace("IP", "Port");
			strPropertyName = strPropertyName.replace("str", "int");
			intBufferSize = Integer.parseInt(dcp2pProp.getProperty("intBufferSize"));
			intIndexingServerPort = Integer.parseInt(dcp2pProp.getProperty(strPropertyName));
			strServerLogFileName = dcp2pProp.getProperty(strKeyName.replace("IP", "LogFile"));
			strDelimiter = dcp2pProp.getProperty("strDelimiter");
			intIndexingServerPort = 50500;
		}

		catch (IOException e)
		{
			System.out.println (e.getStackTrace());
			System.exit(1);
		}
		//Reading parameters' data from Properties file ends over here

		if (intIndexingServerPort != 0)
		{
			//Starting the listener for Index Server
			try
			{
				lstnrIndexing = new ServerSocket(intIndexingServerPort);
				System.out.println("The Distributed Indexing Server is running.");
				while (true)
				{
					sockIndexing = lstnrIndexing.accept();
					System.out.println("Distributed Indexing Server accepted connection from peer " + sockIndexing);
					intClientID = intClientID + 1;
					DataOutputStream dos = new DataOutputStream (sockIndexing.getOutputStream());
					dos.writeUTF(Integer.toString(intClientID));
					dos.flush();
					Thread t = new Thread (new RegisterSearch(sockIndexing, strServerLogFileName, strDelimiter, intBufferSize));
					t.start();
				}
			}

			catch (Exception e)
			{
				System.err.println("Distributed Indexing Server Port " + intIndexingServerPort + " is already utilized for listening to the Distributed Indexing Server connections, please check another port");
				System.exit(1);
			}

			finally
			{
				if (lstnrIndexing!= null)
				{
					lstnrIndexing.close();
				}
				if (sockIndexing!= null)
				{
					sockIndexing.close();
				}
			}
		}
		else
		{
			//Unable to start the Index Server due to unavailability of all or some of the mandatory parameters
			String strErrorMsg = "Unable to proceed due to unavailability of \n";
			if (intIndexingServerPort == 0)
			{
				strErrorMsg += "Distributed Indexing Server Port variable\n";
			}
			System.out.println (strErrorMsg);
			System.exit(1);
		}
	}
}