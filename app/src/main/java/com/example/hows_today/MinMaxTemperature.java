package com.example.hows_today;

public class MinMaxTemperature extends Weather {
    private String minTemperature;  // TMN 최저기온

    public MinMaxTemperature(String TMN, String TMX, String date) {
        super(TMX, date);
        this.minTemperature = TMN;
    }
    public String getMaxMinTMP() {
        int min = (int) Double.parseDouble(this.minTemperature);
        int max = (int) Double.parseDouble(super.temperature);

        StringBuilder sb = new StringBuilder();

        sb.append("최저 ").append(min).append(chTemperature);
        sb.append(" / ");
        sb.append("최고 ").append(max).append(chTemperature);

        return sb.toString();
    }
}
