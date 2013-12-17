package org.bensteele.jirrigate.weather.weatherunderground.response;

public class Display_location {
  private String city;
  private String country;
  private String country_iso3166;
  private String elevation;
  private String full;
  private String latitude;
  private String longitude;
  private String magic;
  private String state;
  private String state_name;
  private String wmo;
  private String zip;

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

  public String getMagic() {
    return this.magic;
  }

  public String getState() {
    return this.state;
  }

  public String getState_name() {
    return this.state_name;
  }

  public String getWmo() {
    return this.wmo;
  }

  public String getZip() {
    return this.zip;
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

  public void setMagic(String magic) {
    this.magic = magic;
  }

  public void setState(String state) {
    this.state = state;
  }

  public void setState_name(String state_name) {
    this.state_name = state_name;
  }

  public void setWmo(String wmo) {
    this.wmo = wmo;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }
}
