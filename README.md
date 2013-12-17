jirrigate v1.0
==============

Java based irrigation controller that ties in weather station data
to make intelligent decisions on whether or not to water your garden.

Features
--------
Multiple controller support (at the same time)
Multiple weather station support (at the same time)
Custom scheduling times
Weather threshold supporesion including:
 - x # days to look ahead for chance of rain
 - x # days to look back for previous rain
 - wind speed
 - max/min temperatures

Currently supporting the following controllers:
-----------------------------------------------
EtherRain8 - http://www.quicksmart.com/qs_etherrain.html

Currently supporting the following weather stations:
----------------------------------------------------
WeatherUnderground Personal Weather Station (PWS) - http://www.wunderground.com/weather/api/


To run jirrigate:
-----------------
java -jar jirrigate-1.0-jar-with-dependencies.jar --config /path/to/jirrigate.config

Configuration:
--------------
The configuration file can contain the following fields, see sample.config for a sample.

X means a numeric value, generally starting at 1 and incrementing as required
(i.e. controller3 would indicate your third controller configuration).

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
   
   
   
   
Problems, bugs and feature requests to: Ben Steele (ben at bensteele.org)
