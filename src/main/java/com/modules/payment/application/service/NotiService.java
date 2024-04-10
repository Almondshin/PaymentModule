package com.modules.payment.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modules.payment.application.domain.AgencyInfoKey;
import com.modules.payment.application.port.in.NotiUseCase;
import com.modules.payment.application.port.out.load.LoadEncryptDataPort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Service
public class NotiService implements NotiUseCase {

    private final LoadEncryptDataPort loadEncryptDataPort;

    public NotiService(LoadEncryptDataPort loadEncryptDataPort) {
        this.loadEncryptDataPort = loadEncryptDataPort;
    }

    @Override
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

            System.out.println("전달 URL 체크 : " + url);
            System.out.println("전달 data 체크 : " + responseData);

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

    @Override
    public String getAgencyUrlByAgencyInfoKey(String agencyId, String type) {
        Optional<AgencyInfoKey> agencyInfoKey = loadEncryptDataPort.getAgencyInfoKey(agencyId);
        if (agencyInfoKey.isPresent()) {
            AgencyInfoKey info = agencyInfoKey.get();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> agencyUrlJson = null;
            try {
                agencyUrlJson = mapper.readValue(info.getAgencyUrl(), new TypeReference<>() {
                });
                return agencyUrlJson.get(type);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }


}
