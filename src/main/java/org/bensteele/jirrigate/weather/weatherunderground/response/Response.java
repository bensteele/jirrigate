package org.bensteele.jirrigate.weather.weatherunderground.response;

public class Response {
  private Features features;
  private String termsofService;
  private String version;

  public Features getFeatures() {
    return this.features;
  }

  public String getTermsofService() {
    return this.termsofService;
  }

  public String getVersion() {
    return this.version;
  }

  public void setFeatures(Features features) {
    this.features = features;
  }

  public void setTermsofService(String termsofService) {
    this.termsofService = termsofService;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
