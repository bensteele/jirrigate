package org.bensteele.jirrigate.weather.weatherunderground;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import jline.internal.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bensteele.jirrigate.weather.WeatherStation;
import org.bensteele.jirrigate.weather.weatherunderground.response.Forecastday;
import org.bensteele.jirrigate.weather.weatherunderground.response.WeatherUndergroundForecastResponse;
import org.bensteele.jirrigate.weather.weatherunderground.response.WeatherUndergroundResponse;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

/**
 * A WeatherUnderground implementation of a {@link WeatherStation}. Designed to be used with
 * Personal Weather Stations (PWS) that upload data to WeatherUnderground. An API account is
 * required to get the API key to use this class, the PWS Station ID can be your own or one close by
 * that suits.
 * <p>
 * The {@link WeatherUndergroundResponse} and {@link WeatherUndergroundForecastResponse} that this
 * class produces were generated through a simple JSON to POJO generator and not intended for
 * modification.
 * <p>
 * {@link http://www.wunderground.com/weather/api/}
 * 
 * @author Ben Steele (ben@bensteele.org)
 */
public class WeatherUndergroundStation implements WeatherStation {

  private final String apiKey;
  private final String stationId;
  private final String url;
  protected final List<WeatherUndergroundResponse> responses = Collections
      .synchronizedList(new ArrayList<WeatherUndergroundResponse>());
  private final AtomicReference<WeatherUndergroundForecastResponse> forecastResponse = new AtomicReference<WeatherUndergroundForecastResponse>();
  private final ExecutorService requestExecutor = Executors.newSingleThreadExecutor();
  private final String name;
  private final DecimalFormat twoDecimals = new DecimalFormat("###.##");
  private boolean isActive;

  // Enough for 14 days of responses at 10 minute intervals.
  private static final int MAX_RESPONSES_SIZE = 2000;

  public WeatherUndergroundStation(String name, String apiKey, String stationId) {
    this(name, apiKey, stationId, "http://api.wunderground.com");
  }

  public WeatherUndergroundStation(final String name, String apiKey, String stationId, String url) {
    this.name = name;
    this.apiKey = apiKey;
    this.stationId = stationId;
    this.url = url;
    this.isActive = true;

    // Fetch new weather data from wunderground every 15 minutes.
    requestExecutor.execute(new Runnable() {
      @Override
      public void run() {
        final int SLEEP_15_MINUTES_IN_MS = (1000 * 60) * 15;
        while (true) {
          try {
            if (isActive()) {
              if (responses.size() > MAX_RESPONSES_SIZE) {
                responses.remove(responses.size() - 1);
              }
              // Try getting the current data, if that's successful then go for the forecast as
              // well.
              WeatherUndergroundResponse r = fetchCurrentWeatherData();
              if (r != null) {
                responses.add(0, r);
                WeatherUndergroundForecastResponse fr = fetchForecastWeatherData();
                if (fr != null) {
                  forecastResponse.set(fr);
                }
              }
            }
            Thread.sleep(SLEEP_15_MINUTES_IN_MS);
          } catch (ClientProtocolException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    });
  }

  protected WeatherUndergroundResponse fetchCurrentWeatherData() throws ClientProtocolException,
      IOException {
    final String wundergroundUrl = url + "/api/" + apiKey + "/conditions/q/pws:" + stationId
        + ".json";

    final String response = sendHttpGet(wundergroundUrl);
    WeatherUndergroundResponse wundergroundResponse = new WeatherUndergroundResponse();
    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.readerForUpdating(wundergroundResponse).readValue(response);
    } catch (UnrecognizedPropertyException e) {
      // TODO (bensteele) log this as we should understand why this is happening.
      return null;
    }
    return wundergroundResponse;
  }

  protected WeatherUndergroundForecastResponse fetchForecastWeatherData()
      throws ClientProtocolException, IOException {
    final String wundergroundUrl = url + "/api/" + apiKey + "/forecast10day/q/pws:" + stationId
        + ".json";

    final String response = sendHttpGet(wundergroundUrl);
    WeatherUndergroundForecastResponse wundergroundResponse = new WeatherUndergroundForecastResponse();
    ObjectMapper mapper = new ObjectMapper();
    // try {
    mapper.readerForUpdating(wundergroundResponse).readValue(response);
    // } catch (UnrecognizedPropertyException e) {
    // TODO (bensteele) log this as we should understand why this is happening.
    // return null;
    // }
    return wundergroundResponse;
  }

  @Override
  public double getCurrentRelativeHumidityPercentage() {
    if (!responses.isEmpty()) {
      // wunderground response looks like "24%"
      String[] humidity = responses.get(0).getCurrent_observation().getRelative_humidity()
          .split("%");
      return Double.parseDouble(humidity[0]);
    }
    return -1000;
  }

  @Override
  public double getCurrentTemperatureCelcius() {
    if (!responses.isEmpty()) {
      return responses.get(0).getCurrent_observation().getTemp_c().doubleValue();
    }
    return -1000;
  }

  @Override
  public double getCurrentTemperatureFahrenheit() {
    if (!responses.isEmpty()) {
      return responses.get(0).getCurrent_observation().getTemp_f().doubleValue();
    }
    return -1000;
  }

  @Override
  public double getCurrentWindspeedKiloMetresPerHour() {
    if (!responses.isEmpty()) {
      return responses.get(0).getCurrent_observation().getWind_kph().doubleValue();
    }
    return -1000;
  }

  @Override
  public double getCurrentWindspeedMilesPerHour() {
    if (!responses.isEmpty()) {
      return responses.get(0).getCurrent_observation().getWind_mph().doubleValue();
    }
    return -1000;
  }

  protected WeatherUndergroundForecastResponse getForecastResponse() {
    return forecastResponse.get();
  }

  @Override
  public double getLastXDaysAvgTemperatureCelcius(int days) {
    // Don't let a value that can't be supported slip through.
    if (days > 14) {
      days = 14;
    }
    DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMM yyyy");
    LocalDate today = new LocalDate();

    double totalTemp = 0.0;
    int daysFound = 0;
    for (int i = 0; i < days; i++) {
      double dayTemp = 0.0;
      int count = 0;
      for (WeatherUndergroundResponse r : responses) {
        if (r.getCurrent_observation().getObservation_time_rfc822().contains(fmt.print(today))) {
          dayTemp += r.getCurrent_observation().getTemp_c().doubleValue();
          count++;
        }
      }
      if (count > 0) {
        daysFound++;
      }
      totalTemp += (dayTemp / count);
      today.minusDays(1);
    }

    // Incase we don't have as many results as we are asked to go back we cap it at the days we can
    // go back so the average result still looks sane.
    if (daysFound < days) {
      days = daysFound;
    }

    double avgTemp = (totalTemp / days);
    return Double.parseDouble(twoDecimals.format(avgTemp));
  }

  @Override
  public double getLastXDaysAvgTemperatureFahrenheit(int days) {
    // Don't let a value that can't be supported slip through.
    if (days > 14) {
      days = 14;
    }
    DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMM yyyy");
    LocalDate today = new LocalDate();

    double totalTemp = 0.0;
    int daysFound = 0;
    for (int i = 0; i < days; i++) {
      double dayTemp = 0.0;
      int count = 0;
      for (WeatherUndergroundResponse r : responses) {
        if (r.getCurrent_observation().getObservation_time_rfc822().contains(fmt.print(today))) {
          dayTemp += r.getCurrent_observation().getTemp_f().doubleValue();
          count++;
        }
      }
      if (count > 0) {
        daysFound++;
      }
      totalTemp += (dayTemp / count);
      today.minusDays(1);
    }

    // Incase we don't have as many results as we are asked to go back we cap it at the days we can
    // go back so the average result still looks sane.
    if (daysFound < days) {
      days = daysFound;
    }

    double avgTemp = (totalTemp / days);
    return Double.parseDouble(twoDecimals.format(avgTemp));
  }

  @Override
  public double getLastXDaysRainfallInches(int days) {
    // Don't let a value that can't be supported slip through.
    if (days > 14) {
      days = 14;
    }
    DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMM yyyy");
    LocalDate today = new LocalDate();
    double rainfall = 0.0;
    int count = 0;
    for (WeatherUndergroundResponse r : responses) {
      if (r.getCurrent_observation().getObservation_time_rfc822().contains(fmt.print(today))) {
        rainfall += Double.parseDouble(r.getCurrent_observation().getPrecip_today_in());
        today.minusDays(1);
        count++;
      }
      if (count == days) {
        break;
      }
    }
    return rainfall;
  }

  @Override
  public double getLastXDaysRainfallMilliLitres(int days) {
    // Don't let a value that can't be supported slip through.
    if (days > 14) {
      days = 14;
    }
    DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMM yyyy");
    LocalDate today = new LocalDate();

    double rainfall = 0.0;
    int count = 0;
    for (WeatherUndergroundResponse r : responses) {
      if (r.getCurrent_observation().getObservation_time_rfc822().contains(fmt.print(today))) {
        rainfall += Double.parseDouble(r.getCurrent_observation().getPrecip_today_metric());
        today.minusDays(1);
        count++;
      }
      if (count == days) {
        break;
      }
    }
    return rainfall;
  }

  @Override
  public double getMaxTemperatureCelcius() {
    double maxTemp = -1000.0;
    for (WeatherUndergroundResponse r : responses) {
      if (r.getCurrent_observation().getTemp_c().doubleValue() > maxTemp) {
        maxTemp = r.getCurrent_observation().getTemp_c().doubleValue();
      }
    }
    return maxTemp;
  }

  @Override
  public double getMaxTemperatureFahrenheit() {
    double maxTemp = -1000.0;
    for (WeatherUndergroundResponse r : responses) {
      if (r.getCurrent_observation().getTemp_f().doubleValue() > maxTemp) {
        maxTemp = r.getCurrent_observation().getTemp_f().doubleValue();
      }
    }
    return maxTemp;
  }

  @Override
  public double getMinTemperatureCelcius() {
    double minTemp = 1000.0;
    for (WeatherUndergroundResponse r : responses) {
      if (r.getCurrent_observation().getTemp_c().doubleValue() < minTemp) {
        minTemp = r.getCurrent_observation().getTemp_c().doubleValue();
      }
    }
    return minTemp;
  }

  @Override
  public double getMinTemperatureFahrenheit() {
    double minTemp = 1000.0;
    for (WeatherUndergroundResponse r : responses) {
      if (r.getCurrent_observation().getTemp_f().doubleValue() < minTemp) {
        minTemp = r.getCurrent_observation().getTemp_f().doubleValue();
      }
    }
    return minTemp;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public int getNextXDaysPercentageOfPrecipitation(int days) {
    if (forecastResponse.get() == null) {
      return 0;
    }
    // Don't let a value that can't be supported slip through.
    if (days > 10) {
      days = 10;
    }

    // The forecast is delivered in periods, a period is half a day (day and night).
    int periods = days * 2;

    int pop = 0;
    for (int i = 0; i < periods; i++) {
      for (Forecastday day : forecastResponse.get().getForecast().getTxt_forecast()
          .getForecastday()) {
        if (day.getPeriod().intValue() == i) {
          if (day.getPop().intValue() > pop) {
            pop = day.getPop().intValue();
          }
        }
      }
    }
    return pop;
  }

  protected List<WeatherUndergroundResponse> getResponses() {
    return this.responses;
  }

  @Override
  public String getStatus() {
    if (!responses.isEmpty()) {
      return responses.get(0).getCurrent_observation().getObservation_time();
    } else {
      final String wundergroundUrl = url + "/api/" + apiKey + "/conditions/q/pws:" + stationId
          + ".json";
      return "No valid responses received from Weather Underground. The URL I am trying is:\n"
          + wundergroundUrl;
    }
  }

  @Override
  public double getTodaysRainfallInches() {
    if (!responses.isEmpty()) {
      return Double.parseDouble(responses.get(0).getCurrent_observation().getPrecip_today_in());
    }
    return -1000;
  }

  @Override
  public double getTodaysRainfallMilliLitres() {
    if (!responses.isEmpty()) {
      return Double.parseDouble(responses.get(0).getCurrent_observation().getPrecip_today_metric());
    }
    return -1000;
  }

  @Override
  public WeatherStationType getType() {
    return WeatherStationType.WUNDERGROUND;
  }

  protected String getUrl() {
    return url;
  }

  @Override
  public boolean isActive() {
    return this.isActive;
  }

  /**
   * Generic "helper" method to send a HTTP GET to a specified URL.
   * 
   * @param url
   *          The full URL that you wish to send this request to.
   * @return The body of the reply line by line in a List.
   * @throws IOException
   *           If something goes wrong with the request.
   */
  private String sendHttpGet(String url) throws ClientProtocolException, IOException {
    // Set the timeout to 10 seconds.
    final int HTTP_TIMEOUT = 10000;
    CloseableHttpClient httpclient = HttpClients
        .custom()
        .setDefaultRequestConfig(
            RequestConfig.custom().setSocketTimeout(HTTP_TIMEOUT)
                .setConnectionRequestTimeout(HTTP_TIMEOUT).setConnectTimeout(HTTP_TIMEOUT).build())
        .build();

    try {
      HttpGet httpget = new HttpGet(url);

      ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

        @Override
        public String handleResponse(final HttpResponse response) throws ClientProtocolException,
            IOException {
          int status = response.getStatusLine().getStatusCode();
          if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
          } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
          }
        }

      };
      String responseBody = httpclient.execute(httpget, responseHandler);

      return responseBody;

    } catch (Exception e) {
      Log.error(name
          + " timed out trying to fetch data from Weather Underground; affected URL was: " + url);
      return "";
    } finally {
      httpclient.close();
    }
  }

  @Override
  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }

  @Override
  public DateTime getNewestRecordTime() {
    int epoch = 0;
    for (WeatherUndergroundResponse r : responses) {
      if (Integer.parseInt(r.getCurrent_observation().getObservation_epoch()) > epoch) {
        epoch = Integer.parseInt(r.getCurrent_observation().getObservation_epoch());
      }
    }
    return new DateTime((long) epoch * 1000);
  }

  @Override
  public DateTime getOldestRecordTime() {
    int epoch = Integer.MAX_VALUE;
    for (WeatherUndergroundResponse r : responses) {
      if (Integer.parseInt(r.getCurrent_observation().getObservation_epoch()) < epoch) {
        epoch = Integer.parseInt(r.getCurrent_observation().getObservation_epoch());
      }
    }
    return new DateTime((long) epoch * 1000);
  }

  @Override
  public int getNumberOfRecords() {
    return this.responses.size();
  }
}
