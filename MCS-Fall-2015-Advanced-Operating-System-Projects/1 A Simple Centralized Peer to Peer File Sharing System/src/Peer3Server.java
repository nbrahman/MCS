import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;

public class Peer3Server
{
	static int intPeerServerPort = 0;
	private static Socket sockPeerServer = null;
	private static ServerSocket listenerPeerServer;

	public static void main(String[] args) throws Exception
	{
		int intBufferSize = 0;
	
		//Reading parameters' data from Properties file starts from here
		Properties p2pProp = new Properties();
		try
		{
			p2pProp.load(new FileInputStream("./p2pprop.properties"));
			intPeerServerPort = Integer.parseInt(p2pProp.getProperty("intPeer3ServerPort"));
			intBufferSize = Integer.parseInt(p2pProp.getProperty("intBufferSize"));
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
				listenerPeerServer = new ServerSocket(intPeerServerPort);
				System.out.println("The Peer Server is running.");
				int intFileNumber = 0;
				while (true)
				{
					sockPeerServer = listenerPeerServer.accept();
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
				if (listenerPeerServer!= null)
				{
					listenerPeerServer.close();
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
