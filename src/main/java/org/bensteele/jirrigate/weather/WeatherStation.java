package org.bensteele.jirrigate.weather;

import org.bensteele.jirrigate.Irrigator;
import org.bensteele.jirrigate.weather.weatherunderground.WeatherUndergroundStation;
import org.joda.time.DateTime;

/**
 * An interface to provide weather information to an {@link Irrigator} so it can make informed
 * decisions on whether or not to irrigate. A complete workable implementation of a class that
 * extends this will <i>successfully</i> implement all the abstract methods presented.
 * <p>
 * See {@link WeatherUndergroundStation} for a reference implementation of this class.
 * 
 * @author Ben Steele (ben@bensteele.org)
 */
public interface WeatherStation {

  public enum WeatherStationType {
    WUNDERGROUND
  }

  public String getName();

  public double getCurrentTemperatureCelcius();

  public double getCurrentTemperatureFahrenheit();

  public double getTodaysMaxTemperatureCelcius();

  public double getTodaysMaxTemperatureFahrenheit();

  public double getTodaysMinTemperatureCelcius();

  public double getTodaysMinTemperatureFahrenheit();

  public double getLastXDaysAvgTemperatureCelcius(int days);

  public double getLastXDaysAvgTemperatureFahrenheit(int days);

  public double getTodaysRainfallMilliLitres();

  public double getLastXDaysRainfallMilliLitres(int days);

  public double getLastXDaysRainfallInches(int days);

  public int getNextXDaysPercentageOfPrecipitation(int days);

  public double getTodaysRainfallInches();

  public double getCurrentWindspeedKiloMetresPerHour();

  public double getCurrentWindspeedMilesPerHour();

  public double getCurrentRelativeHumidityPercentage();

  public String getStatus();

  public boolean isActive();

  public void setActive(boolean isActive);

  public WeatherStationType getType();

  public DateTime getOldestRecordTime();

  public DateTime getNewestRecordTime();

  public int getNumberOfRecords();

  public double getNextXDaysMaxTempCelcius(int days);

  public double getNextXDaysMaxTempFahrenheit(int days);
}
