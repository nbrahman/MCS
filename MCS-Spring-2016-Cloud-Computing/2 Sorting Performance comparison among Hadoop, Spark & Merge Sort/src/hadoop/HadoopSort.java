import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class HadoopSort
{

	//Mapper class, used to convert line from text file into Key - Value pair starts from here
	public static class MyMapper
		extends Mapper<Object, Text, Text, Text>
	{
		private Text strKey;
		private Text strValue;
		public void map(Object key, Text value, Context context) 
			throws IOException, InterruptedException
		{
			StringTokenizer itr = new StringTokenizer(value.toString());
			strKey = new Text(value.toString().substring(0,10));	
			strValue = new Text(value.toString().substring(10));
			context.write(strKey, strValue);
		}
	}
	//Mapper class, used to convert line from text file into Key - Value pair ends over here

	//Reducer class, used to merge Key - Value pair as a single line for the referred key starts from here
	public static class MyReducer extends Reducer<Text,Text,Text,Text>
	{
		private Text strLine = new Text();
		private Text strNewValue = new Text();
		public void reduce(Text strKey, Text strValue, Context context) 
			throws IOException, InterruptedException
		{
			strLine.set(strKey.toString() + strValue.toString());
			strNewValue.set("");
			context.write(strLine, strNewValue);
		}
	}
	//Reducer class, used to merge Key - Value pair as a single line for the referred key ends over here

	//main function starts from here
	public static void main(String[] args) throws Exception
	{
		//define & initialize Configuration object
		Configuration conf = new Configuration();
		conf.set("mapred.textoutputformat.separator", "");

		//get access to Job Instance
		Job job = Job.getInstance(conf, "Hadoop Sort");
		//define job class
		job.setJarByClass(HadoopSort.class);
		//define mapper class
		job.setMapperClass(MyMapper.class);
		//define combiner class
		job.setCombinerClass(MyReducer.class);
		//define reducer class
		job.setReducerClass(MyReducer.class);
		//define output key class
		job.setOutputKeyClass(Text.class);
		//define output value class
		job.setOutputValueClass(Text.class);
		//define map key class
		job.setMapOutputKeyClass(Text.class);
		//define map value class
		job.setMapOutputValueClass(Text.class);
		//pass 0th argument as input path for program
		FileInputFormat.addInputPath(job, new Path(args[0]));
		//pass 1st argument as output path for program
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		//wait for job to complete & then exit
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	//main function ends over here

}
