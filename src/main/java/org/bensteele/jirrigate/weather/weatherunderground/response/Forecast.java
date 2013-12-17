package org.bensteele.jirrigate.weather.weatherunderground.response;


public class Forecast {
  private Simpleforecast simpleforecast;
  private Txt_forecast txt_forecast;

  public Simpleforecast getSimpleforecast() {
    return this.simpleforecast;
  }

  public void setSimpleforecast(Simpleforecast simpleforecast) {
    this.simpleforecast = simpleforecast;
  }

  public Txt_forecast getTxt_forecast() {
    return this.txt_forecast;
  }

  public void setTxt_forecast(Txt_forecast txt_forecast) {
    this.txt_forecast = txt_forecast;
  }
}
