package org.bensteele.jirrigate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.naming.ConfigurationException;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.bensteele.jirrigate.controller.Controller;
import org.bensteele.jirrigate.controller.Controller.ControllerType;
import org.bensteele.jirrigate.controller.EtherRain8Controller;
import org.bensteele.jirrigate.controller.zone.EtherRain8Zone;
import org.bensteele.jirrigate.controller.zone.Zone;
import org.bensteele.jirrigate.weather.WeatherStation;
import org.bensteele.jirrigate.weather.WeatherStation.WeatherStationType;
import org.bensteele.jirrigate.weather.weatherunderground.WeatherUndergroundStation;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * The main class for the jirrigate application.
 * <p>
 * Controls one or more {@link Controller} and (optionally) one or more {@link WeatherStation}.
 * 
 * @author Ben Steele (ben@bensteele.org)
 */
public class Irrigator {

  private static final String VERSION = "1.0";
  private static final String LICENSE_HEADER = "Jirrigate v" + VERSION
      + "  Copyright (C) 2013  Ben Steele\n" + "This program comes with ABSOLUTELY NO WARRANTY;\n"
      + "This is free software, and you are welcome to redistribute it\n"
      + "under certain conditions; type 'show license' for details.\n";
  private static final Logger LOG = Logger.getRootLogger();

  public static void main(String[] args) throws IOException, ConfigurationException {
    System.out.println(LICENSE_HEADER);
    Properties config = parseConfigurationFile(args);
    Irrigator i = new Irrigator(config);
    try {
      System.out.println("Processing configuration file...");
      i.processConfiguration();
      System.out.println("Done!");
    } catch (ConfigurationException e) {
      System.out.println(e.getMessage());
      System.exit(1);
    }
    Console c = new Console(i);
    c.startConsole();
  }

  protected static Properties parseConfigurationFile(String[] args) throws IOException {
    OptionParser parser = new OptionParser() {
      {
        accepts("config").withRequiredArg().required().ofType(String.class)
            .describedAs("Path to the configuration file.");
      }
    };
    OptionSet options = null;
    try {
      options = parser.parse(args);
    } catch (OptionException e) {
      parser.printHelpOn(System.out);
      System.exit(1);
    }

    String configFilePath = (String) options.valueOf("config");
    Properties config = new Properties();
    config.load(new FileInputStream(configFilePath));

    return config;
  }

  private final ExecutorService requestExecutor = Executors.newSingleThreadExecutor();
  private final Set<Integer> wateringDays = new TreeSet<Integer>();
  private final Set<Controller> controllers = new HashSet<Controller>();
  private final Set<WeatherStation> weatherStations = new HashSet<WeatherStation>();
  private final Properties config;
  private final DecimalFormat twoDecimals = new DecimalFormat("###.##");
  private final PatternLayout logPattern = new PatternLayout("%d %-5p- %m%n");
  private LocalTime wateringStartTime;
  private int rainDaysToLookBack;
  private int rainDaysToLookAhead;
  private int popThreshold;
  private double totalRainAmountThresholdInMilliMetres = Double.MAX_VALUE;
  private double currentRainAmountThresholdInMilliMetres = Double.MAX_VALUE;
  private double currentWindThresholdInKmph = Double.MAX_VALUE;
  private double maxTempThresholdInCelcius = Double.MAX_VALUE;
  private double minTempThresholdInCelcius = Double.MAX_VALUE;
  private String logPath;

  /**
   * Creates a single instance of an Irrigator and all of its child {@link Controller} and
   * {@link WeatherStation} Objects. The configuration of these Objects is derived from the
   * configuration properties passed into the constructor and may have the following values:
   * 
   * <pre>
   * X means a numeric value, generally starting at 1 and incrementing as required
   * (i.e. controller3 would indicate your third controller configuration).
   * 
   * logging_directory - The path to the directory to write the jirrigate.log file.
   * 
   * controllerX_name - Friendly name. <required>
   * controllerX_ip - IP address. <required>
   * controllerX_port - Port to connect to. <required>
   * controllerX_username - Login Username. <optional>
   * controllerX_password - Login Password. <optional>
   * controllerX_type - Type of this controller, i.e. EtherRain8. <required>
   * 
   * controllerX_zoneX_name - Friendly name. <required>
   * controllerX_zoneX_id - ID of this zone on the controller. <required>
   * controllerX_zoneX_duration - How long to irrigate this zone, accepts s/m/h delimiters. <required>
   * 
   * watering_days - Days to irrigate, i.e. monday,wednesday,saturday. <required>
   * watering_start_time - Time to start irrigation in 24-hour format i.e. 23:30 for 11:30pm. <required>
   * 
   * <WeatherStation as a whole is optional>
   * weatherstationX_name - Friendly name. <required>
   * weatherstationX_type - Type i.e. wunderground. <required>
   * weatherstationX_staionid - Station specific ID i.e. AUSOUTH123 <required>
   * weatherstationX_api - API key <required>
   * 
   * <Thresholds are optional and require a valid WeatherStation to be accepted>
   * weather_threshold_rain_days_to_look_ahead - # of days in advance to use for rain thresholds<optional>
   * weather_threshold_rain_days_to_look_back - # of days in past to use for rain thresholds<optional>
   * weather_threshold_total_rain_amount - Total rain from weather_threshold_rain_days_to_look_ahead, accepts mm/in.<optional>
   * weather_threshold_current_rain_amount - Amount of rain from today, accepts mm/in.<optional>
   * weather_threshold_pop - Chance of rain over the weather_threshold_rain_days_to_look_ahead period. <optional>
   * weather_threshold_wind_speed - Current wind speed, accepts kph/mph.<optional>
   * weather_threshold_min_temp - Current temperature low cut off, accepts C/F.<optional>
   * weather_threshold_max_temp - Current temperature high cut off, accepts C/F.<optional>
   * 
   * 
   * @param config
   *          The configuration to be loaded into the {@link Irrigator}.
   * @throws IOException
   *           If log4j has trouble creating the log file.
   * @throws ConfigurationException
   * If the configuration contains invalid or missing properties.
   */
  public Irrigator(Properties config) throws IOException, ConfigurationException {
    this.config = config;

    // Logging setup
    if (config.getProperty("logging_directory") == null) {
      logPath = System.getProperty("user.dir");
    } else {
      File file = new File(config.getProperty("logging_directory"));
      if (!file.isDirectory()) {
        throw new ConfigurationException("ERROR: Must specify a logging_directory");
      } else {
        logPath = config.getProperty("logging_directory");
      }
    }
    RollingFileAppender fileAppender = new RollingFileAppender(logPattern, logPath + File.separator
        + "jirrigate.log", true);
    fileAppender.setMaxFileSize("10MB");
    fileAppender.setMaxBackupIndex(10);
    LOG.addAppender(fileAppender);
    LOG.setLevel(Level.INFO);

    // Main irrigator thread.
    requestExecutor.execute(new Runnable() {
      @Override
      public void run() {
        startIrrigator();
      }
    });
  }

  public Properties getConfig() {
    return config;
  }

  public Controller getController(String controllerName) {
    for (Controller c : controllers) {
      if (c.getName().matches(controllerName)) {
        return c;
      }
    }
    return null;
  }

  public Set<Controller> getControllers() {
    return this.controllers;
  }

  public double getCurrentRainAmountThresholdInMilliMetres() {
    return Double.valueOf(twoDecimals.format(currentRainAmountThresholdInMilliMetres));
  }

  public double getCurrentWindThresholdInKmph() {
    return Double.valueOf(twoDecimals.format(currentWindThresholdInKmph));
  }

  public double getMaxTempThresholdInCelcius() {
    return Double.valueOf(twoDecimals.format(maxTempThresholdInCelcius));
  }

  public double getMinTempThresholdInCelcius() {
    return Double.valueOf(twoDecimals.format(minTempThresholdInCelcius));
  }

  public int getPopThreshold() {
    return this.popThreshold;
  }

  public int getRainDaysToLookAhead() {
    return this.rainDaysToLookAhead;
  }

  public int getRainDaysToLookBack() {
    return rainDaysToLookBack;
  }

  public double getTotalRainAmountThresholdInMilliMetres() {
    return Double.valueOf(twoDecimals.format(totalRainAmountThresholdInMilliMetres));
  }

  public String getVersion() {
    return VERSION;
  }

  public Set<Integer> getWateringDays() {
    return this.wateringDays;
  }

  public LocalTime getWateringStartTime() {
    return this.wateringStartTime;
  }

  public WeatherStation getWeatherStation(String weatherStationName) {
    for (WeatherStation w : weatherStations) {
      if (w.getName().matches(weatherStationName)) {
        return w;
      }
    }
    return null;
  }

  public Set<WeatherStation> getWeatherStations() {
    return this.weatherStations;
  }

  protected boolean isIrrigating() {
    for (Controller c : controllers) {
      if (c.isIrrigating()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the time and date the next irrigation is due based on the watering_days and
   * watering_start_time. It does not take into account whether or not any of the {@link Controller}
   * are active.
   * 
   * @return The time and date of the next irrigation for any controller under this irrigator's
   *         control.
   */
  protected DateTime nextIrrigationAt() {
    DateTime dt = new DateTime();
    for (int i = 0; i < 7; i++) {
      for (int dayOfWeek : wateringDays) {
        if (dayOfWeek == (dt.getDayOfWeek())) {
          // If it's the first run then we may match the same day we are currently on, in this case
          // we need to check that we don't report a time in the past. Validate that the hour and
          // minute right now are not past the scheduled watering time. If it's not the first run
          // then it's ok to let through.
          if (i != 0 || (i == 0 && dt.toLocalTime().isBefore(wateringStartTime))) {

            // Reset the hour to 0 and increment until we match the watering hour.
            dt = dt.withHourOfDay(0);
            while (dt.getHourOfDay() < wateringStartTime.getHourOfDay()) {
              dt = dt.plusHours(1);
            }

            // Reset the minute to 0 and increment until we match the watering minute.
            dt = dt.withMinuteOfHour(0);
            while (dt.getMinuteOfHour() < wateringStartTime.getMinuteOfHour()) {
              dt = dt.plusMinutes(1);
            }
            return dt;
          }
        }
      }
      dt = dt.plusDays(1);
    }
    return null;
  }

  public void processConfiguration() throws ConfigurationException, IOException {
    // Process the weather stations.
    processWeatherStationConfiguration();
    // Process the weather thresholds.
    processWeatherThresholdConfiguration();
    // Process the days that irrigation can be performed.
    processWateringDaysConfiguration();
    // Process the days that irrigation can be performed.
    processWateringStartTimeConfiguration();
    // Process the irrigation controllers.
    processControllerConfiguration();
  }

  public void processControllerConfiguration() throws ConfigurationException, IOException {
    for (int i = 1; i < Integer.MAX_VALUE; i++) {
      String controllerValue = "controller" + i;
      String controllerName = config.getProperty(controllerValue + "_name");
      if (controllerName == null) {
        // No more controllers to parse.
        break;
      } else {

        // IP Address
        if (config.getProperty(controllerValue + "_ip") == null) {
          throw new ConfigurationException("ERROR: Unable to parse IP address of "
              + controllerValue + "_ip");
        }
        InetAddress ipAddress;
        try {
          ipAddress = InetAddress.getByName(config.getProperty(controllerValue + "_ip"));
        } catch (UnknownHostException e) {
          throw new ConfigurationException("ERROR: Unable to parse IP address of "
              + controllerValue + "_ip");
        }

        // Port
        if (config.getProperty(controllerValue + "_port") == null) {
          throw new ConfigurationException("ERROR: Unable to parse port of " + controllerValue
              + "_port");
        }
        int port;
        try {
          port = Integer.parseInt(config.getProperty(controllerValue + "_port"));
        } catch (NumberFormatException e) {
          throw new ConfigurationException("ERROR: Unable to parse port of " + controllerValue
              + "_port");
        }

        // Username
        String username = config.getProperty(controllerValue + "_username");

        // Password
        String password = config.getProperty(controllerValue + "_password");

        // Instantiate controller based on controller_type value.
        if (config.getProperty(controllerValue + "_type") == null) {
          throw new ConfigurationException("ERROR: " + "no controller type for " + controllerValue
              + "_type");
        }
        Controller c = null;
        String controllerType = config.getProperty(controllerValue + "_type");
        for (ControllerType type : Controller.ControllerType.values()) {
          if (controllerType.toUpperCase().matches(type.name())) {
            if (type.name().matches("ETHERRAIN8")) {
              c = new EtherRain8Controller(controllerName, ipAddress, port, username, password, LOG);
            }
          }
        }
        if (c == null) {
          throw new ConfigurationException("ERROR: " + controllerType
              + " is not a valid controller type for " + controllerValue + "_type");
        }

        // Process the zones for this controller.
        processControllerZonesConfiguration(c, controllerValue);

        // Controller configuration has passed, add it to the list of controllers.
        controllers.add(c);
      }
    }
  }

  /**
   * Processes the {@link Zone} configurations for a given {@link Controller}. It will convert any
   * duration values into seconds but will leave the specifics on what is and isn't valid for a
   * duration to the {@link Zone} implementation.
   * 
   * @param c
   *          The {@link Controller} the {@link Zone} belong to.
   * @param controllerValue
   *          The order in which it's processed from the configuration ie "controller3" for the
   *          third controller.
   * @throws ConfigurationException
   *           If the configuration is invalid.
   */
  private void processControllerZonesConfiguration(Controller c, String controllerValue)
      throws ConfigurationException {
    for (int j = 1; j < Integer.MAX_VALUE; j++) {
      String zone = controllerValue + "_zone" + j;
      String zoneName = config.getProperty(zone + "_name");
      if (zoneName == null) {
        // No more zones to parse.
        break;
      } else {
        String zoneId = config.getProperty(zone + "_id");
        if (zoneId == null) {
          throw new ConfigurationException("ERROR: Could not find configuration for " + zone
              + "_id");
        }

        // Create the zone, it will automatically add itself to the controller.
        Zone z = null;
        try {
          if (c.getClass().equals(EtherRain8Controller.class)) {
            z = new EtherRain8Zone(c, zoneName, 0, zoneId);
          }
        } catch (IllegalArgumentException e) {
          throw new ConfigurationException(e.getMessage());
        }

        // Duration is an optional value expressed in [value][s|m|h] where s=seconds, m=minutes
        // and h=hours in the configuration file. This will be converted into seconds for use
        // with the controllers.
        String zoneDuration = config.getProperty(zone + "_duration");
        if (zoneDuration != null) {
          try {
            long durationValue = Long
                .parseLong(zoneDuration.substring(0, zoneDuration.length() - 1));
            String durationDelimiter = zoneDuration.substring(zoneDuration.length() - 1);
            if (durationDelimiter.equalsIgnoreCase("s")) {
              z.setDuration(durationValue);
            } else if (durationDelimiter.equalsIgnoreCase("m")) {
              z.setDuration(durationValue * 60);
            } else if (durationDelimiter.equalsIgnoreCase("h")) {
              z.setDuration((durationValue * 60) * 60);
            } else {
              throw new ConfigurationException("ERROR: Unable to parse duration of " + zone
                  + "_duration");
            }
          } catch (NumberFormatException e) {
            throw new ConfigurationException("ERROR: Unable to parse duration of " + zone
                + "_duration");
          } catch (IllegalArgumentException e) {
            throw new ConfigurationException(e.getMessage());
          }
        }
      }
    }
  }

  /**
   * Parse the configuration file for names of days of the week from the "watering_days" value.
   * Convert to java.util.Calendar.DAY_OF_WEEK int value and store for scheduling use.
   * 
   * @param config
   *          The configuration file.
   * @throws ConfigurationException
   *           If the configuration is invalid.
   */
  public void processWateringDaysConfiguration() throws ConfigurationException {
    final String errorMsg = "ERROR: Could not read watering_days from configuration file";

    if (config.getProperty("watering_days") == null) {
      throw new ConfigurationException(errorMsg);
    }

    String[] wateringDaysArg = config.getProperty("watering_days").split(",");

    for (String s : wateringDaysArg) {
      s = s.trim();
      if (s.equalsIgnoreCase("sunday")) {
        wateringDays.add(DateTimeConstants.SUNDAY);
      }
      if (s.equalsIgnoreCase("monday")) {
        wateringDays.add(DateTimeConstants.MONDAY);
      }
      if (s.equalsIgnoreCase("tuesday")) {
        wateringDays.add(DateTimeConstants.TUESDAY);
      }
      if (s.equalsIgnoreCase("wednesday")) {
        wateringDays.add(DateTimeConstants.WEDNESDAY);
      }
      if (s.equalsIgnoreCase("thursday")) {
        wateringDays.add(DateTimeConstants.THURSDAY);
      }
      if (s.equalsIgnoreCase("friday")) {
        wateringDays.add(DateTimeConstants.FRIDAY);
      }
      if (s.equalsIgnoreCase("saturday")) {
        wateringDays.add(DateTimeConstants.SATURDAY);
      }
    }
    if (wateringDays.isEmpty()) {
      throw new ConfigurationException(errorMsg);
    }
  }

  /**
   * Parse the configuration file for names of days of the week from the "watering_start_time"
   * value. Expects a 24 hour ":" separated value, stores it as Joda LocalTime.
   * 
   * @param config
   *          The configuration file.
   * @throws ConfigurationException
   *           If the configuration is invalid.
   */
  public void processWateringStartTimeConfiguration() throws ConfigurationException {
    if (config.getProperty("watering_start_time") == null) {
      throw new ConfigurationException(
          "ERROR: Could not read watering_start_time from configuration file");
    }

    String startTimeString = config.getProperty("watering_start_time");

    // Expect 24 hour time format ie 23:10 for 11:10PM.
    String[] timeSplit = startTimeString.split(":");
    try {
      int hour = Integer.parseInt(timeSplit[0]);
      int minute = Integer.parseInt(timeSplit[1]);
      LocalTime wateringStartTime = new LocalTime(hour, minute);
      this.wateringStartTime = wateringStartTime;
    } catch (NumberFormatException e) {
      throw new ConfigurationException("ERROR: Unable to parse watering_start_time value");
    }
  }

  public void processWeatherStationConfiguration() throws ConfigurationException {
    for (int i = 1; i < Integer.MAX_VALUE; i++) {
      String weatherStationValue = "weatherstation" + i;
      String weatherStationName = config.getProperty(weatherStationValue + "_name");
      if (weatherStationName == null) {
        // No more weather stations to parse.
        break;
      } else {
        // Instantiate weather station based on weatherstation_type value.
        if (config.getProperty(weatherStationValue + "_type") == null) {
          throw new ConfigurationException("ERROR: " + "no weather station type for "
              + weatherStationValue + "_type");
        }
        WeatherStation w = null;
        String weatherStationType = config.getProperty(weatherStationValue + "_type");
        for (WeatherStationType type : WeatherStation.WeatherStationType.values()) {
          if (weatherStationType.toUpperCase().matches(type.name())) {
            if (type.name().matches("WUNDERGROUND")) {

              if (config.getProperty(weatherStationValue + "_stationid") == null) {
                throw new ConfigurationException("ERROR: " + "no stationid type for "
                    + weatherStationValue + "_stationid");
              }
              String stationId = config.getProperty(weatherStationValue + "_stationid");

              if (config.getProperty(weatherStationValue + "_api") == null) {
                throw new ConfigurationException("ERROR: " + "no api key for "
                    + weatherStationValue + "_api");
              }
              String api = config.getProperty(weatherStationValue + "_api");

              w = new WeatherUndergroundStation(weatherStationName, api, stationId);
            }
          }
        }
        if (w == null) {
          throw new ConfigurationException("ERROR: " + weatherStationType
              + " is not a valid weather station type for " + weatherStationValue + "_type");
        }

        // Weather Station configuration has passed, add it to the list of weatherstations.
        weatherStations.add(w);
      }
    }
  }

  public void processWeatherThresholdConfiguration() throws ConfigurationException {
    final String NO_WEATHER_STATION = "ERROR: cannot have weather_threshold values without a weatherstation configured";
    if (config.getProperty("weather_threshold_rain_days_to_look_back") != null) {
      if (weatherStations.isEmpty()) {
        throw new ConfigurationException(NO_WEATHER_STATION);
      }
      if (config.getProperty("weather_threshold_total_rain_amount") == null) {
        throw new ConfigurationException(
            "ERROR: must supply a weather_threshold_total_rain_amount value");
      }
      rainDaysToLookBack = Math.abs(Integer.parseInt(config
          .getProperty("weather_threshold_rain_days_to_look_back")));
      String totalRain = config.getProperty("weather_threshold_total_rain_amount");
      String delimiter = totalRain.substring(totalRain.length() - 2);
      totalRainAmountThresholdInMilliMetres = Math.abs(Double.parseDouble(totalRain.substring(0,
          totalRain.length() - 2)));
      if (delimiter.equalsIgnoreCase("in")) {
        totalRainAmountThresholdInMilliMetres = (totalRainAmountThresholdInMilliMetres * 25.4);
      } else if (!delimiter.equalsIgnoreCase("mm")) {
        throw new ConfigurationException(
            "ERROR: must supply a delimiter of \"mm\" or \"in\" to weather_threshold_total_rain_amount value");
      }
    }

    if (config.getProperty("weather_threshold_current_rain_amount") != null) {
      if (weatherStations.isEmpty()) {
        throw new ConfigurationException(NO_WEATHER_STATION);
      }
      String currentRain = config.getProperty("weather_threshold_current_rain_amount");
      String delimiter = currentRain.substring(currentRain.length() - 2);
      currentRainAmountThresholdInMilliMetres = Math.abs(Double.parseDouble(currentRain.substring(
          0, currentRain.length() - 2)));
      if (delimiter.equalsIgnoreCase("in")) {
        currentRainAmountThresholdInMilliMetres = (currentRainAmountThresholdInMilliMetres * 25.4);
      } else if (!delimiter.equalsIgnoreCase("mm")) {
        throw new ConfigurationException(
            "ERROR: must supply a delimiter of \"mm\" or \"in\" to weather_threshold_current_rain_amount");
      }
    }

    if (config.getProperty("weather_threshold_wind_speed") != null) {
      if (weatherStations.isEmpty()) {
        throw new ConfigurationException(NO_WEATHER_STATION);
      }
      String currentWind = config.getProperty("weather_threshold_wind_speed");
      String delimiter = currentWind.substring(currentWind.length() - 3);
      currentWindThresholdInKmph = Math.abs(Double.parseDouble(currentWind.substring(0,
          currentWind.length() - 3)));
      if (delimiter.equalsIgnoreCase("mph")) {
        currentWindThresholdInKmph = (currentWindThresholdInKmph * 1.6);
      } else if (!delimiter.equalsIgnoreCase("kph")) {
        throw new ConfigurationException(
            "ERROR: must supply a delimiter of \"kph\" or \"mph\" to weather_threshold_wind_speed");
      }
    }

    if (config.getProperty("weather_threshold_max_temp") != null) {
      if (weatherStations.isEmpty()) {
        throw new ConfigurationException(NO_WEATHER_STATION);
      }
      String maxTemp = config.getProperty("weather_threshold_max_temp");
      String delimiter = maxTemp.substring(maxTemp.length() - 1);
      maxTempThresholdInCelcius = Double.parseDouble(maxTemp.substring(0, maxTemp.length() - 1));
      if (delimiter.equalsIgnoreCase("F")) {
        maxTempThresholdInCelcius = (maxTempThresholdInCelcius - 32) * (5.0 / 9.0);
      } else if (!delimiter.equalsIgnoreCase("C")) {
        throw new ConfigurationException(
            "ERROR: must supply a delimiter of \"C\" or \"F\" to weather_threshold_max_temp");
      }
    }

    if (config.getProperty("weather_threshold_min_temp") != null) {
      if (weatherStations.isEmpty()) {
        throw new ConfigurationException(NO_WEATHER_STATION);
      }
      String minTemp = config.getProperty("weather_threshold_min_temp");
      String delimiter = minTemp.substring(minTemp.length() - 1);
      minTempThresholdInCelcius = Double.parseDouble(minTemp.substring(0, minTemp.length() - 1));
      if (delimiter.equalsIgnoreCase("F")) {
        minTempThresholdInCelcius = (minTempThresholdInCelcius - 32) * (5.0 / 9.0);
      } else if (!delimiter.equalsIgnoreCase("C")) {
        throw new ConfigurationException(
            "ERROR: must supply a delimiter of \"C\" or \"F\" to weather_threshold_min_temp");
      }
    }

    if (config.getProperty("weather_threshold_rain_days_to_look_ahead") != null) {
      if (weatherStations.isEmpty()) {
        throw new ConfigurationException(NO_WEATHER_STATION);
      }
      if (config.getProperty("weather_threshold_pop") == null) {
        throw new ConfigurationException("ERROR: must supply a weather_threshold_pop value");
      }
      rainDaysToLookAhead = Math.abs(Integer.parseInt(config
          .getProperty("weather_threshold_rain_days_to_look_ahead")));
      popThreshold = Math.abs(Integer.parseInt(config.getProperty("weather_threshold_pop")));
    }
  }

  public void startIrrigator() {
    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
    LOG.info("Irrigator v" + VERSION + " started");
    LOG.info("Next irrigation due at " + nextIrrigationAt().toString(formatter));
    while (true) {
      if (!isIrrigating() && timeToIrrigate()) {
        for (Controller c : controllers) {
          if (c.isActive()) {
            c.irrigationRequest(c.generateDefaultIrrigationRequest());
          }
        }
        LOG.info("Next irrigation due at " + nextIrrigationAt().toString(formatter));
      }
      try {
    	// Try to irrigate once a minute.
        Thread.sleep(60000);
      } catch (InterruptedException e) {
        System.out.println("Thread interrupted: " + e.getMessage());
        System.exit(1);
      }
    }
  }

  protected boolean thresholdsExceeded() {
    for (WeatherStation ws : weatherStations) {
      if (currentRainAmountThresholdInMilliMetres <= ws.getTodaysRainfallMilliLitres()) {
        LOG.info("Threshold exceeded: today's rainfall " + ws.getTodaysRainfallMilliLitres()
            + "mm greater than threshold of " + currentRainAmountThresholdInMilliMetres + "mm");
        return true;
      }
      if (currentWindThresholdInKmph <= ws.getCurrentWindspeedKiloMetresPerHour()) {
        LOG.info("Threshold exceeded: current windspeed "
            + ws.getCurrentWindspeedKiloMetresPerHour() + "kph greater than threshold of "
            + currentWindThresholdInKmph + "kph");
        return true;
      }
      if (maxTempThresholdInCelcius <= ws.getCurrentTemperatureCelcius()) {
        LOG.info("Threshold exceeded: current temperature " + ws.getCurrentTemperatureCelcius()
            + "C greater than threshold of " + maxTempThresholdInCelcius + "C");
        return true;
      }
      if (minTempThresholdInCelcius >= ws.getCurrentTemperatureCelcius()) {
        LOG.info("Threshold exceeded: current temperature " + ws.getCurrentTemperatureCelcius()
            + "C less than threshold of " + maxTempThresholdInCelcius + "C");
        return true;
      }
      if (rainDaysToLookBack > 0) {
        if (totalRainAmountThresholdInMilliMetres <= ws
            .getLastXDaysRainfallMilliLitres(rainDaysToLookBack)) {
          LOG.info("Threshold exceeded: last " + rainDaysToLookBack + " days rainfall "
              + ws.getLastXDaysRainfallMilliLitres(rainDaysToLookBack)
              + "mm greater than threshold of " + totalRainAmountThresholdInMilliMetres + "mm");
          return true;
        }
      }
      if (rainDaysToLookAhead > 0) {
        if (popThreshold <= ws.getNextXDaysPercentageOfPrecipitation(rainDaysToLookAhead)) {
          LOG.info("Threshold exceeded: next " + rainDaysToLookAhead + " days PoP "
              + ws.getNextXDaysPercentageOfPrecipitation(rainDaysToLookAhead)
              + "% greater than threshold of " + popThreshold + "%");
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Determines whether or not it is appropriate to irrigate based on the watering_days,
   * watering_start_time and watering_threshold values in the configuration. Used by the
   * {@code #startIrrigator()} worker thread.
   * 
   * @return true to irrigate.
   */
  protected boolean timeToIrrigate() {
    LocalTime lt = new LocalTime();
    for (int dayOfWeek : wateringDays) {
      if (dayOfWeek == (lt.toDateTimeToday().getDayOfWeek())) {
        if (wateringStartTime.getHourOfDay() == lt.getHourOfDay()) {
          if (wateringStartTime.getMinuteOfHour() == lt.getMinuteOfHour()) {
            LOG.info("It's time to irrigate!");
            if (thresholdsExceeded()) {
              LOG.info("Irrigation cancelled due to threshold being exceeded");
              return false;
            } else {
              LOG.info("Thresholds all ok");
              return true;
            }
          }
        }
      }
    }
    return false;
  }
}
