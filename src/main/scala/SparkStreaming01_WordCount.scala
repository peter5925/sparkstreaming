import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming01_WordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("spark").setMaster("local[*]")

    val ssc = new StreamingContext(sparkConf, Seconds(3))
    val line = ssc.socketTextStream("hadoop102", 9999)
    val result = line.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)
    result.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
