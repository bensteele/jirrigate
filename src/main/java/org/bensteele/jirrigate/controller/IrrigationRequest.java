package org.bensteele.jirrigate.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bensteele.jirrigate.controller.zone.Zone;

/**
 * Provides information to a {@link Controller} on what zones and duration to irrigate. This is a
 * one off irrigation request.
 * 
 * @author Ben Steele (ben@bensteele.org)
 */
public class IrrigationRequest {

  // <Zone,Long> where long is the duration in this particular request for the zone.
  private final Map<Zone, Long> zones = new LinkedHashMap<Zone, Long>();

  public Map<Zone, Long> getZones() {
    return this.zones;
  }

  @Override
  public String toString() {
    String request = "Zone(s): ";
    long duration = 0;
    for (Zone z : zones.keySet()) {
      request += "<" + z.getName() + ",";
      if (zones.get(z) < 60) {
        request += zones.get(z) + "s> ";
      } else {
        request += (zones.get(z) / 60) + "m> ";
      }
      duration += zones.get(z);
    }
    if (duration < 60) {
      request += "\nTotal duration: " + duration + "s";
    } else {
      request += "\nTotal duration: " + (duration / 60) + "m";
    }
    return request;
  }
}
