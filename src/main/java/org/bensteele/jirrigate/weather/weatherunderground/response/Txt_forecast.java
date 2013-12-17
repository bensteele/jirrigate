package org.bensteele.jirrigate.weather.weatherunderground.response;

import java.util.List;

public class Txt_forecast {
  private String date;
  private List<Forecastday> forecastday;

  public String getDate() {
    return this.date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public List<Forecastday> getForecastday() {
    return this.forecastday;
  }

  public void setForecastday(List<Forecastday> forecastday) {
    this.forecastday = forecastday;
  }
}
