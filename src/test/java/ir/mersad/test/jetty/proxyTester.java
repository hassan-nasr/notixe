package ir.mersad.test.jetty;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by vaio on 2015-01-01.
 */
public class proxyTester {

    public static boolean test() {
        try {
            URL googleInfoUrl = new URL("https://www.facebook.com");
            HttpURLConnection googleInfoResponse = (HttpURLConnection) googleInfoUrl.openConnection();
            googleInfoResponse.connect();
            int status = googleInfoResponse.getResponseCode();
            return status == 200 || status == 201;
        } catch (MalformedURLException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}
