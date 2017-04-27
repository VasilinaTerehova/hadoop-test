package by.vbalanse.spark.test;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Iterator;


/**
 * Created by Vasilina on 25.03.2017.
 */
public class SparkTest {
    public static void main(String[] args) throws InterruptedException {
        SparkConf conf = new SparkConf().setAppName("spark-test").setMaster("local[*]");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, new Duration(1000));
        JavaDStream<String> stringJavaDStream = jssc.textFileStream("D:\text");


        JavaDStream<String> words = stringJavaDStream.flatMap(
                new FlatMapFunction<String, String>() {
                    public Iterator<String> call(String x) {
                        return Arrays.asList(x.split(" ")).iterator();
                    }
                });

        // Count each word in each batch
        JavaPairDStream<String, Integer> pairs = words.mapToPair(
                new PairFunction<String, String, Integer>() {
                    public Tuple2<String, Integer> call(String s) {
                        return new Tuple2<String, Integer>(s, 1);
                    }
                });
        JavaPairDStream<String, Integer> wordCounts = pairs.reduceByKey(
                new Function2<Integer, Integer, Integer>() {
                    public Integer call(Integer i1, Integer i2) {
                        return i1 + i2;
                    }
                });

// Print the first ten elements of each RDD generated in this DStream to the console
        System.out.println("start ouput!!!!");
        wordCounts.saveAsHadoopFiles("wordcount results", "suffix");
        wordCounts.foreachRDD(new VoidFunction2<JavaPairRDD<String, Integer>, Time>() {
            public void call(JavaPairRDD<String, Integer> stringIntegerJavaPairRDD, Time time) throws Exception {
                System.out.println("!!!keys:" + stringIntegerJavaPairRDD.count());
            }
        });
        wordCounts.print();
//        wordCounts.count();
        System.out.println("count of words: " + wordCounts.count());
        jssc.start();              // Start the computation
        jssc.awaitTermination();
    }
}
