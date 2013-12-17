package org.bensteele.jirrigate.weather.weatherunderground.response;

import java.util.List;

public class Simpleforecast {
  private List<Forecastday> forecastday;

  public List<Forecastday> getForecastday() {
    return this.forecastday;
  }

  public void setForecastday(List<Forecastday> forecastday) {
    this.forecastday = forecastday;
  }
}
