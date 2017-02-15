public_name=$(curl -s http://169.254.169.254/latest/meta-data/public-hostname)
pem_name="PA4_Niks.pem"

echo "setting up passwordless access"
eval `ssh-agent -s`
sudo chmod 600 $pem_name
sudo ssh-add $pem_name

#echo "Creating and setting up SSH certificates"
#sudo mkdir $HOME/.ssh
#sudo ssh-keygen -t rsa -P "" -f $HOME/.ssh/id_rsa
#sudo cat $HOME/.ssh/id_rsa.pub >> $HOME/.ssh/authorized_keys
#sudo chmod 0600 ~/.ssh/authorized_keys
#sudo ssh $public_name

echo "Downloading and unzipping Hadoop 2.7.2"
sudo wget http://mirrors.sonic.net/apache/hadoop/common/hadoop-2.7.2/hadoop-2.7.2.tar.gz
sudo tar xvzf hadoop-2.7.2.tar.gz

echo "Installing Hadoop"
sudo mkdir /usr/local/hadoop
sudo mv hadoop-2.7.2 /usr/local/hadoop/
sudo chown -R hduser:hadoop /usr/local/hadoop/hadoop-2.7.2
sudo cp /usr/local/hadoop/hadoop-2.7.2/etc/hadoop/mapred-site.xml.template /usr/local/hadoop/hadoop-2.7.2/etc/hadoop/mapred-site.xml
sudo chmod 777 /usr/local/hadoop/hadoop-2.7.2/etc/hadoop/mapred-site.xml

echo "updating bashrc"
sudo cat << bashrc >> ~/.bashrc
#HADOOP VARIABLES START
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
export HADOOP_INSTALL=/usr/local/hadoop/hadoop-2.7.2
export PATH=$PATH:$HADOOP_INSTALL/bin
export PATH=$PATH:$HADOOP_INSTALL/sbin
export HADOOP_MAPRED_HOME=$HADOOP_INSTALL
export HADOOP_COMMON_HOME=$HADOOP_INSTALL
export HADOOP_HDFS_HOME=$HADOOP_INSTALL
export YARN_HOME=$HADOOP_INSTALL
export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_INSTALL/lib/native
export HADOOP_OPTS="-Djava.library.path=$HADOOP_INSTALL/lib"
export HADOOP_OPTS=-Djava.net.preferIPv4Stack=true
export CONF=/usr/local/hadoop/hadoop-2.7.2/etc/hadoop

# Some convenient aliases and functions for running Hadoop-related commands
unalias fs &> /dev/null
alias fs="hadoop fs"
unalias hls &> /dev/null
alias hls="fs -ls"

# If you have LZO compression enabled in your Hadoop cluster and
# compress job outputs with LZOP (not covered in this tutorial):
# Conveniently inspect an LZOP compressed file from the command
# line; run via:
#
# $ lzohead /hdfs/path/to/lzop/compressed/file.lzo
#
# Requires installed 'lzop' command.
#
lzohead () {
    hadoop fs -cat $1 | lzop -dc | head -1000 | less
}
#HADOOP VARIABLES END
bashrc
source ~/.bashrc

echo "updating hadoop-env.sh"
sudo cat << hadoop-env >> /usr/local/hadoop/hadoop-2.7.2/etc/hadoop/hadoop-env.sh
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
hadoop-env

sudo mkdir -p /app/hadoop/tmp
sudo chown hduser:hadoop /app/hadoop/tmp
sudo chmod 750 /app/hadoop/tmp

echo "updating core-site.xml"
sudo cat > /usr/local/hadoop/hadoop-2.7.2/etc/hadoop/core-site.xml << core-site
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
 <property>
  <name>hadoop.tmp.dir</name>
  <value>/app/hadoop/tmp</value>
 </property>
 <property>
  <name>io.sort.factor</name>
  <value>100</value>
 </property>
 <property>
  <name>io.sort.mb</name>
  <value>768</value>
 </property>
 <property>
  <name>fs.default.name</name>
  <value>hdfs://$public_name:54310</value>
  <description>The name of the default file system.  A URI whose
  scheme and authority determine the FileSystem implementation.  The
  uri's scheme determines the config property (fs.SCHEME.impl) naming
  the FileSystem implementation class.  The uri's authority is used to
  determine the host, port, etc. for a filesystem.</description>
 </property>
</configuration>
core-site

echo "updating hdfs-site.xml"
sudo mkdir -p /usr/local/hadoop_store/hdfs/namenode
sudo mkdir -p /usr/local/hadoop_store/hdfs/datanode
sudo chown -R hduser:hadoop /usr/local/hadoop_store
sudo cat > /usr/local/hadoop/hadoop-2.7.2/etc/hadoop/hdfs-site.xml << hdfs-site
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
 <property>
  <name>dfs.replication</name>
  <value>1</value>
  <description>Default block replication.
  The actual number of replications can be specified when the file is created.
  The default is used if replication is not specified in create time.
  </description>
 </property>
 <property>
  <name>dfs.permissions</name>
  <value>false</value>
 </property>
 <property>
   <name>dfs.namenode.name.dir</name>
   <value>file:/usr/local/hadoop_store/hdfs/namenode</value>
 </property>
 <property>
   <name>dfs.datanode.data.dir</name>
   <value>file:/usr/local/hadoop_store/hdfs/datanode</value>
 </property>
 <property>
   <name>dfs.blocksize</name>
   <value>134217728</value>
 </property>
</configuration>
hdfs-site

sudo echo "updating yarn-site.xml"
sudo cat > /usr/local/hadoop/hadoop-2.7.2/etc/hadoop/yarn-site.xml << yarn-site
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
  <property>
    <name>yarn.nodemanager.aux-services</name>
    <value>mapreduce_shuffle</value>
  </property>
  <property>
    <name>yarn.resourcemanager.scheduler.address</name>
    <value>$public_name:54312</value>
  </property>
  <property>
    <name>yarn.resourcemanager.address</name>
    <value>$public_name:54313</value>
  </property>
  <property>
    <name>yarn.resourcemanager.webapp.address</name>
    <value>$public_name:54314</value>
  </property>
  <property>
    <name>yarn.resourcemanager.resource-tracker.address</name>
    <value>$public_name:54315</value>
  </property>
  <property>
    <name>yarn.resourcemanager.admin.address</name>
    <value>$public_name:54316</value>
  </property>
  <property>
    <name>yarn.nodemanager.aux-services.mapreduce.shuffle.class</name>
    <value>org.apache.hadoop.mapred.ShuffleHandler</value>
  </property>
  <property>
    <name>yarn.app.mapreduce.am.command-opts</name>
    <value>-Xmx819m</value>
  </property>
</configuration>
yarn-site

echo "updating mapred-site.xml"
sudo cat << mapred-site >> /usr/local/hadoop/hadoop-2.7.2/etc/hadoop/mapred-site.xml 
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
 <property>
  <name>mapred.job.tracker</name>
  <value>$public_name:54311</value>
 </property>
 <property>
  <name>mapred.framework.name</name>
  <value>yarn</value>
 </property>
 <property>
  <name>mapred.tasktracker.map.tasks.maximum</name>
  <value>3</value>
 </property>
 <property>
  <name>mapred.tasktracker.reduce.tasks.maximum</name>
  <value>4</value>
 </property>
 <property>
  <name>mapred.child.java.opts</name>
  <value>-Xmx3500M</value>
 </property>
 <property>
  <name>mapred.job.map.memory.mb</name>
  <value>-1</value>
 </property>
 <property>
  <name>mapred.job.reduce.memory.mb</name>
  <value>-1</value>
 </property>
 <property>
  <name>mapred.jobtracker.maxtasks.per.job</name>
  <value>-1</value>
 </property>
 <property>
  <name>mapred.reduce.parallel.copies</name>
  <value>-15</value>
 </property>
 <property>
  <name>mapreduce.reduce.input.limit</name>
  <value>-1</value>
 </property>
</configuration>
mapred-site

echo "Formatting HDFS"
sudo /usr/local/hadoop/hadoop-2.7.2/bin/hdfs namenode -format

echo "starting Hadoop"
sudo /usr/local/hadoop/hadoop-2.7.2/sbin/start-all.sh


echo "Downloading Spark"
sudo wget http://mirrors.gigenet.com/apache/spark/spark-1.6.1/spark-1.6.1-bin-hadoop2.6.tgz
sudo tar xvzf spark-1.6.1-bin-hadoop2.6.tgz
