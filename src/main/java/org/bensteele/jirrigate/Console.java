package org.bensteele.jirrigate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import jline.console.ConsoleReader;
import jline.console.completer.StringsCompleter;

import org.bensteele.jirrigate.controller.Controller;
import org.bensteele.jirrigate.controller.IrrigationResult;
import org.bensteele.jirrigate.controller.zone.Zone;
import org.bensteele.jirrigate.weather.WeatherStation;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * A simple class to provide an interactive console/shell to jirrigate using jline2.
 * 
 * @author Ben Steele (ben@bensteele.org)
 */
public class Console {

  private final Irrigator irrigator;
  private final Set<String> commands = new TreeSet<String>();

  public Console(Irrigator irrigator) {
    this.irrigator = irrigator;
  }

  private void activateController(String input) {
    for (Controller c : irrigator.getControllers()) {
      if (input.toLowerCase().contains(c.getName().toLowerCase())) {
        System.out.print("Activating " + c.getName() + ": ");
        c.setActive(true);
        System.out.println("OK");
      }
    }
  }

  private void activateWeatherStation(String input) {
    for (WeatherStation ws : irrigator.getWeatherStations()) {
      if (input.toLowerCase().contains(ws.getName().toLowerCase())) {
        System.out.print("Activating " + ws.getName() + ": ");
        ws.setActive(true);
        System.out.println("OK");
      }
    }
  }

  private void deactivateController(String input) {
    for (Controller c : irrigator.getControllers()) {
      if (input.toLowerCase().contains(c.getName().toLowerCase())) {
        System.out.print("Deactivating " + c.getName() + ": ");
        c.setActive(false);
        System.out.println("OK");
      }
    }
  }

  private void deactivateWeatherStation(String input) {
    for (WeatherStation ws : irrigator.getWeatherStations()) {
      if (input.toLowerCase().contains(ws.getName().toLowerCase())) {
        System.out.print("Deactivating " + ws.getName() + ": ");
        ws.setActive(false);
        System.out.println("OK");
      }
    }
  }

  private void printControllerInfo(String input) {
    System.out.format("%-25s%-20s%-20s%-10s%-15s%-15s%-10s\n", "Controller", "Type", "IP", "Port",
        "Username", "Password", "# Zones");
    for (Controller c : irrigator.getControllers()) {
      if (input.toLowerCase().contains(c.getName().toLowerCase())) {
        System.out.format("%-25s%-20s%-20s%-10s%-15s%-15s%-10s\n", c.getName(),
            c.getControllerType(), c.getIpAddress().getHostAddress(), c.getPort(), c.getUsername(),
            c.getPassword(), c.getZones().size());
      }
    }
  }

  private void printControllerInfoAll(String input) {
    System.out.format("%-25s%-20s%-20s%-10s%-15s%-15s%-10s\n", "Controller", "Type", "IP", "Port",
        "Username", "Password", "# Zones");
    for (Controller c : irrigator.getControllers()) {
      System.out.format("%-25s%-20s%-20s%-10s%-15s%-15s%-10s\n", c.getName(),
          c.getControllerType(), c.getIpAddress().getHostAddress(), c.getPort(), c.getUsername(),
          c.getPassword(), c.getZones().size());
    }
  }

  private void printControllerStatus(String input) {
    DateTimeFormatter statusFormatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
    System.out.format("%-25s%-20s%-25s%-10s%-15s%-28s\n", "Controller", "Status",
        "Currently Irrigating", "Active", "# Irrigations", "Next Irrigation Due");
    for (Controller c : irrigator.getControllers()) {
      if (input.toLowerCase().contains(c.getName().toLowerCase())) {
        System.out.format("%-25s%-20s%-25s%-10s%-15s%-28s\n", c.getName(), c.getStatus(),
            c.isIrrigating(), c.isActive(), c.getIrrigationResults().size(),
            statusFormatter.print(irrigator.nextIrrigationAt()));
      }
    }
  }

  private void printControllerStatusAll(String input) {
    DateTimeFormatter statusFormatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
    System.out.format("%-25s%-20s%-25s%-10s%-15s%-28s\n", "Controller", "Status",
        "Currently Irrigating", "Active", "# Irrigations", "Next Irrigation Due");
    for (Controller c : irrigator.getControllers()) {
      System.out.format("%-25s%-20s%-25s%-10s%-15s%-28s\n", c.getName(), c.getStatus(),
          c.isIrrigating(), c.isActive(), c.getIrrigationResults().size(),
          statusFormatter.print(irrigator.nextIrrigationAt()));
    }
  }

  private void printControllerZones(String input) {
    for (Controller c : irrigator.getControllers()) {
      if (input.toLowerCase().contains(c.getName().toLowerCase())) {
        System.out.format("%-30s%-6s%-6s\n", "Name", "Id", "Duration");
        for (Zone z : c.getZones()) {
          String durationAmount = "";
          if (z.getDuration() < 60) {
            durationAmount = z.getDuration() + "s";
          } else {
            durationAmount = (z.getDuration() / 60) + "m";
          }
          System.out.format("%-30s%-6s%-6s\n", z.getName(), z.getId(), durationAmount);
        }
      }
    }
  }

  private void printHelpMenu() {
    System.out.println("Available commands are:\n");
    for (String command : commands) {
      System.out.println(command);
    }
  }

  private void printIrrigationResults(String input) {
    for (Controller c : irrigator.getControllers()) {
      if (input.toLowerCase().contains(c.getName().toLowerCase())) {
        String[] amountString = input.split("results last ");
        int duration = Integer.parseInt(amountString[1]);
        Iterator<IrrigationResult> results = c.getIrrigationResults().iterator();
        for (int i = 0; i < duration; i++) {
          if (results.hasNext()) {
            System.out.println("\n" + results.next());
          }
        }
      }
    }
  }

  private void printVersion() {
    System.out.println("v" + irrigator.getVersion() + " by Ben Steele.\n");
  }

  private void printWeatherStationInfo(String input) {
    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
    System.out.format("%-25s%-15s%-10s%-12s%-20s%-20s\n", "Station", "Type", "Active", "# Records",
        "Oldest Record", "Newest Record");
    for (WeatherStation ws : irrigator.getWeatherStations()) {
      if (input.toLowerCase().contains(ws.getName().toLowerCase())) {
        System.out.format("%-25s%-15s%-10s%-12s%-20s%-20s\n", ws.getName(), ws.getType(),
            ws.isActive(), ws.getNumberOfRecords(), formatter.print(ws.getOldestRecordTime()),
            formatter.print(ws.getNewestRecordTime()));
      }
    }
  }

  private void printWeatherStationStatus(String input) {
    for (WeatherStation ws : irrigator.getWeatherStations()) {
      if (input.toLowerCase().contains(ws.getName().toLowerCase())) {
        System.out.println("Station: " + ws.getName());
        System.out.println("Record: " + ws.getStatus());
        System.out.println("Current Temp (C): " + ws.getCurrentTemperatureCelcius());
        System.out.println("Max Temp (C): " + ws.getMaxTemperatureCelcius());
        System.out.println("Min Temp (C): " + ws.getMinTemperatureCelcius());
        System.out.println("Current Temp (F): " + ws.getCurrentTemperatureFahrenheit());
        System.out.println("Max Temp (F): " + ws.getMaxTemperatureFahrenheit());
        System.out.println("Min Temp (F): " + ws.getMinTemperatureFahrenheit());
        System.out.println("Avg 7-day Temp (C): " + ws.getLastXDaysAvgTemperatureCelcius(7));
        System.out.println("Avg 7-day Temp (F): " + ws.getLastXDaysAvgTemperatureFahrenheit(7));
        System.out.println("Today's Rainfall (mm): " + ws.getTodaysRainfallMilliLitres());
        System.out.println("Today's Rainfall (in): " + ws.getTodaysRainfallInches());
        System.out.println("Last 7-day Rainfall (mm): " + ws.getLastXDaysRainfallMilliLitres(7));
        System.out.println("Last 7-day Rainfall (in): " + ws.getLastXDaysRainfallInches(7));
        System.out.println("Humidity (%): " + ws.getCurrentRelativeHumidityPercentage());
        System.out.println("Current Wind (kph): " + ws.getCurrentWindspeedKiloMetresPerHour());
        System.out.println("Current Wind (mph): " + ws.getCurrentWindspeedMilesPerHour());
        System.out.println("PoP 1-day (%): " + ws.getNextXDaysPercentageOfPrecipitation(1));
        System.out.println("PoP 3-day (%): " + ws.getNextXDaysPercentageOfPrecipitation(3));
        System.out.println("PoP 7-day (%): " + ws.getNextXDaysPercentageOfPrecipitation(7));
      }
    }
  }

  private void processConsoleInput(String input) {
    input = input.trim();

    if (input.matches("activate controller.*")) {
      activateController(input);
    }

    else if (input.matches("deactivate controller.*")) {
      deactivateController(input);
    }

    else if (input.matches("activate weatherstation.*")) {
      activateWeatherStation(input);
    }

    else if (input.matches("deactivate weatherstation.*")) {
      deactivateWeatherStation(input);
    }

    else if (input.matches("show controller all info")) {
      printControllerInfoAll(input);
    }

    else if (input.matches("show controller.*info")) {
      printControllerInfo(input);
    }

    else if (input.matches("show controller all status")) {
      printControllerStatusAll(input);
    }

    else if (input.matches("show controller.*results last \\d")) {
      printIrrigationResults(input);
    }

    else if (input.matches("show controller.*status")) {
      printControllerStatus(input);
    }

    else if (input.matches("show controller.*zones")) {
      printControllerZones(input);
    }

    else if (input.matches("show version")) {
      printVersion();
    }

    else if (input.matches("show weatherstation.*info")) {
      printWeatherStationInfo(input);
    }

    else if (input.matches("show weatherstation.*status")) {
      printWeatherStationStatus(input);
    }

    else if (input.matches("start irrigation all")) {
      startAllIrrigation();
    }

    else if (input.matches("stop irrigation all")) {
      stopAllIrrigation();
    }

    else if (input.matches("test controller.*zone.*duration \\d+")) {
      testZone(input);
    }

    else {
      printHelpMenu();
    }
  }

  private void startAllIrrigation() {
    for (Controller c : irrigator.getControllers()) {
      System.out.print("Starting default irrigation for " + c.getName() + ": ");
      c.irrigationRequest(c.generateDefaultIrrigationRequest());
      System.out.println("OK");
    }
  }

  public void startConsole() throws IOException {
    ConsoleReader reader = new ConsoleReader();
    reader.setPrompt("jirrigate> ");

    // Add the controllers to the auto-complete
    for (Controller c : irrigator.getControllers()) {
      commands.add("show controller " + c.getName() + " info");
      commands.add("show controller " + c.getName() + " results last <x>");
      commands.add("show controller " + c.getName() + " status");
      commands.add("show controller " + c.getName() + " zones");
      commands.add("deactivate controller " + c.getName());
      commands.add("activate controller " + c.getName());
    }
    commands.add("show controller all info");
    commands.add("show controller all status");

    // Add zone testing
    for (Controller c : irrigator.getControllers()) {
      for (Zone z : c.getZones()) {
        commands.add("test controller " + c.getName() + " zone " + z.getName()
            + " duration <seconds> ");
      }
    }

    // Add the weather stations to the auto-complete
    for (WeatherStation ws : irrigator.getWeatherStations()) {
      commands.add("show weatherstation " + ws.getName() + " info");
      commands.add("show weatherstation " + ws.getName() + " status");
      commands.add("deactivate weatherstation " + ws.getName());
      commands.add("activate weatherstation " + ws.getName());
    }

    // Add irrigator specific commands to the auto-complete
    commands.add("stop irrigation all");
    commands.add("start irrigation all");
    commands.add("show version");

    reader.addCompleter(new StringsCompleter(commands));

    String line;
    PrintWriter out = new PrintWriter(reader.getOutput());

    while ((line = reader.readLine()) != null) {

      if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
        out.println("Goodbye!");
        out.flush();
        System.exit(0);
      }

      processConsoleInput(line);
      out.flush();
    }
  }

  private void stopAllIrrigation() {
    for (Controller c : irrigator.getControllers()) {
      System.out.print("Stopping " + c.getName() + ": ");
      if (c.stopIrrigation()) {
        System.out.println("OK");
      } else {
        System.out.println("FAIL");
      }
    }
  }

  private void testZone(String input) {
    for (Controller c : irrigator.getControllers()) {
      if (input.toLowerCase().contains(c.getName().toLowerCase())) {
        for (Zone z : c.getZones()) {
          if (input.toLowerCase().contains(z.getName().toLowerCase())) {
            String[] durationString = input.split("duration ");
            long duration = Long.parseLong(durationString[1]);
            System.out.println("Testing " + c.getName() + " zone " + z.getName() + " for "
                + duration + " seconds");
            c.testZone(z, duration);
          }
        }
      }
    }
  }
}
