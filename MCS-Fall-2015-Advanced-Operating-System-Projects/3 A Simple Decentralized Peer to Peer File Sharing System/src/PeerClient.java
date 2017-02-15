//import java.io.InputStream;
//import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.io.FileInputStream;
import java.net.SocketException;

public class PeerClient
{
	//variable declaration
	private static Socket socket;
	private static int intBufferSize=0;
	private static int intIndexingServerCount=0;
	private static ArrayList<Socket> arrlstIndexingServerSockets;
	private static String strClientID="";
	private static String strDelimiter="%@$#";
	private static long lngTotalStart, lngTotalEnd, lngIndividualStart, lngIndividualEnd, lngTotalTimeTaken;
	//private static int intTotalDataPoints = 100000;
	private static int intClientID;
	private static String strPeerServerIP="'";
	private static int intPeerServerPort=0;
	private static String strPeerDirectoryPath="";
	private static String strPeerDownloadPath="";
	private static String strLogFileName="";
	private static int intReplicationFactor=0;
	private static int intHashCode, intServerNumber;//, intSockIndex, intDR1SockIndex, intDR2SockIndex;

	//function to download the file from Peer Server starts from here
	public static String obtain (String strFileName, String strFilePath, String strIPAddress, int intPortNumber)
	{
		String strStatus = "";
		String strPeerServerIP = strIPAddress;
		int intPeerServerPort = intPortNumber;
		InetAddress address;
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
			strClientFilePath = strPeerDownloadPath;
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

	//function to register the file to Indexing Server starts from here
	public static String registry (String strFileName, String strFilePath, String strIPAddress, int intPortNumber, Socket sockTemp, String strReplication)
	{
		String strStatus = "";
		DataOutputStream dosOutput;
		DataInputStream disInput;
		if (strFileName != "")
		{
			File strServerFile = new File (strFilePath + strFileName);
			if (strServerFile.exists())
			{
				try
				{

					//Initializing Input and Output Streams for Socket
					dosOutput = new DataOutputStream(sockTemp.getOutputStream());
					disInput = new DataInputStream(sockTemp.getInputStream());

					//forming the string of parameters
					String strParams = "1" + strDelimiter + strFileName + strDelimiter + strFilePath + strDelimiter 
							+ strIPAddress + strDelimiter + intPortNumber + strDelimiter + strServerFile.length() + strDelimiter 
							+ strReplication;

					//Sending the length of string of parameters to Indexing Server
					//dosOutput.writeInt (strParams.length());
					//dosOutput.flush();

					//Sending the string of parameters to Indexing Server
					//System.out.println(strParams);
					dosOutput.writeUTF (strParams);
					dosOutput.flush();

					//Sending the choice to either Register or Search or unregister a file to Indexing Server
					//dosOutput.writeInt (1);
					//dosOutput.flush();

					//Sending the File Name to be registered on Indexing Server
					//dosOutput.writeUTF (strFileName);
					//dosOutput.flush();

					//Sending the File Path to be registered on Indexing Server
					//dosOutput.writeUTF (strFilePath);
					//dosOutput.flush();

					//Sending the Peer Server IP Address to be registered on Indexing Server
					//dosOutput.writeUTF (strIPAddress);
					//dosOutput.flush();

					//Sending the Peer Server Port Number to be registered on Indexing Server
					//dosOutput.writeInt (intPortNumber);
					//dosOutput.flush();

					//Sending the File Size to be registered on Indexing Server
					//dosOutput.writeLong (strServerFile.length());
					//dosOutput.flush();

					//Receiving the Status Length from Index Server
					int intMsgLength = Integer.parseInt(disInput.readUTF());
					byte[] buffer = new byte [intMsgLength];


					//Receiving the Status from Index Server
					disInput.read(buffer);
					strStatus = new String(buffer);
				}

				catch (Exception e)
				{
					strStatus = e.getMessage();
					return strStatus;
				}
			}
			else
			{
				strStatus = "The File does not exist in the Path mentioned. Please check the File Name and File Path again!!\n";
				return strStatus;
			}
		}
		else
		{
			strStatus = "The File does not exist in the Path mentioned. Please check the File Name and File Path again!!\n";
			return strStatus;
		}
		return strStatus;
	}
	//function to register the file to Indexing Server ends over here

	//function to search the file on Indexing Server starts from here
	public static ArrayList <String> search (String strFileName, Socket sockTemp)
	{
		ArrayList <String> arrLstSearchResult = new ArrayList <String> ();
		DataOutputStream dosOutput=null;
		DataInputStream disInput=null;
		int intAlternateSocket=-1;
		Socket sockLocal;
		int i=0;
		int j=0;
		if (strFileName != "")
		{
			try
			{

				try
				{
					sockLocal = sockTemp;
					//Initializing Input and Output Streams for Socket
					dosOutput = new DataOutputStream(sockLocal.getOutputStream());
					disInput = new DataInputStream(sockLocal.getInputStream());
					intAlternateSocket = 0;
	
					//forming the string of parameters
					String strParams = "2" + strDelimiter + strFileName;
					//System.out.println(strParams+"\n");
	
					//Sending the length of string of parameters to Indexing Server
					//dosOutput.writeInt (strParams.length());
					//dosOutput.flush();
	
					//Sending the string of parameters to Indexing Server
					dosOutput.writeUTF (strParams);
					dosOutput.flush();
					//System.out.println(sockLocal);
					//System.out.println("After flush\n");

					//Sending the choice to either Register or Search a file to Indexing Server
					//dosOutput.writeInt (2);
					//dosOutput.flush();

					//Sending the File Name to be registered file to Indexing Server
					//dosOutput.writeUTF (strFileName);
					//dosOutput.flush();

					//Receiving the Search Result Count from Indexing Server
					disInput = new DataInputStream(sockLocal.getInputStream());
					int intSearchResultCnt = Integer.parseInt(disInput.readUTF());
					//System.out.println("intSearchResultCnt="+intSearchResultCnt);

					if (intSearchResultCnt < 0)
					{
						arrLstSearchResult.add("-1");
						arrLstSearchResult.add("Error while searching " + strFileName + " on Indexing Server");
						return arrLstSearchResult;
					}
					else if (intSearchResultCnt == 0)
					{
						arrLstSearchResult.add("0");
						arrLstSearchResult.add("No search results found for file " + strFileName + " on Indexing Server");
						return arrLstSearchResult;
					}
					else
					{
						arrLstSearchResult.add(Integer.toString(intSearchResultCnt));
						for (i=0; i <= intSearchResultCnt; i++)
						{
							//Receiving the Search Result details from Indexing Server
							String strResultDet = disInput.readUTF();
							//System.out.println(strResultDet);
							arrLstSearchResult.add(strResultDet);
						}
					}
				}

				catch (IOException expSock)
				{
					//System.out.println("Inside Exception\n");
					if (intReplicationFactor>0)
					{
						intAlternateSocket = -1;
						try
						{
							for (j=0; ((j < intIndexingServerCount) && (intAlternateSocket==-1));j++)
							{
								sockLocal = arrlstIndexingServerSockets.get(j);
								//Initializing Input and Output Streams for Socket
								dosOutput = new DataOutputStream(sockLocal.getOutputStream());
								disInput = new DataInputStream(sockLocal.getInputStream());
								intAlternateSocket = 0;
				
								//forming the string of parameters
								String strParams = "2" + strDelimiter + strFileName;
				
								//Sending the length of string of parameters to Indexing Server
								//dosOutput.writeInt (strParams.length());
								//dosOutput.flush();
				
								//Sending the string of parameters to Indexing Server
								dosOutput.writeUTF (strParams);
								dosOutput.flush();
								//System.out.println(sockLocal);
								//System.out.println("After flush\n");

								//Sending the choice to either Register or Search a file to Indexing Server
								//dosOutput.writeInt (2);
								//dosOutput.flush();

								//Sending the File Name to be registered file to Indexing Server
								//dosOutput.writeUTF (strFileName);
								//dosOutput.flush();

								//Receiving the Search Result Count from Indexing Server
								int intSearchResultCnt = Integer.parseInt(disInput.readUTF());
								//System.out.println(intSearchResultCnt);

								if (intSearchResultCnt < 0)
								{
									arrLstSearchResult.add("-1");
									arrLstSearchResult.add("Error while searching " + strFileName + " on Indexing Server");
									return arrLstSearchResult;
								}
								else if (intSearchResultCnt == 0)
								{
									arrLstSearchResult.add("0");
									arrLstSearchResult.add("No search results found for file " + strFileName + " on Indexing Server");
									return arrLstSearchResult;
								}
								else
								{
									arrLstSearchResult.add(Integer.toString(intSearchResultCnt));
									for (i=0; i <= intSearchResultCnt; i++)
									{
										//Receiving the Search Result details from Indexing Server
										String strResultDet = disInput.readUTF();
										//System.out.println(strResultDet);
										arrLstSearchResult.add(strResultDet);
									}
								}
							}
						}

						catch (IOException expSock2)
						{
							intAlternateSocket = -1;
						}
					}
					else
					{
						//System.out.println("Inside exception else\n");
						arrLstSearchResult.add("-1");
						arrLstSearchResult.add(expSock.getMessage()+"\n");
						return arrLstSearchResult;
					}
				}

				catch (Exception expSock6)
				{
					arrLstSearchResult.add("-1");
					arrLstSearchResult.add(expSock6.getMessage()+"\n");
					return arrLstSearchResult;
				}
			}

			catch (Exception e)
			{
				arrLstSearchResult.add("-1");
				arrLstSearchResult.add(e.getMessage());
				return arrLstSearchResult;
			}

			finally
			{
				if (intAlternateSocket==-1)
				{
					arrLstSearchResult.add("-1");
					arrLstSearchResult.add("Unable to connect to primary Indexing Server and no backup Indexing Servers available\n");
				}
			}
		}
		else
		{
			arrLstSearchResult.add("-1");
			arrLstSearchResult.add("File Name cannot be blank. Please check the File Name again!!");
			return arrLstSearchResult;
		}
		return arrLstSearchResult;
	}
	//function to search the file on Indexing Server ends over here

	//function to register the file to Indexing Server starts from here
	public static String unregister (String strFileName, String strIPAddress, Socket sockTemp)
	{
		String strStatus = "";
		DataOutputStream dosOutput;
		DataInputStream disInput;
		if (strFileName != "")
		{

			try
			{

				//Initializing Input and Output Streams for Socket
				dosOutput = new DataOutputStream(sockTemp.getOutputStream());
				disInput = new DataInputStream(sockTemp.getInputStream());

				//forming the string of parameters
				String strParams = "3" + strDelimiter + strFileName + strDelimiter + strIPAddress;

				//Sending the length of string of parameters to Indexing Server
				//dosOutput.writeInt (strParams.length());
				//dosOutput.flush();

				//Sending the string of parameters to Indexing Server
				dosOutput.writeUTF (strParams);
				dosOutput.flush();

				//Sending the File Name to be registered on Indexing Server
				//dosOutput.writeUTF (strFileName);
				//dosOutput.flush();

				//Sending the Peer Server IP Address to be registered on Indexing Server
				//dosOutput.writeUTF (strIPAddress);
				//dosOutput.flush();

				//Receiving the Status Length from Indexing Server
				int intMsgLength = Integer.parseInt(disInput.readUTF());
				byte[] buffer = new byte [intMsgLength];

				//Receiving the Status from Index Server
				disInput.read(buffer);
				strStatus = new String(buffer);
			}

			catch (Exception e)
			{
				strStatus = e.getMessage();
				return strStatus;
			}
		}
		else
		{
			strStatus = "The File does not exist in the Path mentioned. Please check the File Name and File Path again!!\n";
			return strStatus;
		}
		return strStatus;
	}
	//function to unregister the file to Indexing Server ends over here

	//code for console based inputs starts from here
	/*
	public static void main (String args[])
	{
		String strFileName;
		String strFilePath;
		String strIPAddress;
		int intPortNumber;
		int choice;
		String strStatus;
		Scanner scan = new Scanner(System.in);
		do
		{
			System.out.println("\nClient Operations\n");
			System.out.println("1.    Register ");
			System.out.println("2.    Search");
			System.out.println("<= 0. Quit");

			choice = scan.nextInt();
			switch (choice)
			{
				case 1 :
					System.out.println("Enter File Name: ");
					strFileName = scan.next();
					System.out.println("Enter File Path: ");
					strFilePath = scan.next();
					System.out.println("Enter Peer IP Address: ");
					strIPAddress = scan.next();
					System.out.println("Enter Port Number: ");
					intPortNumber = scan.nextInt();
					strStatus = registry (strFileName, strFilePath, strIPAddress, intPortNumber);
					System.out.println (strStatus);
					break;

				case 2 :
					System.out.println("Enter File Name: ");
					strFileName = scan.next();
					ArrayList <String> arrLstSearchResult = search (strFileName);
					System.out.println (arrLstSearchResult);
					break;

				default :
					System.out.println("Wrong Entry \n ");
					break;
			}
			System.out.println("\nDo you want to continue (Type any number less than or equal to zero to quit) \n");
			choice = scan.nextInt();
		} while (choice > 0);
		scan.close();
	}
	*/
	//code for console based inputs ends over here

	public static void main (String args[])
	{
		String [] arrIPAddress = new String [50];
		String strMessage="", strKeyName="";
		int i = 0;
		//Reading parameters' data from Properties file starts from here
		Properties dcp2pProp = new Properties();
		try
		{
			dcp2pProp.load(new FileInputStream("./dcp2p.properties"));
			intBufferSize = Integer.parseInt(dcp2pProp.getProperty("intBufferSize"));
			strDelimiter = dcp2pProp.getProperty("strDelimiter");

			//Reading the Indexing Server details from properties file starts from here
			intIndexingServerCount = Integer.parseInt(dcp2pProp.getProperty("intIndexServerCount"));
			try
			{
				intReplicationFactor = Integer.parseInt(dcp2pProp.getProperty("intIndexReplicationFactor"));
			}
			catch (Exception e)
			{
				intReplicationFactor = 0;
			}
			
			if (intIndexingServerCount!=0)
			{
				arrlstIndexingServerSockets = new ArrayList<Socket>();
				for (i=0; i<intIndexingServerCount;i++)
				{
					String strIndexingServerIP = dcp2pProp.getProperty("strIndexServer" + (i + 1) + "IP");
					int intIndexingServerPort = Integer.parseInt(dcp2pProp.getProperty("intIndexServer" + (i + 1) + "Port"));
					InetAddress address;
					try
					{
						//reading the Indexing Servers' info and storing the sockets into an ArrayList
						address = InetAddress.getByName(strIndexingServerIP);
						Socket sockIndexing = new Socket(address, intIndexingServerPort);
						if (i == 0)
						{
							//System.out.println(sockIndexing +"\n");
							intClientID = Integer.parseInt(new DataInputStream (sockIndexing.getInputStream()).readUTF());
						}
						arrlstIndexingServerSockets.add(sockIndexing);
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
				System.out.println ("No Distributed Indexing Server details are available in properties file!!!\n");
				System.exit(1);
			}
			//Reading the Indexing Server details from properties file ends over here

			//Reading the Peer Server details from properties file starts from here
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

			for (String key : dcp2pProp.stringPropertyNames())
			{
				if ((key.substring(0, 7).toUpperCase().equals("STRPEER")) && (key.substring(key.length() - 2).toUpperCase().equals("IP")))
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
			if (strKeyName != "")
			{
				strPeerServerIP = dcp2pProp.getProperty(strKeyName);
				String strPropertyName = strKeyName.replace("IP", "Port");
				strPropertyName = strPropertyName.replace("str", "int");
				intPeerServerPort = Integer.parseInt(dcp2pProp.getProperty(strPropertyName));
				String strPeerDirectoryRelPath = dcp2pProp.getProperty(strKeyName.replace("IP",  "DirectoryPath"));
				try
				{
					strPeerDirectoryPath = new File (strPeerDirectoryRelPath).getCanonicalPath()+"/";
				}

				catch (Exception ex)
				{
					System.out.println (ex.getMessage());
				}

				String strPeerDownloadRelPath = dcp2pProp.getProperty(strKeyName.replace("IP",  "DownloadPath"));
				try
				{
					strPeerDownloadPath = new File (strPeerDownloadRelPath).getCanonicalPath()+"/";
				}

				catch (Exception ex)
				{
					System.out.println (ex.getMessage());
				}
				strLogFileName = dcp2pProp.getProperty(strKeyName.replace("IP",  "LogFileName"));
			}
			else
			{
				System.out.println ("No Peer Server details are available in properties file!!!\n");
				System.exit(2);
			}
			//Reading the Peer Server details from properties file starts from here
		}

		catch (IOException e)
		{
			System.out.println (e.getStackTrace());
			System.exit(1);
		}
		//Reading parameters' data from Properties file ends over here

		if ((intBufferSize != 0) && (arrlstIndexingServerSockets.size() > 0) &&
				(strPeerServerIP != "") && (intPeerServerPort != 0) && (strPeerDirectoryPath != "") &&
				(strPeerDownloadPath != "") && (strLogFileName != ""))
		{
			//code to read peer directory and register all the files in the directory on Indexing Server starts from here
			File[] PeerDirectoryFiles = new File (strPeerDirectoryPath).listFiles();

			String strAbsPeerDirectoryPath = "";
			try
			{
				strAbsPeerDirectoryPath = new File (strPeerDirectoryPath).getCanonicalPath()+"/";
			}

			catch (Exception e)
			{
				System.out.println (e.getMessage());
			}

			if (strAbsPeerDirectoryPath != "")
			{
				int intFileCount = 1;
				try
				{
					strClientID = String.format("%04d", intClientID);
					strLogFileName = "./ClientOperations_" + strClientID +".txt";
					FileOutputStream outputStream = new FileOutputStream(strLogFileName);
					strMessage = "Registry for files started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
					System.out.println (strMessage);
					outputStream.write(strMessage.getBytes());
					outputStream.flush();


					lngTotalStart = 0;
					lngTotalEnd = 0;
					lngTotalStart = System.currentTimeMillis();
					for (File file : PeerDirectoryFiles)
					{
						if (!(file.isDirectory()))
						{
							strMessage = "Registry for FILE " + intFileCount + " (" + file.getName() + ") started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
							//System.out.println (strMessage);
							//outputStream.write(strMessage.getBytes());
							//outputStream.flush();
							intHashCode = Math.abs(file.getName().hashCode());
							intServerNumber = intHashCode % intIndexingServerCount;
							/*intSockIndex = intServerNumber;
							if (intIndexingServerCount>1)
							{
								intDR1SockIndex = intServerNumber - 1;
								if (intDR1SockIndex < 0)
								{
									intDR1SockIndex = Math.abs((intDR1SockIndex + intIndexingServerCount)) % intIndexingServerCount;
								}
							}
							else
							{
								intDR1SockIndex = -1;
							}
							//System.out.println("intDR1SockIndex is " + intDR1SockIndex + "\n");
							if (intIndexingServerCount>2)
							{
								intDR2SockIndex = intServerNumber + 1;
								if (intDR2SockIndex >= intIndexingServerCount)
								{
									intDR2SockIndex = (intDR2SockIndex + intIndexingServerCount) % intIndexingServerCount;
								}
							}
							else
							{
								intDR2SockIndex = -1;
							}*/
							//System.out.println("intDR2SockIndex is " + intDR2SockIndex + "\n");
							//System.out.println(intServerNumber);
							strMessage = registry (file.getName(), strAbsPeerDirectoryPath, strPeerServerIP, intPeerServerPort, arrlstIndexingServerSockets.get(intServerNumber), "N");
							//System.out.println (strMessage+"\n");
							//outputStream.write(strMessage.getBytes());
							//outputStream.flush();
							if (intReplicationFactor>0)
							{
								for (i=0;((i<intReplicationFactor)&&(intReplicationFactor<intIndexingServerCount));i++)
								{
									if (i != intServerNumber)
									{
										strMessage = registry (file.getName(), strAbsPeerDirectoryPath, strPeerServerIP, intPeerServerPort, arrlstIndexingServerSockets.get(i), "Y");
									}
								}
							}
							strMessage = "Registry for FILE " + intFileCount + " (" + file.getName() + ") completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
							//System.out.println (strMessage);
							//outputStream.write(strMessage.getBytes());
							//outputStream.flush();
							intFileCount++;
						}
					}
					strMessage = "Registry for " + (intFileCount-1) + " files completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
					System.out.println (strMessage);
					outputStream.write(strMessage.getBytes());
					outputStream.flush();
					lngTotalEnd = System.currentTimeMillis();
					strMessage = " Time taken for " + (intFileCount-1) + " individual Registry is " + (lngTotalEnd - lngTotalStart) + "mSec\n";
					System.out.println (strMessage);
					outputStream.write(strMessage.getBytes());
					outputStream.flush();
					//code to read peer directory and register all the files in the directory on Indexing Server ends over here

					lngTotalTimeTaken = 0;
					lngTotalTimeTaken = lngTotalTimeTaken + (lngTotalEnd - lngTotalStart);

					//code to read peer directory and lookup all the files in the directory on Indexing Server starts from here
					intFileCount = 1;
					lngTotalStart = System.currentTimeMillis();

					strMessage = "Lookup for files started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
					System.out.println (strMessage);
					outputStream.write(strMessage.getBytes());
					outputStream.flush();
					for (File file : PeerDirectoryFiles)
					{
						if (!(file.isDirectory()))
						{
							strMessage = "Lookup for FILE " + intFileCount + " (" + file.getName() + ") started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
							//System.out.println (strMessage);
							//outputStream.write(strMessage.getBytes());
							//outputStream.flush();
							intHashCode = Math.abs(file.getName().hashCode());
							intServerNumber = intHashCode % intIndexingServerCount;
							/*intSockIndex = intServerNumber;
							if (intIndexingServerCount>1)
							{
								intDR1SockIndex = intServerNumber - 1;
								if (intDR1SockIndex < 0)
								{
									intDR1SockIndex = Math.abs((intDR1SockIndex + intIndexingServerCount)) % intIndexingServerCount;
								}
							}
							else
							{
								intDR1SockIndex = -1;
							}
							//System.out.println("intDR1SockIndex is " + intDR1SockIndex + "\n");
							if (intIndexingServerCount>2)
							{
								intDR2SockIndex = intServerNumber + 1;
								if (intDR2SockIndex >= intIndexingServerCount)
								{
									intDR2SockIndex = (intDR2SockIndex + intIndexingServerCount) % intIndexingServerCount;
								}
							}
							else
							{
								intDR2SockIndex = -1;
							}
							//System.out.println("intDR2SockIndex is " + intDR2SockIndex + "\n");*/
							System.out.println(file.getName()+"\n");
							ArrayList <String> arrLstFile1 = search (file.getName(), arrlstIndexingServerSockets.get((intServerNumber)));
							//strMessage = "Lookup for FILE " + intFileCount + " (" + file.getName() + ") completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
							//System.out.println (strMessage);
							//outputStream.write(strMessage.getBytes());
							//outputStream.flush();

							strMessage = "Search Results for FILE " + intFileCount + " (" + file.getName() + ") are " + "\n";
							System.out.println (strMessage);
							//outputStream.write(strMessage.getBytes());
							//outputStream.flush();
							for (int j=1; j<arrLstFile1.size(); j++)
							{
								strMessage = arrLstFile1.get(j) + "\n";
								System.out.println (strMessage);
								//outputStream.write(strMessage.getBytes());
								//outputStream.flush();
							}
							intFileCount++;
						}
					}
					strMessage = "Lookup for " + (intFileCount-1) + " files completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
					System.out.println (strMessage);
					outputStream.write(strMessage.getBytes());
					outputStream.flush();
					lngTotalEnd = System.currentTimeMillis();
					strMessage = " Time taken for " + (intFileCount-1) + " individual Search is " + (lngTotalEnd - lngTotalStart) + "mSec\n";
					System.out.println (strMessage);
					outputStream.write(strMessage.getBytes());
					outputStream.flush();
					//code to read peer directory and lookup all the files in the directory on Indexing Server ends over here

					lngTotalTimeTaken = lngTotalTimeTaken + (lngTotalEnd - lngTotalStart);

					//code to read peer directory and download all the files in the download directory on Indexing Server starts from here.
					//In case if Search returns more than one result then the first file will be downloaded automatically for automation purpose
					intFileCount = 1;
					lngTotalStart = System.currentTimeMillis();

					strMessage = "Download for files started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
					System.out.println (strMessage);
					outputStream.write(strMessage.getBytes());
					outputStream.flush();
					for (File file : PeerDirectoryFiles)
					{
						if (!(file.isDirectory()))
						{
							intHashCode = Math.abs(file.getName().hashCode());
							intServerNumber = intHashCode % intIndexingServerCount;
							/*intSockIndex = intServerNumber;
							if (intIndexingServerCount>1)
							{
								intDR1SockIndex = intServerNumber - 1;
								if (intDR1SockIndex < 0)
								{
									intDR1SockIndex = Math.abs((intDR1SockIndex + intIndexingServerCount)) % intIndexingServerCount;
								}
							}
							else
							{
								intDR1SockIndex = -1;
							}
							System.out.println("intDR1SockIndex is " + intDR1SockIndex + "\n");
							if (intIndexingServerCount>2)
							{
								intDR2SockIndex = intServerNumber + 1;
								if (intDR2SockIndex >= intIndexingServerCount)
								{
									intDR2SockIndex = (intDR2SockIndex + intIndexingServerCount) % intIndexingServerCount;
								}
							}
							else
							{
								intDR2SockIndex = -1;
							}
							System.out.println("intDR2SockIndex is " + intDR2SockIndex + "\n");*/
							ArrayList <String> arrLstFile1 = search (file.getName(), arrlstIndexingServerSockets.get((intServerNumber)));
							if (arrLstFile1!=null)
							{
								String[] arrParams = arrLstFile1.get(2).split("\t");
								strMessage = "Download for FILE " + intFileCount + " (" + file.getName() + ") started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
								System.out.println (strMessage);
								//outputStream.write(strMessage.getBytes());
								//outputStream.flush();
								strMessage = obtain (arrParams[1], arrParams[3], arrParams[4],Integer.parseInt(arrParams[5]));
								System.out.println (strMessage);
								//outputStream.write(strMessage.getBytes());
								//outputStream.flush();
								strMessage = "Download for FILE " + intFileCount + " (" + file.getName() + ") completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
								System.out.println (strMessage);
								//outputStream.write(strMessage.getBytes());
								//outputStream.flush();
							}
							intFileCount++;
						}
					}
					strMessage = "Download for " + (intFileCount-1) + " files completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
					System.out.println (strMessage);
					outputStream.write(strMessage.getBytes());
					outputStream.flush();
					lngTotalEnd = System.currentTimeMillis();
					strMessage = " Time taken for " + (intFileCount-1) + " individual Downloads is " + (lngTotalEnd - lngTotalStart) + "mSec\n";
					System.out.println (strMessage);
					outputStream.write(strMessage.getBytes());
					outputStream.flush();
					//code to read peer directory and download all the files in the download directory on Indexing Server ends over here.

					lngTotalTimeTaken = lngTotalTimeTaken + (lngTotalEnd - lngTotalStart);

					strMessage = "Total Time taken for " + (intFileCount-1) + " individual Registry, Search and Downloads is " + (lngTotalTimeTaken) + "mSec\n";
					System.out.println (strMessage);
					outputStream.write(strMessage.getBytes());
					outputStream.flush();

					outputStream.close();
				}

				catch(FileNotFoundException e)
				{
					String strErrorMessage = "Error while writing the requested file "+ strLogFileName +"!!\n" + e.getMessage() + "\n";
					System.out.println(strErrorMessage);
				}

				catch(IOException e)
				{
					String strErrorMessage = "Error while writing the requested file "+ strLogFileName +"!!\n";
					System.out.println(strErrorMessage);
				}
			}
			else
			{
				System.out.println ("Unable to determine the absolute Path for Peer Directory\n");
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
			if (arrlstIndexingServerSockets.size() <= 0)
			{
				strErrorMsg += "Connection to various Distrbuted Indexing Servers";
			}
			if (strPeerServerIP == "")
			{
				strErrorMsg += "Peer Server IP variable\n";
			}
			if (intPeerServerPort == 0)
			{
				strErrorMsg += "Peer Server Port variable\n";
			}
			if (strPeerDirectoryPath == "")
			{
				strErrorMsg += "Peer Directory Path variable\n";
			}
			if (strPeerDownloadPath == "")
			{
				strErrorMsg += "Peer Download Path variable\n";
			}
			if (strLogFileName == "")
			{
				strErrorMsg += "Log File Name variable\n";
			}
			System.out.println (strErrorMsg);
			System.exit(1);
		}
	}
}