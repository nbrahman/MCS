import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;

public class PeerServer5
{
	static int intPeerServerPort = 0;
	private static Socket sockPeerServer = null;
	private static ServerSocket lstnrPeerServer;

	public static void main(String[] args) throws Exception
	{
		int intBufferSize = 0;
		String [] arrIPAddress = new String [50];
		int i = 0;
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
				if ((key.substring(key.length() - 2).toUpperCase().equals("IP")) && (key.substring(0, 7).toUpperCase().equals("STRPEER")))
				{
					String value = dcp2pProp.getProperty(key);
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
			//System.out.println(strKeyName);
			String strPropertyName = strKeyName.replace("IP", "Port");
			strPropertyName = strPropertyName.replace("str", "int");
			intPeerServerPort = Integer.parseInt(dcp2pProp.getProperty(strPropertyName));
			intBufferSize = Integer.parseInt(dcp2pProp.getProperty("intBufferSize"));
			//strServerLogFileName = dcp2pProp.getProperty(strKeyName.replace("IP", "LogFile"));
			//strDelimiter = dcp2pProp.getProperty("strDelimiter");
			intPeerServerPort = 25400;
		}

		catch (IOException e)
		{
			System.out.println (e.getStackTrace());
			System.exit(1);
		}
		//Reading parameters' data from Properties file ends over here

		if ((intBufferSize != 0) && (intPeerServerPort != 0))
		{
			//Starting the listener for Peer Server
			try
			{
				lstnrPeerServer = new ServerSocket(intPeerServerPort);
				System.out.println("The Peer Server is running.");
				int intFileNumber = 0;
				while (true)
				{
					sockPeerServer = lstnrPeerServer.accept();
					System.out.println("Peer Server accepted connection from peer " + sockPeerServer);
					Thread t = new Thread (new PassFile(sockPeerServer, intFileNumber++, intBufferSize));
					t.start();
				}
			}

			catch (Exception e)
			{
				System.err.println("Peer Server Port " + intPeerServerPort + " is already utilized for listening to the Peer Server connections, please check another port");
				System.exit(1);
			}

			finally
			{
				if (lstnrPeerServer!= null)
				{
					lstnrPeerServer.close();
				}
				if (sockPeerServer!= null)
				{
					sockPeerServer.close();
				}
			}
		}
		else
		{
			//Unable to start the Peer Server due to unavailability of all or some of the mandatory parameters
			String strErrorMsg = "Unable to proceed due to unavailability of \n";
			if (intBufferSize == 0)
			{
				strErrorMsg += "Buffer Size variable\n";
			}
			if (intPeerServerPort == 0)
			{
				strErrorMsg += "Peer Server Port variable\n";
			}
			System.out.println (strErrorMsg);
			System.exit(1);
		}
	}
}
