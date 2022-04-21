import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming05_Transform {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("transfrom").setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Seconds(3))
    val line = ssc.socketTextStream("hadoop102", 9999)
    println("transform外" + Thread.currentThread().getName)
    val result = line.transform(
      rdd => {
        println("transform内map外" + Thread.currentThread().getName)
        rdd.flatMap(_.split(" "))
          .map(word => {
            println("map内" + Thread.currentThread().getName)
            (word, 1)
          })
          .reduceByKey(_ + _)
      }
    )
    result.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
