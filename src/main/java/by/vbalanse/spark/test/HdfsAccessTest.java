package by.vbalanse.spark.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;


/**
 * Created by Vasilina on 26.03.2017.
 */
public class HdfsAccessTest {
    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        conf.addResource(new Path("/HADOOP_HOME/conf/core-site.xml"));
        conf.addResource(new Path("/HADOOP_HOME/conf/hdfs-site.xml"));
        FileSystem hdfs = FileSystem.get(conf);

        boolean success = hdfs.mkdirs(new Path("/user/cloudera/testdirectory"));
        System.out.println(success);
    }
}
