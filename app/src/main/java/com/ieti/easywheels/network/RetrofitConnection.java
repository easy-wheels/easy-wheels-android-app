package com.ieti.easywheels.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ieti.easywheels.network.service.CloudFunctionsService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConnection {

    private static final String BASE_URL = "https://us-central1-easy-wheels-front-end.cloudfunctions.net";
    private static CloudFunctionsService cloudFunctionsService;


    private static void createCloudFunctionsConnection() {
        if (cloudFunctionsService == null) {
            Gson gson = new GsonBuilder().setLenient().create();
            Retrofit retrofit =
                    new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

            cloudFunctionsService = retrofit.create(CloudFunctionsService.class);
        }
    }

    public static CloudFunctionsService getCloudFunctionsService() {
        if (cloudFunctionsService == null) {
            createCloudFunctionsConnection();
        }
        return cloudFunctionsService;
    }
}
