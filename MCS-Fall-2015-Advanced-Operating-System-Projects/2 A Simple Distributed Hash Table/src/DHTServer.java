import java.io.FileInputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Properties;
import java.net.InetAddress;
import java.io.DataOutputStream;

public class DHTServer
{
	//Variable declaration
	private static int intDHTServerPort = 0;
	private static Socket sockDHT = null;
	private static ServerSocket lstnrDHT;
	private static String strServerLogFileName = "";
	private static int intClientID = 0;

	public static void main(String[] args) throws Exception
	{
		String [] arrIPAddress = new String [50];
		int i = 0;
		String strKeyName = "";
		//Reading parameters' data from Properties file starts from here
		Properties dhtProp = new Properties();
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
					i++;
				}
			}

			dhtProp.load(new FileInputStream("./dht.properties"));
			for (String key : dhtProp.stringPropertyNames())
			{
				if (key.substring(key.length() - 2).toUpperCase().equals("IP"))
				{
					String value = dhtProp.getProperty(key);
					for (i=0; i < arrIPAddress.length; i++)
					{
						if (arrIPAddress[i] != null)
						{
							if (arrIPAddress[i].equals("/" + value))
							{
								strKeyName = key;
							}
						}
					}
				}
			}
			intDHTServerPort = Integer.parseInt(dhtProp.getProperty(strKeyName.replace("IP", "Port")));
			strServerLogFileName = dhtProp.getProperty(strKeyName.replace("IP", "LogFile"));
		}

		catch (IOException e)
		{
			System.out.println (e.getStackTrace());
			System.exit(1);
		}
		//Reading parameters' data from Properties file ends over here

		if (intDHTServerPort != 0)
		{
			//Starting the listener for Index Server
			try
			{
				lstnrDHT = new ServerSocket(intDHTServerPort);
				System.out.println("The Distributed Hash Table server is running.");
				while (true)
				{
					sockDHT = lstnrDHT.accept();
					System.out.println("Distributed Hash Table server accepted connection from peer " + sockDHT);
					intClientID = intClientID + 1;
					DataOutputStream dos = new DataOutputStream (sockDHT.getOutputStream());
					dos.writeInt(intClientID);
					dos.flush();
					Thread t = new Thread (new DHTOperations(sockDHT, strServerLogFileName));
					t.start();
				}
			}

			catch (Exception e)
			{
				System.err.println("Distributed Hash Table Server Port " + intDHTServerPort + " is already utilized for listening to the Distributed Hash Table Server connections, please check another port");
				System.exit(1);
			}

			finally
			{
				if (lstnrDHT!= null)
				{
					lstnrDHT.close();
				}
				if (sockDHT!= null)
				{
					sockDHT.close();
				}
			}
		}
		else
		{
			//Unable to start the Index Server due to unavailability of all or some of the mandatory parameters
			String strErrorMsg = "Unable to proceed due to unavailability of \n";
			if (intDHTServerPort == 0)
			{
				strErrorMsg += "Distributed Hash Table Server Port variable\n";
			}
			System.out.println (strErrorMsg);
			System.exit(1);
		}
	}
}