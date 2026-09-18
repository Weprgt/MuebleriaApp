package com.example.muebleriaapp;

import java.util.Map;

public class ExchangeRateResponse {

    private String result;
    private String base_code;
    private Map<String, Double> rates;

    public String getResult() {
        return result;
    }

    public String getBase_code() {
        return base_code;
    }

    public Map<String, Double> getRates() {
        return rates;
    }
}