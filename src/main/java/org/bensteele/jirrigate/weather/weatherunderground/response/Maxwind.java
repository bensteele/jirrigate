package org.bensteele.jirrigate.weather.weatherunderground.response;


public class Maxwind {
  private Number degrees;
  private String dir;
  private Number kph;
  private Number mph;

  public Number getDegrees() {
    return this.degrees;
  }

  public void setDegrees(Number degrees) {
    this.degrees = degrees;
  }

  public String getDir() {
    return this.dir;
  }

  public void setDir(String dir) {
    this.dir = dir;
  }

  public Number getKph() {
    return this.kph;
  }

  public void setKph(Number kph) {
    this.kph = kph;
  }

  public Number getMph() {
    return this.mph;
  }

  public void setMph(Number mph) {
    this.mph = mph;
  }
}
