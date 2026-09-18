package com.example.muebleriaapp;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ExchangeRateApi {

    @GET("v6/latest/USD")
    Call<ExchangeRateResponse> obtenerTipoCambio();
}