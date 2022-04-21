import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming07_window {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("update").setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Seconds(3))
    val line = ssc.socketTextStream("hadoop102", 9999)
    val wordToOne = line.flatMap(_.split(" ")).map((_, 1))
    val reslut = wordToOne.window(Seconds(12), Seconds(6)).reduceByKey(_ + _)
    reslut.print()
    ssc.start()
    ssc.awaitTermination()

  }
}
