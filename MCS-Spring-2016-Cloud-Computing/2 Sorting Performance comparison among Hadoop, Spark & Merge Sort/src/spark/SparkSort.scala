val irdd = sc.textFile("hdfs://ec2-54-88-112-8.compute-1.amazonaws.com:9000/user/root/test100GB")
val ordd = irdd.map(x=> (x.substring(0,10),x.substring(10,x.length))).sortByKey(true, 1).map(x => x._1.toString + x._2.toString + "\r")
ordd.saveAsTextFile("hdfs://ec2-54-88-112-8.compute-1.amazonaws.com:9000/user/root/dstn")
