package com.petpaw.clients;

import com.petpaw.mappers.ExampleResponseParser;
import com.petpaw.models.ExampleModel;
import com.petpaw.models.Response;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class ExampleClient {
    private static final String BASE_URL = "example.com";
    private final Executor executor;

    private ExampleResponseParser responseParser;

    public static class ExampleResponse {
        public List<ExampleModel> exampleModels = new ArrayList<>();
    }

    class ExampleClientCallBack<ExampleResponse> {
        void onComplete(Response<ExampleResponse> result) {
            System.out.println("onComplete");
        }
    }

    ExampleClientCallBack<ExampleResponse> callback;


    public ExampleClient(Executor executor) {
        this.executor = executor;
        this.callback = new ExampleClientCallBack<>();
    }

    public Response<ExampleResponse> getExampleModels() {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            ExampleResponse exampleResponse = responseParser.parse(connection.getInputStream());
            for (ExampleModel exampleModel : exampleResponse.exampleModels) {
                System.out.println(exampleModel.getName());
            }
            return new Response.Success<ExampleResponse>(exampleResponse);
        } catch (Exception e) {
            return new Response.Error<ExampleResponse>(e);
        }
    }

    public void asyncGetExampleModels() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Response<ExampleResponse> response = getExampleModels();
                callback.onComplete(response);
            }
        });
    }
}

