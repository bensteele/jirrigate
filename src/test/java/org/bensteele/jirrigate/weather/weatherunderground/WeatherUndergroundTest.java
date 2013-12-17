package org.bensteele.jirrigate.weather.weatherunderground;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.bensteele.jirrigate.weather.weatherunderground.response.WeatherUndergroundResponse;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * Tests for the {@link WeatherUndergroundStation}.
 * 
 * @author Ben Steele (ben@bensteele.org)
 */
public class WeatherUndergroundTest {

  @ClassRule
  public static WireMockRule wireMockRule = new WireMockRule(8888);

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    final String apiKey = "abcdefgh123456";
    final String stationId = "IMOUTHAU666";

    stubFor(get(urlEqualTo("/api/" + apiKey + "/conditions/q/pws:" + stationId + ".json"))
        .willReturn(aResponse().withStatus(200).withBody(RESPONSE)));

    stubFor(get(urlEqualTo("/api/" + apiKey + "/forecast10day/q/pws:" + stationId + ".json"))
        .willReturn(aResponse().withStatus(200).withBody(FORECAST_RESPONSE)));

    station = new WeatherUndergroundStation("Test Wunderground Station", apiKey, stationId,
        "http://127.0.0.1:8888");

    // wireMock HTTP server needs a little breathing space before taking connections.
    Thread.sleep(500);
  }

  @After
  public void tearDown() throws Exception {
  }

  private static WeatherUndergroundStation station;

  private final static String FORECAST_RESPONSE = "\r\n"
      + "{\r\n"
      + "  \"response\": {\r\n"
      + "  \"version\":\"0.1\",\r\n"
      + "  \"termsofService\":\"http://www.wunderground.com/weather/api/d/terms.html\",\r\n"
      + "  \"features\": {\r\n"
      + "  \"forecast10day\": 1\r\n"
      + "  }\r\n"
      + "  }\r\n"
      + "    ,\r\n"
      + "  \"forecast\":{\r\n"
      + "    \"txt_forecast\": {\r\n"
      + "    \"date\":\"10:30 AM CST\",\r\n"
      + "    \"forecastday\": [\r\n"
      + "    {\r\n"
      + "    \"period\":0,\r\n"
      + "    \"icon\":\"partlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif\",\r\n"
      + "    \"title\":\"Saturday\",\r\n"
      + "    \"fcttext\":\"Partly cloudy in the morning, then clear. High of 68F. Winds from the South at 5 to 15 mph.\",\r\n"
      + "    \"fcttext_metric\":\"Partly cloudy in the morning, then clear. High of 20C. Breezy. Winds from the South at 10 to 20 km/h.\",\r\n"
      + "    \"pop\":\"0\"\r\n"
      + "    }\r\n"
      + "    ,\r\n"
      + "    {\r\n"
      + "    \"period\":1,\r\n"
      + "    \"icon\":\"clear\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/clear.gif\",\r\n"
      + "    \"title\":\"Saturday Night\",\r\n"
      + "    \"fcttext\":\"Clear. Low of 52F. Winds from the SSE at 5 to 15 mph.\",\r\n"
      + "    \"fcttext_metric\":\"Clear. Low of 11C. Breezy. Winds from the SSE at 10 to 20 km/h.\",\r\n"
      + "    \"pop\":\"0\"\r\n"
      + "    }\r\n"
      + "    ,\r\n"
      + "    {\r\n"
      + "    \"period\":2,\r\n"
      + "    \"icon\":\"mostlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/mostlycloudy.gif\",\r\n"
      + "    \"title\":\"Sunday\",\r\n"
      + "    \"fcttext\":\"Mostly cloudy in the morning, then overcast. High of 75F. Winds from the SSE at 5 to 15 mph.\",\r\n"
      + "    \"fcttext_metric\":\"Mostly cloudy in the morning, then overcast. High of 24C. Breezy. Winds from the SSE at 10 to 20 km/h.\",\r\n"
      + "    \"pop\":\"10\"\r\n"
      + "    }\r\n"
      + "    ,\r\n"
      + "    {\r\n"
      + "    \"period\":3,\r\n"
      + "    \"icon\":\"partlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif\",\r\n"
      + "    \"title\":\"Sunday Night\",\r\n"
      + "    \"fcttext\":\"Overcast in the evening, then clear. Low of 55F. Winds from the SSE at 5 to 15 mph.\",\r\n"
      + "    \"fcttext_metric\":\"Overcast in the evening, then clear. Low of 13C. Breezy. Winds from the SSE at 10 to 20 km/h.\",\r\n"
      + "    \"pop\":\"0\"\r\n"
      + "    }\r\n"
      + "    ,\r\n"
      + "    {\r\n"
      + "    \"period\":4,\r\n"
      + "    \"icon\":\"clear\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/clear.gif\",\r\n"
      + "    \"title\":\"Monday\",\r\n"
      + "    \"fcttext\":\"Clear. High of 81F. Winds from the SSE at 5 to 10 mph.\",\r\n"
      + "    \"fcttext_metric\":\"Clear. High of 27C. Breezy. Winds from the SSE at 10 to 20 km/h.\",\r\n"
      + "    \"pop\":\"0\"\r\n"
      + "    }\r\n"
      + "    ,\r\n"
      + "    {\r\n"
      + "    \"period\":5,\r\n"
      + "    \"icon\":\"clear\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/clear.gif\",\r\n"
      + "    \"title\":\"Monday Night\",\r\n"
      + "    \"fcttext\":\"Clear. Low of 59F. Winds from the SE at 5 to 15 mph.\",\r\n"
      + "    \"fcttext_metric\":\"Clear. Low of 15C. Breezy. Winds from the SE at 10 to 20 km/h.\",\r\n"
      + "    \"pop\":\"0\"\r\n"
      + "    }\r\n"
      + "    ,\r\n"
      + "    {\r\n"
      + "    \"period\":6,\r\n"
      + "    \"icon\":\"clear\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/clear.gif\",\r\n"
      + "    \"title\":\"Tuesday\",\r\n"
      + "    \"fcttext\":\"Clear. High of 86F. Winds from the SE at 5 to 10 mph.\",\r\n"
      + "    \"fcttext_metric\":\"Clear. High of 30C. Winds from the SE at 10 to 15 km/h.\",\r\n"
      + "    \"pop\":\"5\"\r\n"
      + "    }\r\n"
      + "    ,\r\n"
      + "    {\r\n"
      + "    \"period\":7,\r\n"
      + "    \"icon\":\"clear\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/clear.gif\",\r\n"
      + "    \"title\":\"Tuesday Night\",\r\n"
      + "    \"fcttext\":\"Clear. Low of 68F. Winds from the ESE at 5 to 10 mph.\",\r\n"
      + "    \"fcttext_metric\":\"Clear. Low of 20C. Winds from the ESE at 10 to 15 km/h.\",\r\n"
      + "    \"pop\":\"0\"\r\n"
      + "    }\r\n"
      + "    ,\r\n"
      + "    {\r\n"
      + "    \"period\":8,\r\n"
      + "    \"icon\":\"partlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif\",\r\n"
      + "    \"title\":\"Wednesday\",\r\n"
      + "    \"fcttext\":\"Clear in the morning, then partly cloudy. High of 97F. Winds from the ENE at 5 to 15 mph shifting to the WNW in the afternoon.\",\r\n"
      + "    \"fcttext_metric\":\"Clear in the morning, then partly cloudy. High of 36C. Breezy. Winds from the ENE at 10 to 20 km/h shifting to the WNW in the afternoon.\",\r\n"
      + "    \"pop\":\"0\"\r\n"
      + "    }\r\n"
      + "    ,\r\n"
      + "    {\r\n"
      + "    \"period\":9,\r\n"
      + "    \"icon\":\"partlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif\",\r\n"
      + "    \"title\":\"Wednesday Night\",\r\n"
      + "    \"fcttext\":\"Partly cloudy. Low of 75F. Winds from the SSE at 5 to 10 mph shifting to the ENE after midnight.\",\r\n"
      + "    \"fcttext_metric\":\"Partly cloudy. Low of 24C. Winds from the SSE at 10 to 15 km/h shifting to the ENE after midnight.\",\r\n"
      + "    \"pop\":\"0\"\r\n"
      + "    }\r\n"
      + "    ,\r\n"
      + "    {\r\n"
      + "    \"period\":10,\r\n"
      + "    \"icon\":\"partlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif\",\r\n"
      + "    \"title\":\"Thursday\",\r\n"
      + "    \"fcttext\":\"Partly cloudy. High of 86F. Winds from the NNE at 5 to 15 mph.\",\r\n"
      + "    \"fcttext_metric\":\"Partly cloudy. High of 30C. Breezy. Winds from the NNE at 10 to 20 km/h.\",\r\n"
      + "    \"pop\":\"0\"\r\n"
      + "    }\r\n"
      + "    ,\r\n"
      + "    {\r\n"
      + "    \"period\":11,\r\n"
      + "    \"icon\":\"partlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif\",\r\n"
      + "    \"title\":\"Thursday Night\",\r\n"
      + "    \"fcttext\":\"Partly cloudy with a chance of rain. Low of 68F. Winds from the NNE at 5 to 10 mph. Chance of rain 20%.\",\r\n"
      + "    \"fcttext_metric\":\"Partly cloudy with a chance of rain. Low of 20C. Winds from the NNE at 10 to 15 km/h.\",\r\n"
      + "    \"pop\":\"20\"\r\n"
      + "    }\r\n"
      + "    ,\r\n"
      + "    {\r\n"
      + "    \"period\":12,\r\n"
      + "    \"icon\":\"partlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif\",\r\n"
      + "    \"title\":\"Friday\",\r\n"
      + "    \"fcttext\":\"Partly cloudy. High of 86F. Breezy. Winds from the North at 10 to 20 mph shifting to the West in the afternoon.\",\r\n"
      + "    \"fcttext_metric\":\"Partly cloudy. High of 30C. Windy. Winds from the North at 20 to 30 km/h shifting to the West in the afternoon.\",\r\n"
      + "    \"pop\":\"0\"\r\n"
      + "    }\r\n"
      + "    ,\r\n"
      + "    {\r\n"
      + "    \"period\":13,\r\n"
      + "    \"icon\":\"chancerain\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/chancerain.gif\",\r\n"
      + "    \"title\":\"Friday Night\",\r\n"
      + "    \"fcttext\":\"Overcast with a chance of rain. Fog overnight. Low of 61F. Winds from the South at 10 to 15 mph. Chance of rain 40%.\",\r\n"
      + "    \"fcttext_metric\":\"Overcast with a chance of rain. Fog overnight. Low of 16C. Breezy. Winds from the South at 15 to 20 km/h. Chance of rain 40%.\",\r\n"
      + "    \"pop\":\"40\"\r\n"
      + "    }\r\n"
      + "    ,\r\n"
      + "    {\r\n"
      + "    \"period\":14,\r\n"
      + "    \"icon\":\"mostlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/mostlycloudy.gif\",\r\n"
      + "    \"title\":\"Saturday\",\r\n"
      + "    \"fcttext\":\"Overcast. High of 70F. Winds from the South at 10 to 15 mph.\",\r\n"
      + "    \"fcttext_metric\":\"Overcast. High of 21C. Breezy. Winds from the South at 15 to 25 km/h.\",\r\n"
      + "    \"pop\":\"50\"\r\n"
      + "    }\r\n"
      + "    ,\r\n"
      + "    {\r\n"
      + "    \"period\":15,\r\n"
      + "    \"icon\":\"mostlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/mostlycloudy.gif\",\r\n"
      + "    \"title\":\"Saturday Night\",\r\n"
      + "    \"fcttext\":\"Overcast. Low of 54F. Winds from the SSE at 10 to 15 mph.\",\r\n"
      + "    \"fcttext_metric\":\"Overcast. Low of 12C. Breezy. Winds from the SSE at 15 to 25 km/h.\",\r\n"
      + "    \"pop\":\"0\"\r\n"
      + "    }\r\n"
      + "    ,\r\n"
      + "    {\r\n"
      + "    \"period\":16,\r\n"
      + "    \"icon\":\"partlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif\",\r\n"
      + "    \"title\":\"Sunday\",\r\n"
      + "    \"fcttext\":\"Partly cloudy. High of 70F. Winds from the SSE at 5 to 15 mph.\",\r\n"
      + "    \"fcttext_metric\":\"Partly cloudy. High of 21C. Breezy. Winds from the SSE at 10 to 20 km/h.\",\r\n"
      + "    \"pop\":\"0\"\r\n"
      + "    }\r\n"
      + "    ,\r\n"
      + "    {\r\n"
      + "    \"period\":17,\r\n"
      + "    \"icon\":\"partlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif\",\r\n"
      + "    \"title\":\"Sunday Night\",\r\n"
      + "    \"fcttext\":\"Partly cloudy. Low of 57F. Winds from the SSE at 5 to 10 mph.\",\r\n"
      + "    \"fcttext_metric\":\"Partly cloudy. Low of 14C. Winds from the SSE at 5 to 15 km/h.\",\r\n"
      + "    \"pop\":\"0\"\r\n" + "    }\r\n" + "    ,\r\n" + "    {\r\n"
      + "    \"period\":18,\r\n" + "    \"icon\":\"partlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif\",\r\n"
      + "    \"title\":\"Monday\",\r\n"
      + "    \"fcttext\":\"Partly cloudy. High of 72F. Winds less than 5 mph.\",\r\n"
      + "    \"fcttext_metric\":\"Partly cloudy. High of 22C. Winds less than 5 km/h.\",\r\n"
      + "    \"pop\":\"0\"\r\n" + "    }\r\n" + "    ,\r\n" + "    {\r\n"
      + "    \"period\":19,\r\n" + "    \"icon\":\"partlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif\",\r\n"
      + "    \"title\":\"Monday Night\",\r\n"
      + "    \"fcttext\":\"Partly cloudy. Low of 59F. Winds less than 5 mph.\",\r\n"
      + "    \"fcttext_metric\":\"Partly cloudy. Low of 15C. Winds less than 5 km/h.\",\r\n"
      + "    \"pop\":\"0\"\r\n" + "    }\r\n" + "    ]\r\n" + "    },\r\n"
      + "    \"simpleforecast\": {\r\n" + "    \"forecastday\": [\r\n" + "    {\"date\":{\r\n"
      + "  \"epoch\":\"1387022400\",\r\n"
      + "  \"pretty\":\"10:30 PM CST on December 14, 2013\",\r\n" + "  \"day\":14,\r\n"
      + "  \"month\":12,\r\n" + "  \"year\":2013,\r\n" + "  \"yday\":347,\r\n"
      + "  \"hour\":22,\r\n" + "  \"min\":\"30\",\r\n" + "  \"sec\":0,\r\n"
      + "  \"isdst\":\"1\",\r\n" + "  \"monthname\":\"December\",\r\n"
      + "  \"monthname_short\":\"Dec\",\r\n" + "  \"weekday_short\":\"Sat\",\r\n"
      + "  \"weekday\":\"Saturday\",\r\n" + "  \"ampm\":\"PM\",\r\n"
      + "  \"tz_short\":\"CST\",\r\n" + "  \"tz_long\":\"Australia/Adelaide\"\r\n" + "},\r\n"
      + "    \"period\":1,\r\n" + "    \"high\": {\r\n" + "    \"fahrenheit\":\"68\",\r\n"
      + "    \"celsius\":\"20\"\r\n" + "    },\r\n" + "    \"low\": {\r\n"
      + "    \"fahrenheit\":\"52\",\r\n" + "    \"celsius\":\"11\"\r\n" + "    },\r\n"
      + "    \"conditions\":\"Partly Cloudy\",\r\n" + "    \"icon\":\"partlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif\",\r\n"
      + "    \"skyicon\":\"sunny\",\r\n" + "    \"pop\":0,\r\n" + "    \"qpf_allday\": {\r\n"
      + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n" + "    },\r\n" + "    \"qpf_day\": {\r\n"
      + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n" + "    },\r\n" + "    \"qpf_night\": {\r\n"
      + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n" + "    },\r\n"
      + "    \"snow_allday\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n" + "    },\r\n"
      + "    \"snow_day\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n" + "    },\r\n"
      + "    \"snow_night\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n" + "    },\r\n"
      + "    \"maxwind\": {\r\n" + "    \"mph\": 12,\r\n" + "    \"kph\": 19,\r\n"
      + "    \"dir\": \"South\",\r\n" + "    \"degrees\": 171\r\n" + "    },\r\n"
      + "    \"avewind\": {\r\n" + "    \"mph\": 10,\r\n" + "    \"kph\": 16,\r\n"
      + "    \"dir\": \"South\",\r\n" + "    \"degrees\": 177\r\n" + "    },\r\n"
      + "    \"avehumidity\": 77,\r\n" + "    \"maxhumidity\": 87,\r\n"
      + "    \"minhumidity\": 58\r\n" + "    }\r\n" + "    ,\r\n" + "    {\"date\":{\r\n"
      + "  \"epoch\":\"1387108800\",\r\n"
      + "  \"pretty\":\"10:30 PM CST on December 15, 2013\",\r\n" + "  \"day\":15,\r\n"
      + "  \"month\":12,\r\n" + "  \"year\":2013,\r\n" + "  \"yday\":348,\r\n"
      + "  \"hour\":22,\r\n" + "  \"min\":\"30\",\r\n" + "  \"sec\":0,\r\n"
      + "  \"isdst\":\"1\",\r\n" + "  \"monthname\":\"December\",\r\n"
      + "  \"monthname_short\":\"Dec\",\r\n" + "  \"weekday_short\":\"Sun\",\r\n"
      + "  \"weekday\":\"Sunday\",\r\n" + "  \"ampm\":\"PM\",\r\n" + "  \"tz_short\":\"CST\",\r\n"
      + "  \"tz_long\":\"Australia/Adelaide\"\r\n" + "},\r\n" + "    \"period\":2,\r\n"
      + "    \"high\": {\r\n" + "    \"fahrenheit\":\"75\",\r\n" + "    \"celsius\":\"24\"\r\n"
      + "    },\r\n" + "    \"low\": {\r\n" + "    \"fahrenheit\":\"55\",\r\n"
      + "    \"celsius\":\"13\"\r\n" + "    },\r\n" + "    \"conditions\":\"Mostly Cloudy\",\r\n"
      + "    \"icon\":\"mostlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/mostlycloudy.gif\",\r\n"
      + "    \"skyicon\":\"mostlycloudy\",\r\n" + "    \"pop\":0,\r\n"
      + "    \"qpf_allday\": {\r\n" + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n"
      + "    },\r\n" + "    \"qpf_day\": {\r\n" + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n"
      + "    },\r\n" + "    \"qpf_night\": {\r\n" + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n"
      + "    },\r\n" + "    \"snow_allday\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n"
      + "    },\r\n" + "    \"snow_day\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n"
      + "    },\r\n" + "    \"snow_night\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n"
      + "    },\r\n" + "    \"maxwind\": {\r\n" + "    \"mph\": 12,\r\n" + "    \"kph\": 19,\r\n"
      + "    \"dir\": \"South\",\r\n" + "    \"degrees\": 186\r\n" + "    },\r\n"
      + "    \"avewind\": {\r\n" + "    \"mph\": 10,\r\n" + "    \"kph\": 16,\r\n"
      + "    \"dir\": \"South\",\r\n" + "    \"degrees\": 172\r\n" + "    },\r\n"
      + "    \"avehumidity\": 75,\r\n" + "    \"maxhumidity\": 88,\r\n"
      + "    \"minhumidity\": 51\r\n" + "    }\r\n" + "    ,\r\n" + "    {\"date\":{\r\n"
      + "  \"epoch\":\"1387195200\",\r\n"
      + "  \"pretty\":\"10:30 PM CST on December 16, 2013\",\r\n" + "  \"day\":16,\r\n"
      + "  \"month\":12,\r\n" + "  \"year\":2013,\r\n" + "  \"yday\":349,\r\n"
      + "  \"hour\":22,\r\n" + "  \"min\":\"30\",\r\n" + "  \"sec\":0,\r\n"
      + "  \"isdst\":\"1\",\r\n" + "  \"monthname\":\"December\",\r\n"
      + "  \"monthname_short\":\"Dec\",\r\n" + "  \"weekday_short\":\"Mon\",\r\n"
      + "  \"weekday\":\"Monday\",\r\n" + "  \"ampm\":\"PM\",\r\n" + "  \"tz_short\":\"CST\",\r\n"
      + "  \"tz_long\":\"Australia/Adelaide\"\r\n" + "},\r\n" + "    \"period\":3,\r\n"
      + "    \"high\": {\r\n" + "    \"fahrenheit\":\"81\",\r\n" + "    \"celsius\":\"27\"\r\n"
      + "    },\r\n" + "    \"low\": {\r\n" + "    \"fahrenheit\":\"59\",\r\n"
      + "    \"celsius\":\"15\"\r\n" + "    },\r\n" + "    \"conditions\":\"Clear\",\r\n"
      + "    \"icon\":\"clear\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/clear.gif\",\r\n"
      + "    \"skyicon\":\"sunny\",\r\n" + "    \"pop\":0,\r\n" + "    \"qpf_allday\": {\r\n"
      + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n" + "    },\r\n" + "    \"qpf_day\": {\r\n"
      + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n" + "    },\r\n" + "    \"qpf_night\": {\r\n"
      + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n" + "    },\r\n"
      + "    \"snow_allday\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n" + "    },\r\n"
      + "    \"snow_day\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n" + "    },\r\n"
      + "    \"snow_night\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n" + "    },\r\n"
      + "    \"maxwind\": {\r\n" + "    \"mph\": 11,\r\n" + "    \"kph\": 18,\r\n"
      + "    \"dir\": \"South\",\r\n" + "    \"degrees\": 169\r\n" + "    },\r\n"
      + "    \"avewind\": {\r\n" + "    \"mph\": 9,\r\n" + "    \"kph\": 14,\r\n"
      + "    \"dir\": \"South\",\r\n" + "    \"degrees\": 169\r\n" + "    },\r\n"
      + "    \"avehumidity\": 70,\r\n" + "    \"maxhumidity\": 84,\r\n"
      + "    \"minhumidity\": 44\r\n" + "    }\r\n" + "    ,\r\n" + "    {\"date\":{\r\n"
      + "  \"epoch\":\"1387281600\",\r\n"
      + "  \"pretty\":\"10:30 PM CST on December 17, 2013\",\r\n" + "  \"day\":17,\r\n"
      + "  \"month\":12,\r\n" + "  \"year\":2013,\r\n" + "  \"yday\":350,\r\n"
      + "  \"hour\":22,\r\n" + "  \"min\":\"30\",\r\n" + "  \"sec\":0,\r\n"
      + "  \"isdst\":\"1\",\r\n" + "  \"monthname\":\"December\",\r\n"
      + "  \"monthname_short\":\"Dec\",\r\n" + "  \"weekday_short\":\"Tue\",\r\n"
      + "  \"weekday\":\"Tuesday\",\r\n" + "  \"ampm\":\"PM\",\r\n" + "  \"tz_short\":\"CST\",\r\n"
      + "  \"tz_long\":\"Australia/Adelaide\"\r\n" + "},\r\n" + "    \"period\":4,\r\n"
      + "    \"high\": {\r\n" + "    \"fahrenheit\":\"86\",\r\n" + "    \"celsius\":\"30\"\r\n"
      + "    },\r\n" + "    \"low\": {\r\n" + "    \"fahrenheit\":\"68\",\r\n"
      + "    \"celsius\":\"20\"\r\n" + "    },\r\n" + "    \"conditions\":\"Clear\",\r\n"
      + "    \"icon\":\"clear\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/clear.gif\",\r\n"
      + "    \"skyicon\":\"sunny\",\r\n" + "    \"pop\":0,\r\n" + "    \"qpf_allday\": {\r\n"
      + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n" + "    },\r\n" + "    \"qpf_day\": {\r\n"
      + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n" + "    },\r\n" + "    \"qpf_night\": {\r\n"
      + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n" + "    },\r\n"
      + "    \"snow_allday\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n" + "    },\r\n"
      + "    \"snow_day\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n" + "    },\r\n"
      + "    \"snow_night\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n" + "    },\r\n"
      + "    \"maxwind\": {\r\n" + "    \"mph\": 9,\r\n" + "    \"kph\": 14,\r\n"
      + "    \"dir\": \"SSE\",\r\n" + "    \"degrees\": 162\r\n" + "    },\r\n"
      + "    \"avewind\": {\r\n" + "    \"mph\": 7,\r\n" + "    \"kph\": 11,\r\n"
      + "    \"dir\": \"SSE\",\r\n" + "    \"degrees\": 151\r\n" + "    },\r\n"
      + "    \"avehumidity\": 56,\r\n" + "    \"maxhumidity\": 78,\r\n"
      + "    \"minhumidity\": 40\r\n" + "    }\r\n" + "    ,\r\n" + "    {\"date\":{\r\n"
      + "  \"epoch\":\"1387368000\",\r\n"
      + "  \"pretty\":\"10:30 PM CST on December 18, 2013\",\r\n" + "  \"day\":18,\r\n"
      + "  \"month\":12,\r\n" + "  \"year\":2013,\r\n" + "  \"yday\":351,\r\n"
      + "  \"hour\":22,\r\n" + "  \"min\":\"30\",\r\n" + "  \"sec\":0,\r\n"
      + "  \"isdst\":\"1\",\r\n" + "  \"monthname\":\"December\",\r\n"
      + "  \"monthname_short\":\"Dec\",\r\n" + "  \"weekday_short\":\"Wed\",\r\n"
      + "  \"weekday\":\"Wednesday\",\r\n" + "  \"ampm\":\"PM\",\r\n"
      + "  \"tz_short\":\"CST\",\r\n" + "  \"tz_long\":\"Australia/Adelaide\"\r\n" + "},\r\n"
      + "    \"period\":5,\r\n" + "    \"high\": {\r\n" + "    \"fahrenheit\":\"97\",\r\n"
      + "    \"celsius\":\"36\"\r\n" + "    },\r\n" + "    \"low\": {\r\n"
      + "    \"fahrenheit\":\"75\",\r\n" + "    \"celsius\":\"24\"\r\n" + "    },\r\n"
      + "    \"conditions\":\"Partly Cloudy\",\r\n" + "    \"icon\":\"partlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif\",\r\n"
      + "    \"skyicon\":\"partlycloudy\",\r\n" + "    \"pop\":0,\r\n"
      + "    \"qpf_allday\": {\r\n" + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n"
      + "    },\r\n" + "    \"qpf_day\": {\r\n" + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n"
      + "    },\r\n" + "    \"qpf_night\": {\r\n" + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n"
      + "    },\r\n" + "    \"snow_allday\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n"
      + "    },\r\n" + "    \"snow_day\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n"
      + "    },\r\n" + "    \"snow_night\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n"
      + "    },\r\n" + "    \"maxwind\": {\r\n" + "    \"mph\": 10,\r\n" + "    \"kph\": 16,\r\n"
      + "    \"dir\": \"NE\",\r\n" + "    \"degrees\": 49\r\n" + "    },\r\n"
      + "    \"avewind\": {\r\n" + "    \"mph\": 4,\r\n" + "    \"kph\": 6,\r\n"
      + "    \"dir\": \"SSE\",\r\n" + "    \"degrees\": 153\r\n" + "    },\r\n"
      + "    \"avehumidity\": 44,\r\n" + "    \"maxhumidity\": 52,\r\n"
      + "    \"minhumidity\": 36\r\n" + "    }\r\n" + "    ,\r\n" + "    {\"date\":{\r\n"
      + "  \"epoch\":\"1387454400\",\r\n"
      + "  \"pretty\":\"10:30 PM CST on December 19, 2013\",\r\n" + "  \"day\":19,\r\n"
      + "  \"month\":12,\r\n" + "  \"year\":2013,\r\n" + "  \"yday\":352,\r\n"
      + "  \"hour\":22,\r\n" + "  \"min\":\"30\",\r\n" + "  \"sec\":0,\r\n"
      + "  \"isdst\":\"1\",\r\n" + "  \"monthname\":\"December\",\r\n"
      + "  \"monthname_short\":\"Dec\",\r\n" + "  \"weekday_short\":\"Thu\",\r\n"
      + "  \"weekday\":\"Thursday\",\r\n" + "  \"ampm\":\"PM\",\r\n"
      + "  \"tz_short\":\"CST\",\r\n" + "  \"tz_long\":\"Australia/Adelaide\"\r\n" + "},\r\n"
      + "    \"period\":6,\r\n" + "    \"high\": {\r\n" + "    \"fahrenheit\":\"86\",\r\n"
      + "    \"celsius\":\"30\"\r\n" + "    },\r\n" + "    \"low\": {\r\n"
      + "    \"fahrenheit\":\"68\",\r\n" + "    \"celsius\":\"20\"\r\n" + "    },\r\n"
      + "    \"conditions\":\"Partly Cloudy\",\r\n" + "    \"icon\":\"partlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif\",\r\n"
      + "    \"skyicon\":\"partlycloudy\",\r\n" + "    \"pop\":0,\r\n"
      + "    \"qpf_allday\": {\r\n" + "    \"in\": 0.03,\r\n" + "    \"mm\": 0.8\r\n"
      + "    },\r\n" + "    \"qpf_day\": {\r\n" + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n"
      + "    },\r\n" + "    \"qpf_night\": {\r\n" + "    \"in\": 0.03,\r\n" + "    \"mm\": 0.8\r\n"
      + "    },\r\n" + "    \"snow_allday\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n"
      + "    },\r\n" + "    \"snow_day\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n"
      + "    },\r\n" + "    \"snow_night\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n"
      + "    },\r\n" + "    \"maxwind\": {\r\n" + "    \"mph\": 10,\r\n" + "    \"kph\": 16,\r\n"
      + "    \"dir\": \"North\",\r\n" + "    \"degrees\": 358\r\n" + "    },\r\n"
      + "    \"avewind\": {\r\n" + "    \"mph\": 6,\r\n" + "    \"kph\": 10,\r\n"
      + "    \"dir\": \"ESE\",\r\n" + "    \"degrees\": 112\r\n" + "    },\r\n"
      + "    \"avehumidity\": 53,\r\n" + "    \"maxhumidity\": 65,\r\n"
      + "    \"minhumidity\": 38\r\n" + "    }\r\n" + "    ,\r\n" + "    {\"date\":{\r\n"
      + "  \"epoch\":\"1387540800\",\r\n"
      + "  \"pretty\":\"10:30 PM CST on December 20, 2013\",\r\n" + "  \"day\":20,\r\n"
      + "  \"month\":12,\r\n" + "  \"year\":2013,\r\n" + "  \"yday\":353,\r\n"
      + "  \"hour\":22,\r\n" + "  \"min\":\"30\",\r\n" + "  \"sec\":0,\r\n"
      + "  \"isdst\":\"1\",\r\n" + "  \"monthname\":\"December\",\r\n"
      + "  \"monthname_short\":\"Dec\",\r\n" + "  \"weekday_short\":\"Fri\",\r\n"
      + "  \"weekday\":\"Friday\",\r\n" + "  \"ampm\":\"PM\",\r\n" + "  \"tz_short\":\"CST\",\r\n"
      + "  \"tz_long\":\"Australia/Adelaide\"\r\n" + "},\r\n" + "    \"period\":7,\r\n"
      + "    \"high\": {\r\n" + "    \"fahrenheit\":\"86\",\r\n" + "    \"celsius\":\"30\"\r\n"
      + "    },\r\n" + "    \"low\": {\r\n" + "    \"fahrenheit\":\"61\",\r\n"
      + "    \"celsius\":\"16\"\r\n" + "    },\r\n" + "    \"conditions\":\"Partly Cloudy\",\r\n"
      + "    \"icon\":\"partlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif\",\r\n"
      + "    \"skyicon\":\"partlycloudy\",\r\n" + "    \"pop\":0,\r\n"
      + "    \"qpf_allday\": {\r\n" + "    \"in\": 0.03,\r\n" + "    \"mm\": 0.8\r\n"
      + "    },\r\n" + "    \"qpf_day\": {\r\n" + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n"
      + "    },\r\n" + "    \"qpf_night\": {\r\n" + "    \"in\": 0.03,\r\n" + "    \"mm\": 0.8\r\n"
      + "    },\r\n" + "    \"snow_allday\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n"
      + "    },\r\n" + "    \"snow_day\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n"
      + "    },\r\n" + "    \"snow_night\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n"
      + "    },\r\n" + "    \"maxwind\": {\r\n" + "    \"mph\": 17,\r\n" + "    \"kph\": 27,\r\n"
      + "    \"dir\": \"SW\",\r\n" + "    \"degrees\": 216\r\n" + "    },\r\n"
      + "    \"avewind\": {\r\n" + "    \"mph\": 13,\r\n" + "    \"kph\": 21,\r\n"
      + "    \"dir\": \"SSW\",\r\n" + "    \"degrees\": 207\r\n" + "    },\r\n"
      + "    \"avehumidity\": 81,\r\n" + "    \"maxhumidity\": 98,\r\n"
      + "    \"minhumidity\": 33\r\n" + "    }\r\n" + "    ,\r\n" + "    {\"date\":{\r\n"
      + "  \"epoch\":\"1387627200\",\r\n"
      + "  \"pretty\":\"10:30 PM CST on December 21, 2013\",\r\n" + "  \"day\":21,\r\n"
      + "  \"month\":12,\r\n" + "  \"year\":2013,\r\n" + "  \"yday\":354,\r\n"
      + "  \"hour\":22,\r\n" + "  \"min\":\"30\",\r\n" + "  \"sec\":0,\r\n"
      + "  \"isdst\":\"1\",\r\n" + "  \"monthname\":\"December\",\r\n"
      + "  \"monthname_short\":\"Dec\",\r\n" + "  \"weekday_short\":\"Sat\",\r\n"
      + "  \"weekday\":\"Saturday\",\r\n" + "  \"ampm\":\"PM\",\r\n"
      + "  \"tz_short\":\"CST\",\r\n" + "  \"tz_long\":\"Australia/Adelaide\"\r\n" + "},\r\n"
      + "    \"period\":8,\r\n" + "    \"high\": {\r\n" + "    \"fahrenheit\":\"70\",\r\n"
      + "    \"celsius\":\"21\"\r\n" + "    },\r\n" + "    \"low\": {\r\n"
      + "    \"fahrenheit\":\"54\",\r\n" + "    \"celsius\":\"12\"\r\n" + "    },\r\n"
      + "    \"conditions\":\"Mostly Cloudy\",\r\n" + "    \"icon\":\"mostlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/mostlycloudy.gif\",\r\n"
      + "    \"skyicon\":\"cloudy\",\r\n" + "    \"pop\":0,\r\n" + "    \"qpf_allday\": {\r\n"
      + "    \"in\": 0.01,\r\n" + "    \"mm\": 0.3\r\n" + "    },\r\n" + "    \"qpf_day\": {\r\n"
      + "    \"in\": 0.01,\r\n" + "    \"mm\": 0.3\r\n" + "    },\r\n" + "    \"qpf_night\": {\r\n"
      + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n" + "    },\r\n"
      + "    \"snow_allday\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n" + "    },\r\n"
      + "    \"snow_day\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n" + "    },\r\n"
      + "    \"snow_night\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n" + "    },\r\n"
      + "    \"maxwind\": {\r\n" + "    \"mph\": 15,\r\n" + "    \"kph\": 24,\r\n"
      + "    \"dir\": \"South\",\r\n" + "    \"degrees\": 176\r\n" + "    },\r\n"
      + "    \"avewind\": {\r\n" + "    \"mph\": 13,\r\n" + "    \"kph\": 21,\r\n"
      + "    \"dir\": \"South\",\r\n" + "    \"degrees\": 173\r\n" + "    },\r\n"
      + "    \"avehumidity\": 69,\r\n" + "    \"maxhumidity\": 89,\r\n"
      + "    \"minhumidity\": 64\r\n" + "    }\r\n" + "    ,\r\n" + "    {\"date\":{\r\n"
      + "  \"epoch\":\"1387713600\",\r\n"
      + "  \"pretty\":\"10:30 PM CST on December 22, 2013\",\r\n" + "  \"day\":22,\r\n"
      + "  \"month\":12,\r\n" + "  \"year\":2013,\r\n" + "  \"yday\":355,\r\n"
      + "  \"hour\":22,\r\n" + "  \"min\":\"30\",\r\n" + "  \"sec\":0,\r\n"
      + "  \"isdst\":\"1\",\r\n" + "  \"monthname\":\"December\",\r\n"
      + "  \"monthname_short\":\"Dec\",\r\n" + "  \"weekday_short\":\"Sun\",\r\n"
      + "  \"weekday\":\"Sunday\",\r\n" + "  \"ampm\":\"PM\",\r\n" + "  \"tz_short\":\"CST\",\r\n"
      + "  \"tz_long\":\"Australia/Adelaide\"\r\n" + "},\r\n" + "    \"period\":9,\r\n"
      + "    \"high\": {\r\n" + "    \"fahrenheit\":\"70\",\r\n" + "    \"celsius\":\"21\"\r\n"
      + "    },\r\n" + "    \"low\": {\r\n" + "    \"fahrenheit\":\"57\",\r\n"
      + "    \"celsius\":\"14\"\r\n" + "    },\r\n" + "    \"conditions\":\"Partly Cloudy\",\r\n"
      + "    \"icon\":\"partlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif\",\r\n"
      + "    \"skyicon\":\"partlycloudy\",\r\n" + "    \"pop\":0,\r\n"
      + "    \"qpf_allday\": {\r\n" + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n"
      + "    },\r\n" + "    \"qpf_day\": {\r\n" + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n"
      + "    },\r\n" + "    \"qpf_night\": {\r\n" + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n"
      + "    },\r\n" + "    \"snow_allday\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n"
      + "    },\r\n" + "    \"snow_day\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n"
      + "    },\r\n" + "    \"snow_night\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n"
      + "    },\r\n" + "    \"maxwind\": {\r\n" + "    \"mph\": 11,\r\n" + "    \"kph\": 18,\r\n"
      + "    \"dir\": \"SE\",\r\n" + "    \"degrees\": 127\r\n" + "    },\r\n"
      + "    \"avewind\": {\r\n" + "    \"mph\": 7,\r\n" + "    \"kph\": 11,\r\n"
      + "    \"dir\": \"South\",\r\n" + "    \"degrees\": 173\r\n" + "    },\r\n"
      + "    \"avehumidity\": 64,\r\n" + "    \"maxhumidity\": 76,\r\n"
      + "    \"minhumidity\": 48\r\n" + "    }\r\n" + "    ,\r\n" + "    {\"date\":{\r\n"
      + "  \"epoch\":\"1387800000\",\r\n"
      + "  \"pretty\":\"10:30 PM CST on December 23, 2013\",\r\n" + "  \"day\":23,\r\n"
      + "  \"month\":12,\r\n" + "  \"year\":2013,\r\n" + "  \"yday\":356,\r\n"
      + "  \"hour\":22,\r\n" + "  \"min\":\"30\",\r\n" + "  \"sec\":0,\r\n"
      + "  \"isdst\":\"1\",\r\n" + "  \"monthname\":\"December\",\r\n"
      + "  \"monthname_short\":\"Dec\",\r\n" + "  \"weekday_short\":\"Mon\",\r\n"
      + "  \"weekday\":\"Monday\",\r\n" + "  \"ampm\":\"PM\",\r\n" + "  \"tz_short\":\"CST\",\r\n"
      + "  \"tz_long\":\"Australia/Adelaide\"\r\n" + "},\r\n" + "    \"period\":10,\r\n"
      + "    \"high\": {\r\n" + "    \"fahrenheit\":\"72\",\r\n" + "    \"celsius\":\"22\"\r\n"
      + "    },\r\n" + "    \"low\": {\r\n" + "    \"fahrenheit\":\"59\",\r\n"
      + "    \"celsius\":\"15\"\r\n" + "    },\r\n" + "    \"conditions\":\"Partly Cloudy\",\r\n"
      + "    \"icon\":\"partlycloudy\",\r\n"
      + "    \"icon_url\":\"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif\",\r\n"
      + "    \"skyicon\":\"partlycloudy\",\r\n" + "    \"pop\":0,\r\n"
      + "    \"qpf_allday\": {\r\n" + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n"
      + "    },\r\n" + "    \"qpf_day\": {\r\n" + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n"
      + "    },\r\n" + "    \"qpf_night\": {\r\n" + "    \"in\": 0.00,\r\n" + "    \"mm\": 0.0\r\n"
      + "    },\r\n" + "    \"snow_allday\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n"
      + "    },\r\n" + "    \"snow_day\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n"
      + "    },\r\n" + "    \"snow_night\": {\r\n" + "    \"in\": 0,\r\n" + "    \"cm\": 0\r\n"
      + "    },\r\n" + "    \"maxwind\": {\r\n" + "    \"mph\": 5,\r\n" + "    \"kph\": 8,\r\n"
      + "    \"dir\": \"SSW\",\r\n" + "    \"degrees\": 192\r\n" + "    },\r\n"
      + "    \"avewind\": {\r\n" + "    \"mph\": 4,\r\n" + "    \"kph\": 6,\r\n"
      + "    \"dir\": \"South\",\r\n" + "    \"degrees\": 181\r\n" + "    },\r\n"
      + "    \"avehumidity\": 56,\r\n" + "    \"maxhumidity\": 67,\r\n"
      + "    \"minhumidity\": 41\r\n" + "    }\r\n" + "    ]\r\n" + "    }\r\n" + "  }\r\n" + "}";

  private final static String RESPONSE = "{\n  \"response\": {\n  \"version\":\"0.1\",\n"
      + "  \"termsofService\":\"http://www.wunderground.com/weather/api/d/terms.html\",\n"
      + "  \"features\": {\n  \"conditions\": 1\n  }\n\t}\n  ,\t\"current_observation\": "
      + "{\n\t\t\"image\": {\n\t\t\"url\":\"http://icons-ak.wxug.com/graphics/wu2/logo_130x80.png"
      + "\",\n\t\t\"title\":\"Weather Underground\",\n\t\t\"link\":\"http://www.wunderground.com"
      + "\"\n\t\t},\n\t\t\"display_location\": {\n\t\t\"full\":\"Crafers, South Australia\",\n\t"
      + "\t\"city\":\"Crafers\",\n\t\t\"state\":\"SA\",\n\t\t\"state_name\":\"South Australia\","
      + "\n\t\t\"country\":\"AU\",\n\t\t\"country_iso3166\":\"AU\",\n\t\t\"zip\":\"00000\",\n\t\t"
      + "\"magic\":\"2\",\n\t\t\"wmo\":\"666\",\n\t\t\"latitude\":\"-30.015366\",\n\t\t\""
      + "longitude\":\"100.734695\",\n\t\t\"elevation\":\"0.00000000\"\n\t\t},\n\t\t\""
      + "observation_location\": {\n\t\t\"full\":\"Aldgate, SOUTH AUSTRALIA\",\n\t\t\"city"
      + "\":\"Adelaide\",\n\t\t\"state\":\"SOUTH AUSTRALIA\",\n\t\t\"country\":\"AUSTRALIA\",\n\t"
      + "\t\"country_iso3166\":\"AU\",\n\t\t\"latitude\":\"-20.015366\",\n\t\t\"longitude\":\""
      + "100.734695\",\n\t\t\"elevation\":\"1407 ft\"\n\t\t},\n\t\t\"estimated\": {\n\t\t},\n\t"
      + "\t\"station_id\":\"IMOUTHAU666\",\n\t\t\"observation_time\":\"Last Updated on December"
      + " 7, 7:35 PM CST\",\n\t\t\"observation_time_rfc822\":\"Sat, 07 Dec 2013 19:35:35 +1030\","
      + "\n\t\t\"observation_epoch\":\"1386407135\",\n\t\t\"local_time_rfc822\":\"Sat, 07 Dec 2013"
      + " 20:01:25 +1030\",\n\t\t\"local_epoch\":\"1386408685\",\n\t\t\"local_tz_short\":\"CST\","
      + "\n\t\t\"local_tz_long\":\"Australia/Adelaide\",\n\t\t\"local_tz_offset\":\"+1030\",\n\t"
      + "\t\"weather\":\"\",\n\t\t\"temperature_string\":\"77.0 F (25.0 C)\",\n\t\t\"temp_f\":"
      + "77.5,\n\t\t\"temp_c\":25.0,\n\t\t\"relative_humidity\":\"43%\",\n\t\t\"wind_string\":"
      + "\"Calm\",\n\t\t\"wind_dir\":\"West\",\n\t\t\"wind_degrees\":270,\n\t\t\"wind_mph\":0."
      + "0,\n\t\t\"wind_gust_mph\":0,\n\t\t\"wind_kph\":0,\n\t\t\"wind_gust_kph\":0,\n\t\t\""
      + "pressure_mb\":\"1015.1\",\n\t\t\"pressure_in\":\"29.98\",\n\t\t\"pressure_trend\":\"0\""
      + ",\n\t\t\"dewpoint_string\":\"53 F (12 C)\",\n\t\t\"dewpoint_f\":53,\n\t\t\"dewpoint_c\":"
      + "12,\n\t\t\"heat_index_string\":\"NA\",\n\t\t\"heat_index_f\":\"NA\",\n\t\t\"heat_index_c"
      + "\":\"NA\",\n\t\t\"windchill_string\":\"NA\",\n\t\t\"windchill_f\":\"NA\",\n\t\t\""
      + "windchill_c\":\"NA\",\n\t\t\"feelslike_string\":\"77.0 F (26 C)\",\n\t\t\"feelslike_f"
      + "\":\"77.0\",\n\t\t\"feelslike_c\":\"26\",\n\t\t\"visibility_mi\":\"\",\n\t\t\""
      + "visibility_km\":\"\",\n\t\t\"solarradiation\":\"--\",\n\t\t\"UV\":\"--\",\""
      + "precip_1hr_string\":\"0.00 in ( 0 mm)\",\n\t\t\"precip_1hr_in\":\"0.00\",\n\t\t\""
      + "precip_1hr_metric\":\" 0\",\n\t\t\"precip_today_string\":\"0.14 in (4 mm)\",\n\t\t\""
      + "precip_today_in\":\"0.14\",\n\t\t\"precip_today_metric\":\"4\",\n\t\t\"icon\":\"cloudy"
      + "\",\n\t\t\"icon_url\":\"http://icons-ak.wxug.com/i/c/k/cloudy.gif\",\n\t\t\"forecast_url"
      + "\":\"http://www.wunderground.com/global/stations/666.html\",\n\t\t\"history_url\":"
      + "\"http://www.wunderground.com/weatherstation/WXDailyHistory.asp?ID=IMOUTHAU666\",\n"
      + "\t\t\"ob_url\":\"http://www.wunderground.com/cgi-bin/findweather/getForecast?query="
      + "-20.015366,100.734695\"\n\t}\n}";

  @Test
  public void getWeatherUndergroundResponseTest() throws ClientProtocolException, IOException {
    WeatherUndergroundResponse response = station.fetchCurrentWeatherData();
    assertTrue(response.getCurrent_observation().getDisplay_location().getCity().matches("Crafers"));
  }

  @Test
  public void getCurrentTemperatureCelciusTest() throws ClientProtocolException, IOException {
    assertFalse(station.getResponses().isEmpty());
    assertTrue(station.getCurrentTemperatureCelcius() == 25.0);
  }

  @Test
  public void getCurrentTemperatureFahrenheitTest() throws ClientProtocolException, IOException {
    assertFalse(station.getResponses().isEmpty());
    assertTrue(station.getCurrentTemperatureFahrenheit() == 77.5);
  }

  @Test
  public void getMaxTemperatureCelciusTest() throws ClientProtocolException, IOException {
    assertFalse(station.getResponses().isEmpty());
    assertTrue(station.getMaxTemperatureCelcius() == 25.0);
  }

  @Test
  public void getMaxTemperatureFahrenheitTest() throws ClientProtocolException, IOException {
    assertFalse(station.getResponses().isEmpty());
    assertTrue(station.getMaxTemperatureFahrenheit() == 77.5);
  }

  @Test
  public void getMinTemperatureCelciusTest() throws ClientProtocolException, IOException {
    assertFalse(station.getResponses().isEmpty());
    assertTrue(station.getMinTemperatureCelcius() == 25.0);
  }

  @Test
  public void getMinTemperatureFahrenheitTest() throws ClientProtocolException, IOException {
    assertFalse(station.getResponses().isEmpty());
    assertTrue(station.getMinTemperatureFahrenheit() == 77.5);
  }

  @Test
  public void getLastXDaysAvgTemperatureCelciusTest() throws ClientProtocolException, IOException {
    assertFalse(station.getResponses().isEmpty());

    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
    // Match date in our test response data.
    DateTime dt = formatter.parseDateTime("07/12/2013 23:11:15");
    DateTimeUtils.setCurrentMillisFixed(dt.getMillis());

    assertTrue(station.getLastXDaysAvgTemperatureCelcius(1) == 25.0);
  }

  @Test
  public void getLastXDaysAvgTemperatureFahrenheitTest() throws ClientProtocolException,
      IOException {
    assertFalse(station.getResponses().isEmpty());

    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
    // Match date in our test response data.
    DateTime dt = formatter.parseDateTime("07/12/2013 23:11:15");
    DateTimeUtils.setCurrentMillisFixed(dt.getMillis());

    assertTrue(station.getLastXDaysAvgTemperatureFahrenheit(1) == 77.5);
  }

  @Test
  public void getTodaysRainfallMilliMetresTest() throws ClientProtocolException, IOException {
    assertFalse(station.getResponses().isEmpty());
    assertTrue(station.getTodaysRainfallMilliLitres() == 4);
  }

  @Test
  public void getTodaysRainfallInchesTest() throws ClientProtocolException, IOException {
    assertFalse(station.getResponses().isEmpty());
    assertTrue(station.getTodaysRainfallInches() == 0.14);
  }

  @Test
  public void getLastXDaysRainfallMilliMetresTest() throws ClientProtocolException, IOException {
    assertFalse(station.getResponses().isEmpty());

    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
    // Match date in our test response data.
    DateTime dt = formatter.parseDateTime("07/12/2013 23:11:15");
    DateTimeUtils.setCurrentMillisFixed(dt.getMillis());

    assertTrue(station.getLastXDaysRainfallMilliLitres(7) == 4);
  }

  @Test
  public void getNextXDaysPercentageOfPrecipitationTest() throws ClientProtocolException,
      IOException {
    assertTrue(station.getForecastResponse() != null);
    assertTrue(station.getNextXDaysPercentageOfPrecipitation(7) == 40);
  }

  @Test
  public void getLastXDaysRainfallInchesTest() throws ClientProtocolException, IOException {
    assertFalse(station.getResponses().isEmpty());

    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
    // Match date in our test response data.
    DateTime dt = formatter.parseDateTime("07/12/2013 23:11:15");
    DateTimeUtils.setCurrentMillisFixed(dt.getMillis());

    assertTrue(station.getLastXDaysRainfallInches(7) == 0.14);
  }

  @Test
  public void getCurrentRelativeHumidityPercentageTest() throws ClientProtocolException,
      IOException {
    assertFalse(station.getResponses().isEmpty());
    assertTrue(station.getCurrentRelativeHumidityPercentage() == 43);
  }

  @Test
  public void getStatusTest() throws ClientProtocolException, IOException {
    assertFalse(station.getResponses().isEmpty());

    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
    // Match date in our test response data.
    DateTime dt = formatter.parseDateTime("07/12/2013 23:11:15");
    DateTimeUtils.setCurrentMillisFixed(dt.getMillis());

    assertTrue(station.getStatus().contains("Last Updated on December 7"));
  }

  @Test
  public void getNameTest() throws ClientProtocolException, IOException {
    assertTrue(station.getName().matches("Test Wunderground Station"));
  }
}
