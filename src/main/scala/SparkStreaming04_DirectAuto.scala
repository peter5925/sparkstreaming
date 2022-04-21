import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming04_DirectAuto {
  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName("kafka").setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Seconds(3))
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "hadoop102:9092,hadoop103:9092,hadoop104:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("testTopic")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies PreferConsistent,
      ConsumerStrategies Subscribe[String, String](topics, kafkaParams)
    )

    val result = stream.map(_.value).flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)
    result.print()
    ssc.start()
    ssc.awaitTermination()

  }
}
