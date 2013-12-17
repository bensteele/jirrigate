package org.bensteele.jirrigate.controller.zone;

import org.bensteele.jirrigate.controller.Controller;

/**
 * An abstract class for an irrigation controller zone. A complete workable implementation of a
 * class that extends this will <i>successfully</i> implement all the abstract methods presented.
 * <p>
 * This Object will be consumed by a {@link Controller}.
 * <p>
 * See {@link EtherRain8Zone} for a reference implementation of this class.
 * 
 * @author Ben Steele (ben@bensteele.org)
 */
public abstract class Zone {

  private final String name;
  private final Controller controller;
  protected long durationInSeconds;
  protected String id;

  public Zone(Controller controller, String name, long durationInSeconds, String id) {
    this.controller = controller;
    this.name = name;
    setDuration(durationInSeconds);
    setId(id);
    controller.addZone(this);
  }

  public Controller getController() {
    return this.controller;
  }

  public long getDuration() {
    return this.durationInSeconds;
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public abstract void setDuration(long durationInSeconds);

  public abstract void setId(String id);
}
