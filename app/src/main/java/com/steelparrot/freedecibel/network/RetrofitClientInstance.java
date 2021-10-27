package com.steelparrot.freedecibel.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static Retrofit retrofitInstance;
    private  static final String BASE_URL = "https://freedecibel-api.herokuapp.com/";

    public static Retrofit getRetrofitInstance() {
        if(retrofitInstance == null) {
            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofitInstance;
    }
}
