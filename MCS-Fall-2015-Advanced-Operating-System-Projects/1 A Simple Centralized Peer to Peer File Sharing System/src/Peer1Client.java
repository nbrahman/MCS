import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
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
import java.util.Properties;
import java.io.FileInputStream;

public class Peer1Client
{
	//variable declaration
	IndexingServer IndxSvr = new IndexingServer();
	private static Socket socket;
	private static int intBufferSize=0;
	private static String strIndexServerIP="";
	private static String strPeerServerIP="'";
	private static int intPeerServerPort=0;
	private static int intIndexServerPort=0;
	private static String strPeerDirectoryPath="";
	private static String strPeerDownloadPath="";
	private static String strLogFileName="";

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
		OutputStream os;
		DataOutputStream dosOutput;
		long lngFileSize;
		FileOutputStream outputStream;
		byte[] buffer;

		String strServerFilePath;
		String strClientFilePath;
		String strServerFileName;
		String strClientFileName;

		InputStream is;
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

			//sending the file name along with path to peer server
			os = socket.getOutputStream();
			dosOutput = new DataOutputStream (os);
			dosOutput.writeUTF (strServerFileName);
			dosOutput.flush();

			//receive the file size from server
			is = socket.getInputStream();
			disInput = new DataInputStream(is);
			lngFileSize = disInput.readLong();

			if (lngFileSize > 0)
			{
				//sending the buffer size to peer server
				os = socket.getOutputStream();
				dosOutput = new DataOutputStream (os);
				dosOutput.writeInt (intBufferSize);
				dosOutput.flush();

				outputStream = new FileOutputStream(strClientFileName);
				intRead = 0;
				intTotalBytes = 0;

				try
				{
					//code to read the data sent from peer server and store it into a disk file
					buffer = new byte[Math.min (intBufferSize, (int)(lngFileSize-intTotalBytes))];
					is = socket.getInputStream();
					disInput = new DataInputStream(is);
					while((intRead = disInput.read(buffer)) > 0)
					{
						outputStream.write(buffer);
						outputStream.flush();
						intTotalBytes += intRead;
						buffer = new byte [Math.min (intBufferSize, (int)(lngFileSize-intTotalBytes))];
					}
					outputStream.close();
					is = socket.getInputStream();
					disInput = new DataInputStream(is);
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

	//function to register the file to Indexing Server starts from here
	public static synchronized String registry (String strFileName, String strFilePath, String strIPAddress, int intPortNumber)
	{
		String strStatus = "";
		if (strFileName != "")
		{
			File strServerFile = new File (strFilePath + strFileName);
			if (strServerFile.exists())
			{
				InetAddress address;
				try
				{
					address = InetAddress.getByName(strIndexServerIP);
					socket = new Socket(address, intIndexServerPort);
				}

				catch(IOException ex)
				{
					strStatus = ex.getMessage();
					return strStatus;
				}
				OutputStream os;
				DataOutputStream dosOutput;
				InputStream is;
				DataInputStream disInput;

				try
				{
					//Sending the choice to either Register or Search or unregister a file to Indexing Server
					os = socket.getOutputStream();
					dosOutput = new DataOutputStream (os);
					dosOutput.writeInt (1);
					dosOutput.flush();

					//Sending the File Name to be registered on Indexing Server
					os = socket.getOutputStream();
					dosOutput = new DataOutputStream (os);
					dosOutput.writeUTF (strFileName);
					dosOutput.flush();

					//Sending the File Path to be registered on Indexing Server
					os = socket.getOutputStream();
					dosOutput = new DataOutputStream (os);
					dosOutput.writeUTF (strFilePath);
					dosOutput.flush();

					//Sending the Peer Server IP Address to be registered on Indexing Server
					os = socket.getOutputStream();
					dosOutput = new DataOutputStream (os);
					dosOutput.writeUTF (strIPAddress);
					dosOutput.flush();

					//Sending the Peer Server Port Number to be registered on Indexing Server
					os = socket.getOutputStream();
					dosOutput = new DataOutputStream (os);
					dosOutput.writeInt (intPortNumber);
					dosOutput.flush();

					//Sending the File Size to be registered on Indexing Server
					os = socket.getOutputStream();
					dosOutput = new DataOutputStream (os);
					dosOutput.writeLong (strServerFile.length());
					dosOutput.flush();

					//Receiving the Status Length from Index Server
					is = socket.getInputStream();
					disInput = new DataInputStream (is);
					int intMsgLength = disInput.readInt();
					byte[] buffer = new byte [intMsgLength];


					//Receiving the Status from Index Server
					is = socket.getInputStream();
					disInput = new DataInputStream (is);
					int intRead = disInput.read(buffer);
					strStatus = new String(buffer);
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
						String strErrorMessage = e.getMessage();
						System.out.println(strErrorMessage);
					}
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
	public static synchronized ArrayList <String> search (String strFileName)
	{
		ArrayList <String> arrLstSearchResult = new ArrayList <String> ();
		if (strFileName != "")
		{
			InetAddress address;
			try
			{
				address = InetAddress.getByName(strIndexServerIP);
				socket = new Socket(address, intIndexServerPort);
			}

			catch(IOException ex)
			{
				arrLstSearchResult.add ("-1");
				arrLstSearchResult.add ("Error occured while creating socket!!"+ ex.getMessage());
				return arrLstSearchResult;
			}

			OutputStream os;
			DataOutputStream dosOutput;
			InputStream is;
			DataInputStream disInput;

			try
			{
				//Sending the choice to either Register or Search a file to Indexing Server
				os = socket.getOutputStream();
				dosOutput = new DataOutputStream (os);
				dosOutput.writeInt (2);
				dosOutput.flush();

				//Sending the File Name to be registered file to Indexing Server
				os = socket.getOutputStream();
				dosOutput = new DataOutputStream (os);
				dosOutput.writeUTF (strFileName);
				dosOutput.flush();

				//Receiving the Search Result Count from Indexing Server
				is = socket.getInputStream();
				disInput = new DataInputStream (is);
				int intSearchResultCnt = disInput.readInt();

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
					for (int i=0; i <= intSearchResultCnt; i++)
					{
						//Receiving the Search Result details from Indexing Server
						is = socket.getInputStream();
						disInput = new DataInputStream (is);
						String strResultDet = disInput.readUTF();
						arrLstSearchResult.add(strResultDet);
					}
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
				try
				{
					socket.close();
				}

				catch(Exception e)
				{
					arrLstSearchResult.add("-1");
					arrLstSearchResult.add(e.getMessage());
					return arrLstSearchResult;
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
	public static synchronized String unregister (String strFileName, String strIPAddress)
	{
		String strStatus = "";
		if (strFileName != "")
		{
			if (strFileName != "")
			{
				InetAddress address;
				try
				{
					address = InetAddress.getByName(strIndexServerIP);
					socket = new Socket(address, intIndexServerPort);
				}

				catch(IOException ex)
				{
					strStatus = ex.getMessage();
					return strStatus;
				}
				OutputStream os;
				DataOutputStream dosOutput;
				InputStream is;
				DataInputStream disInput;

				try
				{
					//Sending the choice to either Register or Search or unregister a file to Indexing Server
					os = socket.getOutputStream();
					dosOutput = new DataOutputStream (os);
					dosOutput.writeInt (1);
					dosOutput.flush();

					//Sending the File Name to be registered on Indexing Server
					os = socket.getOutputStream();
					dosOutput = new DataOutputStream (os);
					dosOutput.writeUTF (strFileName);
					dosOutput.flush();

					//Sending the Peer Server IP Address to be registered on Indexing Server
					os = socket.getOutputStream();
					dosOutput = new DataOutputStream (os);
					dosOutput.writeUTF (strIPAddress);
					dosOutput.flush();

					//Receiving the Status Length from Index Server
					is = socket.getInputStream();
					disInput = new DataInputStream (is);
					int intMsgLength = disInput.readInt();
					byte[] buffer = new byte [intMsgLength];

					//Receiving the Status from Index Server
					is = socket.getInputStream();
					disInput = new DataInputStream (is);
					int intRead = disInput.read(buffer);
					strStatus = new String(buffer);
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
						String strErrorMessage = e.getMessage();
						System.out.println(strErrorMessage);
					}
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
		//Reading parameters' data from Properties file starts from here
		Properties p2pProp = new Properties();
		try
		{
			p2pProp.load(new FileInputStream("./p2pprop.properties"));
			intBufferSize = Integer.parseInt(p2pProp.getProperty("intBufferSize"));
			strIndexServerIP = p2pProp.getProperty("strIndexServerIP");
			intIndexServerPort = Integer.parseInt(p2pProp.getProperty("intIndexServerPort"));
			strPeerServerIP = p2pProp.getProperty("strPeer1ServerIP");
			intPeerServerPort = Integer.parseInt(p2pProp.getProperty("intPeer1ServerPort"));
			String strPeerDirectoryRelPath = p2pProp.getProperty("strPeer1DirectoryPath");
			try
			{
				strPeerDirectoryPath = new File (strPeerDirectoryRelPath).getCanonicalPath()+"/";
			}

			catch (Exception e)
			{
				System.out.println (e.getMessage());
			}

			String strPeerDownloadRelPath = p2pProp.getProperty("strPeer1DownloadPath");
			try
			{
				strPeerDownloadPath = new File (strPeerDownloadRelPath).getCanonicalPath()+"/";
			}

			catch (Exception e)
			{
				System.out.println (e.getMessage());
			}
			strLogFileName = p2pProp.getProperty("strPeer1LogFileName");
		}

		catch (IOException e)
		{
			System.out.println (e.getStackTrace());
			System.exit(1);
		}
		//Reading parameters' data from Properties file ends over here

		if ((intBufferSize != 0) && (strIndexServerIP != "") && (intIndexServerPort != 0) &&
				(strPeerServerIP != "") && (intPeerServerPort != 0) && (strPeerDirectoryPath != "") &&
				(strPeerDownloadPath != "") && (strLogFileName != ""))
		{
			//code to read peer directory and register all the files in the directory on Indexing Server starts from here
			File[] PeerDirectoryFiles = new File (strPeerDirectoryPath).listFiles();

			String strAbsPeerDirectoryPath = "";
			String strMessage = "";
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
					FileOutputStream outputStream = new FileOutputStream(strLogFileName);
					strMessage = "Registry for files started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
					System.out.println (strMessage);
					outputStream.write(strMessage.getBytes());
					outputStream.flush();

					for (File file : PeerDirectoryFiles)
					{
						if (!(file.isDirectory()))
						{
							strMessage = "Registry for FILE " + intFileCount + " (" + file.getName() + ") started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
							System.out.println (strMessage);
							outputStream.write(strMessage.getBytes());
							outputStream.flush();
							strMessage = registry (file.getName(), strAbsPeerDirectoryPath, strPeerServerIP, intPeerServerPort);
							System.out.println (strMessage+"\n");
							outputStream.write(strMessage.getBytes());
							outputStream.flush();
							strMessage = "Registry for FILE " + intFileCount + " (" + file.getName() + ") completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
							System.out.println (strMessage);
							outputStream.write(strMessage.getBytes());
							outputStream.flush();
							intFileCount++;
						}
					}
					strMessage = "Registry for " + (intFileCount-1) + " files completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
					System.out.println (strMessage);
					outputStream.write(strMessage.getBytes());
					outputStream.flush();
					//code to read peer directory and register all the files in the directory on Indexing Server ends over here

					//code to read peer directory and lookup all the files in the directory on Indexing Server starts from here
					intFileCount = 1;

					strMessage = "Lookup for files started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
					System.out.println (strMessage);
					outputStream.write(strMessage.getBytes());
					outputStream.flush();
					for (File file : PeerDirectoryFiles)
					{
						if (!(file.isDirectory()))
						{
							strMessage = "Lookup for FILE " + intFileCount + " (" + file.getName() + ") started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
							System.out.println (strMessage);
							outputStream.write(strMessage.getBytes());
							outputStream.flush();
							ArrayList <String> arrLstFile1 = search (file.getName());
							strMessage = "Lookup for FILE " + intFileCount + " (" + file.getName() + ") completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
							System.out.println (strMessage);
							outputStream.write(strMessage.getBytes());
							outputStream.flush();

							strMessage = "Search Results for FILE " + intFileCount + " (" + file.getName() + ") are " + "\n";
							System.out.println (strMessage);
							outputStream.write(strMessage.getBytes());
							outputStream.flush();
							for (int i=1; i<arrLstFile1.size(); i++)
							{
								strMessage = arrLstFile1.get(i) + "\n";
								System.out.println (strMessage);
								outputStream.write(strMessage.getBytes());
								outputStream.flush();
							}
							intFileCount++;
						}
					}
					strMessage = "Lookup for " + (intFileCount-1) + " files completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
					System.out.println (strMessage);
					outputStream.write(strMessage.getBytes());
					outputStream.flush();
					//code to read peer directory and lookup all the files in the directory on Indexing Server ends over here

					//code to read peer directory and download all the files in the download directory on Indexing Server starts from here.
					//In case if Search returns more than one result then the first file will be downloaded automatically for automation purpose
					intFileCount = 1;

					strMessage = "Download for files started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
					System.out.println (strMessage);
					outputStream.write(strMessage.getBytes());
					outputStream.flush();
					for (File file : PeerDirectoryFiles)
					{
						if (!(file.isDirectory()))
						{
							ArrayList <String> arrLstFile1 = search (file.getName());
							if (arrLstFile1!=null)
							{
								String[] arrParams = arrLstFile1.get(2).split("\t");
								strMessage = "Download for FILE " + intFileCount + " (" + file.getName() + ") started at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
								System.out.println (strMessage);
								outputStream.write(strMessage.getBytes());
								outputStream.flush();
								strMessage = obtain (arrParams[1], arrParams[3], arrParams[4],Integer.parseInt(arrParams[5]));
								System.out.println (strMessage);
								outputStream.write(strMessage.getBytes());
								outputStream.flush();
								strMessage = "Download for FILE " + intFileCount + " (" + file.getName() + ") completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
								System.out.println (strMessage);
								outputStream.write(strMessage.getBytes());
								outputStream.flush();
							}
							intFileCount++;
						}
					}
					strMessage = "Download for " + (intFileCount-1) + " files completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()) + "\n";
					System.out.println (strMessage);
					outputStream.write(strMessage.getBytes());
					outputStream.flush();
					//code to read peer directory and download all the files in the download directory on Indexing Server ends over here.

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
			if (strIndexServerIP == "")
			{
				strErrorMsg += "Index Server IP variable\n";
			}
			if (intIndexServerPort == 0)
			{
				strErrorMsg += "Index Server Port variable\n";
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