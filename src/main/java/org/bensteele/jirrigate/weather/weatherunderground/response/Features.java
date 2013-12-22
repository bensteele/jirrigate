package org.bensteele.jirrigate.weather.weatherunderground.response;

public class Features {
  private Number conditions;
  private Number forecast10day;

  public Number getForecast10day() {
    return forecast10day;
  }

  public void setForecast10day(Number forecast10day) {
    this.forecast10day = forecast10day;
  }

  public Number getConditions() {
    return this.conditions;
  }

  public void setConditions(Number conditions) {
    this.conditions = conditions;
  }
}
