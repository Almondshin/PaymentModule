package com.modules.link.infrastructure.notification;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public class NotificationSender {
    public String sendNotification(String targetUrl, String responseData) {
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(10000);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setDoOutput(true);

            try (DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream())) {
                dataOutputStream.write(responseData.getBytes(StandardCharsets.UTF_8));
            }

            System.out.println("[Notify Target URL] : " + url);
            System.out.println("[Notify Data] : " + responseData);

            DataInputStream inputStream = new DataInputStream(connection.getInputStream());
            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            byte[] buf = new byte[1024];

            String resData;
            while (true) {
                int n = inputStream.read(buf);
                if (n == -1) {
                    break;
                }
                bout.write(buf, 0, n);
            }

            bout.flush();

            inputStream.close();
            bout.close();

            resData = bout.toString(StandardCharsets.UTF_8);

            System.out.println("[Response Code] : " + connection.getResponseCode());
            System.out.println("[Response Data] : " + resData);
            System.out.println("[Response Data length] : " + resData.getBytes().length);
            return "Success";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
