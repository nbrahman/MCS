import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PassFile implements Runnable
{
	private int intBufferSize;
	private Socket socketClient;
	private int intFileNumber;
	private long lngFileSize;
	private File strServerFile;
	private InputStream is;
	private DataInputStream disInput;
	private String strFileName = "";
	private byte[] buffer = new byte [intBufferSize];
	private FileInputStream inpStr;
	private int intTotalBytes;
	private int intRead;
	private OutputStream os;
	private DataOutputStream dosOutput;

	//Constructor
	public PassFile (Socket socket, int intFileNumber, int intBufferSize)
	{
		this.socketClient = socket;
		this.intFileNumber = intFileNumber;
		this.intBufferSize = intBufferSize;
		System.out.println ("Sending File " + this.intFileNumber + " to " + socketClient);
	}

	//thread handler code for Peer Server
	public synchronized void run()
	{
		String strStatus = "";
		try
		{
			//Receiving the File Name to be downloaded by Peer
			is = socketClient.getInputStream();
			disInput = new DataInputStream (is);
			strFileName = disInput.readUTF();

			//determine the existence and length of the file before starting the transfer
			if (strFileName != "")
			{
					strServerFile = new File (strFileName);
					if (strServerFile.exists())
					{
						lngFileSize = strServerFile.length();
					}
					else
					{
						lngFileSize = -1;
					}
			}
			else
			{
				lngFileSize = 0;
			}

			//Sending the file length back to client
			os = socketClient.getOutputStream();
			dosOutput = new DataOutputStream(os);
			dosOutput.writeLong(lngFileSize);
			dosOutput.flush();

			if (lngFileSize > 0)
			{
				try
				{
					//byte buffer to read and store the file data
					intTotalBytes = 0;
					intRead = 0;

					//buffer size from client so that Buffer size can be changed depending upon the client's requirement
					is = socketClient.getInputStream();
					disInput = new DataInputStream (is);
					intBufferSize = disInput.readInt();

					//code to read and transfer the file starts from here
					inpStr = new FileInputStream (strFileName);
					buffer = new byte [Math.min (intBufferSize, (int)(lngFileSize-intTotalBytes))];
					strStatus += "File Transfer initiated at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date())+"\n";
					while((intRead = inpStr.read(buffer)) > 0)
					{
						os = socketClient.getOutputStream();
						dosOutput = new DataOutputStream(os);
						dosOutput.write(buffer);
						dosOutput.flush();
						intTotalBytes += intRead;
						buffer = new byte [Math.min (intBufferSize, (int)(lngFileSize-intTotalBytes))];
					}

					strStatus += "File Transfer completed at " + new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date())+"\n";
					strStatus += "Total Bytes transferred = " + intTotalBytes + "\n";

					//sending the final transfer status back to client
					os = socketClient.getOutputStream();
					dosOutput = new DataOutputStream(os);
					dosOutput.writeUTF(strStatus);
					dosOutput.flush();
					System.out.println(strStatus);
				}

				catch(FileNotFoundException ex)
				{
					//Sending the exception details to Client
					String strErrorMessage = "Unable to find the requested file (" + strFileName + ") on Peer Server!!\n";
					os = socketClient.getOutputStream();
					dosOutput = new DataOutputStream(os);
					dosOutput.writeChars(strErrorMessage);
					dosOutput.flush();
					System.out.println(strErrorMessage);
				}

				catch(IOException ex)
				{
					//Sending the exception details to Client
					String strErrorMessage = "Error while reading the requested file (" + strFileName + ") from Peer Server!!\n";
					os = socketClient.getOutputStream();
					dosOutput = new DataOutputStream(os);
					dosOutput.writeChars(strErrorMessage);
					dosOutput.flush();
					System.out.println(strErrorMessage);
				}
			}
			else
			{
				//Sending the error details to Client
				String strErrorMessage = "Unable to find the requested file on Peer Server!!\nError while reading the requested file from Peer Server!!\n";
				os = socketClient.getOutputStream();
				dosOutput = new DataOutputStream(os);
				dosOutput.writeChars(strErrorMessage);
				dosOutput.flush();
				System.out.println(strErrorMessage);
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