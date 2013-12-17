jirrigate v1.0
==============

Java based irrigation controller that ties in weather station data
to make intelligent decisions on whether or not to water your garden.

Features
--------
Multiple controller support (at the same time)<p>
Multiple weather station support (at the same time)<p>
Custom scheduling times<p>
Interactive shell to provide real-time control and information<p>
Weather threshold supporesion including:<p>
 - x # days to look ahead for chance of rain
 - x # days to look back for previous rain
 - wind speed
 - max/min temperatures<p>
<p>

Currently supporting the following controllers:
-----------------------------------------------
EtherRain8 - http://www.quicksmart.com/qs_etherrain.html

Currently supporting the following weather stations:
----------------------------------------------------
WeatherUnderground Personal Weather Station (PWS) - http://www.wunderground.com/weather/api/


To run jirrigate:
-----------------
java -jar jirrigate-1.0-jar-with-dependencies.jar --config /path/to/jirrigate.config

Example:
--------
<pre>
$ java -jar code/misc-workspace/jirrigation/target/jirrigate-1.0-jar-with-dependencies.jar --config jirrigate.config
jirrigate v1.0

Processing configuration file...
Done!
jirrigate> show controller EtherRain8 Garage status
Controller               Status              Currently Irrigating     Active    # Irrigations  Next Irrigation Due  
EtherRain8 Garage        Device is ready     false                    true      9              17/12/2013 23:00   

jirrigate> show weatherstation Roof status 
Station: Roof
Record: Last Updated on December 17, 6:05 PM CST
Current Temp (C): 27.3
Max Temp (C): 27.3
Min Temp (C): 27.3
Current Temp (F): 81.1
Max Temp (F): 81.1
Min Temp (F): 81.1
Avg 7-day Temp (C): 27.3
Avg 7-day Temp (F): 81.1
Today's Rainfall (mm): 0.0
Today's Rainfall (in): 0.0
Last 7-day Rainfall (mm): 0.0
Last 7-day Rainfall (in): 0.0
Humidity (%): 20.0
Current Wind (kph): 7.2
Current Wind (mph): 4.5
PoP 1-day (%): 0
PoP 3-day (%): 0
PoP 7-day (%): 50

jirrigate> help
Available commands are:

activate controller EtherRain8 Garage
activate weatherstation Roof
deactivate controller EtherRain8 Garage
deactivate weatherstation Roof
show controller EtherRain8 Garage info
show controller EtherRain8 Garage results last <x>
show controller EtherRain8 Garage status
show controller EtherRain8 Garage zones
show controller all info
show controller all status
show version
show weatherstation Roof info
show weatherstation Roof status
start irrigation all
stop irrigation all
test controller EtherRain8 Garage zone Back Garden (central) duration <seconds> 
test controller EtherRain8 Garage zone Back Garden (garden bed) duration <seconds> 
test controller EtherRain8 Garage zone Back Garden (south) duration <seconds> 
test controller EtherRain8 Garage zone Front Garden (central) duration <seconds> 
test controller EtherRain8 Garage zone Front Garden (east) duration <seconds> 
test controller EtherRain8 Garage zone Front Garden (west) duration <seconds> 
test controller EtherRain8 Garage zone Pagola duration <seconds> 

</pre>

Configuration:
--------------
The configuration file can contain the following fields, see sample.config for a sample.

<b>X</b> means a numeric value, generally starting at 1 and incrementing as required
(i.e. controller3 would indicate your third controller configuration).
<p>
<b>Logging</b>
<p>
<i>logging_directory</i> - The path to the directory to write the jirrigate.log file.<p>
<p>
<b>Controllers</b><p>
<p>
<i>controllerX_name</i> - Friendly name. <required><p>
<i>controllerX_ip</i> - IP address. <required><p>
<i>controllerX_port</i> - Port to connect to. <required><p>
<i>controllerX_username</i> - Login Username. <optional><p>
<i>controllerX_password</i> - Login Password. <optional><p>
<i>controllerX_type</i> - Type of this controller, i.e. EtherRain8. <required><p>
<i>controllerX_zoneX_name</i> - Friendly name. <required><p>
<i>controllerX_zoneX_id</i> - ID of this zone on the controller. <required><p>
<i>controllerX_zoneX_duration</i> - How long to irrigate this zone, accepts s/m/h delimiters. <required><p>
<p>
<b>Schedules</b>
<p>
<i>watering_days</i> - Days to irrigate, i.e. monday,wednesday,saturday. <required><p>
<i>watering_start_time</i> - Time to start irrigation in 24-hour format i.e. 23:30 for 11:30pm. <required><p>
<p>
<b>Weather Stations <optional></b><p>
<p>
<i>weatherstationX_name</i> - Friendly name. <required><p>
<i>weatherstationX_type</i> - Type i.e. wunderground. <required><p>
<i>weatherstationX_staionid</i> - Station specific ID i.e. AUSOUTH123 <required><p>
<i>weatherstationX_api</i> - API key <required><p>
<p>
<b>Thresholds <optional, require weather station></b>
<p>
<i>weather_threshold_rain_days_to_look_ahead</i> - # of days in advance to use for rain thresholds<optional><p>
<i>weather_threshold_rain_days_to_look_back</i> - # of days in past to use for rain thresholds<optional><p>
<i>weather_threshold_total_rain_amount</i> - Total rain from weather_threshold_rain_days_to_look_ahead, accepts mm/in.<optional><p>
<i>weather_threshold_current_rain_amount</i> - Amount of rain from today, accepts mm/in.<optional><p>
<i>weather_threshold_pop</i> - Chance of rain over the weather_threshold_rain_days_to_look_ahead period. <optional><p>
<i>weather_threshold_wind_speed</i> - Current wind speed, accepts kph/mph.<optional><p>
<i>weather_threshold_min_temp</i> - Current temperature low cut off, accepts C/F.<optional><p>
<i>weather_threshold_max_temp</i> - Current temperature high cut off, accepts C/F.<optional><p>
<p>
<p>   
<b>Problems, bugs and feature requests to:</b> Ben Steele (ben at bensteele.org)
