import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
//import java.io.InputStream;
//import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterSearch implements Runnable
{
	//variable declaration
	private Socket socketClient;
	//private InputStream is;
	private DataInputStream disInput;
	private String strFileName = "";
	private String strFilePath = "";
	private String strIPAddress = "";
	private int intPortNumber = 0;
	private long lngFileSize;
	private String strReplication="N";
	private int choice;
	//private OutputStream os;
	private DataOutputStream dosOutput;
	private int intSearchResultCnt;
	private static String strDelimiter = "%@$#";
	private static int intBufferSize;
	private static ConcurrentHashMap<Integer, ArrayList<String>> concurrentHashMap = new ConcurrentHashMap<Integer, ArrayList<String>> (1000,0.9f,200);

	//Constructor
	public RegisterSearch (Socket socket, String strServerLogFileName, String strDelimiter, int intBufferSize)
	{
		this.socketClient = socket;
		//this.strServerLogFileName = strServerLogFileName;
		//outputStream = new FileOutputStream(this.strServerLogFileName, true);
		//strMessage = strMessage + "Distributed Hash Table server accepted connection from peer " + this.socketClient + "\n";
		//outputStream.write(strMessage.getBytes());
		//outputStream.flush();
		this.strDelimiter = strDelimiter;
		this.intBufferSize = intBufferSize;
		System.out.println("Distributed Indexing Server accepted connection from peer " + this.socketClient + "\n");
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
	public void registry()
	{
		try
		{
			//Receiving the File Name to be registered file from Peer
			//is = socketClient.getInputStream();
			//disInput = new DataInputStream (is);
			//strFileName = disInput.readUTF();
			//strFileName = arrStrParams[0];

			//Receiving the File Path to be registered from Peer
			//is = socketClient.getInputStream();
			//disInput = new DataInputStream (is);
			//strFilePath = disInput.readUTF();
			//strFilePath = arrStrParams[1];

			//Receiving the Peer Server IP Address to be registered from Peer
			//is = socketClient.getInputStream();
			//disInput = new DataInputStream (is);
			//strIPAddress = disInput.readUTF();
			//strIPAddress = arrStrParams[2];

			//Receiving the Peer Server Port Number to be registered from Peer
			//is = socketClient.getInputStream();
			//disInput = new DataInputStream (is);
			//intPortNumber = disInput.readInt();
			//intPortNumber = Integer.parseInt(arrStrParams[3]);

			//Receiving the File Size to be registered from Peer
			//is = socketClient.getInputStream();
			//disInput = new DataInputStream (is);
			//lngFileSize = disInput.readLong();
			//lngFileSize = Long.parseLong(arrStrParams[4]);

			String strStatus = "";
			//System.out.println(socketClient.getLocalAddress().toString() + "\n" + socketClient.getLocalPort());

			if (strReplication=="Y")
			{
				//System.out.println("Inside Replication\n");
				strStatus = obtain (strFileName, strFilePath, strIPAddress, intPortNumber);
				strStatus = insertEntryConcurrMap (strFileName, strFilePath, socketClient.getLocalAddress().toString(), socketClient.getLocalPort(), lngFileSize);
			}

			//function call to Register the file with Index database with received parameters
			strStatus = insertEntryConcurrMap (strFileName, strFilePath, strIPAddress, intPortNumber, lngFileSize);

			//Sending the Status Length
			//os = socketClient.getOutputStream();
			//dosOutput = new DataOutputStream(os);
			dosOutput.writeUTF(Integer.toString(strStatus.length()));
			dosOutput.flush();

			//Sending the Status of Registration
			//os = socketClient.getOutputStream();
			//dosOutput = new DataOutputStream(os);
			dosOutput.write(strStatus.getBytes());
			dosOutput.flush();
		}

		catch (Exception e)
		{
			try
			{
				//Sending the exception details to Client
				String strErrorMessage = e.getMessage();
				//os = socketClient.getOutputStream();
				//dosOutput = new DataOutputStream(os);
				dosOutput.writeUTF(strErrorMessage);
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
	public void search()
	{
		try
		{
			//System.out.println("Inside search 1\n");
			//Receiving the File Name to be searched from Peer
			//is = socketClient.getInputStream();
			//disInput = new DataInputStream (is);
			//strFileName = disInput.readUTF();
			//strFileName = arrStrParams[0];

			//function call to Register the file with received parameters
			//System.out.println("Inside search 2\n");
			ArrayList<String> ResultList = getEntryConcurrMap (strFileName);
			//System.out.println("Inside search 3\n");

			//sending the Search Result count to client
			//os = socketClient.getOutputStream();
			//dosOutput = new DataOutputStream(os);
			intSearchResultCnt = Integer.parseInt (ResultList.get(0));
			//System.out.println(ResultList);
			//System.out.println("Inside search 4\nintSearchResultCnt="+intSearchResultCnt+"\n");
			dosOutput.writeUTF(Integer.toString(intSearchResultCnt));
			//System.out.println("intSearchResultCnt="+intSearchResultCnt+"\n");
			dosOutput.flush();
			//System.out.println("intSearchResultCnt="+intSearchResultCnt+"\n");

			//Looping through the Search Results and passing it to client
			for (int i=1; i < ResultList.size(); i++)
			{
				//os = socketClient.getOutputStream();
				//dosOutput = new DataOutputStream(os);
				//System.out.println("Inside search 4\ni="+i+"\n");
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
				//os = socketClient.getOutputStream();
				//dosOutput = new DataOutputStream(os);
				dosOutput.writeUTF(strErrorMessage);
				dosOutput.flush();
			}

			catch (Exception ex)
			{
				System.out.println (ex.getStackTrace());
			}
		}	}
	//Indexing Server function to search the data starts from here

	//Indexing Server function to unregister the data starts from here
	public void unregister()
	{
		String strStatus;
		try
		{
			//Receiving the File Name to be registered file from Peer
			//is = socketClient.getInputStream();
			//disInput = new DataInputStream (is);
			//strFileName = disInput.readUTF();
			//strFileName = arrStrParams[0];

			//Receiving the IP Address to be registered file from Peer
			//is = socketClient.getInputStream();
			//disInput = new DataInputStream (is);
			//strIPAddress = disInput.readUTF();
			//strIPAddress = arrStrParams[1];

			//function call to Unregister the file with received parameters
			strStatus = removeEntryConcurrMap (strFileName, strIPAddress);

			//Sending the Status Length
			//os = socketClient.getOutputStream();
			//dosOutput = new DataOutputStream(os);
			dosOutput.writeUTF(Integer.toString(strStatus.length()));
			dosOutput.flush();

			//Sending the Status of unregister
			//os = socketClient.getOutputStream();
			//dosOutput = new DataOutputStream(os);
			dosOutput.write(strStatus.getBytes());
			dosOutput.flush();
		}

		catch (Exception e)
		{
			try
			{
				//Sending the exception details to Client
				String strErrorMessage = e.getMessage();
				//os = socketClient.getOutputStream();
				//dosOutput = new DataOutputStream(os);
				dosOutput.writeUTF(strErrorMessage);
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
	public void run()
	{
		try
		{
			while (true)
			{
				//System.out.println("Inside run 1\n");
				//Receiving the choice to either Register or Search or Unregister a file from Peer
				disInput = new DataInputStream (socketClient.getInputStream());
				//System.out.println("Inside run 2\n");
				dosOutput = new DataOutputStream(socketClient.getOutputStream());
				//System.out.println("Inside run 3\n");
				//int intParamsLength = disInput.readInt();
				String strParams = disInput.readUTF();
				//System.out.println("Inside run 4\n");
				//System.out.println(strParams);
				//System.out.println("Inside run 5\n");
				splitParams (strParams, strDelimiter);
				//System.out.println("Inside run 6\n");
				switch (choice)
				{
					case 1:
						//System.out.println("Before Registry\n");
						registry();
						break;
	
					case 2:
						//System.out.println("Before Search\n");
						search();
						break;
	
					case 3:
						unregister();
						break;
	
					default:
						String strStatus = "Invalid choice\n";
						dosOutput.writeUTF(strStatus);
						dosOutput.flush();
				}
			}
		}

		catch (Exception e)
		{
			String strErrorMessage = e.getMessage();
			System.out.println(strErrorMessage);
		}
	}

	//function to split the received input parameters starts from here
	public int splitParams(String strParamsLocal, String strDelimiterLocal)
	{
		int i=0;
		int k=0;
		i = strParamsLocal.indexOf(strDelimiterLocal);
		while (i != -1)
		{
			//System.out.println(i);
			String strTemp = strParamsLocal.substring(0, i);
			//System.out.println("strTemp=" + strTemp + "\n");
			if (k==0)
			{
				choice = Integer.parseInt(strTemp);
				//System.out.println("choice=" + choice + "\n");
			}
			switch (choice)
			{
				case 1:
					switch (k)
					{
						case 0:
							choice = Integer.parseInt(strTemp);
							//System.out.println("choice" + choice +"\n");
							break;
						case 1:
							strFileName = strTemp;
							//System.out.println("strFileName" + strFileName +"\n");
							break;
						case 2:
							strFilePath = strTemp;
							//System.out.println("strFilePath" + strFilePath +"\n");
							break;
						case 3:
							strIPAddress = strTemp;
							//System.out.println("strIPAddress" + strIPAddress +"\n");
							break;
						case 4:
							intPortNumber = Integer.parseInt(strTemp);
							//System.out.println("intPortNumber" + intPortNumber +"\n");
							break;
						case 5:
							lngFileSize = Long.parseLong(strTemp);
							//System.out.println("lngFileSize" + lngFileSize +"\n");
							strParamsLocal = strParamsLocal.substring(i + strDelimiterLocal.length());
							i = strParamsLocal.indexOf(strDelimiterLocal);
							//System.out.println("strParamsLocal"+strParamsLocal+"\n");
							strReplication = strParamsLocal;
							//System.out.println("strReplication" + strReplication +"\n");
							break;
					}
					break;
				case 2:
						switch (k)
						{
							case 0:
								choice = Integer.parseInt(strTemp);
								//System.out.println("choice" + choice +"\n");
								strParamsLocal = strParamsLocal.substring(i + strDelimiterLocal.length());
								i = strParamsLocal.indexOf(strDelimiterLocal);
								//System.out.println("strParamsLocal"+strParamsLocal+"\n");
								strFileName = strParamsLocal;
								//System.out.println("strFileName" + strFileName +"\n");
								break;
						}
				case 3:
					switch (k)
					{
						case 0:
							choice = Integer.parseInt(strTemp);
							//System.out.println("choice" + choice +"\n");
							break;
						case 1:
							strFileName = strTemp;
							//System.out.println("strFileName" + strFileName +"\n");
							strParamsLocal = strParamsLocal.substring(i + strDelimiterLocal.length());
							i = strParamsLocal.indexOf(strDelimiterLocal);
							//System.out.println("strParamsLocal"+strParamsLocal+"\n");
							strIPAddress = strParamsLocal;
							//System.out.println("strIPAddress" + strIPAddress +"\n");
							break;
					}
					break;
			}
			if (strParamsLocal.indexOf(strDelimiterLocal)>=0)
			{
				k++;
				strParamsLocal = strParamsLocal.substring(i + strDelimiterLocal.length());
				//System.out.println("strParamsLocal"+strParamsLocal+"\n");
				i = strParamsLocal.indexOf(strDelimiterLocal);
			}
		}
		return 0;
	}
	//function to split the received input parameters ends over here

	//function to download the file from Peer Server starts from here
	public static String obtain (String strFileName, String strFilePath, String strIPAddress, int intPortNumber)
	{
		String strStatus = "";
		String strPeerServerIP = strIPAddress;
		int intPeerServerPort = intPortNumber;
		InetAddress address;
		Socket socket;
		try
		{
			address = InetAddress.getByName(strPeerServerIP);
			socket = new Socket(address, intPeerServerPort);
		}

		catch(IOException ex)
		{
			strStatus = ex.getMessage();
			return strStatus;
		}

		File theDir;
		//OutputStream os;
		DataOutputStream dosOutput;
		long lngFileSize;
		FileOutputStream outputStream;
		byte[] buffer;

		String strServerFilePath;
		String strClientFilePath;
		String strServerFileName;
		String strClientFileName;

		//InputStream is;
		DataInputStream disInput;
		int intRead = 0;
		int intTotalBytes = 0;

		try
		{
			strServerFilePath = strFilePath;
			strClientFilePath = ".\\PeerFiles\\";
			theDir = new File (strClientFilePath);
			if (!theDir.exists())
			{
				theDir.mkdir();
			}
			strServerFileName = strServerFilePath + strFileName;
			strClientFileName = strClientFilePath + strFileName;

			//initializing the streams
			dosOutput = new DataOutputStream (socket.getOutputStream());
			disInput = new DataInputStream(socket.getInputStream());

			//sending the file name along with path to peer server
			dosOutput.writeUTF (strServerFileName);
			dosOutput.flush();

			//receive the file size from server
			lngFileSize = Long.parseLong(disInput.readUTF());

			if (lngFileSize > 0)
			{
				//sending the buffer size to peer server
				dosOutput.writeUTF (Integer.toString(intBufferSize));
				dosOutput.flush();

				outputStream = new FileOutputStream(strClientFileName);
				intRead = 0;
				intTotalBytes = 0;

				try
				{
					//code to read the data sent from peer server and store it into a disk file
					buffer = new byte[Math.min (intBufferSize, (int)(lngFileSize-intTotalBytes))];
					while((intRead = disInput.read(buffer)) > 0)
					{
						outputStream.write(buffer);
						outputStream.flush();
						intTotalBytes += intRead;
						buffer = new byte [Math.min (intBufferSize, (int)(lngFileSize-intTotalBytes))];
					}
					outputStream.close();
					strStatus = disInput.readUTF();
				}

				catch(IOException e)
				{
					strStatus = "Error while writing the requested file "+ strClientFileName +"!!\n";
					return strStatus;
				}
			}
			else
			{
				strStatus = "Unable to find the requested file on Peer Server!!\n";
				return strStatus;
			}
		}

		catch (Exception e)
		{
			strStatus = e.getMessage();
			return strStatus;
		}

		finally
		{
			try
			{
				socket.close();
			}

			catch(Exception e)
			{
				strStatus = e.getMessage();
				return strStatus;
			}
		}
		return strStatus;
	}
	//function to download the file from Peer Server ends over here

}