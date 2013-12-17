package org.bensteele.jirrigate.weather.weatherunderground.response;

public class Forecastday {
  private Number avehumidity;
  private Avewind avewind;
  private String conditions;
  private String fcttext;
  private String fcttext_metric;
  private Date date;
  private High high;
  private String icon;
  private String title;
  private String icon_url;
  private Low low;
  private Number maxhumidity;
  private Maxwind maxwind;
  private Number minhumidity;
  private Number period;
  private Number pop;
  private Qpf_allday qpf_allday;
  private Qpf_day qpf_day;
  private Qpf_night qpf_night;
  private String skyicon;
  private Snow_allday snow_allday;
  private Snow_day snow_day;
  private Snow_night snow_night;

  public String getFcttext() {
    return fcttext;
  }

  public void setFcttext(String fcttext) {
    this.fcttext = fcttext;
  }

  public String getFcttext_metric() {
    return fcttext_metric;
  }

  public void setFcttext_metric(String fcttext_metric) {
    this.fcttext_metric = fcttext_metric;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Number getAvehumidity() {
    return this.avehumidity;
  }

  public void setAvehumidity(Number avehumidity) {
    this.avehumidity = avehumidity;
  }

  public Avewind getAvewind() {
    return this.avewind;
  }

  public void setAvewind(Avewind avewind) {
    this.avewind = avewind;
  }

  public String getConditions() {
    return this.conditions;
  }

  public void setConditions(String conditions) {
    this.conditions = conditions;
  }

  public Date getDate() {
    return this.date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public High getHigh() {
    return this.high;
  }

  public void setHigh(High high) {
    this.high = high;
  }

  public String getIcon() {
    return this.icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getIcon_url() {
    return this.icon_url;
  }

  public void setIcon_url(String icon_url) {
    this.icon_url = icon_url;
  }

  public Low getLow() {
    return this.low;
  }

  public void setLow(Low low) {
    this.low = low;
  }

  public Number getMaxhumidity() {
    return this.maxhumidity;
  }

  public void setMaxhumidity(Number maxhumidity) {
    this.maxhumidity = maxhumidity;
  }

  public Maxwind getMaxwind() {
    return this.maxwind;
  }

  public void setMaxwind(Maxwind maxwind) {
    this.maxwind = maxwind;
  }

  public Number getMinhumidity() {
    return this.minhumidity;
  }

  public void setMinhumidity(Number minhumidity) {
    this.minhumidity = minhumidity;
  }

  public Number getPeriod() {
    return this.period;
  }

  public void setPeriod(Number period) {
    this.period = period;
  }

  public Number getPop() {
    return this.pop;
  }

  public void setPop(Number pop) {
    this.pop = pop;
  }

  public Qpf_allday getQpf_allday() {
    return this.qpf_allday;
  }

  public void setQpf_allday(Qpf_allday qpf_allday) {
    this.qpf_allday = qpf_allday;
  }

  public Qpf_day getQpf_day() {
    return this.qpf_day;
  }

  public void setQpf_day(Qpf_day qpf_day) {
    this.qpf_day = qpf_day;
  }

  public Qpf_night getQpf_night() {
    return this.qpf_night;
  }

  public void setQpf_night(Qpf_night qpf_night) {
    this.qpf_night = qpf_night;
  }

  public String getSkyicon() {
    return this.skyicon;
  }

  public void setSkyicon(String skyicon) {
    this.skyicon = skyicon;
  }

  public Snow_allday getSnow_allday() {
    return this.snow_allday;
  }

  public void setSnow_allday(Snow_allday snow_allday) {
    this.snow_allday = snow_allday;
  }

  public Snow_day getSnow_day() {
    return this.snow_day;
  }

  public void setSnow_day(Snow_day snow_day) {
    this.snow_day = snow_day;
  }

  public Snow_night getSnow_night() {
    return this.snow_night;
  }

  public void setSnow_night(Snow_night snow_night) {
    this.snow_night = snow_night;
  }
}
