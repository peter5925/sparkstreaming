import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object sparkStreaming06_updateStateByKey {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("update").setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Seconds(3))
    ssc.checkpoint("D:\\idea_bigdata0225\\sparksteaming-0225\\output")
    val line = ssc.socketTextStream("hadoop102", 9999)
    val result = line.flatMap(_.split(" ")).map((_, 1)).updateStateByKey(updateFunc)
    result.print()
    ssc.start()
    ssc.awaitTermination()


    }
  def updateFunc = (seq:Seq[Int],opt:Option[Int])=>{
    val currentCount: Int = seq.sum
    val stateCount = opt.getOrElse(0)
    Some(currentCount+stateCount)
  }

}
