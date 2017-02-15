import sys

from pyspark import SparkContext

sc = SparkContext(appName="SparkSort")
distFile = sc.textFile(sys.argv[1])
words = distFile.map(lambda x: [x[:10],x[10:]])
output = words.sortByKey(True, 1)
output = sc.parallelize(output.collect())
toFile = output.map(lambda (n, ln): n + ln + "\r")
toFile.repartition(1).saveAsTextFile(sys.argv[2])
sc.stop()

