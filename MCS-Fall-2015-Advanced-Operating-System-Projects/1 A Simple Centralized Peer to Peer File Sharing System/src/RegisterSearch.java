import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterSearch implements Runnable
{
	//variable declaration
	private Socket socketClient;
	private InputStream is;
	private DataInputStream disInput;
	private String strFileName = "";
	private String strFilePath = "";
	private String strIPAddress = "";
	private int intPortNumber = 0;
	private long lngFileSize;
	private int choice;
	private OutputStream os;
	private DataOutputStream dosOutput;
	private int intSearchResultCnt;
	private static ConcurrentHashMap<Integer, ArrayList<String>> concurrentHashMap = new ConcurrentHashMap<Integer, ArrayList<String>> (1000,0.9f,200);

	//Constructor
	public RegisterSearch (Socket socket)
	{
		this.socketClient = socket;
	}

	//Function to insert the Registry data into Concurrent Hash Map structure starts from here
	public String insertEntryConcurrMap (String strFileName, String strFilePath, String strIPAddress, int intPortNumber, long lngFileSize)
	{
		String strStatus;
		try
		{
			//getting the Hash Code (Key) value for the File Name parameter
			int intHashKey = strFileName.hashCode();

			//Defining the Array List for using as Value in Concurrent Hash Map
			ArrayList <String> arrLstValue = new ArrayList <String>();

			//Adding the Value Data to Array List
			arrLstValue.add(strFileName + "\t" + lngFileSize + "\t" + strFilePath + "\t" + strIPAddress + "\t" + intPortNumber);

			//Adding the Key Value pair into Concurrent Hash Map if Key is absent
			ArrayList <String> arrLstResultKey = concurrentHashMap.putIfAbsent(intHashKey, arrLstValue);

			//If Key is already available into Concurrent Hash Map
			if (arrLstResultKey != null)
			{
				int intAddElement = -1;

				//Looping the Value data from Concurrent Hash Map to check for duplication of registration with same data
				for (int i=0; i<arrLstResultKey.size(); i++)
				{
					//Check if the File Name already exists in the Concurrent Hash Map data
					if (arrLstResultKey.get(i).contains(strFileName + "\t"))
					{
						//Check if the File Name already exists for the same IP Address (duplicate entry)
						if (arrLstResultKey.get(i).contains(strIPAddress + "\t"))
						{
							//Update the existing Value data with the latest data provided
							arrLstResultKey.set(i, arrLstValue.get(0));
							intAddElement = 0;
						}
					}
				}

				//Add the newly provided data to Concurrent Hash Map in case of no duplication
				if (intAddElement == -1)
				{
					arrLstResultKey.add (arrLstValue.get(0));
					concurrentHashMap.put(intHashKey, arrLstResultKey);
				}
			}

			//returning the status to calling function
			strStatus = "New entry registered successfully\n";
			return strStatus;
		}

		catch (Exception e)
		{
			strStatus = e.getMessage() + "\n";
			return strStatus;
		}
	}
	//Function to insert the Registry data into Concurrent Hash Map structure ends over here

	//Function to lookup the file name into Concurrent Hash Map structure starts from here
	public ArrayList<String> getEntryConcurrMap (String strFileName)
	{
		int intResultCnt = 0;

		//Array List for the final Search Result
		ArrayList<String> arrLstResult = new ArrayList<String>();

		//Array List for the final Value data
		ArrayList<String> arrLstResultKey = new ArrayList<String>();

		try
		{
			//getting the Hash Code (Key) value for the File Name parameter
			int intHashKey = strFileName.hashCode();

			//getting the Value data based on the Key from Concurrent Hash Map
			arrLstResultKey = concurrentHashMap.get(intHashKey);

			//check if the Result is not null
			if (arrLstResultKey != null)
			{
				//Looping through the Value data
				for (int i=0; i<arrLstResultKey.size();i++)
				{
					//check if the data contains the File Name
					if (arrLstResultKey.get(i).contains(strFileName + "\t"))
					{
						//increase the result count
						intResultCnt++;

						//check if it's the first result to be added so that we can headers for the Search Result
						if (intResultCnt == 1)
						{
							arrLstResult.add ("Sr. No." + "\t" + "File Name" + "\t" + "File Size (in Bytes)" + "\t" + "File Path" + "\t" + "Peer IP Address" + "\t" + "Peer Server Port Number" + "\n");
						}

						//add actual Search Result
						arrLstResult.add(String.valueOf(intResultCnt) + "\t" + arrLstResultKey.get(i));
					}
				}

				//add the total Search Result count at the 0th Position
				arrLstResult.add(0, String.valueOf(intResultCnt));
			}
			else
			{
				//If no result is found
				arrLstResult.add ("-1");
				arrLstResult.add ("No records found!!!\n");
			}
			return arrLstResult;
		}

		catch (Exception e)
		{
			arrLstResult.add ("-1");
			arrLstResult.add (e.getMessage());
			return arrLstResult;
		}
	}
	//Function to lookup the file name into Concurrent Hash Map structure ends over here

	//Function to remove the data from Concurrent Hash Map structure starts from here
	public String removeEntryConcurrMap (String strFileName, String strFilePath)
	{
		String strStatus;
		try
		{
			//getting the Hash Code (Key) value for the File Name parameter
			int intHashKey = strFileName.hashCode();

			//Defining the Array List for using as Value in Concurrent Hash Map
			ArrayList <String> arrLstValue = new ArrayList <String>();

			//Adding the Key Value pair into Concurrent Hash Map if Key is absent
			ArrayList <String> arrLstResultKey = concurrentHashMap.get(intHashKey);

			//If Key is already available into Concurrent Hash Map
			if (arrLstResultKey != null)
			{

				//Looping the Value data from Concurrent Hash Map
				for (int i=0; i<arrLstResultKey.size(); i++)
				{
					//Check if the File Name already exists in the Concurrent Hash Map data
					if (arrLstResultKey.get(i).contains(strFileName + "\t"))
					{
						//Check if the File Name already exists for the same IP Address (duplicate entry)
						if (arrLstResultKey.get(i).contains(strIPAddress + "\t"))
						{
							//removing the Array List entry corresponding to the File Name and IP Address combination
							arrLstResultKey.remove(i);
						}
					}
				}
			}

			//if the Value Array List is null then remove the complete entry from Concurrent Hash Map else add the updated Array List with same key
			if (arrLstResultKey != null)
			{
				//add the updated Array List with same key back to Concurrent Hash Map
				arrLstResultKey.add (arrLstValue.get(0));
				concurrentHashMap.put(intHashKey, arrLstResultKey);
			}
			else
			{
				//remove the complete entry from Concurrent Hash Map
				concurrentHashMap.remove(intHashKey);
			}

			//returning the status to calling function
			strStatus = "Entry unregistered successfully\n";
			return strStatus;
		}

		catch (Exception e)
		{
			strStatus = e.getMessage() + "\n";
			return strStatus;
		}
	}
	//Function to remove the data from Concurrent Hash Map structure ends over here

	//Indexing Server function to register the data starts from here
	public synchronized void registry()
	{
		try
		{
			//Receiving the File Name to be registered file from Peer
			is = socketClient.getInputStream();
			disInput = new DataInputStream (is);
			strFileName = disInput.readUTF();

			//Receiving the File Path to be registered from Peer
			is = socketClient.getInputStream();
			disInput = new DataInputStream (is);
			strFilePath = disInput.readUTF();

			//Receiving the Peer Server IP Address to be registered from Peer
			is = socketClient.getInputStream();
			disInput = new DataInputStream (is);
			strIPAddress = disInput.readUTF();

			//Receiving the Peer Server Port Number to be registered from Peer
			is = socketClient.getInputStream();
			disInput = new DataInputStream (is);
			intPortNumber = disInput.readInt();

			//Receiving the File Size to be registered from Peer
			is = socketClient.getInputStream();
			disInput = new DataInputStream (is);
			lngFileSize = disInput.readLong();

			String strStatus = "";

			//function call to Register the file with Index database with received parameters
			strStatus = insertEntryConcurrMap (strFileName, strFilePath, strIPAddress, intPortNumber, lngFileSize);

			//Sending the Status Length
			os = socketClient.getOutputStream();
			dosOutput = new DataOutputStream(os);
			dosOutput.writeInt(strStatus.length());
			dosOutput.flush();

			//Sending the Status of Registration
			os = socketClient.getOutputStream();
			dosOutput = new DataOutputStream(os);
			dosOutput.write(strStatus.getBytes());
			dosOutput.flush();
		}

		catch (Exception e)
		{
			try
			{
				//Sending the exception details to Client
				String strErrorMessage = e.getMessage();
				os = socketClient.getOutputStream();
				dosOutput = new DataOutputStream(os);
				dosOutput.writeChars(strErrorMessage);
				dosOutput.flush();
			}

			catch (Exception ex)
			{
				System.out.println (ex.getStackTrace());
			}
		}
	}
	//Indexing Server function to register the data ends over here

	//Indexing Server function to search the data starts from here
	public synchronized void search()
	{
		try
		{
			//Receiving the File Name to be searched from Peer
			is = socketClient.getInputStream();
			disInput = new DataInputStream (is);
			strFileName = disInput.readUTF();

			//function call to Register the file with received parameters
			ArrayList<String> ResultList = getEntryConcurrMap (strFileName);

			//sending the Search Result count to client
			os = socketClient.getOutputStream();
			dosOutput = new DataOutputStream(os);
			intSearchResultCnt = Integer.parseInt (ResultList.get(0));
			dosOutput.writeInt(intSearchResultCnt);
			dosOutput.flush();

			//Looping through the Search Results and passing it to client
			for (int i=1; i < ResultList.size(); i++)
			{
				os = socketClient.getOutputStream();
				dosOutput = new DataOutputStream(os);
				dosOutput.writeUTF(ResultList.get(i));
				dosOutput.flush();
			}
		}

		catch (Exception e)
		{
			try
			{
				//Sending the exception details to Client
				String strErrorMessage = e.getMessage();
				os = socketClient.getOutputStream();
				dosOutput = new DataOutputStream(os);
				dosOutput.writeChars(strErrorMessage);
				dosOutput.flush();
			}

			catch (Exception ex)
			{
				System.out.println (ex.getStackTrace());
			}
		}	}
	//Indexing Server function to search the data starts from here

	//Indexing Server function to unregister the data starts from here
	public synchronized void unregister()
	{
		String strStatus;
		try
		{
			//Receiving the File Name to be registered file from Peer
			is = socketClient.getInputStream();
			disInput = new DataInputStream (is);
			strFileName = disInput.readUTF();

			//Receiving the IP Address to be registered file from Peer
			is = socketClient.getInputStream();
			disInput = new DataInputStream (is);
			strIPAddress = disInput.readUTF();

			//function call to Unregister the file with received parameters
			strStatus = removeEntryConcurrMap (strFileName, strIPAddress);

			//Sending the Status Length
			os = socketClient.getOutputStream();
			dosOutput = new DataOutputStream(os);
			dosOutput.writeInt(strStatus.length());
			dosOutput.flush();

			//Sending the Status of unregister
			os = socketClient.getOutputStream();
			dosOutput = new DataOutputStream(os);
			dosOutput.write(strStatus.getBytes());
			dosOutput.flush();
		}

		catch (Exception e)
		{
			try
			{
				//Sending the exception details to Client
				String strErrorMessage = e.getMessage();
				os = socketClient.getOutputStream();
				dosOutput = new DataOutputStream(os);
				dosOutput.writeChars(strErrorMessage);
				dosOutput.flush();
			}

			catch (Exception ex)
			{
				System.out.println (ex.getStackTrace());
			}
		}
	}
	//Indexing Server function to unregister the data ends over here

	//thread handler code for Indexing Server
	public synchronized void run()
	{
		try
		{
			//Receiving the choice to either Register or Search or Unregister a file from Peer
			is = socketClient.getInputStream();
			disInput = new DataInputStream (is);
			choice = disInput.readInt();
			switch (choice)
			{
				case 1:
					registry();
					break;

				case 2:
					search();
					break;

				case 3:
					unregister();
					break;

				default:
					String strStatus = "Invalid choice\n";
					os = socketClient.getOutputStream();
					dosOutput = new DataOutputStream(os);
					dosOutput.writeChars(strStatus);
					dosOutput.flush();
			}
		}

		catch (Exception e)
		{
			String strErrorMessage = e.getMessage();
			System.out.println(strErrorMessage);
		}

		finally
		{
			try
			{
				socketClient.close();
			}

			catch(Exception e)
			{
				String strErrorMessage = e.getMessage();
				System.out.println(strErrorMessage);
			}
		}
	 }
}