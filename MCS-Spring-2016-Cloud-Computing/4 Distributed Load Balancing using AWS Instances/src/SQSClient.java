import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.Tables;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//class to store job details
class Job
{
	public String strJobID;
	public String strJob;
	public boolean blnIsProcessed=false;
	public String getJob()
	{
		return strJob;
	}
	public String getID()
	{
		return strJobID;
	}
}

//class to store result details
class JobResult
{
	public String strJobID;
	public String strResult;
	public boolean blnIsProcessed=false;
	public String getResult()
	{
		return strResult;
	}
	public String getID()
	{
		return strJobID;
	}
}

public class SQSClient
{
	//class level variables
	public static ArrayList<Job> arrlstJobs = new ArrayList<Job>();
	public static ArrayList<JobResult> arrlstJobResults = new ArrayList<JobResult>();
	public static int intNoOfThreads=0;
	public static String strQueueName="";
	public static String strResultQueueName="";
	public static String strFileName="";
	public static int intJobCnt=0;
	public static int intResultCnt=0;

	//function to get AWS credentials starts from here
	public static BasicAWSCredentials GetCredentials()
	{
		BasicAWSCredentials credentials=null;
		try
		{
			credentials = new BasicAWSCredentials ("XXXXXXXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		}

		catch (Exception e)
		{
			System.out.println("Exception in GetCredentials()\n" + e.getMessage());
		}
		return credentials;
	}
	//function to get AWS credentials ends over here

	//function to initialize SQS connection starts from here
	public static AmazonSQS initSQS (String strQueueName)
	{
		AmazonSQS retSQS=null;
		BasicAWSCredentials credentials = GetCredentials();
		if (credentials!=null)
		{
			try
			{
				retSQS = new AmazonSQSClient(credentials);
				Region usEast1 = Region.getRegion(Regions.US_EAST_1);
				retSQS.setRegion(usEast1);
			}
			catch (AmazonServiceException ase)
			{
				System.out.println("Exception in initSQS()\nRequest made Amazon SQS " + strQueueName + " was rejected with an error with following details.");
				System.out.println("Error: " + ase.getMessage());
				System.out.println("AWS Error Code: " + ase.getErrorCode());
				System.out.println("Error Type: " + ase.getErrorType());
				return null;
			}
			catch (AmazonClientException ace)
			{
				System.out.println("Exception in initSQS()\nSQS Client " + strQueueName + " is having issues");
				System.out.println("Error Message: " + ace.getMessage());
				return null;
			}
			return retSQS;
		}
		else
		{
			return null;
		}
	}
	//function to initialize SQS connection ends over here

	//function to Read the jobs from file and store them into the queue starts from here
	public static int ReadStoreJobs (String strFileName, String strQueueName)
	{
		AmazonSQS sqs = null;
		AmazonDynamoDBClient dynamoDBClient = null;
		//System.out.println("Queue Name is = " + strQueueName);
		SendMessageResult smrSQS = null;
		if ((strFileName.isEmpty()==false)&&(strFileName.equals("")==false))
		{
			try
			{
				File fJobs = new File (strFileName);
				if (fJobs.exists())
				{
					if (strQueueName.equals("LOCAL")==false)
					{
						BasicAWSCredentials credentials = new BasicAWSCredentials ("XXXXXXXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
						Region usEast1 = Region.getRegion(Regions.US_EAST_1);

						try
						{
							sqs = new AmazonSQSClient(credentials);
							sqs.setRegion(usEast1);
						}
						catch (AmazonServiceException ase)
						{
							System.out.println("Request made Amazon SQS was rejected with an error with following details.");
							System.out.println("Error: " + ase.getMessage());
							System.out.println("AWS Error Code: " + ase.getErrorCode());
							System.out.println("Error Type: " + ase.getErrorType());
							return -5;
						}
						catch (AmazonClientException ace)
						{
							System.out.println("SQS Client is having issues");
							System.out.println("Error Message: " + ace.getMessage());
							return -6;
						}

						try
						{
							dynamoDBClient = new AmazonDynamoDBClient(credentials);
							dynamoDBClient.setRegion(usEast1);

							// Create table if it does not exist yet
							if (Tables.doesTableExist(dynamoDBClient, strQueueName))
							{
								//System.out.println("Table " + strQueueName + " is already ACTIVE");
							}
							else
							{
								// Create a table with a primary hash key named 'name', which holds a string
								CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(strQueueName)
										.withKeySchema(new KeySchemaElement().withAttributeName("MessageID").withKeyType(KeyType.HASH))
										.withAttributeDefinitions(new AttributeDefinition().withAttributeName("MessageID").withAttributeType(ScalarAttributeType.S))
										.withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));
								TableDescription createdTableDescription = dynamoDBClient.createTable(createTableRequest).getTableDescription();
								//System.out.println("Created Table: " + createdTableDescription);

								// Wait for it to become active
								//System.out.println("Waiting for " + strQueueName + " to become ACTIVE...");
								Tables.awaitTableToBecomeActive(dynamoDBClient, strQueueName);
							}
						}
						catch (AmazonServiceException ase)
						{
							System.out.println("Request made DynamoDB was rejected with an error with following details.");
							System.out.println("Error: " + ase.getMessage());
							System.out.println("AWS Error Code: " + ase.getErrorCode());
							System.out.println("Error Type: " + ase.getErrorType());
							return -5;
						}
						catch (AmazonClientException ace)
						{
							System.out.println("DynamoDB Client is having issues");
							System.out.println("Error Message: " + ace.getMessage());
							return -6;
						}
					}
					// Construct BufferedReader from FileReader
					BufferedReader br = new BufferedReader(new FileReader(fJobs));
					int i = 0;
					String strJob=null;
					while ((strJob = br.readLine()) != null)
					{
						//System.out.println(i + ": " + strJob);
						if ((strJob!="") && (strJob.isEmpty()==false))
						{
							if (strQueueName.equals("LOCAL")==false)
							{
								try
								{
									smrSQS = sqs.sendMessage(new SendMessageRequest(strQueueName, strJob));
								}
								catch (AmazonServiceException ase)
								{
									System.out.println("Request made Amazon SQS was rejected with an error with following details.");
									System.out.println("Error: " + ase.getMessage());
									System.out.println("AWS Error Code: " + ase.getErrorCode());
									System.out.println("Error Type: " + ase.getErrorType());
									return -5;
								}
								catch (AmazonClientException ace)
								{
									System.out.println("SQS Client is having issues");
									System.out.println("Error Message: " + ace.getMessage());
									return -6;
								}

								try
								{
									Job curJob = new Job();
									curJob.strJobID = smrSQS.getMessageId();
									//System.out.println("Job="+strJob+"\t"+i);
									curJob.strJob = strJob;
									arrlstJobs.add(curJob);
									intJobCnt++;
									Map<String, AttributeValue> curItem = 
											newItem(smrSQS.getMessageId(), strJob, false, "");

									// Write the item to the table
									PutItemRequest putItemRequest = new PutItemRequest(strQueueName, curItem);
									PutItemResult putItemResult = dynamoDBClient.putItem(putItemRequest);
									//System.out.println("Result: " + putItemResult);
								}
								catch (AmazonServiceException ase)
								{
									System.out.println("Request made Amazon DynamoDB was rejected with an error with following details.");
									System.out.println("Error: " + ase.getMessage());
									System.out.println("AWS Error Code: " + ase.getErrorCode());
									System.out.println("Error Type: " + ase.getErrorType());
									return -5;
								}
								catch (AmazonClientException ace)
								{
									System.out.println("DynamoDB Client is having issues");
									System.out.println("Error Message: " + ace.getMessage());
									return -6;
								}
							}
							else
							{
								Job curJob = new Job();
								curJob.strJobID = String.valueOf(i);
								//System.out.println("Job="+strJob+"\t"+i);
								curJob.strJob = strJob;
								arrlstJobs.add(curJob);
								intJobCnt++;
							}
						}
						i++;
					}
					br.close();
					return 0;
				}
				else
				{
					System.out.println("Invalid File Name. Please check if file really exists\n");
					return(-3);
				}
			}
			catch (Exception e)
			{
				System.out.println("Exception in ReadStoreJobs()\n" + e.getMessage());
				return -2;
			}
		}
		else
		{
			System.out.println("Invalid File Name\n");
			return(-1);
		}
	}
	//function to Read the jobs from file and store them into the queue ends over here

	private static Map<String, AttributeValue> newItem(String strMessageID, String strJobCommand, boolean blnIsProcessed, String rating, String... fans)
	{
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put("MessageID", new AttributeValue(strMessageID));
		item.put("JobCommand", new AttributeValue(strJobCommand));
		item.put("IsProcessed", new AttributeValue().withBOOL(blnIsProcessed));
		item.put("JobResult", new AttributeValue().withNULL(true));

		return item;
	}

	//main function starts from here
	public static void main(String[] args)
	{
		long lngStartTime, lngQueueEndTime, lngEndTime;
		LocalWorkerExecution lwe=null;
		ResultPuller rp=null;
		JobPusher jp=null;
		//Command Line Argument Parsing code
		try
		{
			if (args.length>0)
			{
				if (args[0].equals("-s")==false)
				{
					System.out.println("Usage if LOCAL Queue: " + SQSClient.class.getName() + " -s <LOCAL> -t <No. of Threads> -w <Workload File>\nUsage if SQS: " + SQSClient.class.getName() + " -s <SQS Queue Name> -w <Workload File>\n");
					System.exit(-1);
				}
				else
				{
					if (args[1].equals("-t")==true)
					{
						System.out.println("Usage if LOCAL Queue: " + SQSClient.class.getName() + " -s <LOCAL> -t <No. of Threads> -w <Workload File>\nUsage if SQS: " + SQSClient.class.getName() + " -s <SQS Queue Name> -w <Workload File>\nPlease enter the Local or Remote Queue Name\n");
						System.exit(-1);
					}
					else
					{
						if (args.length>2)
						{
							strQueueName = args[1];
							strResultQueueName = args[1]+"_Results";
						}
						else
						{
							System.out.println("Usage if LOCAL Queue: " + SQSClient.class.getName() + " -s <LOCAL> -t <No. of Threads> -w <Workload File>\nUsage if SQS: " + SQSClient.class.getName() + " -s <SQS Queue Name> -w <Workload File>\nPlease enter the Local or Remote Queue Name\n");
							System.exit(-1);
						}
	
						if ((args[2].equals("-t")==false) && (strQueueName.equals("LOCAL")==true))
						{
							System.out.println("Usage if LOCAL Queue: " + SQSClient.class.getName() + " -s <LOCAL> -t <No. of Threads> -w <Workload File>\nUsage if SQS: " + SQSClient.class.getName() + " -s <SQS Queue Name> -w <Workload File>\nPlease enter No. of Threads for LOCAL Queue");
							System.exit(-1);
						}
						else if ((args[2].equals("-t")==true) && (strQueueName.equals("LOCAL")==true))
						{
							try
							{
								intNoOfThreads = Integer.parseInt(args[3]);
		
								if (args[4].equals("-w")==false)
								{
									System.out.println("Usage if LOCAL Queue: " + SQSClient.class.getName() + " -s <LOCAL> -t <No. of Threads> -w <Workload File>\nUsage if SQS: " + SQSClient.class.getName() + " -s <SQS Queue Name> -w <Workload File>\nPlease enter the Workload File Name\n");
									System.exit(-1);
								}
								else
								{
									if (args.length<6)
									{
										System.out.println("Usage if LOCAL Queue: " + SQSClient.class.getName() + " -s <LOCAL> -t <No. of Threads> -w <Workload File>\nUsage if SQS: " + SQSClient.class.getName() + " -s <SQS Queue Name> -w <Workload File>\nPlease enter the Workload File Name\n");
										System.exit(-1);
									}
									else
									{
										strFileName = args[5];
									}
								}
							}
							catch (Exception e)
							{
								System.out.println("Usage if LOCAL Queue: " + SQSClient.class.getName() + " -s <LOCAL> -t <No. of Threads> -w <Workload File>\nUsage if SQS: " + SQSClient.class.getName() + " -s <SQS Queue Name> -w <Workload File>\nPlease enter No. of Threads for LOCAL queue\n");
								System.exit(-1);
							}
						}
						else if ((args[2].equals("-w")==true) && (strQueueName.equals("LOCAL")==true))
						{
							System.out.println("Usage if LOCAL Queue: " + SQSClient.class.getName() + " -s <LOCAL> -t <No. of Threads> -w <Workload File>\nUsage if SQS: " + SQSClient.class.getName() + " -s <SQS Queue Name> -w <Workload File>\nPlease enter No. of Threads for LOCAL Queue");
							System.exit(-1);
						}
						else if ((args[2].equals("-w")==true) && (strQueueName.equals("LOCAL")==false))
						{
							if (args.length<4)
							{
								System.out.println("Usage if LOCAL Queue: " + SQSClient.class.getName() + " -s <LOCAL> -t <No. of Threads> -w <Workload File>\nUsage if SQS: " + SQSClient.class.getName() + " -s <SQS Queue Name> -w <Workload File>\nPlease enter the Workload File Name\n");
								System.exit(-1);
							}
							else
							{
								strFileName = args[3];
							}
						}
						else
						{
							System.out.println("Usage if LOCAL Queue: " + SQSClient.class.getName() + " -s <LOCAL> -t <No. of Threads> -w <Workload File>\nUsage if SQS: " + SQSClient.class.getName() + " -s <SQS Queue Name> -w <Workload File>\n");
							System.exit(-1);
						}
					}
				}
			}
			else
			{
				System.out.println("Usage if LOCAL Queue: " + SQSClient.class.getName() + " -s <LOCAL> -t <No. of Threads> -w <Workload File>\nUsage if SQS: " + SQSClient.class.getName() + " -s <SQS Queue Name> -w <Workload File>\n");
				System.exit(-1);
			}
		}
		catch (Exception e)
		{
			System.out.println("Usage if LOCAL Queue: " + SQSClient.class.getName() + " -s <LOCAL> -t <No. of Threads> -w <Workload File>\nUsage if SQS: " + SQSClient.class.getName() + " -s <SQS Queue Name> -w <Workload File>\n");
			System.exit(-1);
		}
		if ((strQueueName!="") && (strResultQueueName!="") && (strFileName!=""))
		{
			lngStartTime = System.currentTimeMillis();
			//jp = new JobPusher();
			//jp.start();
			ReadStoreJobs (strFileName, strQueueName);
			lngQueueEndTime = System.currentTimeMillis();
			if (strQueueName.equals("LOCAL")==false)
			{
				rp = new ResultPuller();
				rp.start();
			}
			if (strQueueName.equals("LOCAL")==true)
			{
				lwe = new LocalWorkerExecution();
				lwe.start();
			}
			while (true)
			{
				//System.out.println("Result size is " + arrlstJobResults.size() + "\t" + "Job size is " + arrlstJobs.size());
				System.out.println("Result size is " + intResultCnt + "\t" + "Job size is " + intJobCnt);
				try
				{
					Thread.sleep(1000);
				}
				catch (Exception e)
				{
					
				}
				if (intResultCnt>=intJobCnt)
				{
					if (strQueueName.equals("LOCAL")==false)
					{
						rp.stop();
					}
					if (strQueueName.equals("LOCAL")==true)
					{
						lwe.stop();
					}
					lngEndTime = System.currentTimeMillis();
					System.out.println("Total no. of Jobs = "+ intJobCnt);
					System.out.println("Total no. of Results = "+ intResultCnt);
					System.out.println("Total time taken to insert the tasks into Queue is "+ (lngQueueEndTime - lngStartTime) + " milliseconds");
					System.out.println("Total time taken to complete the task (including Queuing time) is "+ (lngEndTime - lngStartTime) + " milliseconds");
					System.exit(0);
				}
			}
		}
	}
	//main function ends over here

	//function to Add Result to local queue starts from here
	public void AddResult (JobResult jr)
	{
		arrlstJobResults.add(jr);
		intResultCnt++;
		//System.out.println("Result Queue size is " + arrlstJobResults.size());
	}
	//function to Add Result to local queue ends over here

	//function to get results from remote queue starts from here
	public static void GetResultsFromQueue (String strQueueName)
	{
		AmazonSQS sqs = initSQS (strQueueName);
		if (sqs!= null)
		{
			//System.out.println("Inside GetResultsFromQueue() 2");
			try
			{
				//System.out.println("Inside GetResultsFromQueue() 3");
				ReceiveMessageRequest rmr = new ReceiveMessageRequest(strQueueName);
				//System.out.println("Inside GetResultsFromQueue() 4");
				List<Message> messages = sqs.receiveMessage(rmr).getMessages();
				//System.out.println("Inside GetResultsFromQueue() 5");
				for (Message message : messages)
				{
					//System.out.println("Inside GetResultsFromQueue() 6");
					JobResult jobResultCurrent = new JobResult();
					//System.out.println("Inside GetResultsFromQueue() 7");
					jobResultCurrent.strJobID = message.getMessageId();
					//System.out.println("Inside GetResultsFromQueue() 8");
					jobResultCurrent.strResult = message.getBody();
					//System.out.println("Inside GetResultsFromQueue() 9");
					//System.out.println("GetResultsFromQueue\n" + message.getMessageId()+"\t"+message.getBody());
					arrlstJobResults.add(jobResultCurrent);
					intResultCnt++;
					//System.out.println("Inside GetResultsFromQueue() 10");
					String messageReceiptHandle = message.getReceiptHandle();
					//System.out.println("Inside GetResultsFromQueue() 11");
					sqs.deleteMessage(new DeleteMessageRequest()
							.withQueueUrl(strQueueName)
							.withReceiptHandle(messageReceiptHandle));
					//System.out.println("Inside GetResultsFromQueue() 12");
				}
			}
			catch (AmazonServiceException ase)
			{
				System.out.println("Exception in GetResultsFromQueue()\nRequest made Amazon Jobs SQS was rejected with an error with following details.");
				System.out.println("Error: " + ase.getMessage());
				System.out.println("AWS Error Code: " + ase.getErrorCode());
				System.out.println("Error Type: " + ase.getErrorType());
				sqs.shutdown();
				//return -5;
			}
			catch (AmazonClientException ace)
			{
				System.out.println("Exception in GetResultsFromQueue()\nJobs SQS Client is having issues");
				System.out.println("Error Message: " + ace.getMessage());
				sqs.shutdown();
				//return -6;
			}
			sqs.shutdown();
			//return 0;
		}
		else
		{
			System.out.println("Error in GetResultsFromQueue() while initializing queue");
		}
	}
	//function to get results from remote queue ends over here

}

//class for Local Worker execution starts from here
class WorkerThread implements Runnable
{
	//ArrayList <JobResult> arrlstJobResults = new ArrayList<JobResult>();
	private String strJob;
	private String strJobID;
	public WorkerThread (String job, String JobID)
	{
		this.strJob = job;
		this.strJobID = JobID;
	}

	public void run()
	{
		//System.out.println(Thread.currentThread().getName()+" (Start) Job = "+strJob);
		ProcessJob(strJob, strJobID);//call processmessage method that sleeps the thread for 2 seconds
		//System.out.println(Thread.currentThread().getName()+" (End)");//prints thread name
	}

	public void ProcessJob(String strCurrentJob, String strCurrentJobID)
	{
		SQSClient c = new SQSClient();
		Runtime r = Runtime.getRuntime();
		try
		{
			Process p = r.exec(strCurrentJob);
			//System.out.println("Current job is ="+strCurrentJob);
			/*BufferedReader stdInput = new BufferedReader(new 
					InputStreamReader(p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new 
					InputStreamReader(p.getErrorStream()));

			// read the output from the command
			System.out.println("Here is the standard output of the command:\n");
			String s = null;
			while ((s = stdInput.readLine()) != null)
			{
				System.out.println(s);
			}

			// read any errors from the attempted command
			System.out.println("Here is the standard error of the command (if any):\n");
			while ((s = stdError.readLine()) != null)
			{
				System.out.println(s);
			}*/

			int intExitVal = p.waitFor();
			//System.out.println(intExitVal);
			JobResult jrCurrentJobResult = new JobResult();
			jrCurrentJobResult.strJobID = strCurrentJobID;
			jrCurrentJobResult.strResult = String.valueOf(intExitVal);
			c.AddResult(jrCurrentJobResult);
		}
		catch (Exception e)
		{
			System.out.println("Exception in ProcessJob()\n" + e.getMessage());
			JobResult jrCurrentJobResult = new JobResult();
			jrCurrentJobResult.strJobID = strCurrentJobID;
			jrCurrentJobResult.strResult = e.getMessage();
			c.AddResult(jrCurrentJobResult);
		}
	}
}
//class for Local Worker execution ends over here

//class for Local Worker execution starts from here
class LocalWorkerExecution extends Thread
{
	@Override
	public void run()
	{
		try
		{
			if (SQSClient.strQueueName.equals("LOCAL")==true)
			{
				//System.out.println(arrlstJobs.size());
				ExecutorService executor = Executors.newFixedThreadPool(SQSClient.intNoOfThreads);//creating a pool of 5 threads  
				for (int i = 0; i < SQSClient.arrlstJobs.size(); i++)
				{
					Runnable worker = new WorkerThread(SQSClient.arrlstJobs.get(i).strJob, SQSClient.arrlstJobs.get(i).strJobID);
					executor.execute(worker);//calling execute method of ExecutorService
				}
				executor.shutdown();
				while (!executor.isTerminated())
				{
					
				}

				//System.out.println("Finished all threads");
			}
			}
		catch (Exception e)
		{
			System.out.println("Exception in LocalWorkerExecution Class\n" + e.getMessage());
		}
	}
}
//class for Local Worker execution starts from here

//class for pulling the results
class ResultPuller extends Thread
{
	@Override
	public void run()
	{
		//System.out.println("Inside ResultPuller Run() 1");
		if (SQSClient.strResultQueueName.equals("LOCAL")==false)
		{
			//System.out.println("Inside ResultPuller Run() 2");
			try
			{
				//System.out.println("Inside ResultPuller Run() 3");
				while (true)
				{
					//System.out.println("Inside ResultPuller Run() 4");
					//System.out.println("Before SQSClient.GetResultsFromQueue");
					SQSClient.GetResultsFromQueue(SQSClient.strResultQueueName);
					//System.out.println("After SQSClient.GetResultsFromQueue");
					//System.out.println("Inside ResultPuller Run() 5");
					//Thread.sleep(1000);
					//System.out.println("Inside ResultPuller Run() 6");
				}
			}
			catch (Exception e)
			{
				System.out.println("Exception in ResultPuller Class\n" + e.getMessage());
			}
		}
	}
}

class JobPusher extends Thread
{
	public void run()
	{
		try
		{
			SQSClient.ReadStoreJobs(SQSClient.strFileName, SQSClient.strQueueName);
			this.stop();
		}
		catch (Exception e)
		{
			System.out.println("Exception in JobPusher Class\n" + e.getMessage());
		}
	}
}