jirrigate v1.1
==============

Java based irrigation controller that ties in weather station data
to make intelligent decisions on whether or not to water your garden.

Features
--------
Multiple controller support (at the same time)<p>
Multiple weather station support (at the same time)<p>
Custom scheduling times<p>
Interactive shell to provide real-time control and information<p>
Weather threshold suppression including:<p>
 - x # days to look ahead for chance of rain
 - x # days to look back for previous rain
 - wind speed
 - max/min temperatures<p>
<p>
Extreme temperature irrigation multiplier:<p>
 - Temperature to trigger at
 - X # of days to look ahead for trigger
 - Amount to multiply irrigation by if triggered
<p>

Currently supporting the following controllers:
-----------------------------------------------
EtherRain8 - http://www.quicksmart.com/qs_etherrain.html

Currently supporting the following weather stations:
----------------------------------------------------
WeatherUnderground Personal Weather Station (PWS) - http://www.wunderground.com/weather/api/


To run jirrigate:
-----------------
Make sure you have Java 1.6 or newer installed.<p>
Download jirrigate: https://github.com/bensteele/jirrigate/blob/master/jirrigate-1.1-jar-with-dependencies.jar<p>
Create configuration file: (see below section "Configuration")<p>
Run jirrigate: java -jar jirrigate-1.1-jar-with-dependencies.jar --config /path/to/jirrigate.config<p>

Example:
--------
<pre>
$ java -jar jirrigate-1.1-jar-with-dependencies.jar --config jirrigate.config
Jirrigate v1.1
Copyright (C) 2014  Ben Steele
This program comes with ABSOLUTELY NO WARRANTY;
This is free software, and you are welcome to redistribute it
under certain conditions; type 'show license' for details.

Processing configuration file...
Done!
jirrigate> show controller all info
Controller               Type                IP                  Port      Username       Password       # Zones
Garage                   ETHERRAIN8          10.0.0.1            80        admin          pass           7

jirrigate> show controller Garage status
Controller               Status              Currently Irrigating     Active    # Irrigations  Next Irrigation Due  
Garage                   Device is ready     false                    true      9              17/12/2013 23:00   

jirrigate> show controller Garage zones
Name                          Id    Duration
Front Garden (central)        1     10m
Front Garden (east)           2     8m
Front Garden (west)           3     10m
Back Garden (central)         4     10m
Back Garden (south)           5     10m
Back Garden (garden bed)      6     10m
Pagola                        7     6m

jirrigate> show weatherstation Roof info
Station                  Type           Active    # Records   Oldest Record       Newest Record
Roof                     WUNDERGROUND   true      74          17/12/2013 14:35    18/12/2013 09:05

jirrigate> show weatherstation Roof status
Station: Roof
Record: Last Updated on December 18, 10:05 AM CST
Current Temp (C): 35.2
Max Temp (C): 35.2
Min Temp (C): 11.9
Current Temp (F): 95.4
Max Temp (F): 95.4
Min Temp (F): 53.4
Avg 7-day Temp (C): 17.16
Avg 7-day Temp (F): 62.88
Today's Rainfall (mm): 0.0
Today's Rainfall (in): 0.0
Last 7-day Rainfall (mm): 0.0
Last 7-day Rainfall (in): 0.0
Humidity (%): 15.0
Current Wind (kph): 6.1
Current Wind (mph): 3.8
PoP 1-day (%): 0
PoP 3-day (%): 50
PoP 7-day (%): 70

jirrigate> help
Available commands are:

activate controller EtherRain8 Garage
activate weatherstation Roof
deactivate controller EtherRain8 Garage
deactivate weatherstation Roof
show controller Garage info
show controller Garage results last <x>
show controller Garage status
show controller Garage zones
show controller all info
show controller all status
show license
show version
show weatherstation Roof info
show weatherstation Roof status
start irrigation all
stop irrigation all
test controller Garage zone Back Garden (central) duration <seconds> 
test controller Garage zone Back Garden (garden bed) duration <seconds> 
test controller Garage zone Back Garden (south) duration <seconds> 
test controller Garage zone Front Garden (central) duration <seconds> 
test controller Garage zone Front Garden (east) duration <seconds> 
test controller Garage zone Front Garden (west) duration <seconds> 
test controller Garage zone Pagola duration <seconds> 

LOG OUTPUT EXAMPLES:
--------------------
2013-12-17 14:54:08,590 INFO - Irrigator v1.0 started
2013-12-17 14:54:08,811 INFO - Next irrigation due at 17/12/2013 23:00
2013-12-17 23:00:10,110 INFO - It's time to irrigate!
2013-12-17 23:00:10,111 INFO - Threshold exceeded: next 7 days PoP 50% greater than threshold of 40%
2013-12-17 23:00:10,111 INFO - Irrigation cancelled due to threshold being exceeded


2013-12-17 23:16:33,498 INFO - Garage starting irrigation request:
Zone(s): (Front Garden (central),10m) (Front Garden (east),8m) (Front Garden (west),10m) (Back Garden (central),10m) (Back Garden (south),10m) (Back Garden (garden bed),10m) (Pagola,6m)
Total duration: 64m

2013-12-18 00:21:28,528 INFO - Irrigation Result:
Start: 17/12/2013 23:16
End: 18/12/2013 00:21
Duration: 64m
Zones: Back Garden (south),Front Garden (east),Back Garden (garden bed),Pagola,Front Garden (west),Front Garden (central),Back Garden (central),
Result: SUCCESS
Command Sent: http://10.0.0.1:80/result.cgi?xi=0:10:8:10:10:10:10:6:0
Message: INFO: Irrigation completed with no issues
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
<b>Multiplier <optional, require weather station></b>
<p>
<i>weather_multiplier_max_temp</i> - Temperature to trigger multiplier, accepts C/F.<optional><p>
<i>weather_multiplier_value</i> - Amount to multiply irrigation by, ie 1.5 for 50%.<optional><p>
<i>weather_multiplier_days_to_look_ahead</i> - Number of days to look ahead in weather forecast for trigger.<optional><p>
