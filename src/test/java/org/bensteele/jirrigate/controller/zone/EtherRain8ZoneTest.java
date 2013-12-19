package org.bensteele.jirrigate.controller.zone;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.bensteele.jirrigate.controller.EtherRain8Controller;
import org.junit.After;
import org.junit.Test;

/**
 * Tests for the {@link EtherRain8Zone}.
 * 
 * @author Ben Steele (ben@bensteele.org)
 */
public class EtherRain8ZoneTest {

  @After
  public void tearDown() throws Exception {
    File log = new File("jirrigate.log");
    if (log.exists()) {
      log.delete();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIncorrectZoneDurationConfigurationEtherRain() throws IOException {
    // EtherRain Zone duration needs to be <= 240 minutes.
    long minutes240ToSeconds = (60 * 240);
    new EtherRain8Zone(new EtherRain8Controller(null, null, 0, "admin", "pw", null), "Test Zone",
        (minutes240ToSeconds + 100), "1");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIncorrectZoneIdConfigurationEtherRain() throws IOException {
    // EtherRain Zone id needs to be between 1 and 8 to be valid.
    new EtherRain8Zone(new EtherRain8Controller(null, null, 0, "admin", "pw", null), "Test Zone",
        100, "0");
    new EtherRain8Zone(new EtherRain8Controller(null, null, 0, "admin", "pw", null), "Test Zone",
        100, "9");
    new EtherRain8Zone(new EtherRain8Controller(null, null, 0, "admin", "pw", null), "Test Zone",
        100, "bad id");
  }

  @Test
  public void testNegativeZoneDurationConfigurationEtherRain() throws IOException {
    // Negative durations should be change to 0.
    Zone z = new EtherRain8Zone(new EtherRain8Controller(null, null, 0, "admin", "pw", null),
        "Test Zone", -100, "1");
    assertTrue(z.getDuration() == 0);
  }
}
