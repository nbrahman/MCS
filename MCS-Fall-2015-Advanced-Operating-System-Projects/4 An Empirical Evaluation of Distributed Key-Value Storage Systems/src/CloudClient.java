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

public class CloudClient
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
	private static String strMessage;
	private static String [][] arrKeyValuePairs = new String[intTotalDataPoints][3];

	//function to read properties file starts from here
	public static void readParams(boolean blnConnectMongoDB, boolean blnConnectRedis, boolean blnConnectCouchDB)
	{
		int i=0;
		//Reading parameters' data from Properties file starts from here
		Properties dhtProp = new Properties();
		try
		{
			//System.out.println("Prop 1\n");
			dhtProp.load(new FileInputStream("./CloudProp.properties"));
			intTotalDataPoints = Integer.parseInt(dhtProp.getProperty("intTotalDataPoints"));
			intBufferSize = Integer.parseInt(dhtProp.getProperty("intBufferSize"));
			intDHTServerCount = Integer.parseInt(dhtProp.getProperty("intDHTServerCount"));
			if (intDHTServerCount!=0)
			{
				//System.out.println("Prop 2\n");
				arrlstDHTServerSockets = new ArrayList<Socket>();
				for (i=0; i<intDHTServerCount;i++)
				{
					//System.out.println("Prop 3\n");
					String strDHTServerIP = dhtProp.getProperty("strDHTServer" + (i + 1) + "IP");
					int intDHTServerPort = Integer.parseInt(dhtProp.getProperty("strDHTServer" + (i + 1) + "Port"));
					InetAddress address;
					try
					{
						//System.out.println("Prop 4\n");
						//reading the DHT Servers' info and storing the sockets into an ArrayList
						address = InetAddress.getByName(strDHTServerIP);
						Socket sockDHT = new Socket(address, intDHTServerPort);
						if (i ==0)
						{
							//System.out.println("Prop 5\n");
							//System.out.println(sockDHT +"\n");
							intClientID = Integer.parseInt(new DataInputStream (sockDHT.getInputStream()).readUTF());
						}
						arrlstDHTServerSockets.add(sockDHT);
					}

					catch(IOException ex)
					{
						//System.out.println("Prop 6\n");
						strMessage = ex.getMessage();
						System.out.println (strMessage+"\n");
					}

					catch(Exception ex)
					{
						//System.out.println("Prop 7\n");
						strMessage = ex.getStackTrace().toString();
						System.out.println (strMessage+"\n");
						System.out.println (ex.getMessage()+"\n");
					}
				}
			}
			else
			{
				//System.out.println("Prop 8\n");
				System.out.println ("No dISTRIBUTED Hash Server details are available in properties file!!!\n");
				System.exit(1);
			}
		}

		catch (Exception e)
		{
			//System.out.println("Prop 9\n");
			System.out.println (e.getStackTrace());
			System.exit(1);
		}
		//Reading parameters' data from Properties file ends over here
		//System.out.println("intBufferSize=" + intBufferSize + "\n" + "arrlstDHTServerSockets.size()=" + arrlstDHTServerSockets.size()
			//+ "\n" + "intDHTServerCount=" + intDHTServerCount + "\n");
	}
	//function to read properties file ends over here

	//function to generate workload starts from here
	public static void generateWorkLoad()
	{
		int i=0;
		try
		{
			//Code to generate Key - Value pair data starts from here
			for (i = 0; i < intTotalDataPoints; i++)
			{
				//System.out.println("3\n");
				String strKey = "C" + String.format("%02d", Integer.parseInt(strClientID)) + String.format("%07d", i);
				String strValue = "C" + String.format("%02d", Integer.parseInt(strClientID)) + "_KEY_" + String.format("%071d", i) + "_VALUE_TEST";
				arrKeyValuePairs[i][0] = strKey;
				arrKeyValuePairs[i][1] = strValue;
				arrKeyValuePairs[i][2] = Integer.toString(Math.abs(strKey.hashCode()) % intDHTServerCount);
			}
			//Code to generate Key - Value pair data ends over here
		}

		catch(Exception e)
		{
			String strErrorMessage = e.getMessage() + "\n";
			System.out.println(strErrorMessage);
		}
	}
	//function to generate workload ends over here

	//function containing Assignment2 code starts from here
	public static void evalAssignment2(int intNumDataPoints)
	{
		boolean boolOutput;
		int i;
		DataOutputStream dosOutput;
		DataInputStream disInput;

		//code to test concurrenthashmap starts from here
		strClientLogFileName = "./ClientOperations_" + strClientID +".txt";

		try
		{
			//System.out.println("2\n");
			FileOutputStream outputStream = new FileOutputStream(strClientLogFileName);

			strMessage = "";

			//code to put intNumDataPoints Key - Value Pairs in Distributed Hash Table starts from here
			lngTotalStart = 0;
			lngTotalEnd = 0;
			lngTotalStart = System.nanoTime();
			strMessage = "Put for Key - Value pairs started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			for (i=0; i < intNumDataPoints; i++)
			{
				String strKey = arrKeyValuePairs[i][0];
				String strValue = arrKeyValuePairs[i][1];
				int intServerNumber = Integer.parseInt(arrKeyValuePairs[i][2]);
				if ((strKey != "") && (strValue != ""))
				{
					lngIndividualStart = 0;
					lngIndividualEnd = 0;
					lngIndividualStart = System.nanoTime();

					//computing the Hash Code using inbuilt java function
					//int intHashCode = Math.abs(strKey.hashCode());

					//computing the destination DHT Server index
					//int intServerNumber = intHashCode % intDHTServerCount;

					//Initializing socket for destination DHT Server
					Socket sockTemp = arrlstDHTServerSockets.get(intServerNumber);

					//Initializing Input and Output Streams for Socket
					dosOutput = new DataOutputStream(sockTemp.getOutputStream());
					disInput = new DataInputStream(sockTemp.getInputStream());

					//Sending the Key - Value Pair data along with the type of operation to be performed
					dosOutput.writeUTF (("01"+strKey+strValue));
					dosOutput.flush();

					//Receiving the boolean status for the Put operation
					boolOutput = false;
					boolOutput = Boolean.parseBoolean(disInput.readUTF());

					lngIndividualEnd = System.nanoTime();

					if (boolOutput == true)
					{
						strMessage = "Key " + arrKeyValuePairs[i][0] + " successfully inserted into Disributed Hash Table in " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						//outputStream.write (strMessage.getBytes());
						//outputStream.flush();
					}
					else
					{
						strMessage = "Error while inserting Key " + arrKeyValuePairs[i][0] + " into Disributed Hash Table\n";
						strMessage = strMessage + (i+1) + " - Time taken for individual put is " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						//outputStream.write (strMessage.getBytes());
						//outputStream.flush();
					}
				}
				else if (strKey == "")
				{
					strMessage = i + "- Key cannot be blank\n";
					//outputStream.write (strMessage.getBytes());
					//outputStream.flush();
				}
				else if (strValue == "")
				{
					strMessage = i + "- Value cannot be blank\n";
					//outputStream.write (strMessage.getBytes());
					//outputStream.flush();
				}
			}

			strMessage = "Put for " + (i) + " Key - Value pairs completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			lngTotalEnd = System.nanoTime();
			strMessage = strMessage + " Time taken for " + (i) + " individual puts is " + (lngTotalEnd - lngTotalStart) + "nSec\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			//code to put intNumDataPoints Key - Value Pairs in Distributed Hash Table ends over here

			lngTotalTimeTaken = 0;
			lngTotalTimeTaken = lngTotalTimeTaken + (lngTotalEnd - lngTotalStart);

			//code to get intNumDataPoints Key - Value Pairs from Distributed Hash Table starts from here
			lngTotalStart = 0;
			lngTotalEnd = 0;
			lngTotalStart = System.nanoTime();
			strMessage = "Get for Key - Value pairs started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			for (i=0; i < intNumDataPoints; i++)
			{
				String strKey = arrKeyValuePairs[i][0];
				int intServerNumber = Integer.parseInt(arrKeyValuePairs[i][2]);
				if (strKey != "")
				{
					System.out.println("put " + i + "\n");
					lngIndividualStart = 0;
					lngIndividualEnd = 0;
					lngIndividualStart = System.nanoTime();

					//computing the Hash Code using inbuilt java function
					//int intHashCode = Math.abs(strKey.hashCode());

					//computing the destination DHT Server index
					//int intServerNumber = intHashCode % intDHTServerCount;

					//Initializing socket for destination DHT Server
					Socket sockTemp = arrlstDHTServerSockets.get(intServerNumber);

					//Initializing Input and Output Streams for Socket
					dosOutput = new DataOutputStream(sockTemp.getOutputStream());
					disInput = new DataInputStream(sockTemp.getInputStream());

					//Sending the Key data along with the type of operation to be performed
					dosOutput.writeUTF (("02"+strKey));
					dosOutput.flush();

					//Receiving the boolean status for the Put operation
					int intSearchResultLength = Integer.parseInt(disInput.readUTF());
					//byte[] buffer = new byte [intSearchResultLength];
					String strSearchResult = disInput.readUTF();
					//String strSearchResult = new String (buffer);

					lngIndividualEnd = System.nanoTime();

					if ((strSearchResult != "") && (strSearchResult != "0") && (strSearchResult != "-1"))
					{
						System.out.println("Assignment2 Get Result (Value) for Key = " + strKey + " is " + strSearchResult + "\n");
						strMessage = "Get Result (Value) for Key = " + strKey + " is " + strSearchResult + "\n";
						outputStream.write (strMessage.getBytes());
						outputStream.flush();
					}
					else if (strSearchResult == "0")
					{
						System.out.println("Assignment2 No Get Result (Value) for Key = " + strKey + "\n");
						strMessage = "No Get Result (Value) for Key = " + strKey + "\n";
						strMessage = strMessage + (i+1) + " - Time taken for individual get is " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						outputStream.write (strMessage.getBytes());
						outputStream.flush();
					}
					else if (strSearchResult == "-1")
					{
						System.out.println("Assignment2 Error while getting Value for Key = " + strKey + "\n");
						strMessage = "Error while getting Value for Key = " + strKey + "\n";
						strMessage = strMessage + (i+1) + " - Time taken for individual get is " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						outputStream.write (strMessage.getBytes());
						outputStream.flush();
					}
				}
				else if (strKey == "")
				{
					strMessage = i + "- Key cannot be blank\n";
					//outputStream.write (strMessage.getBytes());
					//outputStream.flush();
					//System.out.println (strMessage = strMessage + i + "- );
					//boolStatus = false;
				}
			}

			strMessage = "Get for " + (i) + " Key - Value pairs completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			lngTotalEnd = System.nanoTime();
			strMessage = strMessage + " Time taken for " + (i) + " individual gets is " + (lngTotalEnd - lngTotalStart) + "nSec\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			//code to get intNumDataPoints Key - Value Pairs from Distributed Hash Table ends over here

			lngTotalTimeTaken = lngTotalTimeTaken + (lngTotalEnd - lngTotalStart);

			//code to delete intNumDataPoints Key - Value Pairs from Distributed Hash Table starts from here
			lngTotalStart = 0;
			lngTotalEnd = 0;
			lngTotalStart = System.nanoTime();
			strMessage = "Delete for Key - Value pairs started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			for (i=0; i < intNumDataPoints; i++)
			{
				String strKey = arrKeyValuePairs[i][0];
				int intServerNumber = Integer.parseInt(arrKeyValuePairs[i][2]);
				if (strKey != "")
				{
					lngIndividualStart = 0;
					lngIndividualEnd = 0;
					lngIndividualStart = System.nanoTime();

					//computing the Hash Code using inbuilt java function
					//int intHashCode = Math.abs(strKey.hashCode());

					//computing the destination DHT Server index
					//int intServerNumber = intHashCode % intDHTServerCount;

					//Initializing socket for destination DHT Server
					Socket sockTemp = arrlstDHTServerSockets.get(intServerNumber);

					//Initializing Input and Output Streams for Socket
					dosOutput = new DataOutputStream(sockTemp.getOutputStream());
					disInput = new DataInputStream(sockTemp.getInputStream());

					//Sending the Key data along with the type of operation to be performed
					dosOutput.writeUTF (("03"+strKey));
					dosOutput.flush();

					//Receiving the boolean status for the Put operation
					boolOutput = false;
					boolOutput = Boolean.parseBoolean(disInput.readUTF());

					lngIndividualEnd = System.nanoTime();

					if (boolOutput == true)
					{
						strMessage = "Key " + arrKeyValuePairs[i][0] + " successfully deleted from Disributed Hash Table in " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						//outputStream.write (strMessage.getBytes());
						//outputStream.flush();
					}
					else
					{
						strMessage = "Error while deleting Key " + arrKeyValuePairs[i][0] + " from Disributed Hash Table\n";
						strMessage = strMessage + (i+1) + " - Time taken for individual delete is " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						//outputStream.write (strMessage.getBytes());
						//outputStream.flush();
					}
				}
				else if (strKey == "")
				{
					strMessage = i + "- Key cannot be blank\n";
					//outputStream.write (strMessage.getBytes());
					//outputStream.flush();
				}
			}

			strMessage = "Delete for " + (i) + " Key - Value pairs completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			lngTotalEnd = System.nanoTime();
			strMessage = strMessage + " Time taken for " + (i) + " individual deletes is " + (lngTotalEnd - lngTotalStart) + "nSec\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			//code to delete intNumDataPoints Key - Value Pairs from Distributed Hash Table ends over here

			lngTotalTimeTaken = lngTotalTimeTaken + (lngTotalEnd - lngTotalStart);
			strMessage = strMessage + "Total Time taken for " + (i) + " puts, gets and deletes is " + lngTotalTimeTaken + "nSec\n";
			outputStream.write(strMessage.getBytes());
			outputStream.flush();

			outputStream.close();
			//code to test concurrenthashmap ends over here
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
	//function containing Assignment2 code ends over here

	//function containing MongoDB code starts from here
	public static void evalMongoDB(int intNumDataPoints)
	{
		boolean boolOutput;
		int i;
		DataOutputStream dosOutput;
		DataInputStream disInput;

		//code to test MongoDB starts from here
		strClientLogFileName = "./ClientOperations_" + strClientID +"_MongoDB.txt";

		try
		{
			//System.out.println("2\n");
			FileOutputStream outputStream = new FileOutputStream(strClientLogFileName);

			strMessage = "";

			//code to put intNumDataPoints Key - Value Pairs in MongoDB starts from here
			lngTotalStart = 0;
			lngTotalEnd = 0;
			lngTotalStart = System.nanoTime();
			strMessage = "Put for Key - Value pairs started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			for (i=0; i < intNumDataPoints; i++)
			{
				String strKey = arrKeyValuePairs[i][0];
				String strValue = arrKeyValuePairs[i][1];
				int intServerNumber = Integer.parseInt(arrKeyValuePairs[i][2]);
				if ((strKey != "") && (strValue != ""))
				{
					lngIndividualStart = 0;
					lngIndividualEnd = 0;
					lngIndividualStart = System.nanoTime();

					//computing the Hash Code using inbuilt java function
					//int intHashCode = Math.abs(strKey.hashCode());

					//computing the destination DHT Server index
					//int intServerNumber = intHashCode % intDHTServerCount;

					//Initializing socket for destination DHT Server
					Socket sockTemp = arrlstDHTServerSockets.get(intServerNumber);

					//Initializing Input and Output Streams for Socket
					dosOutput = new DataOutputStream(sockTemp.getOutputStream());
					disInput = new DataInputStream(sockTemp.getInputStream());

					//Sending the Key - Value Pair data along with the type of operation to be performed
					dosOutput.writeUTF (("11"+strKey+strValue));
					dosOutput.flush();

					//Receiving the boolean status for the Put operation
					boolOutput = false;
					boolOutput = Boolean.parseBoolean(disInput.readUTF());

					lngIndividualEnd = System.nanoTime();

					if (boolOutput == true)
					{
						strMessage = "Key " + arrKeyValuePairs[i][0] + " successfully inserted into MongoDB in " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						//outputStream.write (strMessage.getBytes());
						//outputStream.flush();
					}
					else
					{
						strMessage = "Error while inserting Key " + arrKeyValuePairs[i][0] + " into MongoDB\n";
						strMessage = strMessage + (i+1) + " - Time taken for individual put is " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						//outputStream.write (strMessage.getBytes());
						//outputStream.flush();
					}
				}
				else if (strKey == "")
				{
					strMessage = i + "- Key cannot be blank\n";
					//outputStream.write (strMessage.getBytes());
					//outputStream.flush();
				}
				else if (strValue == "")
				{
					strMessage = i + "- Value cannot be blank\n";
					//outputStream.write (strMessage.getBytes());
					//outputStream.flush();
				}
			}

			strMessage = "Put for " + (i) + " Key - Value pairs completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			lngTotalEnd = System.nanoTime();
			strMessage = strMessage + " Time taken for " + (i) + " individual puts is " + (lngTotalEnd - lngTotalStart) + "nSec\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			//code to put intNumDataPoints Key - Value Pairs in MongoDB ends over here

			lngTotalTimeTaken = 0;
			lngTotalTimeTaken = lngTotalTimeTaken + (lngTotalEnd - lngTotalStart);

			//code to get intNumDataPoints Key - Value Pairs from MongoDB starts from here
			lngTotalStart = 0;
			lngTotalEnd = 0;
			lngTotalStart = System.nanoTime();
			strMessage = "Get for Key - Value pairs started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			for (i=0; i < intNumDataPoints; i++)
			{
				String strKey = arrKeyValuePairs[i][0];
				int intServerNumber = Integer.parseInt(arrKeyValuePairs[i][2]);
				if (strKey != "")
				{
					System.out.println("put " + i + "\n");
					lngIndividualStart = 0;
					lngIndividualEnd = 0;
					lngIndividualStart = System.nanoTime();

					//computing the Hash Code using inbuilt java function
					//int intHashCode = Math.abs(strKey.hashCode());

					//computing the destination DHT Server index
					//int intServerNumber = intHashCode % intDHTServerCount;

					//Initializing socket for destination DHT Server
					Socket sockTemp = arrlstDHTServerSockets.get(intServerNumber);

					//Initializing Input and Output Streams for Socket
					dosOutput = new DataOutputStream(sockTemp.getOutputStream());
					disInput = new DataInputStream(sockTemp.getInputStream());

					//Sending the Key data along with the type of operation to be performed
					dosOutput.writeUTF (("12"+strKey));
					dosOutput.flush();

					//Receiving the boolean status for the Put operation
					int intSearchResultLength = Integer.parseInt(disInput.readUTF());
					//byte[] buffer = new byte [intSearchResultLength];
					String strSearchResult = disInput.readUTF();
					//String strSearchResult = new String (buffer);

					lngIndividualEnd = System.nanoTime();

					if ((strSearchResult != "") && (strSearchResult != "0") && (strSearchResult != "-1"))
					{
						System.out.println("MongoDB Get Result (Value) for Key = " + strKey + " is " + strSearchResult + "\n");
						strMessage = "Get Result (Value) for Key = " + strKey + " is " + strSearchResult + "\n";
						outputStream.write (strMessage.getBytes());
						outputStream.flush();
					}
					else if (strSearchResult == "0")
					{
						System.out.println("MongoDB No Get Result (Value) for Key = " + strKey + "\n");
						strMessage = "No Get Result (Value) for Key = " + strKey + "\n";
						strMessage = strMessage + (i+1) + " - Time taken for individual get is " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						outputStream.write (strMessage.getBytes());
						outputStream.flush();
					}
					else if (strSearchResult == "-1")
					{
						System.out.println("MongoDB Error while getting Value for Key = " + strKey + "\n");
						strMessage = "Error while getting Value for Key = " + strKey + "\n";
						strMessage = strMessage + (i+1) + " - Time taken for individual get is " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						outputStream.write (strMessage.getBytes());
						outputStream.flush();
					}
				}
				else if (strKey == "")
				{
					strMessage = i + "- Key cannot be blank\n";
					//outputStream.write (strMessage.getBytes());
					//outputStream.flush();
					//System.out.println (strMessage = strMessage + i + "- );
					//boolStatus = false;
				}
			}

			strMessage = "Get for " + (i) + " Key - Value pairs completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			lngTotalEnd = System.nanoTime();
			strMessage = strMessage + " Time taken for " + (i) + " individual gets is " + (lngTotalEnd - lngTotalStart) + "nSec\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			//code to get intNumDataPoints Key - Value Pairs from MongoDB ends over here

			lngTotalTimeTaken = lngTotalTimeTaken + (lngTotalEnd - lngTotalStart);

			//code to delete intNumDataPoints Key - Value Pairs from MongoDB starts from here
			lngTotalStart = 0;
			lngTotalEnd = 0;
			lngTotalStart = System.nanoTime();
			strMessage = "Delete for Key - Value pairs started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			for (i=0; i < intNumDataPoints; i++)
			{
				String strKey = arrKeyValuePairs[i][0];
				int intServerNumber = Integer.parseInt(arrKeyValuePairs[i][2]);
				if (strKey != "")
				{
					lngIndividualStart = 0;
					lngIndividualEnd = 0;
					lngIndividualStart = System.nanoTime();

					//computing the Hash Code using inbuilt java function
					//int intHashCode = Math.abs(strKey.hashCode());

					//computing the destination DHT Server index
					//int intServerNumber = intHashCode % intDHTServerCount;

					//Initializing socket for destination DHT Server
					Socket sockTemp = arrlstDHTServerSockets.get(intServerNumber);

					//Initializing Input and Output Streams for Socket
					dosOutput = new DataOutputStream(sockTemp.getOutputStream());
					disInput = new DataInputStream(sockTemp.getInputStream());

					//Sending the Key data along with the type of operation to be performed
					dosOutput.writeUTF (("13"+strKey));
					dosOutput.flush();

					//Receiving the boolean status for the Put operation
					boolOutput = false;
					boolOutput = Boolean.parseBoolean(disInput.readUTF());

					lngIndividualEnd = System.nanoTime();

					if (boolOutput == true)
					{
						strMessage = "Key " + arrKeyValuePairs[i][0] + " successfully deleted from MongoDB in " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						//outputStream.write (strMessage.getBytes());
						//outputStream.flush();
					}
					else
					{
						strMessage = "Error while deleting Key " + arrKeyValuePairs[i][0] + " from MongoDB\n";
						strMessage = strMessage + (i+1) + " - Time taken for individual delete is " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						//outputStream.write (strMessage.getBytes());
						//outputStream.flush();
					}
				}
				else if (strKey == "")
				{
					strMessage = i + "- Key cannot be blank\n";
					//outputStream.write (strMessage.getBytes());
					//outputStream.flush();
				}
			}

			strMessage = "Delete for " + (i) + " Key - Value pairs completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			lngTotalEnd = System.nanoTime();
			strMessage = strMessage + " Time taken for " + (i) + " individual deletes is " + (lngTotalEnd - lngTotalStart) + "nSec\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			//code to delete intNumDataPoints Key - Value Pairs from MongoDB ends over here

			lngTotalTimeTaken = lngTotalTimeTaken + (lngTotalEnd - lngTotalStart);
			strMessage = strMessage + "Total Time taken for " + (i) + " puts, gets and deletes is " + lngTotalTimeTaken + "nSec\n";
			outputStream.write(strMessage.getBytes());
			outputStream.flush();

			outputStream.close();
			//code to test MongoDB ends over here
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
	//function containing MongoDB code ends over here

	//function containing Redis code starts from here
	public static void evalRedis(int intNumDataPoints)
	{
		boolean boolOutput;
		int i;
		DataOutputStream dosOutput;
		DataInputStream disInput;

		//code to test Redis starts from here
		strClientLogFileName = "./ClientOperations_" + strClientID +"_Redis.txt";

		try
		{
			//System.out.println("2\n");
			FileOutputStream outputStream = new FileOutputStream(strClientLogFileName);

			strMessage = "";

			//code to put intNumDataPoints Key - Value Pairs in Redis starts from here
			lngTotalStart = 0;
			lngTotalEnd = 0;
			lngTotalStart = System.nanoTime();
			strMessage = "Put for Key - Value pairs started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			for (i=0; i < intNumDataPoints; i++)
			{
				String strKey = arrKeyValuePairs[i][0];
				String strValue = arrKeyValuePairs[i][1];
				int intServerNumber = Integer.parseInt(arrKeyValuePairs[i][2]);
				if ((strKey != "") && (strValue != ""))
				{
					lngIndividualStart = 0;
					lngIndividualEnd = 0;
					lngIndividualStart = System.nanoTime();

					//computing the Hash Code using inbuilt java function
					//int intHashCode = Math.abs(strKey.hashCode());

					//computing the destination DHT Server index
					//int intServerNumber = intHashCode % intDHTServerCount;

					//Initializing socket for destination DHT Server
					Socket sockTemp = arrlstDHTServerSockets.get(intServerNumber);

					//Initializing Input and Output Streams for Socket
					dosOutput = new DataOutputStream(sockTemp.getOutputStream());
					disInput = new DataInputStream(sockTemp.getInputStream());

					//Sending the Key - Value Pair data along with the type of operation to be performed
					dosOutput.writeUTF (("21"+strKey+strValue));
					dosOutput.flush();

					//Receiving the boolean status for the Put operation
					boolOutput = false;
					boolOutput = Boolean.parseBoolean(disInput.readUTF());

					lngIndividualEnd = System.nanoTime();

					if (boolOutput == true)
					{
						strMessage = "Key " + arrKeyValuePairs[i][0] + " successfully inserted into RedisDB in " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						//outputStream.write (strMessage.getBytes());
						//outputStream.flush();
					}
					else
					{
						strMessage = "Error while inserting Key " + arrKeyValuePairs[i][0] + " into RedisDB\n";
						strMessage = strMessage + (i+1) + " - Time taken for individual put is " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						//outputStream.write (strMessage.getBytes());
						//outputStream.flush();
					}
				}
				else if (strKey == "")
				{
					strMessage = i + "- Key cannot be blank\n";
					//outputStream.write (strMessage.getBytes());
					//outputStream.flush();
				}
				else if (strValue == "")
				{
					strMessage = i + "- Value cannot be blank\n";
					//outputStream.write (strMessage.getBytes());
					//outputStream.flush();
				}
			}

			strMessage = "Put for " + (i) + " Key - Value pairs completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			lngTotalEnd = System.nanoTime();
			strMessage = strMessage + " Time taken for " + (i) + " individual puts is " + (lngTotalEnd - lngTotalStart) + "nSec\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			//code to put intNumDataPoints Key - Value Pairs in Redis ends over here

			lngTotalTimeTaken = 0;
			lngTotalTimeTaken = lngTotalTimeTaken + (lngTotalEnd - lngTotalStart);

			//code to get intNumDataPoints Key - Value Pairs from Redis starts from here
			lngTotalStart = 0;
			lngTotalEnd = 0;
			lngTotalStart = System.nanoTime();
			strMessage = "Get for Key - Value pairs started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			for (i=0; i < intNumDataPoints; i++)
			{
				String strKey = arrKeyValuePairs[i][0];
				int intServerNumber = Integer.parseInt(arrKeyValuePairs[i][2]);
				if (strKey != "")
				{
					System.out.println("put " + i + "\n");
					lngIndividualStart = 0;
					lngIndividualEnd = 0;
					lngIndividualStart = System.nanoTime();

					//computing the Hash Code using inbuilt java function
					//int intHashCode = Math.abs(strKey.hashCode());

					//computing the destination DHT Server index
					//int intServerNumber = intHashCode % intDHTServerCount;

					//Initializing socket for destination DHT Server
					Socket sockTemp = arrlstDHTServerSockets.get(intServerNumber);

					//Initializing Input and Output Streams for Socket
					dosOutput = new DataOutputStream(sockTemp.getOutputStream());
					disInput = new DataInputStream(sockTemp.getInputStream());

					//Sending the Key data along with the type of operation to be performed
					dosOutput.writeUTF (("22"+strKey));
					dosOutput.flush();

					//Receiving the boolean status for the Put operation
					int intSearchResultLength = Integer.parseInt(disInput.readUTF());
					//byte[] buffer = new byte [intSearchResultLength];
					String strSearchResult = disInput.readUTF();
					//String strSearchResult = new String (buffer);

					lngIndividualEnd = System.nanoTime();

					if ((strSearchResult != "") && (strSearchResult != "0") && (strSearchResult != "-1"))
					{
						System.out.println("Redis Get Result (Value) for Key = " + strKey + " is " + strSearchResult + "\n");
						strMessage = "Get Result (Value) for Key = " + strKey + " is " + strSearchResult + "\n";
						outputStream.write (strMessage.getBytes());
						outputStream.flush();
					}
					else if (strSearchResult == "0")
					{
						System.out.println("Redis No Get Result (Value) for Key = " + strKey + "\n");
						strMessage = "No Get Result (Value) for Key = " + strKey + "\n";
						strMessage = strMessage + (i+1) + " - Time taken for individual get is " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						outputStream.write (strMessage.getBytes());
						outputStream.flush();
					}
					else if (strSearchResult == "-1")
					{
						System.out.println("Redis Error while getting Value for Key = " + strKey + "\n");
						strMessage = "Error while getting Value for Key = " + strKey + "\n";
						strMessage = strMessage + (i+1) + " - Time taken for individual get is " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						outputStream.write (strMessage.getBytes());
						outputStream.flush();
					}
				}
				else if (strKey == "")
				{
					strMessage = i + "- Key cannot be blank\n";
					//outputStream.write (strMessage.getBytes());
					//outputStream.flush();
					//System.out.println (strMessage = strMessage + i + "- );
					//boolStatus = false;
				}
			}

			strMessage = "Get for " + (i) + " Key - Value pairs completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			lngTotalEnd = System.nanoTime();
			strMessage = strMessage + " Time taken for " + (i) + " individual gets is " + (lngTotalEnd - lngTotalStart) + "nSec\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			//code to get intNumDataPoints Key - Value Pairs from Redis ends over here

			lngTotalTimeTaken = lngTotalTimeTaken + (lngTotalEnd - lngTotalStart);

			//code to delete intNumDataPoints Key - Value Pairs from Redis starts from here
			lngTotalStart = 0;
			lngTotalEnd = 0;
			lngTotalStart = System.nanoTime();
			strMessage = "Delete for Key - Value pairs started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			for (i=0; i < intNumDataPoints; i++)
			{
				String strKey = arrKeyValuePairs[i][0];
				int intServerNumber = Integer.parseInt(arrKeyValuePairs[i][2]);
				if (strKey != "")
				{
					lngIndividualStart = 0;
					lngIndividualEnd = 0;
					lngIndividualStart = System.nanoTime();

					//computing the Hash Code using inbuilt java function
					//int intHashCode = Math.abs(strKey.hashCode());

					//computing the destination DHT Server index
					//int intServerNumber = intHashCode % intDHTServerCount;

					//Initializing socket for destination DHT Server
					Socket sockTemp = arrlstDHTServerSockets.get(intServerNumber);

					//Initializing Input and Output Streams for Socket
					dosOutput = new DataOutputStream(sockTemp.getOutputStream());
					disInput = new DataInputStream(sockTemp.getInputStream());

					//Sending the Key data along with the type of operation to be performed
					dosOutput.writeUTF (("23"+strKey));
					dosOutput.flush();

					//Receiving the boolean status for the Put operation
					boolOutput = false;
					boolOutput = Boolean.parseBoolean(disInput.readUTF());

					lngIndividualEnd = System.nanoTime();

					if (boolOutput == true)
					{
						strMessage = "Key " + arrKeyValuePairs[i][0] + " successfully deleted from RedisDB in " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						//outputStream.write (strMessage.getBytes());
						//outputStream.flush();
					}
					else
					{
						strMessage = "Error while deleting Key " + arrKeyValuePairs[i][0] + " from RedisDB\n";
						strMessage = strMessage + (i+1) + " - Time taken for individual delete is " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						//outputStream.write (strMessage.getBytes());
						//outputStream.flush();
					}
				}
				else if (strKey == "")
				{
					strMessage = i + "- Key cannot be blank\n";
					//outputStream.write (strMessage.getBytes());
					//outputStream.flush();
				}
			}

			strMessage = "Delete for " + (i) + " Key - Value pairs completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			lngTotalEnd = System.nanoTime();
			strMessage = strMessage + " Time taken for " + (i) + " individual deletes is " + (lngTotalEnd - lngTotalStart) + "nSec\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			//code to delete intNumDataPoints Key - Value Pairs from RedisDB ends over here

			lngTotalTimeTaken = lngTotalTimeTaken + (lngTotalEnd - lngTotalStart);
			strMessage = strMessage + "Total Time taken for " + (i) + " puts, gets and deletes is " + lngTotalTimeTaken + "nSec\n";
			outputStream.write(strMessage.getBytes());
			outputStream.flush();

			outputStream.close();
			//code to test Redis ends over here
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
	//function containing Redis code ends over here

	//function containing CouchDB code starts from here
	public static void evalCouchDB(int intNumDataPoints)
	{
		boolean boolOutput;
		int i;
		DataOutputStream dosOutput;
		DataInputStream disInput;

		//code to test CouchDB starts from here
		strClientLogFileName = "./ClientOperations_" + strClientID +"_CouchDB.txt";

		try
		{
			//System.out.println("2\n");
			FileOutputStream outputStream = new FileOutputStream(strClientLogFileName);

			strMessage = "";

			//code to put intNumDataPoints Key - Value Pairs in Redis starts from here
			lngTotalStart = 0;
			lngTotalEnd = 0;
			lngTotalStart = System.nanoTime();
			strMessage = "Put for Key - Value pairs started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			for (i=0; i < intNumDataPoints; i++)
			{
				String strKey = arrKeyValuePairs[i][0];
				String strValue = arrKeyValuePairs[i][1];
				int intServerNumber = Integer.parseInt(arrKeyValuePairs[i][2]);
				if ((strKey != "") && (strValue != ""))
				{
					lngIndividualStart = 0;
					lngIndividualEnd = 0;
					lngIndividualStart = System.nanoTime();

					//computing the Hash Code using inbuilt java function
					//int intHashCode = Math.abs(strKey.hashCode());

					//computing the destination DHT Server index
					//int intServerNumber = intHashCode % intDHTServerCount;

					//Initializing socket for destination DHT Server
					Socket sockTemp = arrlstDHTServerSockets.get(intServerNumber);

					//Initializing Input and Output Streams for Socket
					dosOutput = new DataOutputStream(sockTemp.getOutputStream());
					disInput = new DataInputStream(sockTemp.getInputStream());

					//Sending the Key - Value Pair data along with the type of operation to be performed
					dosOutput.writeUTF (("31"+strKey+strValue));
					dosOutput.flush();

					//Receiving the boolean status for the Put operation
					boolOutput = false;
					boolOutput = Boolean.parseBoolean(disInput.readUTF());

					lngIndividualEnd = System.nanoTime();

					if (boolOutput == true)
					{
						strMessage = "Key " + arrKeyValuePairs[i][0] + " successfully inserted into CouchDB in " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						//outputStream.write (strMessage.getBytes());
						//outputStream.flush();
					}
					else
					{
						strMessage = "Error while inserting Key " + arrKeyValuePairs[i][0] + " into CouchDB\n";
						strMessage = strMessage + (i+1) + " - Time taken for individual put is " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						//outputStream.write (strMessage.getBytes());
						//outputStream.flush();
					}
				}
				else if (strKey == "")
				{
					strMessage = i + "- Key cannot be blank\n";
					//outputStream.write (strMessage.getBytes());
					//outputStream.flush();
				}
				else if (strValue == "")
				{
					strMessage = i + "- Value cannot be blank\n";
					//outputStream.write (strMessage.getBytes());
					//outputStream.flush();
				}
			}

			strMessage = "Put for " + (i) + " Key - Value pairs completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			lngTotalEnd = System.nanoTime();
			strMessage = strMessage + " Time taken for " + (i) + " individual puts is " + (lngTotalEnd - lngTotalStart) + "nSec\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			//code to put intNumDataPoints Key - Value Pairs in CouchDB ends over here

			lngTotalTimeTaken = 0;
			lngTotalTimeTaken = lngTotalTimeTaken + (lngTotalEnd - lngTotalStart);

			//code to get intNumDataPoints Key - Value Pairs from CouchDB starts from here
			lngTotalStart = 0;
			lngTotalEnd = 0;
			lngTotalStart = System.nanoTime();
			strMessage = "Get for Key - Value pairs started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			for (i=0; i < intNumDataPoints; i++)
			{
				String strKey = arrKeyValuePairs[i][0];
				int intServerNumber = Integer.parseInt(arrKeyValuePairs[i][2]);
				if (strKey != "")
				{
					System.out.println("put " + i + "\n");
					lngIndividualStart = 0;
					lngIndividualEnd = 0;
					lngIndividualStart = System.nanoTime();

					//computing the Hash Code using inbuilt java function
					//int intHashCode = Math.abs(strKey.hashCode());

					//computing the destination DHT Server index
					//int intServerNumber = intHashCode % intDHTServerCount;

					//Initializing socket for destination DHT Server
					Socket sockTemp = arrlstDHTServerSockets.get(intServerNumber);

					//Initializing Input and Output Streams for Socket
					dosOutput = new DataOutputStream(sockTemp.getOutputStream());
					disInput = new DataInputStream(sockTemp.getInputStream());

					//Sending the Key data along with the type of operation to be performed
					dosOutput.writeUTF (("32"+strKey));
					dosOutput.flush();

					//Receiving the boolean status for the Put operation
					int intSearchResultLength = Integer.parseInt(disInput.readUTF());
					//byte[] buffer = new byte [intSearchResultLength];
					String strSearchResult = disInput.readUTF();
					//String strSearchResult = new String (buffer);

					lngIndividualEnd = System.nanoTime();

					if ((strSearchResult != "") && (strSearchResult != "0") && (strSearchResult != "-1"))
					{
						System.out.println("CouchDB Get Result (Value) for Key = " + strKey + " is " + strSearchResult + "\n");
						strMessage = "Get Result (Value) for Key = " + strKey + " is " + strSearchResult + "\n";
						outputStream.write (strMessage.getBytes());
						outputStream.flush();
					}
					else if (strSearchResult == "0")
					{
						System.out.println("CouchDB No Get Result (Value) for Key = " + strKey + "\n");
						strMessage = "No Get Result (Value) for Key = " + strKey + "\n";
						strMessage = strMessage + (i+1) + " - Time taken for individual get is " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						outputStream.write (strMessage.getBytes());
						outputStream.flush();
					}
					else if (strSearchResult == "-1")
					{
						System.out.println("CouchDB Error while getting Value for Key = " + strKey + "\n");
						strMessage = "Error while getting Value for Key = " + strKey + "\n";
						strMessage = strMessage + (i+1) + " - Time taken for individual get is " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						outputStream.write (strMessage.getBytes());
						outputStream.flush();
					}
				}
				else if (strKey == "")
				{
					strMessage = i + "- Key cannot be blank\n";
					//outputStream.write (strMessage.getBytes());
					//outputStream.flush();
					//System.out.println (strMessage = strMessage + i + "- );
					//boolStatus = false;
				}
			}

			strMessage = "Get for " + (i) + " Key - Value pairs completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			lngTotalEnd = System.nanoTime();
			strMessage = strMessage + " Time taken for " + (i) + " individual gets is " + (lngTotalEnd - lngTotalStart) + "nSec\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			//code to get intNumDataPoints Key - Value Pairs from Redis ends over here

			lngTotalTimeTaken = lngTotalTimeTaken + (lngTotalEnd - lngTotalStart);

			//code to delete intNumDataPoints Key - Value Pairs from Redis starts from here
			lngTotalStart = 0;
			lngTotalEnd = 0;
			lngTotalStart = System.nanoTime();
			strMessage = "Delete for Key - Value pairs started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			for (i=0; i < intNumDataPoints; i++)
			{
				String strKey = arrKeyValuePairs[i][0];
				int intServerNumber = Integer.parseInt(arrKeyValuePairs[i][2]);
				if (strKey != "")
				{
					lngIndividualStart = 0;
					lngIndividualEnd = 0;
					lngIndividualStart = System.nanoTime();

					//computing the Hash Code using inbuilt java function
					//int intHashCode = Math.abs(strKey.hashCode());

					//computing the destination DHT Server index
					//int intServerNumber = intHashCode % intDHTServerCount;

					//Initializing socket for destination DHT Server
					Socket sockTemp = arrlstDHTServerSockets.get(intServerNumber);

					//Initializing Input and Output Streams for Socket
					dosOutput = new DataOutputStream(sockTemp.getOutputStream());
					disInput = new DataInputStream(sockTemp.getInputStream());

					//Sending the Key data along with the type of operation to be performed
					dosOutput.writeUTF (("33"+strKey));
					dosOutput.flush();

					//Receiving the boolean status for the Put operation
					boolOutput = false;
					boolOutput = Boolean.parseBoolean(disInput.readUTF());

					lngIndividualEnd = System.nanoTime();

					if (boolOutput == true)
					{
						strMessage = "Key " + arrKeyValuePairs[i][0] + " successfully deleted from CouchDB in " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						//outputStream.write (strMessage.getBytes());
						//outputStream.flush();
					}
					else
					{
						strMessage = "Error while deleting Key " + arrKeyValuePairs[i][0] + " from CouchDB\n";
						strMessage = strMessage + (i+1) + " - Time taken for individual delete is " + (lngIndividualEnd - lngIndividualStart) + "nSec\n";
						//outputStream.write (strMessage.getBytes());
						//outputStream.flush();
					}
				}
				else if (strKey == "")
				{
					strMessage = i + "- Key cannot be blank\n";
					//outputStream.write (strMessage.getBytes());
					//outputStream.flush();
				}
			}

			strMessage = "Delete for " + (i) + " Key - Value pairs completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
			lngTotalEnd = System.nanoTime();
			strMessage = strMessage + " Time taken for " + (i) + " individual deletes is " + (lngTotalEnd - lngTotalStart) + "nSec\n";
			outputStream.write (strMessage.getBytes());
			outputStream.flush();
			//code to delete intNumDataPoints Key - Value Pairs from CouchDB ends over here

			lngTotalTimeTaken = lngTotalTimeTaken + (lngTotalEnd - lngTotalStart);
			strMessage = strMessage + "Total Time taken for " + (i) + " puts, gets and deletes is " + lngTotalTimeTaken + "nSec\n";
			outputStream.write(strMessage.getBytes());
			outputStream.flush();

			outputStream.close();
			//code to test CouchDB ends over here
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
	//function containing CouchDB code ends over here

	public static void main(String[] args)
	{
		readParams(false, true, false);

		if ((intBufferSize != 0) && (arrlstDHTServerSockets.size() > 0) && (intDHTServerCount > 0))
		{
			//System.out.println("1\n");
			strClientID = String.format("%04d", intClientID);

			try
			{
				generateWorkLoad();
				System.out.println("1\n");
				evalAssignment2(10);
				System.out.println("2\n");
				evalMongoDB(10);
				System.out.println("3\n");
				evalRedis(10);
				System.out.println("4\n");
				evalCouchDB(10);
				System.out.println("5\n");
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
				System.out.println("Error 1\n");
				strErrorMsg += "Buffer Size variable\n";
			}
			if (intDHTServerCount == 0)
			{
				System.out.println("Error 2\n");
				strErrorMsg += "Distrbuted Hash Table Servers count\n";
			}
			if (arrlstDHTServerSockets.size() <= 0)
			{
				System.out.println("Error 3\n");
				strErrorMsg += "Connection to various Distrbuted Hash Table Servers\n";
			}
			System.out.println (strErrorMsg);
			System.exit(1);
		}
	}
} 