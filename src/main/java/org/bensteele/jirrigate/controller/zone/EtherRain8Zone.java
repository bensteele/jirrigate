package org.bensteele.jirrigate.controller.zone;

import org.bensteele.jirrigate.controller.Controller;
import org.bensteele.jirrigate.controller.EtherRain8Controller;

/**
 * An EtherRain8 implementation of a {@link Zone}.
 * <p>
 * {@link http://www.quicksmart.com/qs_etherrain.html}
 * 
 * @author Ben Steele (ben@bensteele.org)
 */
public class EtherRain8Zone extends Zone {

  private static final String ZONE_ID_ERROR = "ERROR: Zone id must be between"
      + " 1 and 8 for EtherRain8 controllers";
  private static final String ZONE_DURATION_ERROR = "ERROR: Zone duration must be between"
      + " 0 and 240 minutes for EtherRain8 controllers";

  public EtherRain8Zone(Controller controller, String name, long durationInSeconds, String id) {
    super(controller, name, durationInSeconds, id);
  }

  @Override
  public void setId(String id) {
    // EtherRain8 zone validation.
    if (getController().getClass().equals(EtherRain8Controller.class)) {
      // EtherRain8 zone id must be between 1 and 8.
      try {
        if (Integer.parseInt(id) < 1 || Integer.parseInt(id) > 8) {
          throw new IllegalArgumentException(ZONE_ID_ERROR);
        }
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(ZONE_ID_ERROR);
      }
    }
    this.id = id;
  }

  @Override
  public void setDuration(long durationInSeconds) {
    // Get rid of any negative values.
    if (durationInSeconds < 0) {
      durationInSeconds = 0;
    }

    // EtherRain8 zone validation.
    if (getController().getClass().equals(EtherRain8Controller.class)) {
      // EtherRain8 only accepts durations of up to 240 minutes.
      if ((durationInSeconds / 60) > 240) {
        throw new IllegalArgumentException(ZONE_DURATION_ERROR);
      }
    }
    this.durationInSeconds = durationInSeconds;
  }
}
