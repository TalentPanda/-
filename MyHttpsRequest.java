package com.demo.demos.FindU.SearchByWiFi.core.https;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class MyHttpsRequest {

    //发起HTTPS请求
    public static String httpsRequest(String requestURL, String requestMethod, String outputStr){
        StringBuffer buffer = new StringBuffer();
        BufferedReader br = null;
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManager[] tm = {new MyX509TrustManager()};
            sslContext.init(null,tm, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            URL url = new URL(requestURL);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(requestMethod);
            conn.setSSLSocketFactory(sslSocketFactory);
            if (outputStr != null ){
                OutputStream os = conn.getOutputStream();
                os.write(outputStr.getBytes("utf-8"));
                os.close();
            }

            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String line = null;
            while((line = br.readLine()) != null){
                buffer.append(line);
            }
            br.close();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response = buffer.toString();
        String organization = null;
        String pattern = "<td bgcolor=\"#FFFFFF\" style=\"font-size:16px;\">(.*?)</td>";
        if (response != null){
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(response);
            if (m.find()){
                organization = m.group(1);
            }
        }
        return organization;
    }
}
