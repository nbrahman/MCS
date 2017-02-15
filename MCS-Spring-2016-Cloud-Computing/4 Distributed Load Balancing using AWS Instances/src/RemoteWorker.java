import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.Tables;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/*class Job
{
	public String strJobID;
	public String strJob;
	public String getJob()
	{
		return strJob;
	}
	public String getID()
	{
		return strJobID;
	}
}

class JobResult
{
	public String strJobID;
	public String strResult;
	public String getResult()
	{
		return strResult;
	}
	public String getID()
	{
		return strJobID;
	}
}*/

public class RemoteWorker
{
	private static ArrayList<Job> arrlstJobs = new ArrayList<Job>();
	private static ArrayList<JobResult> arrlstJobResults = new ArrayList<JobResult>();
	private static ExecutorService executor = null;
	private static String strDynamoDBTableName="";
	private static String strQueueName = "";
	private static String strResultQueueName = "";
	private static int intNoOfThreads=1;

	//private static AmazonSQS sqs = null;
	//private static AmazonSQS sqsResult = null;
	//private static AmazonDynamoDBClient dynamoDBClient = null;
	//private static BasicAWSCredentials credentials = null;
	//private static int intIdleCount=0;

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

	//function to initialize SQS connection
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
				//System.out.println("Exception in initSQS()\nRequest made Amazon SQS " + strQueueName + " was rejected with an error with following details.");
				System.out.println("initSQS() Error: " + ase.getMessage());
				//System.out.println("AWS Error Code: " + ase.getErrorCode());
				//System.out.println("Error Type: " + ase.getErrorType());
				return null;
			}
			catch (AmazonClientException ace)
			{
				//System.out.println("Exception in initSQS()\nSQS Client " + strQueueName + " is having issues");
				System.out.println("initSQS() Error Message: " + ace.getMessage());
				return null;
			}
			return retSQS;
		}
		else
		{
			return null;
		}
	}

	//function to initialize DynamoDB
	public static AmazonDynamoDBClient initDynamoDB ()
	{
		AmazonDynamoDBClient retDynamoDBClient=null;
		BasicAWSCredentials credentials = GetCredentials();
		if (credentials!=null)
		{
			try
			{
				retDynamoDBClient = new AmazonDynamoDBClient(credentials);
				Region usEast1 = Region.getRegion(Regions.US_EAST_1);
				retDynamoDBClient.setRegion(usEast1);
			}
			catch (AmazonServiceException ase)
			{
				//System.out.println("Exception in initDynamoDB()\nRequest made Amazon DynamoDB was rejected with an error with following details.");
				System.out.println("initDynamoDB() Error: " + ase.getMessage());
				//System.out.println("AWS Error Code: " + ase.getErrorCode());
				//System.out.println("Error Type: " + ase.getErrorType());
				return null;
			}
			catch (AmazonClientException ace)
			{
				//System.out.println("Exception in initDynamoDB()\nDynamoDB Client is having issues");
				System.out.println("initDynamoDB() Error Message: " + ace.getMessage());
				return null;
			}
			return retDynamoDBClient;
		}
		else
		{
			return null;
		}
	}

	//function to fetch jobs from queue
	public static void GetJobsFromQueue (String strQueueName)
	{
		/*System.out.println("GetJobsFromQueue(): 1");
		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				while (true)
				{
					if (arrlstJobs.size()<=0)
					{*/
						AmazonSQS sqs = initSQS (strQueueName);
						if (sqs!= null)
						{
							try
							{
								ReceiveMessageRequest rmr = new ReceiveMessageRequest(strQueueName);
								rmr.setMaxNumberOfMessages(GetActiveThreadCount());
								List<Message> messages = sqs.receiveMessage(rmr).getMessages();
								for (Message message : messages)
								{
									Job jobCurrent = new Job();
									jobCurrent.strJobID = message.getMessageId();
									jobCurrent.strJob = message.getBody();
									//System.out.println("GetJobsFromQueue\n" + message.getMessageId()+"\t"+message.getBody());
									arrlstJobs.add(jobCurrent);
									String messageReceiptHandle = message.getReceiptHandle();
									sqs.deleteMessage(new DeleteMessageRequest()
											.withQueueUrl(strQueueName)
											.withReceiptHandle(messageReceiptHandle));
								}
							}
							catch (AmazonServiceException ase)
							{
								//System.out.println("Exception in GetJobsFromQueue()\nRequest made Amazon Jobs SQS was rejected with an error with following details.");
								System.out.println("GetJobsFromQueue() Error: " + ase.getMessage());
								//System.out.println("AWS Error Code: " + ase.getErrorCode());
								//System.out.println("Error Type: " + ase.getErrorType());
								sqs.shutdown();
								//return -5;
							}
							catch (AmazonClientException ace)
							{
								//System.out.println("Exception in GetJobsFromQueue()\nJobs SQS Client is having issues");
								System.out.println("GetJobsFromQueue() Error Message: " + ace.getMessage());
								sqs.shutdown();
								//return -6;
							}
							sqs.shutdown();
							//return 0;
						}
						else
						{
							System.out.println("Error in GetJobsFromQueue() while initializing queue");
						}
						/*long timeToSleep = 1000;
						boolean interrupted=false;
	
						//while(timeToSleep > 0)
						//{
						try
						{
							System.out.println("GetJobsFromQueue(): 2");
							Thread.sleep(timeToSleep);
							System.out.println("GetJobsFromQueue(): 3");
							//break;
						}
						catch(InterruptedException e)
						{
							interrupted=true;
						}
						//}
	
						if(interrupted)
						{
							//restore interruption before exit
							Thread.currentThread().interrupt();
						}
						System.out.println("GetJobsFromQueue(): 4");
					}
				}
			}
		});
		System.out.println("GetJobsFromQueue(): 5");
		t.start();
		System.out.println("GetJobsFromQueue(): 6");*/
	}

	//function to update job processing status
	public static int UpdateDynamoDBJobProcessingStatus (String strJobID)
	{
		AmazonDynamoDBClient dynamoDBClient = initDynamoDB();
		if (dynamoDBClient!=null)
		{
			try
			{
				// Write the item to the table
				UpdateItemRequest updateItemRequest = new UpdateItemRequest()
						.withTableName(strDynamoDBTableName)
						.withReturnValues(ReturnValue.ALL_NEW)
						.addKeyEntry("MessageID", new AttributeValue(strJobID))
						.addExpectedEntry("IsProcessed", new ExpectedAttributeValue()
								.withComparisonOperator(ComparisonOperator.EQ)
								.withValue(new AttributeValue().withBOOL(false)))
						.addAttributeUpdatesEntry("IsProcessed", new AttributeValueUpdate()
								.withValue(new AttributeValue().withBOOL(true))
								.withAction(AttributeAction.PUT));
				
				UpdateItemResult updateItemResult = dynamoDBClient.updateItem(updateItemRequest);
				//System.out.println("UpdateDynamoDBJobProcessingStatus()\nResult: " + updateItemResult);
			}
			catch (AmazonServiceException ase)
			{
				//System.out.println("Exception in UpdateDynamoDBJobProcessingStatus()\nRequest made Amazon DynamoDB was rejected with an error with following details.");
				System.out.println("UpdateDynamoDBJobProcessingStatus() Error: " + ase.getMessage());
				//System.out.println("AWS Error Code: " + ase.getErrorCode());
				//System.out.println("Error Type: " + ase.getErrorType());
				dynamoDBClient.shutdown();
				return -5;
			}
			catch (AmazonClientException ace)
			{
				//System.out.println("Exception in UpdateDynamoDBJobProcessingStatus()\nDynamoDB Client is having issues");
				System.out.println("UpdateDynamoDBJobProcessingStatus() Error Message: " + ace.getMessage());
				dynamoDBClient.shutdown();
				return -6;
			}

			dynamoDBClient.shutdown();
			return 0;
		}
		else
		{
			return -1;
		}
	}

	//function to get duplicate job count
	public static int GetDuplicateJobCount (String strJobID)
	{
		AmazonDynamoDBClient dynamoDBClient = initDynamoDB();
		if (dynamoDBClient!=null)
		{
			//System.out.println("GetDuplicateJobCount ()\nstrJobID = " + strJobID);
			HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
			Condition condition = new Condition()
					.withComparisonOperator(ComparisonOperator.EQ.toString())
					.withAttributeValueList(new AttributeValue().withS(strJobID));
			scanFilter.put("MessageID", condition);
			condition = new Condition()
					.withComparisonOperator(ComparisonOperator.EQ.toString())
					.withAttributeValueList(new AttributeValue().withBOOL(false));
			scanFilter.put("IsProcessed", condition);
			ScanRequest scanRequest = new ScanRequest(strDynamoDBTableName).withScanFilter(scanFilter);
			ScanResult scanResult = dynamoDBClient.scan(scanRequest);
			//System.out.println("GetDuplicateJobCount()\nResult: " + scanResult);
			int intRetValue = scanResult.getCount();
			//System.out.println("GetDuplicateJobCount()\nResult Count: " + intRetValue);
			dynamoDBClient.shutdown();
			return intRetValue;
		}
		else
		{
			return 1;
		}
	}

	//function to sotre the results to remote queue
	public static int PutResultsToQueue (String strResultQueueName)
	{
		SendMessageResult smrSQS=null;
		AmazonSQS sqsResult = initSQS (strResultQueueName);
		AmazonDynamoDBClient dynamoDBClient = initDynamoDB();
		if (sqsResult!= null)
		{
			for (; arrlstJobResults.size()>0;)
			{
				//if (arrlstJobResults.get(i).blnIsProcessed==false)
				//{
					JobResult jrCurrent = arrlstJobResults.get(0);
					String strJobResult = jrCurrent.strJobID + "\t" + jrCurrent.strResult;
					try
					{
						smrSQS = sqsResult.sendMessage(new SendMessageRequest(strResultQueueName, strJobResult));
						//System.out.println("Result pushed to Queue");
						arrlstJobResults.get(0).blnIsProcessed=true;
					}
					catch (AmazonServiceException ase)
					{
						//System.out.println("Exception in PutResultsToQueue()\nRequest made Amazon Result SQS was rejected with an error with following details.");
						System.out.println("PutResultsToQueue() Error: " + ase.getMessage());
						//System.out.println("AWS Error Code: " + ase.getErrorCode());
						//System.out.println("Error Type: " + ase.getErrorType());
						arrlstJobResults.get(0).blnIsProcessed=false;
						return -5;
					}
					catch (AmazonClientException ace)
					{
						//System.out.println("Exception in PutResultsToQueue()\nResult SQS Client is having issues");
						System.out.println("PutResultsToQueue() Error Message: " + ace.getMessage());
						arrlstJobResults.get(0).blnIsProcessed=false;
						return -6;
					}
				//}

				/*if (dynamoDBClient!=null)
				{
					try
					{
						// Write the item to the table
						UpdateItemRequest updateItemRequest = new UpdateItemRequest()
								.withTableName(strDynamoDBTableName)
								.withReturnValues(ReturnValue.ALL_NEW)
								.addKeyEntry("MessageID", new AttributeValue(jrCurrent.strJobID))
								.addExpectedEntry("JobResult", new ExpectedAttributeValue()
										.withComparisonOperator(ComparisonOperator.EQ)
										.withValue(new AttributeValue().withNULL(null)))
								.addAttributeUpdatesEntry("JobResult", new AttributeValueUpdate()
										.withValue(new AttributeValue().withS(jrCurrent.strResult))
										.withAction(AttributeAction.PUT));
						
						UpdateItemResult updateItemResult = dynamoDBClient.updateItem(updateItemRequest);
						System.out.println("Job Result Update Result: " + updateItemResult);
					}
					catch (AmazonServiceException ase)
					{
						System.out.println("Exception in PutResultsToQueue()\nRequest made Amazon DynamoDB was rejected with an error with following details.");
						System.out.println("Error: " + ase.getMessage());
						System.out.println("AWS Error Code: " + ase.getErrorCode());
						System.out.println("Error Type: " + ase.getErrorType());
						//dynamoDBClient.shutdown();
						return -5;
					}
					catch (AmazonClientException ace)
					{
						System.out.println("Exception in PutResultsToQueue()\nDynamoDB Client is having issues");
						System.out.println("Error Message: " + ace.getMessage());
						//dynamoDBClient.shutdown();
						return -6;
					}

					//dynamoDBClient.shutdown();
					//return 0;
				}
				else
				{
					return -1;
				}*/
				if (arrlstJobResults.get(0).blnIsProcessed==true)
				{
					arrlstJobResults.remove(0);
				}
			}
			dynamoDBClient.shutdown();
			sqsResult.shutdown();
			return 0;
		}
		else
		{
			return -2;
		}
	}

	public static int GetActiveThreadCount()
	{
		return intNoOfThreads;
	}

	//main function
	public static void main(String[] args)
	{
		//Command Line Argument Parsing code
		try
		{
			if (args.length>0)
			{
				if (args[0].equals("-s")==false)
				{
					System.out.println("Usage: " + RemoteWorker.class.getName() + " -s <SQS Queue Name> -t <No. of Threads>");
					System.exit(-1);
				}
				else
				{
					if (args[1].equals("-t")==true)
					{
						System.out.println("Usage: " + RemoteWorker.class.getName() + " -s <SQS Queue Name> -t <No. of Threads>\nPlease enter the Remote Queue Name\n");
						System.exit(-1);
					}
					else
					{
						strQueueName = args[1];
						strDynamoDBTableName = args[1];
						strResultQueueName = args[1]+"_Results";
		
						if ((args[2].equals("-t")==true) && (strQueueName.equals("")==false))
						{
							try
							{
								intNoOfThreads = Integer.parseInt(args[3]);
							}
							catch (Exception e)
							{
								System.out.println("Usage: " + RemoteWorker.class.getName() + " -s <SQS Queue Name> -t <No. of Threads>\nPlease enter No. of Threads\n");
								System.exit(-1);
							}
						}
						else
						{
							System.out.println("Usage: " + RemoteWorker.class.getName() + " -s <SQS Queue Name> -t <No. of Threads>\n");
							System.exit(-1);
						}
					}
				}
			}
			else
			{
				System.out.println("Usage: " + RemoteWorker.class.getName() + " -s <SQS Queue Name> -t <No. of Threads>\n");
				System.exit(-1);
			}
		}
		catch (Exception e)
		{
			System.out.println("Usage: " + RemoteWorker.class.getName() + " -s <SQS Queue Name> -t <No. of Threads>\n");
			System.exit(-1);
		}
		if ((strQueueName!="") && (strResultQueueName!="") && (intNoOfThreads!=0))
		{
			JobPuller jp = new JobPuller();
			jp.start();
	
			JobExecuter jw = new JobExecuter();
			jw.start();
	
			ResultPusher rp = new ResultPusher();
			rp.start();
			//CreateExecuteThreadPool(intNoOfThreads);
			/*try
			{
				while (true)
				{*/
					//GetJobsThreading(strQueueName);
					//GetJobsFromQueue(strQueueName);
					//if (strQueueName != "LOCAL")
					//{
						//ExecJobsThreading (intNoOfThreads);
						//CreateExecuteThreadPool(intNoOfThreads);
					//}
					//System.out.println("Main()\n"+PutResultsToQueue(strResultQueueName));
					//ReadStoreJobs (strQueueName);
					//System.out.println("Main()\nFinished all threads");
					//Thread.sleep(1000);
				/*}
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
			}*/
		}
	}

	//function to create thread pool and handle job executions
	public static void CreateExecuteThreadPool(int intNoOfThreads)
	{
		try
		{
			//while (true)
			{
				//System.out.println("CreateExecuteThreadPool()\n");
				//System.out.println("CreateExecuteThreadPool()\n"+arrlstJobs.size());
				//if (executor==null)
				{
					executor = Executors.newFixedThreadPool(intNoOfThreads);
				}
				//System.out.println("1\n");
				for (int i = 0; i < arrlstJobs.size(); i++)
				{
					if (arrlstJobs.get(i).blnIsProcessed==false)
					{
						//System.out.println("2\n");
						Runnable worker = new RemoteWorkerThread(arrlstJobs.get(i).strJob, arrlstJobs.get(i).strJobID);
						//System.out.println("3\n");
						executor.execute(worker);//calling execute method of ExecutorService
						//System.out.println("4\n");
						arrlstJobs.get(i).blnIsProcessed=true;
						//arrlstJobs.remove(0);
					}
				}
				//System.out.println("5\n");
				executor.shutdown();
				//System.out.println("6\n");
				while (!executor.isTerminated())
				{
					
				}
		
				//System.out.println("CreateExecuteThreadPool()\nFinished all threads");
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception in CreateExecuteThreadPool\n" + e.getMessage()+"\n"+e.getStackTrace());
			System.exit(-5);
		}
	}

	public static String GetJobQueueName()
	{
		return strQueueName;
	}

	public static String GetResultQueueName()
	{
		return strResultQueueName;
	}

	public static int GetNoOfThreads()
	{
		return intNoOfThreads;
	}

	public static int GetJobQueueSize()
	{
		int intNotProcessedCnt=0;
		for (int i=0;i<arrlstJobs.size();i++)
		{
			if (arrlstJobs.get(i).blnIsProcessed==false)
			{
				intNotProcessedCnt++;
			}
		}
		return intNotProcessedCnt;
	}

	public static int GetResultQueueSize()
	{
		int intNotProcessedCnt=0;
		for (int i=0;i<arrlstJobResults.size();i++)
		{
			if (arrlstJobResults.get(i).blnIsProcessed==false)
			{
				intNotProcessedCnt++;
			}
		}
		return intNotProcessedCnt;
	}

	public void AddResult (JobResult jr)
	{
		arrlstJobResults.add(jr);
		//System.out.println("AddResult()\nResult Queue size is " + arrlstJobResults.size());
	}
}

class RemoteWorkerThread implements Runnable
{
	//ArrayList <JobResult> arrlstJobResults = new ArrayList<JobResult>();
	private String strJob;
	private String strJobID;
	public RemoteWorkerThread (String job, String JobID)
	{
		this.strJob = job;
		this.strJobID = JobID;
	}

	public void run()
	{
		//System.out.println("run()\n"+Thread.currentThread().getName()+" (Start) Job = "+strJob);
		ProcessJob(strJob, strJobID);//call processmessage method that sleeps the thread for 2 seconds
		//System.out.println("run()\n"+Thread.currentThread().getName()+" (End)");//prints thread name
	}

	public void ProcessJob(String strCurrentJob, String strCurrentJobID)
	{
		RemoteWorker c = new RemoteWorker();
		int intDuplicateCnt=RemoteWorker.GetDuplicateJobCount(strCurrentJobID);
		//System.out.println("ProcessJob()\nintDuplicateCnt="+intDuplicateCnt);
		if (intDuplicateCnt>0)
		{
			Runtime r = Runtime.getRuntime();
			try
			{
				Process p = r.exec(strCurrentJob);
				int intExitVal = p.waitFor();
				RemoteWorker.UpdateDynamoDBJobProcessingStatus(strCurrentJobID);
				//System.out.println("ProcessJob()\nUpdate Status=" + RemoteWorker.UpdateDynamoDBJobProcessingStatus(strCurrentJobID));
				//System.out.println("ProcessJob()\nintExitVal"+intExitVal);
				JobResult jrCurrentJobResult = new JobResult();
				jrCurrentJobResult.strJobID = strCurrentJobID;
				jrCurrentJobResult.strResult = String.valueOf(intExitVal);
				c.AddResult(jrCurrentJobResult);
			}
			catch (Exception e)
			{
				System.out.println("Exception in ProcessJob()\n"+e.getMessage());
				JobResult jrCurrentJobResult = new JobResult();
				jrCurrentJobResult.strJobID = strCurrentJobID;
				jrCurrentJobResult.strResult = e.getMessage();
				c.AddResult(jrCurrentJobResult);
			}
		}
	}
}

class JobPuller extends Thread
{
	@Override
	public void run()
	{
		try
		{
			while (true)
			{
				if (RemoteWorker.GetJobQueueSize()<=0)
				{
					//System.out.println("Before RemoteWorker.GetJobsFromQueue");
					RemoteWorker.GetJobsFromQueue(RemoteWorker.GetJobQueueName());
					//System.out.println("After RemoteWorker.GetJobsFromQueue");
				}
				Thread.sleep(1000);
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception in JobPuller Class\n" + e.getMessage());
		}
	}

}

class JobExecuter extends Thread
{
	@Override
	public void run()
	{
		try
		{
			while (true)
			{
				if (RemoteWorker.GetJobQueueSize()>0)
				{
					//System.out.println("Before RemoteWorker.CreateExecuteThreadPool");
					RemoteWorker.CreateExecuteThreadPool(RemoteWorker.GetNoOfThreads());
					//System.out.println("After RemoteWorker.CreateExecuteThreadPool");
				}
				Thread.sleep(1000);
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception in Jobexecuter Class\n" + e.getMessage());
		}
	}

}

class ResultPusher extends Thread
{
	@Override
	public void run()
	{
		try
		{
			//System.out.println("Inside ResultPusher 1");
			while (true)
			{
				//System.out.println("Inside ResultPusher 2");
				if (RemoteWorker.GetResultQueueSize()>0)
				{
					//System.out.println("Before RemoteWorker.PutResultsToQueue");
					RemoteWorker.PutResultsToQueue(RemoteWorker.GetResultQueueName());
					//System.out.println("After RemoteWorker.PutResultsToQueue");
				}
				Thread.sleep(1000);
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception in ResultPusher Class\n" + e.getMessage());
		}
	}

}