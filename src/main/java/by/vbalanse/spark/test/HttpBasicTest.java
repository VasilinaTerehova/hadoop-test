package by.vbalanse.spark.test;

import org.omg.CORBA.NameValuePair;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


/**
 * Created by Vasilina_Terehova on 4/29/2017.
 */
public class HttpBasicTest {
    public static void main(String[] args) throws IOException {
        javaPureGet();
        javaPureGetBasic();
    }

    private static void javaPureGet() throws IOException {
        URL url = new URL("http://localhost:8080/manager/html");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        int responseCode = urlConnection.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("response code " + responseCode);
    }

    private static void javaPureGetBasic() throws IOException {
        URL url = new URL("http://localhost:8080/manager/html");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String encoded = Base64.getEncoder().encodeToString(("tomcat"+":"+"tomcat").getBytes(StandardCharsets.UTF_8));  //Java 8
        urlConnection.setRequestProperty("Authorization", "Basic "+encoded);

        int responseCode = urlConnection.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("response code " + responseCode);
    }
}
