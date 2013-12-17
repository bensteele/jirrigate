package org.bensteele.jirrigate.weather.weatherunderground.response;

public class Observation_location {
  private String city;
  private String country;
  private String country_iso3166;
  private String elevation;
  private String full;
  private String latitude;
  private String longitude;
  private String state;

  public String getCity() {
    return this.city;
  }

  public String getCountry() {
    return this.country;
  }

  public String getCountry_iso3166() {
    return this.country_iso3166;
  }

  public String getElevation() {
    return this.elevation;
  }

  public String getFull() {
    return this.full;
  }

  public String getLatitude() {
    return this.latitude;
  }

  public String getLongitude() {
    return this.longitude;
  }

  public String getState() {
    return this.state;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void setCountry_iso3166(String country_iso3166) {
    this.country_iso3166 = country_iso3166;
  }

  public void setElevation(String elevation) {
    this.elevation = elevation;
  }

  public void setFull(String full) {
    this.full = full;
  }

  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  public void setState(String state) {
    this.state = state;
  }
}
