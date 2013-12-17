package org.bensteele.jirrigate.weather.weatherunderground.response;


public class WeatherUndergroundForecastResponse {
  private Forecast forecast;
  private Response response;

  public Forecast getForecast() {
    return this.forecast;
  }

  public void setForecast(Forecast forecast) {
    this.forecast = forecast;
  }

  public Response getResponse() {
    return this.response;
  }

  public void setResponse(Response response) {
    this.response = response;
  }
}
