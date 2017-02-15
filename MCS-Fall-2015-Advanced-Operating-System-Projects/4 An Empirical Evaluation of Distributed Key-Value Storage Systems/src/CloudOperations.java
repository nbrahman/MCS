import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import javax.security.auth.login.Configuration;

//MongoDB import starts from here
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
//MongoDB import ends over here

//Redis import starts from here
import redis.clients.jedis.Jedis;
//Redis import ends over here

//Couch import starts from here
import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;
import com.fourspaces.couchdb.Session;
//Couch import ends over here

/*
//Hbase import starts from here
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import java.lang.Object;
//Hbase import ends over here
*/
public class CloudOperations implements Runnable
{
	//variable declaration
	//private static int intNoOfHashBuckets = 5000;
	//private static int intBufferSize = 1024;
	private Socket socketClient;
	private DataInputStream disInput;
	private String strKey = "";
	private String strValue = "";
	private String choice;
	private DataOutputStream dosOutput;
	private String strServerLogFileName;
	private String strMessage = "";
	private static ConcurrentHashMap<Integer, ArrayList<String>> concurrentHashMap = new ConcurrentHashMap<Integer, ArrayList<String>> (); //(intNoOfHashBuckets,0.9f,200);
	private static long lngNoOfElements = 0;
	FileOutputStream outputStream;
	private static DBCollection collMongoDB;
	private static Jedis jdRedis;
	private static Database dbCouch;

	//Constructor
	public CloudOperations (Socket socket, String strServerLogFileName)
	{
		try
		{
			this.socketClient = socket;
			this.strServerLogFileName = strServerLogFileName;
			dosOutput = new DataOutputStream (this.socketClient.getOutputStream());
			disInput = new DataInputStream (this.socketClient.getInputStream());
			outputStream = new FileOutputStream(this.strServerLogFileName, true);
			strMessage = strMessage + "Distributed Hash Table server accepted connection from peer " + this.socketClient + "\n";
			//outputStream.write(strMessage.getBytes());
			//outputStream.flush();
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
		//int intBucketNo;
		try
		{
			//getting the Hash Code (Key) value for the File Name parameter
			int intHashKey = Math.abs(strKey.hashCode());
			//intBucketNo = intHashKey % intNoOfHashBuckets;

			//Defining the Array List for using as Value in Concurrent Hash Map
			ArrayList <String> arrLstValue = new ArrayList <String>();

			//Adding the Value Data to Array List
			arrLstValue.add(strKey + "\t" + strValue);

			//Adding the Key Value pair into Concurrent Hash Map if Key is absent
			//ArrayList <String> arrLstResultKey = concurrentHashMap.putIfAbsent(intBucketNo, arrLstValue);
			ArrayList <String> arrLstResultKey = concurrentHashMap.putIfAbsent(intHashKey, arrLstValue);

			//if Key is already available into Concurrent Hash Map
			if (arrLstResultKey != null)
			{
				//Add the newly provided data to Concurrent Hash Map in case of no duplication
				arrLstResultKey.add (arrLstValue.get(0));
				concurrentHashMap.put(intHashKey, arrLstResultKey);
			}

			//returning the status to calling function
			lngNoOfElements = lngNoOfElements + 1;
			strMessage = "Key " + '"' + strKey + '"' + " inserted on DHT Server " + socketClient.getLocalAddress().getHostAddress() + " successfully in bucket " + intHashKey + "\nTotal elements in Bucket are " + concurrentHashMap.get(intHashKey).size() + "\n";
			//outputStream.write(strMessage.getBytes());
			//outputStream.flush();
			boolStatus = true;
			return boolStatus;
		}

		catch (Exception e)
		{
			try
			{
				strMessage = "Error while inserting Key " + '"' + strKey + '"' + " DHT Server " + socketClient.getLocalAddress().getHostAddress() + " successfully\n";
				strMessage = strMessage + e.getMessage() + "\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
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
		//int intBucketNo;

		//Array List for the final Search Result
		String strResult = "";

		//Array List for the final Value data
		ArrayList<String> arrLstResultKey = new ArrayList<String>();

		try
		{
			//getting the Hash Code (Key) value for the File Name parameter
			int intHashKey = Math.abs(strKey.hashCode());
			//intBucketNo = intHashKey % intNoOfHashBuckets;

			//getting the Value data based on the Key from Concurrent Hash Map
			arrLstResultKey = concurrentHashMap.get(intHashKey);

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
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
				return strResult;
			}
			else
			{
				//If no result is found
				strResult = "0";
				strMessage = "Search for Key " + '"' + strKey + '"' + " on DHT Server " + socketClient.getLocalAddress().getHostAddress() + " returned no rows\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
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
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
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
		//int intBucketNo;

		try
		{
			//getting the Hash Code (Key) value for the File Name parameter
			int intHashKey = Math.abs(strKey.hashCode());
			//intBucketNo = intHashKey % intNoOfHashBuckets;

			//Adding the Key Value pair into Concurrent Hash Map if Key is absent
			ArrayList <String> arrLstResultKey = concurrentHashMap.get(intHashKey);

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
					concurrentHashMap.put(intHashKey, arrLstResultKey);
				}
				else
				{
					//remove the complete entry from Concurrent Hash Map
					concurrentHashMap.remove(intHashKey);
				}
			}
			else
			{
				//remove the complete entry from Concurrent Hash Map
				concurrentHashMap.remove(intHashKey);
			}

			//returning the status to calling function
			if (boolEntryFound == true)
			{
				strMessage = "Key " + '"' + strKey + '"' + " deleted from DHT Server " + socketClient.getLocalAddress().getHostAddress() + " successfully from bucket " + intHashKey + "\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
				boolStatus = true;
				lngNoOfElements = lngNoOfElements - 1;
				return boolStatus;
			}
			else
			{
				strMessage = "No records found for Key " + '"' + strKey + '"' + " on DHT Server " + socketClient.getLocalAddress().getHostAddress() + " while deleting\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
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
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
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

	//Function to put the Key - Value pair data into MongoDB starts from here
	public boolean putEntryMongoDB (String strKey, String strValue)
	{
		boolean boolStatus = false;
		WriteResult wrPutResult;
		try
		{
			//Inserting values into MongoDBCollection
			BasicDBObject dbObj = new BasicDBObject();
			dbObj.append("Key", strKey);
			dbObj.append("Value", strValue);
			wrPutResult = collMongoDB.insert(dbObj);
			System.out.println(wrPutResult.getN());
			if (wrPutResult.getN()>0)
			{
				strMessage = "Key " + '"' + strKey + '"' + " inserted on MongoDB " + socketClient.getLocalAddress().getHostAddress() + " successfully\n";
				boolStatus = true;
			}
			else
			{
				strMessage = "Error while inserting Key " + '"' + strKey + '"' + " in MongoDB " + socketClient.getLocalAddress().getHostAddress() + "\n";
				boolStatus = false;
			}
			System.out.println(strMessage);
			return boolStatus;
		}

		catch (Exception e)
		{
			try
			{
				strMessage = "Error while inserting Key " + '"' + strKey + '"' + " in MongoDB " + socketClient.getLocalAddress().getHostAddress() + "\n";
				strMessage = strMessage + e.getMessage() + "\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
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
	//Function to put the Key - Value pair data into MongoDB ends over here

	//Function to get the Key - Value pair data from MongoDB starts from here
	public String getEntryMongoDB (String strKey)
	{
		String strResult="";

		try
		{
			BasicDBObject docGet = new BasicDBObject();
			docGet.put ("Key", strKey);

			//Getting the Values for the key
			DBCursor dbcGet = collMongoDB.find(docGet);

			if (dbcGet.hasNext())
			{
				strResult = dbcGet.next().toString();
				strMessage = "Search for Key " + '"' + strKey + '"' + " in MongoDB " + socketClient.getLocalAddress().getHostAddress() + " successfully returned 1 row(s)\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
				return strResult;
			}
			else
			{
				//If no result is found
				strResult = "0";
				strMessage = "Search for Key " + '"' + strKey + '"' + " in MongoDB " + socketClient.getLocalAddress().getHostAddress() + " returned no rows\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
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
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
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

	//Function to delete the Key - Value pair data from MongoDB starts from here
	public boolean deleteEntryMongoDB (String strKey)
	{
		boolean boolStatus = false;
		WriteResult wrRemove;

		try
		{
			//Initializing DBObject
			BasicDBObject docRemove = new BasicDBObject();
			docRemove.put ("Key", strKey);

			//Getting the Values for the key
			wrRemove = collMongoDB.remove(docRemove);

			//returning the status to calling function
			if (wrRemove.getN() > 0)
			{
				strMessage = "Key " + '"' + strKey + '"' + " deleted from MongoDB " + socketClient.getLocalAddress().getHostAddress() + " successfully\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
				boolStatus = true;
				lngNoOfElements = lngNoOfElements - 1;
				return boolStatus;
			}
			else
			{
				strMessage = "No records found for Key " + '"' + strKey + '"' + " on MongoDB " + socketClient.getLocalAddress().getHostAddress() + " while deleting\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
				boolStatus = false;
				return boolStatus;
			}
		}

		catch (Exception e)
		{
			try
			{
				boolStatus = false;
				strMessage = "Error while deleting Key " + '"' + strKey + '"' + " from MongoDB " + socketClient.getLocalAddress().getHostAddress() + "\n";
				strMessage = strMessage + e.getMessage() + "\n";
				strMessage = strMessage + e.getStackTrace().toString() + "\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
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
	//Function to delete the Key - Value pair data from MongoDB ends over here

	//Function to put the Key - Value pair data into Redis starts from here
	public boolean putEntryRedisDB (String strKey, String strValue)
	{
		String strPutResult="";
		boolean boolStatus = false;
		try
		{
			//Inserting values into Redis String
			strPutResult = jdRedis.set (strKey, strValue);
			System.out.println(strPutResult);

			if (strPutResult != "")
			{
				strMessage = "Key " + '"' + strKey + '"' + " inserted on Redis " + socketClient.getLocalAddress().getHostAddress() + " successfully\n";
				boolStatus = true;
			}
			else
			{
				strMessage = "Error while inserting Key " + '"' + strKey + '"' + " in Redis " + socketClient.getLocalAddress().getHostAddress() + "\n";
				boolStatus = false;
			}
			return boolStatus;
		}

		catch (Exception e)
		{
			try
			{
				strMessage = "Error while inserting Key " + '"' + strKey + '"' + " in Redis " + socketClient.getLocalAddress().getHostAddress() + "\n";
				strMessage = strMessage + e.getMessage() + "\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
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
	//Function to put the Key - Value pair data into Redis ends over here

	//Function to get the Key - Value pair data from Redis starts from here
	public String getEntryRedisDB (String strKey)
	{
		String strResult="";

		try
		{
			//Getting the Values for the key
			strResult = jdRedis.get (strKey);

			if (strResult != "")
			{
				strMessage = "Search for Key " + '"' + strKey + '"' + " in Redis " + socketClient.getLocalAddress().getHostAddress() + " successfully returned 1 row(s)\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
				return strResult;
			}
			else
			{
				//If no result is found
				strResult = "0";
				strMessage = "Search for Key " + '"' + strKey + '"' + " in Redis " + socketClient.getLocalAddress().getHostAddress() + " returned no rows\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
				return strResult;
			}
		}

		catch (Exception e)
		{
			strResult = "-1";
			try
			{
				strMessage = "Error while searching Key " + '"' + strKey + '"' + " on Redis " + socketClient.getLocalAddress().getHostAddress() + "\n";
				strMessage = strMessage + e.getMessage();
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
				return strResult;
			}
			catch (Exception ex)
			{
				System.out.println(ex.getMessage()+"\n");
				return strResult;
			}
		}
	}
	//Function to get the Key - Value pair data from Redis starts from here

	//Function to delete the Key - Value pair data from Redis starts from here
	public boolean deleteEntryRedisDB (String strKey)
	{
		boolean boolStatus = false;
		Long lngRemoveResult;

		try
		{
			//Getting the Values for the key
			lngRemoveResult = jdRedis.del(strKey);
			System.out.println(lngRemoveResult);

			//returning the status to calling function
			if (lngRemoveResult > 0)
			{
				strMessage = "Key " + '"' + strKey + '"' + " deleted from Redis " + socketClient.getLocalAddress().getHostAddress() + " successfully\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
				boolStatus = true;
				return boolStatus;
			}
			else
			{
				strMessage = "No records found for Key " + '"' + strKey + '"' + " on Redis " + socketClient.getLocalAddress().getHostAddress() + " while deleting\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
				boolStatus = false;
				return boolStatus;
			}
		}

		catch (Exception e)
		{
			try
			{
				boolStatus = false;
				strMessage = "Error while deleting Key " + '"' + strKey + '"' + " from Redis " + socketClient.getLocalAddress().getHostAddress() + "\n";
				strMessage = strMessage + e.getMessage() + "\n";
				strMessage = strMessage + e.getStackTrace().toString() + "\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
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
	//Function to delete the Key - Value pair data from Redis ends over here

	//Function to put the Key - Value pair data into CouchDB starts from here
	public boolean putEntryCouchDB (String strKey, String strValue)
	{
		boolean boolStatus = false;
		try
		{
			//Inserting values into CouchDB Document
			Document docCouch = new Document();
			docCouch.setId(strKey);
			docCouch.put("Key", strKey);
			docCouch.put("Value", strValue);
			try
			{
				dbCouch.saveDocument(docCouch);
			}
			catch (Exception e)
			{
				System.out.println(e.getStackTrace()+"\n"+e.getCause()+"\n"+e.getMessage());
				boolStatus = false;
				return boolStatus;
			}

			strMessage = "Key " + '"' + strKey + '"' + " inserted on CouchDB " + socketClient.getLocalAddress().getHostAddress() + " successfully\n";
			boolStatus = true;
			return boolStatus;
		}

		catch (Exception e)
		{
			try
			{
				strMessage = "Error while inserting Key " + '"' + strKey + '"' + " in CouchDB " + socketClient.getLocalAddress().getHostAddress() + "\n";
				strMessage = strMessage + e.getMessage() + "\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
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
	//Function to put the Key - Value pair data into CouchDB ends over here

	//Function to get the Key - Value pair data from CouchDB starts from here
	public String getEntryCouchDB (String strKey)
	{
		String strResult="";

		try
		{
			//Getting the Values for the key
			Document docGet = dbCouch.getDocument(strKey);
			if (docGet != null)
			{
				strResult = docGet.toString();
			}

			if (strResult != "")
			{
				strMessage = "Search for Key " + '"' + strKey + '"' + " in CouchDB " + socketClient.getLocalAddress().getHostAddress() + " successfully returned 1 row(s)\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
				return strResult;
			}
			else
			{
				//If no result is found
				strResult = "0";
				strMessage = "Search for Key " + '"' + strKey + '"' + " in CouchDB " + socketClient.getLocalAddress().getHostAddress() + " returned no rows\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
				return strResult;
			}
		}

		catch (Exception e)
		{
			strResult = "-1";
			try
			{
				strMessage = "Error while searching Key " + '"' + strKey + '"' + " on CouchDB " + socketClient.getLocalAddress().getHostAddress() + "\n";
				strMessage = strMessage + e.getMessage();
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
				return strResult;
			}
			catch (Exception ex)
			{
				System.out.println(ex.getMessage()+"\n");
				return strResult;
			}
		}
	}
	//Function to get the Key - Value pair data from CouchDB starts from here

	//Function to delete the Key - Value pair data from CouchDB starts from here
	public boolean deleteEntryCouchDB (String strKey)
	{
		boolean boolStatus = false;

		try
		{
			//Getting the Values for the key
			Document docRemove = dbCouch.getDocument(strKey);
			boolStatus = dbCouch.deleteDocument(docRemove);

			//returning the status to calling function
			if (boolStatus == true)
			{
				strMessage = "Key " + '"' + strKey + '"' + " deleted from CouchDB " + socketClient.getLocalAddress().getHostAddress() + " successfully\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
				boolStatus = true;
				return boolStatus;
			}
			else
			{
				strMessage = "No records found for Key " + '"' + strKey + '"' + " on CouchDB " + socketClient.getLocalAddress().getHostAddress() + " while deleting\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
				boolStatus = false;
				return boolStatus;
			}
		}

		catch (Exception e)
		{
			try
			{
				boolStatus = false;
				strMessage = "Error while deleting Key " + '"' + strKey + '"' + " from CouchDB " + socketClient.getLocalAddress().getHostAddress() + "\n";
				strMessage = strMessage + e.getMessage() + "\n";
				strMessage = strMessage + e.getStackTrace().toString() + "\n";
				//outputStream.write(strMessage.getBytes());
				//outputStream.flush();
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
	//Function to delete the Key - Value pair data from CouchDB ends over here

	//function to initialize MongoDB connection to local database starts from here
	public void initializeMongoDBConnection()
	{
		//code for MongoDb Server connection starts from here
		MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);
		// Now connect to your databases
		DB db = mongoClient.getDB("KeyValuePair");
		collMongoDB = db.getCollection("KeyValuePair");
		//code for MongoDb Server connection ends over here
	}
	//function to initialize MongoDB connection to local database ends over here

	//function to initialize Redis connection to local database starts from here
	public void initializeRedisDBConnection()
	{
		//code for Redis Server connection starts from here
		//Connecting to Redis server on localhost
		jdRedis = new Jedis("127.0.0.1");
		//code for Redis Server connection ends over here
	}
	//function to initialize Redis connection to local database ends over here

	//function to initialize CouchDB connection to local database starts from here
	public void initializeCouchDBConnection()
	{
		//code for CouchDB Server connection starts from here
		Session dbSession = new Session("127.0.0.1", 5984);
		String dbname = "keyvaluepair";
		dbSession.deleteDatabase(dbname);
		dbSession.createDatabase(dbname);
		dbCouch = dbSession.getDatabase(dbname);
		//code for CouchDB Server connection ends over here
	}
	//function to initialize CouchDB connection to local database ends over here

/*	
	//function to initialize CouchDB connection to local database starts from here
	public void initializeHbaseDBConnection()
	{
		//code for HbaseDB Server connection starts from here
		Configuration config = HBaseConfiguration.create();
		Connection connection = ConnectionFactory.createConnection(config);
		try
		{
			Table table = connection.getTable(TableName.valueOf("myLittleHBaseTable"));
		}
		finally
		{
			
		}
		//code for CouchDB Server connection ends over here
	}
	//function to initialize CouchDB connection to local database ends over here
*/	

	public void run()
	{
		String choice2;
		String strGetResult="";
		boolean boolOutput = false;
		try
		{
			initializeMongoDBConnection();
			initializeRedisDBConnection();
			initializeCouchDBConnection();
			while (true)
			{
				//Receiving the choice to along with the Key - Value Pair Data 
				//byte[] buffer = new byte[intBufferSize];

				//System.out.println("Server 1\n");
				choice = disInput.readUTF();
				//choice = new String (buffer);
				if (choice != "")
				{
					//Extracting the choice of operation from the data received 
					choice2 = choice.substring(0, 2);
					switch (choice2)
					{
						case "01":
							//Extracting the Key from the data received
							strKey = choice.substring(2, 12);
							//Extracting the Value from the data received
							strValue = choice.substring(12, 102);
							boolOutput = false;
							boolOutput = putEntryConcurrMap(strKey, strValue);
							dosOutput.writeUTF (Boolean.toString(boolOutput));
							dosOutput.flush();
							break;
		
						case "02":
							//Extracting the Key from the data received
							strKey = choice.substring(2, 12);
							strGetResult = "";
							strGetResult = getEntryConcurrMap(strKey);
							dosOutput.writeUTF(Integer.toString(strGetResult.length()));
							dosOutput.writeUTF(strGetResult);
							dosOutput.flush();
							break;
		
						case "03":
							//Extracting the Key from the data received
							strKey = choice.substring(2, 12);
							boolOutput = false;
							boolOutput = deleteEntryConcurrMap(strKey);
							dosOutput.writeUTF (Boolean.toString(boolOutput));
							dosOutput.flush();
							break;

						case "11":
							//Extracting the Key from the data received
							strKey = choice.substring(2, 12);
							//Extracting the Value from the data received
							strValue = choice.substring(12, 102);
							boolOutput = false;
							boolOutput = putEntryMongoDB(strKey, strValue);
							dosOutput.writeUTF (Boolean.toString(boolOutput));
							dosOutput.flush();
							break;
		
						case "12":
							//Extracting the Key from the data received
							strKey = choice.substring(2, 12);
							strGetResult = "";
							strGetResult = getEntryMongoDB(strKey);
							dosOutput.writeUTF(Integer.toString(strGetResult.length()));
							dosOutput.writeUTF(strGetResult);
							dosOutput.flush();
							break;
		
						case "13":
							//Extracting the Key from the data received
							strKey = choice.substring(2, 12);
							boolOutput = false;
							boolOutput = deleteEntryMongoDB(strKey);
							dosOutput.writeUTF (Boolean.toString(boolOutput));
							dosOutput.flush();
							break;

						case "21":
							//Extracting the Key from the data received
							strKey = choice.substring(2, 12);
							//Extracting the Value from the data received
							strValue = choice.substring(12, 102);
							boolOutput = false;
							boolOutput = putEntryRedisDB(strKey, strValue);
							dosOutput.writeUTF (Boolean.toString(boolOutput));
							dosOutput.flush();
							break;
		
						case "22":
							//Extracting the Key from the data received
							strKey = choice.substring(2, 12);
							strGetResult = "";
							strGetResult = getEntryRedisDB(strKey);
							dosOutput.writeUTF(Integer.toString(strGetResult.length()));
							dosOutput.writeUTF(strGetResult);
							dosOutput.flush();
							break;
		
						case "23":
							//Extracting the Key from the data received
							strKey = choice.substring(2, 12);
							boolOutput = false;
							boolOutput = deleteEntryRedisDB(strKey);
							dosOutput.writeUTF (Boolean.toString(boolOutput));
							dosOutput.flush();
							break;

						case "31":
							//Extracting the Key from the data received
							strKey = choice.substring(2, 12);
							//Extracting the Value from the data received
							strValue = choice.substring(12, 102);
							boolOutput = false;
							boolOutput = putEntryCouchDB(strKey, strValue);
							dosOutput.writeUTF (Boolean.toString(boolOutput));
							dosOutput.flush();
							break;
		
						case "32":
							//Extracting the Key from the data received
							strKey = choice.substring(2, 12);
							strGetResult = "";
							strGetResult = getEntryCouchDB(strKey);
							dosOutput.writeUTF(Integer.toString(strGetResult.length()));
							dosOutput.writeUTF(strGetResult);
							dosOutput.flush();
							break;
		
						case "33":
							//Extracting the Key from the data received
							strKey = choice.substring(2, 12);
							boolOutput = false;
							boolOutput = deleteEntryCouchDB(strKey);
							dosOutput.writeUTF (Boolean.toString(boolOutput));
							dosOutput.flush();
							break;

						/*case "41":
							//Extracting the Key from the data received
							strKey = choice.substring(2, 12);
							//Extracting the Value from the data received
							strValue = choice.substring(12, 102);
							boolOutput = false;
							boolOutput = putEntryHbaseDB(strKey, strValue);
							dosOutput.writeUTF (Boolean.toString(boolOutput));
							dosOutput.flush();
							break;
		
						case "42":
							//Extracting the Key from the data received
							strKey = choice.substring(2, 12);
							strGetResult = "";
							strGetResult = getEntryHbaseDB(strKey);
							dosOutput.writeUTF(Integer.toString(strGetResult.length()));
							dosOutput.writeUTF(strGetResult);
							dosOutput.flush();
							break;
		
						case "43":
							//Extracting the Key from the data received
							strKey = choice.substring(2, 12);
							boolOutput = false;
							boolOutput = deleteEntryHbaseDB(strKey);
							dosOutput.writeUTF (Boolean.toString(boolOutput));
							dosOutput.flush();
							break;*/
		
						default:
							String strStatus = "Invalid choice\n";
							dosOutput.writeUTF(strStatus);
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