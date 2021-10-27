package com.steelparrot.freedecibel.network;

import com.steelparrot.freedecibel.model.YTItem;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface GetDataService {

    @POST("/ytitems")
    Call<List<YTItem>> getYTItems(@Body HashMap<String, String> map);

//    @GET("/ytitems")
//    Call<List<YTItem>> getYTItems();

}
