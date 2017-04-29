package by.vbalanse.spark.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;


/**
 * Created by Vasilina on 26.03.2017.
 */
public class HdfsAccessTest {
    public static void main(String[] args) throws IOException {
        FileSystem hdfs = getFileSystem();

        boolean success = hdfs.mkdirs(new Path("/user/devuser/configs"));
        System.out.println(success);
    }

    public static FileSystem getFileSystem() throws IOException {
        Configuration conf = new Configuration();
        conf.addResource(new Path("file:///D:/!!!configs/core-site.xml"));
        conf.addResource(new Path("file:///D:/!!!configs/hdfs-site.xml"));
        UserGroupInformation.setConfiguration(conf);
        return FileSystem.get(conf);
    }
}
