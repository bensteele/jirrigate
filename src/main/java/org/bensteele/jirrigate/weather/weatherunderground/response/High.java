package org.bensteele.jirrigate.weather.weatherunderground.response;


public class High {
  private String celsius;
  private String fahrenheit;

  public String getCelsius() {
    return this.celsius;
  }

  public void setCelsius(String celsius) {
    this.celsius = celsius;
  }

  public String getFahrenheit() {
    return this.fahrenheit;
  }

  public void setFahrenheit(String fahrenheit) {
    this.fahrenheit = fahrenheit;
  }
}
