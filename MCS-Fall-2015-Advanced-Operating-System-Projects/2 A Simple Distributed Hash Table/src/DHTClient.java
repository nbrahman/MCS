import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.io.FileInputStream;

public class DHTClient
{
	//variable declaration
	private static int intBufferSize=0;
	private static int intDHTServerCount=0;
	private static ArrayList<Socket> arrlstDHTServerSockets;
	private static String strClientLogFileName="";
	private static String strClientID="";
	private static long lngTotalStart, lngTotalEnd, lngIndividualStart, lngIndividualEnd, lngTotalTimeTaken;
	private static int intTotalDataPoints = 100000;
	private static int intClientID;

	public static void main(String[] args)
	{
		String strMessage;
		boolean boolOutput;
		String [][] arrKeyValuePairs = new String[intTotalDataPoints][2];
		int i;
		DataOutputStream dosOutput;
		DataInputStream disInput;

		//Reading parameters' data from Properties file starts from here
		Properties dhtProp = new Properties();
		try
		{
			dhtProp.load(new FileInputStream("./dht.properties"));
			intTotalDataPoints = Integer.parseInt(dhtProp.getProperty("intTotalDataPoints"));
			intBufferSize = Integer.parseInt(dhtProp.getProperty("intBufferSize"));
			intDHTServerCount = Integer.parseInt(dhtProp.getProperty("intDHTServerCount"));
			if (intDHTServerCount!=0)
			{
				arrlstDHTServerSockets = new ArrayList<Socket>();
				for (i=0; i<intDHTServerCount;i++)
				{
					String strDHTServerIP = dhtProp.getProperty("strDHTServer" + (i + 1) + "IP");
					int intDHTServerPort = Integer.parseInt(dhtProp.getProperty("strDHTServer" + (i + 1) + "Port"));
					InetAddress address;
					try
					{
						//reading the DHT Servers' info and storing the sockets into an ArrayList
						address = InetAddress.getByName(strDHTServerIP);
						Socket sockDHT = new Socket(address, intDHTServerPort);
						if (i ==0)
						{
							System.out.println(sockDHT +"\n");
							intClientID = new DataInputStream (sockDHT.getInputStream()).readInt();
						}
						arrlstDHTServerSockets.add(sockDHT);
					}

					catch(IOException ex)
					{
						strMessage = ex.getMessage();
						System.out.println (strMessage+"\n");
					}

					catch(Exception ex)
					{
						strMessage = ex.getStackTrace().toString();
						System.out.println (strMessage+"\n");
						System.out.println (ex.getMessage()+"\n");
					}
				}
			}
			else
			{
				System.out.println ("No Distributed Hash Server details are available in properties file!!!\n");
				System.exit(1);
			}
		}

		catch (Exception e)
		{
			System.out.println (e.getStackTrace());
			System.exit(1);
		}
		//Reading parameters' data from Properties file ends over here

		if ((intBufferSize != 0) && (arrlstDHTServerSockets.size() > 0) && (intDHTServerCount > 0))
		{
			strClientID = String.format("%04d", intClientID);
			strClientLogFileName = "./ClientOperations_" + strClientID +".txt";

			try
			{
				FileOutputStream outputStream = new FileOutputStream(strClientLogFileName);

				//Code to generate Key - Value pair data starts from here
				for (i = 0; i < intTotalDataPoints; i++)
				{
					String strKey = "C" + String.format("%04d", Integer.parseInt(strClientID)) + "_KEY_" + String.format("%010d", i);
					String strValue = strKey + "_VALUE_TEST";
					arrKeyValuePairs[i][0] = strKey;
					arrKeyValuePairs[i][1] = strValue;
				}
				//Code to generate Key - Value pair data ends over here

				strMessage = "";

				//code to put intTotalDataPoints Key - Value Pairs in Distributed Hash Table starts from here
				lngTotalStart = 0;
				lngTotalEnd = 0;
				lngTotalStart = System.currentTimeMillis();
				strMessage = strMessage + "Put for Key - Value pairs started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
				for (i=0; i < intTotalDataPoints; i++)
				{
					String strKey = arrKeyValuePairs[i][0];
					String strValue = arrKeyValuePairs[i][1];
					if ((strKey != "") && (strValue != ""))
					{
						lngIndividualStart = 0;
						lngIndividualEnd = 0;
						lngIndividualStart = System.currentTimeMillis();

						//computing the Hash Code using inbuilt java function
						int intHashCode = Math.abs(strKey.hashCode());
	
						//computing the destination DHT Server index
						int intServerNumber = intHashCode % intDHTServerCount;
	
						//Initializing socket for destination DHT Server
						Socket sockTemp = arrlstDHTServerSockets.get(intServerNumber);

						//Initializing Input and Output Streams for Socket
						dosOutput = new DataOutputStream(sockTemp.getOutputStream());
						disInput = new DataInputStream(sockTemp.getInputStream());

						//Sending the Key - Value Pair data along with the type of operation to be performed
						dosOutput.write (("1000"+strKey+strValue).getBytes());
						dosOutput.flush();

						//Receiving the boolean status for the Put operation
						boolOutput = false;
						boolOutput = disInput.readBoolean();

						lngIndividualEnd = System.currentTimeMillis();

						if (boolOutput == true)
						{
							strMessage = strMessage + "Key " + arrKeyValuePairs[i][0] + " successfully inserted into Disributed Hash Table in " + (lngIndividualEnd - lngIndividualStart) + "mSec\n";
						}
						else
						{
							strMessage = strMessage + "Error while inserting Key " + arrKeyValuePairs[i][0] + " into Disributed Hash Table\n";
							strMessage = strMessage + (i+1) + " - Time taken for individual put is " + (lngIndividualEnd - lngIndividualStart) + "mSec\n";
						}
					}
					else if (strKey == "")
					{
						strMessage = strMessage + i + "- Key cannot be blank\n";
					}
					else if (strValue == "")
					{
						strMessage = strMessage + i + "- Value cannot be blank\n";
					}
				}

				strMessage = strMessage + "Put for " + (i) + " Key - Value pairs completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
				lngTotalEnd = System.currentTimeMillis();
				strMessage = strMessage + " Time taken for " + (i) + " individual puts is " + (lngTotalEnd - lngTotalStart) + "mSec\n";
				//code to put intTotalDataPoints Key - Value Pairs in Distributed Hash Table ends over here

				lngTotalTimeTaken = 0;
				lngTotalTimeTaken = lngTotalTimeTaken + (lngTotalEnd - lngTotalStart);

				//code to get intTotalDataPoints Key - Value Pairs from Distributed Hash Table starts from here
				lngTotalStart = 0;
				lngTotalEnd = 0;
				lngTotalStart = System.currentTimeMillis();
				strMessage = strMessage + "Get for Key - Value pairs started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
				for (i=0; i < intTotalDataPoints; i++)
				{
					String strKey = arrKeyValuePairs[i][0];
					if (strKey != "")
					{
						//System.out.println("put " + i + "\n");
						lngIndividualStart = 0;
						lngIndividualEnd = 0;
						lngIndividualStart = System.currentTimeMillis();

						//computing the Hash Code using inbuilt java function
						int intHashCode = Math.abs(strKey.hashCode());
	
						//computing the destination DHT Server index
						int intServerNumber = intHashCode % intDHTServerCount;
	
						//Initializing socket for destination DHT Server
						Socket sockTemp = arrlstDHTServerSockets.get(intServerNumber);

						//Initializing Input and Output Streams for Socket
						dosOutput = new DataOutputStream(sockTemp.getOutputStream());
						disInput = new DataInputStream(sockTemp.getInputStream());

						//Sending the Key data along with the type of operation to be performed
						dosOutput.write (("2000"+strKey).getBytes());
						dosOutput.flush();

						//Receiving the boolean status for the Put operation
						int intSearchResultLength = disInput.readInt();
						byte[] buffer = new byte [intSearchResultLength];
						disInput.read(buffer);
						String strSearchResult = new String (buffer);

						lngIndividualEnd = System.currentTimeMillis();

						if ((strSearchResult != "") && (strSearchResult != "0") && (strSearchResult != "-1"))
						{
							System.out.println("Get Result (Value) for Key = " + strKey + " is " + strSearchResult + "\n");
							strMessage = strMessage + "Get operation for Key = " + strKey + " returned 1 row in " + (lngIndividualEnd - lngIndividualStart) + "mSec\n";
						}
						else if (strSearchResult == "0")
						{
							System.out.println("No Get Result (Value) for Key = " + strKey + "\n");
							strMessage = strMessage + "No Get Result (Value) for Key = " + strKey + "\n";
							strMessage = strMessage + (i+1) + " - Time taken for individual get is " + (lngIndividualEnd - lngIndividualStart) + "mSec\n";
						}
						else if (strSearchResult == "-1")
						{
							System.out.println("Error while getting Value for Key = " + strKey + "\n");
							strMessage = strMessage + "Error while getting Value for Key = " + strKey + "\n";
							strMessage = strMessage + (i+1) + " - Time taken for individual get is " + (lngIndividualEnd - lngIndividualStart) + "mSec\n";
						}
					}
					else if (strKey == "")
					{
						strMessage = strMessage + i + "- Key cannot be blank\n";
						//System.out.println (strMessage = strMessage + i + "- );
						//boolStatus = false;
					}
				}

				strMessage = strMessage + "Get for " + (i) + " Key - Value pairs completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
				lngTotalEnd = System.currentTimeMillis();
				strMessage = strMessage + " Time taken for " + (i) + " individual gets is " + (lngTotalEnd - lngTotalStart) + "mSec\n";
				//code to get intTotalDataPoints Key - Value Pairs from Distributed Hash Table ends over here

				lngTotalTimeTaken = lngTotalTimeTaken + (lngTotalEnd - lngTotalStart);

				//code to delete intTotalDataPoints Key - Value Pairs from Distributed Hash Table starts from here
				lngTotalStart = 0;
				lngTotalEnd = 0;
				lngTotalStart = System.currentTimeMillis();
				strMessage = strMessage + "Delete for Key - Value pairs started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
				for (i=0; i < intTotalDataPoints; i++)
				{
					String strKey = arrKeyValuePairs[i][0];
					if (strKey != "")
					{
						lngIndividualStart = 0;
						lngIndividualEnd = 0;
						lngIndividualStart = System.currentTimeMillis();

						//computing the Hash Code using inbuilt java function
						int intHashCode = Math.abs(strKey.hashCode());
	
						//computing the destination DHT Server index
						int intServerNumber = intHashCode % intDHTServerCount;
	
						//Initializing socket for destination DHT Server
						Socket sockTemp = arrlstDHTServerSockets.get(intServerNumber);

						//Initializing Input and Output Streams for Socket
						dosOutput = new DataOutputStream(sockTemp.getOutputStream());
						disInput = new DataInputStream(sockTemp.getInputStream());

						//Sending the Key data along with the type of operation to be performed
						dosOutput.write (("3000"+strKey).getBytes());
						dosOutput.flush();

						//Receiving the boolean status for the Put operation
						boolOutput = false;
						boolOutput = disInput.readBoolean();

						lngIndividualEnd = System.currentTimeMillis();

						if (boolOutput == true)
						{
							strMessage = strMessage + "Key " + arrKeyValuePairs[i][0] + " successfully deleted from Disributed Hash Table in " + (lngIndividualEnd - lngIndividualStart) + "mSec\n";
						}
						else
						{
							strMessage = strMessage + "Error while deleting Key " + arrKeyValuePairs[i][0] + " from Disributed Hash Table\n";
							strMessage = strMessage + (i+1) + " - Time taken for individual delete is " + (lngIndividualEnd - lngIndividualStart) + "mSec\n";
						}
					}
					else if (strKey == "")
					{
						strMessage = strMessage + i + "- Key cannot be blank\n";
					}
				}

				strMessage = strMessage + "Delete for " + (i) + " Key - Value pairs completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
				lngTotalEnd = System.currentTimeMillis();
				strMessage = strMessage + " Time taken for " + (i) + " individual deletes is " + (lngTotalEnd - lngTotalStart) + "mSec\n";
				//code to delete intTotalDataPoints Key - Value Pairs from Distributed Hash Table ends over here

				lngTotalTimeTaken = lngTotalTimeTaken + (lngTotalEnd - lngTotalStart);
				strMessage = strMessage + "Total Time taken for " + (i) + " puts, gets and deletes is " + lngTotalTimeTaken + "mSec\n";

				outputStream.write(strMessage.getBytes());
				outputStream.flush();
				outputStream.close();
			}

			catch(FileNotFoundException e)
			{
				String strErrorMessage = "Error while writing the requested file "+ strClientLogFileName +"!!\n" + e.getMessage() + "\n";
				System.out.println(strErrorMessage);
			}

			catch(Exception e)
			{
				String strErrorMessage = e.getMessage() + "\n";
				System.out.println(strErrorMessage);
			}
		}
		else
		{
			//Unable to start the Index Server due to unavailability of all or some of the mandatory parameters
			String strErrorMsg = "Unable to proceed due to unavailability of \n";
			if (intBufferSize == 0)
			{
				strErrorMsg += "Buffer Size variable\n";
			}
			if (intDHTServerCount == 0)
			{
				strErrorMsg += "Distrbuted Hash Table Servers count\n";
			}
			if (arrlstDHTServerSockets.size() <= 0)
			{
				strErrorMsg += "Connection to various Distrbuted Hash Table Servers\n";
			}
			System.out.println (strErrorMsg);
			System.exit(1);
		}
	}
}
