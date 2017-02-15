import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class DHTOperations implements Runnable
{
	//variable declaration
	private static int intNoOfHashBuckets = 5000;
	private static int intBufferSize = 1024;
	private Socket socketClient;
	private DataInputStream disInput;
	private String strKey = "";
	private String strValue = "";
	private String choice;
	private DataOutputStream dosOutput;
	private String strServerLogFileName;
	private String strMessage = "";
	private static ConcurrentHashMap<Integer, ArrayList<String>> concurrentHashMap = new ConcurrentHashMap<Integer, ArrayList<String>> (intNoOfHashBuckets,0.9f,200);
	private static long lngNoOfElements = 0;
	FileOutputStream outputStream;

	//Constructor
	public DHTOperations (Socket socket, String strServerLogFileName)
	{
		try
		{
			this.socketClient = socket;
			this.strServerLogFileName = strServerLogFileName;
			dosOutput = new DataOutputStream (this.socketClient.getOutputStream());
			disInput = new DataInputStream (this.socketClient.getInputStream());
			outputStream = new FileOutputStream(this.strServerLogFileName, true);
			strMessage = strMessage + "Distributed Hash Table server accepted connection from peer " + this.socketClient + "\n";
			outputStream.write(strMessage.getBytes());
			outputStream.flush();
		}
		catch (Exception e)
		{
			System.out.println (e.getMessage()+"\n");
			System.exit(1);
		}
	}

	//Function to put the Key - Value pair data into Concurrent Hash Map structure starts from here
	public boolean putEntryConcurrMap (String strKey, String strValue)
	{
		boolean boolStatus = false;
		int intBucketNo;
		try
		{
			//getting the Hash Code (Key) value for the File Name parameter
			int intHashKey = Math.abs(strKey.hashCode());
			intBucketNo = intHashKey % intNoOfHashBuckets;

			//Defining the Array List for using as Value in Concurrent Hash Map
			ArrayList <String> arrLstValue = new ArrayList <String>();

			//Adding the Value Data to Array List
			arrLstValue.add(strKey + "\t" + strValue);

			//Adding the Key Value pair into Concurrent Hash Map if Key is absent
			ArrayList <String> arrLstResultKey = concurrentHashMap.putIfAbsent(intBucketNo, arrLstValue);

			//if Key is already available into Concurrent Hash Map
			if (arrLstResultKey != null)
			{
				//Add the newly provided data to Concurrent Hash Map in case of no duplication
				arrLstResultKey.add (arrLstValue.get(0));
				concurrentHashMap.put(intHashKey, arrLstResultKey);
			}

			//returning the status to calling function
			lngNoOfElements = lngNoOfElements + 1;
			strMessage = "Key " + '"' + strKey + '"' + " inserted on DHT Server " + socketClient.getLocalAddress().getHostAddress() + " successfully in bucket " + intBucketNo + "\nTotal elements in Bucket are " + concurrentHashMap.get(intBucketNo).size() + "\n";
			outputStream.write(strMessage.getBytes());
			outputStream.flush();
			boolStatus = true;
			return boolStatus;
		}

		catch (Exception e)
		{
			try
			{
				strMessage = "Error while inserting Key " + '"' + strKey + '"' + " DHT Server " + socketClient.getLocalAddress().getHostAddress() + " successfully\n";
				strMessage = strMessage + e.getMessage() + "\n";
				outputStream.write(strMessage.getBytes());
				outputStream.flush();
				boolStatus = false;
				return boolStatus;
			}
			catch (Exception ex)
			{
				System.out.println(ex.getMessage()+"\n");
				boolStatus = false;
				return boolStatus;
			}
		}
	}
	//Function to put the Key - Value pair data into Concurrent Hash Map structure ends over here

	//Function to get the Key - Value pair data from Concurrent Hash Map structure starts from here
	public String getEntryConcurrMap (String strKey)
	{
		int intBucketNo;

		//Array List for the final Search Result
		String strResult = "";

		//Array List for the final Value data
		ArrayList<String> arrLstResultKey = new ArrayList<String>();

		try
		{
			//getting the Hash Code (Key) value for the File Name parameter
			int intHashKey = Math.abs(strKey.hashCode());
			intBucketNo = intHashKey % intNoOfHashBuckets;

			//getting the Value data based on the Key from Concurrent Hash Map
			arrLstResultKey = concurrentHashMap.get(intBucketNo);

			//check if the Result is not null
			if (arrLstResultKey != null)
			{
				//Looping through the Value data
				for (int i=0; ((i<arrLstResultKey.size()) && (strResult == ""));i++)
				{
					//check if the data contains the Key
					if (arrLstResultKey.get(i).contains(strKey + "\t"))
					{
						strResult = arrLstResultKey.get(i).replace(strKey + "\t", "");
					}
				}

				//add the total Search Result count at the 0th Position
				strMessage = "Search for Key " + '"' + strKey + '"' + " on DHT Server " + socketClient.getLocalAddress().getHostAddress() + " successfully returned 1 row(s)\n";
				outputStream.write(strMessage.getBytes());
				outputStream.flush();
				return strResult;
			}
			else
			{
				//If no result is found
				strResult = "0";
				strMessage = "Search for Key " + '"' + strKey + '"' + " on DHT Server " + socketClient.getLocalAddress().getHostAddress() + " returned no rows\n";
				outputStream.write(strMessage.getBytes());
				outputStream.flush();
				return strResult;
			}
		}

		catch (Exception e)
		{
			strResult = "-1";
			try
			{
				strMessage = "Error while searching Key " + '"' + strKey + '"' + " on DHT Server " + socketClient.getLocalAddress().getHostAddress() + "\n";
				strMessage = strMessage + e.getMessage();
				outputStream.write(strMessage.getBytes());
				outputStream.flush();
				return strResult;
			}
			catch (Exception ex)
			{
				System.out.println(ex.getMessage()+"\n");
				return strResult;
			}
		}
	}
	//Function to get the Key - Value pair data from Concurrent Hash Map structure starts from here

	//Function to delete the Key - Value pair data from Concurrent Hash Map structure starts from here
	public boolean deleteEntryConcurrMap (String strKey)
	{
		boolean boolStatus = false;
		boolean boolEntryFound = false;
		int intBucketNo;

		try
		{
			//getting the Hash Code (Key) value for the File Name parameter
			int intHashKey = Math.abs(strKey.hashCode());
			intBucketNo = intHashKey % intNoOfHashBuckets;

			//Adding the Key Value pair into Concurrent Hash Map if Key is absent
			ArrayList <String> arrLstResultKey = concurrentHashMap.get(intBucketNo);

			//If Key is already available into Concurrent Hash Map
			if (arrLstResultKey != null)
			{
				//Looping the Value data from Concurrent Hash Map
				for (int i=0; i<arrLstResultKey.size(); i++)
				{
					//Check if the Key already exists in the Concurrent Hash Map data
					if (arrLstResultKey.get(i).contains(strKey + "\t"))
					{
						//removing the Array List entry corresponding to the File Name and IP Address combination
						arrLstResultKey.remove(i);
						boolEntryFound = true;
					}
				}
			}

			//if the Value Array List is null then remove the complete entry from Concurrent Hash Map else add the updated Array List with same key
			if (arrLstResultKey != null)
			{
				//add the updated Array List with same key back to Concurrent Hash Map
				if (arrLstResultKey.size()>0)
				{
					concurrentHashMap.put(intBucketNo, arrLstResultKey);
				}
				else
				{
					//remove the complete entry from Concurrent Hash Map
					concurrentHashMap.remove(intBucketNo);
				}
			}
			else
			{
				//remove the complete entry from Concurrent Hash Map
				concurrentHashMap.remove(intBucketNo);
			}

			//returning the status to calling function
			if (boolEntryFound == true)
			{
				strMessage = "Key " + '"' + strKey + '"' + " deleted from DHT Server " + socketClient.getLocalAddress().getHostAddress() + " successfully from bucket " + intBucketNo + "\n";
				outputStream.write(strMessage.getBytes());
				outputStream.flush();
				boolStatus = true;
				lngNoOfElements = lngNoOfElements - 1;
				return boolStatus;
			}
			else
			{
				strMessage = "No records found for Key " + '"' + strKey + '"' + " on DHT Server " + socketClient.getLocalAddress().getHostAddress() + " while deleting\n";
				outputStream.write(strMessage.getBytes());
				outputStream.flush();
				boolStatus = false;
				return boolStatus;
			}
		}

		catch (Exception e)
		{
			try
			{
				boolStatus = false;
				strMessage = "Error while deleting Key " + '"' + strKey + '"' + " from DHT Server " + socketClient.getLocalAddress().getHostAddress() + "\n";
				strMessage = strMessage + e.getMessage() + "\n";
				strMessage = strMessage + e.getStackTrace().toString() + "\n";
				outputStream.write(strMessage.getBytes());
				outputStream.flush();
				return boolStatus;
			}
			catch (Exception ex)
			{
				System.out.println(ex.getMessage()+"\n");
				boolStatus = false;
				return boolStatus;
			}
		}
	}
	//Function to delete the Key - Value pair data from Concurrent Hash Map structure ends over here

	public void run()
	{
		int choice2;
		String strGetResult="";
		boolean boolOutput = false;
		try
		{
			while (true)
			{
				//Receiving the choice to along with the Key - Value Pair Data 
				byte[] buffer = new byte[intBufferSize];

				disInput.read(buffer);
				choice = new String (buffer);
				if (choice != "")
				{
					//Extracting the choice of operation from the data received 
					choice2 = Integer.parseInt(choice.substring(0, 1));
					switch (Integer.toString(choice2))
					{
						case "1":
							//Extracting the Key from the data received
							strKey = choice.substring(4, 24);
							//Extracting the Value from the data received
							strValue = choice.substring(24, 1024);
							boolOutput = false;
							boolOutput = putEntryConcurrMap(strKey, strValue);
							dosOutput.writeBoolean (boolOutput);
							dosOutput.flush();
							break;
		
						case "2":
							//Extracting the Key from the data received
							strKey = choice.substring(4, 24);
							strGetResult = "";
							strGetResult = getEntryConcurrMap(strKey);
							dosOutput.writeInt(strGetResult.length());
							dosOutput.write(strGetResult.getBytes());
							dosOutput.flush();
							break;
		
						case "3":
							//Extracting the Key from the data received
							strKey = choice.substring(4, 24);
							boolOutput = false;
							boolOutput = deleteEntryConcurrMap(strKey);
							dosOutput.writeBoolean (boolOutput);
							dosOutput.flush();
							break;
		
						default:
							String strStatus = "Invalid choice\n";
							dosOutput.writeChars(strStatus);
							dosOutput.flush();
					}
					strMessage = strMessage + "No. of elements in DHT are " + lngNoOfElements + "\n";
				}
			}
		}

		catch (Exception e)
		{
			String strErrorMessage = e.getMessage();
			System.out.println(strErrorMessage);
		}
	}
}