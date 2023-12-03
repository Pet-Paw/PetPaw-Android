package com.petpaw.mappers;




import com.petpaw.clients.ExampleClient;
import com.petpaw.models.ExampleModel;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

public class ExampleResponseParser {
    static String data = "";
    public static ExampleClient.ExampleResponse parse(InputStream inputStream) {
        StringBuilder builder = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line="";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            data = builder.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONArray jsonArray = new JSONArray(data);
            ExampleClient.ExampleResponse exampleResponse = new ExampleClient.ExampleResponse();
            for (int i = 0; i < jsonArray.length(); i++) {
                exampleResponse.exampleModels.add(new ExampleModel(jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getInt("id")));
                System.out.println(jsonArray.getJSONObject(i).getString("name"));
            }
            return exampleResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
