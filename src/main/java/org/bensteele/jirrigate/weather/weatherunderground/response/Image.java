package org.bensteele.jirrigate.weather.weatherunderground.response;

public class Image {
  private String link;
  private String title;
  private String url;

  public String getLink() {
    return this.link;
  }

  public String getTitle() {
    return this.title;
  }

  public String getUrl() {
    return this.url;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
