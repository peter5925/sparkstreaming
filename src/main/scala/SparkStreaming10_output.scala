import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import redis.clients.jedis.Jedis

object SparkStreaming10_output {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("update").setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Seconds(3))
    val line = ssc.socketTextStream("hadoop102", 9999)
    val word = line.flatMap(_.split(" "))
     word.foreachRDD(word => {
      word.foreachPartition { value => {
        val jedis = new Jedis("hadoop102", 6379)
        value.foreach(word => {
          jedis.set("" + System.currentTimeMillis(), word)
        })
        jedis.close()
      }
      }
    })
    word.print()
    ssc.start()
    ssc.awaitTermination()


  }
}
