package org.bensteele.jirrigate.weather.weatherunderground.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Current_observation {

  private String UV;
  private Number dewpoint_c;
  private Number dewpoint_f;
  private String dewpoint_string;
  private Display_location display_location;
  private Estimated estimated;
  private String feelslike_c;
  private String feelslike_f;
  private String feelslike_string;
  private String forecast_url;
  private String heat_index_c;
  private String heat_index_f;
  private String heat_index_string;
  private String history_url;
  private String icon;
  private String icon_url;
  private Image image;
  private String local_epoch;
  private String local_time_rfc822;
  private String local_tz_long;
  private String local_tz_offset;
  private String local_tz_short;
  private String ob_url;
  private String observation_epoch;
  private Observation_location observation_location;
  private String observation_time;
  private String observation_time_rfc822;
  private String precip_1hr_in;
  private String precip_1hr_metric;
  private String precip_1hr_string;
  private String precip_today_in;
  private String precip_today_metric;
  private String precip_today_string;
  private String pressure_in;
  private String pressure_mb;
  private String pressure_trend;
  private String relative_humidity;
  private String solarradiation;
  private String station_id;
  private Number temp_c;
  private Number temp_f;
  private String temperature_string;
  private String visibility_km;
  private String visibility_mi;
  private String weather;
  private Number wind_degrees;
  private String wind_dir;
  private String wind_gust_kph;
  private String wind_gust_mph;
  private Number wind_kph;
  private Number wind_mph;
  private String wind_string;
  private String windchill_c;
  private String windchill_f;
  private String windchill_string;

  public Number getDewpoint_c() {
    return this.dewpoint_c;
  }

  public Number getDewpoint_f() {
    return this.dewpoint_f;
  }

  public String getDewpoint_string() {
    return this.dewpoint_string;
  }

  public Display_location getDisplay_location() {
    return this.display_location;
  }

  public Estimated getEstimated() {
    return this.estimated;
  }

  public String getFeelslike_c() {
    return this.feelslike_c;
  }

  public String getFeelslike_f() {
    return this.feelslike_f;
  }

  public String getFeelslike_string() {
    return this.feelslike_string;
  }

  public String getForecast_url() {
    return this.forecast_url;
  }

  public String getHeat_index_c() {
    return this.heat_index_c;
  }

  public String getHeat_index_f() {
    return this.heat_index_f;
  }

  public String getHeat_index_string() {
    return this.heat_index_string;
  }

  public String getHistory_url() {
    return this.history_url;
  }

  public String getIcon() {
    return this.icon;
  }

  public String getIcon_url() {
    return this.icon_url;
  }

  public Image getImage() {
    return this.image;
  }

  public String getLocal_epoch() {
    return this.local_epoch;
  }

  public String getLocal_time_rfc822() {
    return this.local_time_rfc822;
  }

  public String getLocal_tz_long() {
    return this.local_tz_long;
  }

  public String getLocal_tz_offset() {
    return this.local_tz_offset;
  }

  public String getLocal_tz_short() {
    return this.local_tz_short;
  }

  public String getOb_url() {
    return this.ob_url;
  }

  public String getObservation_epoch() {
    return this.observation_epoch;
  }

  public Observation_location getObservation_location() {
    return this.observation_location;
  }

  public String getObservation_time() {
    return this.observation_time;
  }

  public String getObservation_time_rfc822() {
    return this.observation_time_rfc822;
  }

  public String getPrecip_1hr_in() {
    return this.precip_1hr_in;
  }

  public String getPrecip_1hr_metric() {
    return this.precip_1hr_metric;
  }

  public String getPrecip_1hr_string() {
    return this.precip_1hr_string;
  }

  public String getPrecip_today_in() {
    return this.precip_today_in;
  }

  public String getPrecip_today_metric() {
    return this.precip_today_metric;
  }

  public String getPrecip_today_string() {
    return this.precip_today_string;
  }

  public String getPressure_in() {
    return this.pressure_in;
  }

  public String getPressure_mb() {
    return this.pressure_mb;
  }

  public String getPressure_trend() {
    return this.pressure_trend;
  }

  public String getRelative_humidity() {
    return this.relative_humidity;
  }

  public String getSolarradiation() {
    return this.solarradiation;
  }

  public String getStation_id() {
    return this.station_id;
  }

  public Number getTemp_c() {
    return this.temp_c;
  }

  public Number getTemp_f() {
    return this.temp_f;
  }

  public String getTemperature_string() {
    return this.temperature_string;
  }

  public String getUV() {
    return this.UV;
  }

  public String getVisibility_km() {
    return this.visibility_km;
  }

  public String getVisibility_mi() {
    return this.visibility_mi;
  }

  public String getWeather() {
    return this.weather;
  }

  public Number getWind_degrees() {
    return this.wind_degrees;
  }

  public String getWind_dir() {
    return this.wind_dir;
  }

  public String getWind_gust_kph() {
    return this.wind_gust_kph;
  }

  public String getWind_gust_mph() {
    return this.wind_gust_mph;
  }

  public Number getWind_kph() {
    return this.wind_kph;
  }

  public Number getWind_mph() {
    return this.wind_mph;
  }

  public String getWind_string() {
    return this.wind_string;
  }

  public String getWindchill_c() {
    return this.windchill_c;
  }

  public String getWindchill_f() {
    return this.windchill_f;
  }

  public String getWindchill_string() {
    return this.windchill_string;
  }

  public void setDewpoint_c(Number dewpoint_c) {
    this.dewpoint_c = dewpoint_c;
  }

  public void setDewpoint_f(Number dewpoint_f) {
    this.dewpoint_f = dewpoint_f;
  }

  public void setDewpoint_string(String dewpoint_string) {
    this.dewpoint_string = dewpoint_string;
  }

  public void setDisplay_location(Display_location display_location) {
    this.display_location = display_location;
  }

  public void setEstimated(Estimated estimated) {
    this.estimated = estimated;
  }

  public void setFeelslike_c(String feelslike_c) {
    this.feelslike_c = feelslike_c;
  }

  public void setFeelslike_f(String feelslike_f) {
    this.feelslike_f = feelslike_f;
  }

  public void setFeelslike_string(String feelslike_string) {
    this.feelslike_string = feelslike_string;
  }

  public void setForecast_url(String forecast_url) {
    this.forecast_url = forecast_url;
  }

  public void setHeat_index_c(String heat_index_c) {
    this.heat_index_c = heat_index_c;
  }

  public void setHeat_index_f(String heat_index_f) {
    this.heat_index_f = heat_index_f;
  }

  public void setHeat_index_string(String heat_index_string) {
    this.heat_index_string = heat_index_string;
  }

  public void setHistory_url(String history_url) {
    this.history_url = history_url;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public void setIcon_url(String icon_url) {
    this.icon_url = icon_url;
  }

  public void setImage(Image image) {
    this.image = image;
  }

  public void setLocal_epoch(String local_epoch) {
    this.local_epoch = local_epoch;
  }

  public void setLocal_time_rfc822(String local_time_rfc822) {
    this.local_time_rfc822 = local_time_rfc822;
  }

  public void setLocal_tz_long(String local_tz_long) {
    this.local_tz_long = local_tz_long;
  }

  public void setLocal_tz_offset(String local_tz_offset) {
    this.local_tz_offset = local_tz_offset;
  }

  public void setLocal_tz_short(String local_tz_short) {
    this.local_tz_short = local_tz_short;
  }

  public void setOb_url(String ob_url) {
    this.ob_url = ob_url;
  }

  public void setObservation_epoch(String observation_epoch) {
    this.observation_epoch = observation_epoch;
  }

  public void setObservation_location(Observation_location observation_location) {
    this.observation_location = observation_location;
  }

  public void setObservation_time(String observation_time) {
    this.observation_time = observation_time;
  }

  public void setObservation_time_rfc822(String observation_time_rfc822) {
    this.observation_time_rfc822 = observation_time_rfc822;
  }

  public void setPrecip_1hr_in(String precip_1hr_in) {
    this.precip_1hr_in = precip_1hr_in;
  }

  public void setPrecip_1hr_metric(String precip_1hr_metric) {
    this.precip_1hr_metric = precip_1hr_metric;
  }

  public void setPrecip_1hr_string(String precip_1hr_string) {
    this.precip_1hr_string = precip_1hr_string;
  }

  public void setPrecip_today_in(String precip_today_in) {
    this.precip_today_in = precip_today_in;
  }

  public void setPrecip_today_metric(String precip_today_metric) {
    this.precip_today_metric = precip_today_metric;
  }

  public void setPrecip_today_string(String precip_today_string) {
    this.precip_today_string = precip_today_string;
  }

  public void setPressure_in(String pressure_in) {
    this.pressure_in = pressure_in;
  }

  public void setPressure_mb(String pressure_mb) {
    this.pressure_mb = pressure_mb;
  }

  public void setPressure_trend(String pressure_trend) {
    this.pressure_trend = pressure_trend;
  }

  public void setRelative_humidity(String relative_humidity) {
    this.relative_humidity = relative_humidity;
  }

  public void setSolarradiation(String solarradiation) {
    this.solarradiation = solarradiation;
  }

  public void setStation_id(String station_id) {
    this.station_id = station_id;
  }

  public void setTemp_c(Number temp_c) {
    this.temp_c = temp_c;
  }

  public void setTemp_f(Number temp_f) {
    this.temp_f = temp_f;
  }

  public void setTemperature_string(String temperature_string) {
    this.temperature_string = temperature_string;
  }

  public void setUV(String UV) {
    this.UV = UV;
  }

  public void setVisibility_km(String visibility_km) {
    this.visibility_km = visibility_km;
  }

  public void setVisibility_mi(String visibility_mi) {
    this.visibility_mi = visibility_mi;
  }

  public void setWeather(String weather) {
    this.weather = weather;
  }

  public void setWind_degrees(Number wind_degrees) {
    this.wind_degrees = wind_degrees;
  }

  public void setWind_dir(String wind_dir) {
    this.wind_dir = wind_dir;
  }

  public void setWind_gust_kph(String wind_gust_kph) {
    this.wind_gust_kph = wind_gust_kph;
  }

  public void setWind_gust_mph(String wind_gust_mph) {
    this.wind_gust_mph = wind_gust_mph;
  }

  public void setWind_kph(Number wind_kph) {
    this.wind_kph = wind_kph;
  }

  public void setWind_mph(Number wind_mph) {
    this.wind_mph = wind_mph;
  }

  public void setWind_string(String wind_string) {
    this.wind_string = wind_string;
  }

  public void setWindchill_c(String windchill_c) {
    this.windchill_c = windchill_c;
  }

  public void setWindchill_f(String windchill_f) {
    this.windchill_f = windchill_f;
  }

  public void setWindchill_string(String windchill_string) {
    this.windchill_string = windchill_string;
  }
}
