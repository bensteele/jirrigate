package org.bensteele.jirrigate.controller;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bensteele.jirrigate.controller.zone.Zone;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Result of an {@link IrrigationRequest} from a {@link Controller}.
 * 
 * @author Ben Steele (ben@bensteele.org)
 */
public class IrrigationResult {

  protected enum Result {
    FAIL, SUCCESS, INTERRUPTED
  }

  private Set<Zone> zones = new LinkedHashSet<Zone>();
  private final String commandSent;
  private String message;
  private Result result;
  private final long startTime;
  private long endTime;

  public IrrigationResult(String commandSent, Set<Zone> zones, long startTime) {
    this.commandSent = commandSent;
    this.startTime = startTime;
    this.zones = zones;
  }

  public String getCommandSent() {
    return this.commandSent;
  }

  public long getEndTime() {
    return endTime;
  }

  public String getMessage() {
    return message;
  }

  public Result getResult() {
    return this.result;
  }

  public long getStartTime() {
    return this.startTime;
  }

  public Set<Zone> getZones() {
    return this.zones;
  }

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setResult(Result result) {
    this.result = result;
  }

  public void setZones(Set<Zone> zones) {
    this.zones = zones;
  }

  @Override
  public String toString() {
    String result = "";
    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
    result += "Start: " + new DateTime(startTime).toString(formatter);
    result += "\nEnd: " + new DateTime(endTime).toString(formatter);
    result += "\nDuration: " + (((endTime - startTime) / 1000) / 60) + "m";
    result += "\nZones: ";
    for (Zone z : zones) {
      result += z.getName() + ",";
    }
    result += "\nResult: " + this.result;
    result += "\nCommand Sent: " + commandSent;
    result += "\nMessage: " + message;
    return result;
  }
}
