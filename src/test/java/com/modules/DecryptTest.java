package com.modules;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Map;

public class  DecryptTest {

    public final String AES_CBC_256_KEY = "tmT6HUMU+3FW/RR5fxU05PbaZCrJkZ1wP/k6pfZnSj8=";
    public final String AES_CBC_256_IV = "/SwvI/9aT7RiMmfm8CfP4g==";

    @Test
    public void test(){
        System.out.println("test");
    }

    @Test
    public void decryptTest(){

        String encryptData = "L+gP0UO7p2gS4mcps35d5e5lbe+pgoZeFzPqwMijph0AfMt/PM2u+V89bEpn+4XYyzhq0KllxlB8t0QIp9qvdNfHo0BdKwdIdbk0P/KxbZ8xTmPCv7TvkEwhJOLzDY81MtfQAtNqT+rKUdtfJ7u3gMyXA5Jucsyaqa7w+lAi4avr1U+4Ww0mkefJVyOvMGEVt0biAdGErVm/S9xmP+ZXLnEBLQbtulooFilCZDyJlH4Fs6jnWFNOGBnnOadKmoRFhv6v6OqJfYNg8bCStMb37BB4UqffQ8tdRkLJQ+rcJAlAP0kAHY2A+sF0U7tKrszQDx/Zu57S/OpesbAyfk+NPKz2wZMDYKeqy1pySb8JLecLV7KYc2TrOm5lpA1o3uFTlXgAHSbT8tPy8aT8GvVsNXmwBa2C+vdDqGcstviM8/2fRUtEPwuhlsDE7XgRGnifYMPykYP1lqdmTDSmNt8dcR5wxcRApqmF5dfFpBaSvJVJswdKYHV2WYJjs6igw7QtWtv3cDa7WPGpGFfl5dy8MclnzT+8IlxLDbrmnpPSS840tGCtVC56D1xvZ0B709M+kkyiT18h6dhI2JCyqKyzfWoVXz1Y2mKW89yi7q79s7QqBGaqf2RXKwQG/s9o+LZ9u3qEKB6Ict7uTpXCwQPYv0TA/AV+VNFGy4qX2ZnISe3evoF6ZAU3gQbKP5ko+1gh";


        byte[] key = Base64.getDecoder().decode(AES_CBC_256_KEY);
        byte[] iv = Base64.getDecoder().decode(AES_CBC_256_IV);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] resultByte = cipher.doFinal(Base64.getDecoder().decode(encryptData));

            String result = new String(resultByte);

            System.out.println(result);


            ObjectMapper mapper = new ObjectMapper();
            try {
                Map<String, Object> map = mapper.readValue(result, new TypeReference<Map<String,Object>>(){});
                for (Map.Entry<String, Object> entry : map.entrySet()) {


                    System.out.println(entry.getKey() + " : " + entry.getValue());

                    if (entry.getKey().equals("detail")) {
                        // Check if the entry value can be converted to a JSON string.
                        if (entry.getValue() instanceof String) {
                            // Get the detail JSON string.
                            String detailJsonString = (String) entry.getValue();

                            // Parse the detail JSON string into another Map object.
                            Map<String, String> detailMap = mapper.readValue(detailJsonString, new TypeReference<Map<String, String>>() {});

                            // Now you can access the detail properties like this:
                            String custName = detailMap.get("custName");
                            String accountNumber = detailMap.get("accountNumber");
                            String bankName = detailMap.get("bankName");

                            System.out.println("custName : " + custName);
                            System.out.println("accountNumber: " + accountNumber);
                            System.out.println("bankName: " + bankName);

                        } else {
                            System.out.println("Cannot convert detail value to a JSON string");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

    }

}