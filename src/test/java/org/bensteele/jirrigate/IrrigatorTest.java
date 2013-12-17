package org.bensteele.jirrigate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

import javax.naming.ConfigurationException;

import junit.framework.TestCase;

import org.bensteele.jirrigate.controller.Controller;
import org.bensteele.jirrigate.controller.EtherRain8Controller;
import org.bensteele.jirrigate.controller.zone.Zone;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for the {@link Irrigator}.
 * 
 * @author Ben Steele (ben@bensteele.org)
 */
public class IrrigatorTest extends TestCase {

  private static final String TMP_CONFIG_FILE = "jirrigate-test.properties";

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    // Delete any temporary properties file stored.
    File config = new File(TMP_CONFIG_FILE);
    if (config.exists()) {
      config.delete();
    }
    File log = new File(System.getProperty("user.dir") + File.pathSeparator + "jirrigate.log");
    if (log.exists()) {
      log.delete();
    }
  }

  @Test
  public void testBadControllerType1Configuration() throws ConfigurationException, IOException {
    Properties config = new Properties();
    // Dummy irrigation controller
    config.setProperty("controller1_name", "MY_controller1");
    config.setProperty("controller1_ip", "172.17.1.1");
    config.setProperty("controller1_port", "8888");
    config.setProperty("controller1_username", "admin1");
    config.setProperty("controller1_password", "password1");
    config.setProperty("controller1_type", "invalid_controller_type");

    Irrigator i = new Irrigator(config);
    try {
      i.processControllerConfiguration();
      // Should not hit this line as we are expecting the ConfigurationException.
      fail();
    } catch (ConfigurationException e) {
      assertTrue(e.getMessage().matches(
          "ERROR: invalid_controller_type is not a valid controller type for controller1_type"));
    }
  }

  @Test
  public void testBadControllerType2Configuration() throws ConfigurationException, IOException {
    Properties config = new Properties();
    // Dummy irrigation controller
    config.setProperty("controller1_name", "MY_controller1");
    config.setProperty("controller1_ip", "172.17.1.1");
    config.setProperty("controller1_port", "8888");
    config.setProperty("controller1_username", "admin1");
    config.setProperty("controller1_password", "password1");

    Irrigator i = new Irrigator(config);
    try {
      i.processControllerConfiguration();
      // Should not hit this line as we are expecting the ConfigurationException.
      fail();
    } catch (ConfigurationException e) {
      assertTrue(e.getMessage().matches("ERROR: no controller type for controller1_type"));
    }
  }

  @Test
  public void testBadIP1ControllerConfiguration() throws ConfigurationException, IOException {
    Properties config = new Properties();
    // Dummy irrigation controller
    config.setProperty("controller1_name", "MY_controller1");
    config.setProperty("controller1_ip", "172.17.j.1");
    config.setProperty("controller1_port", "8888");
    config.setProperty("controller1_username", "admin1");
    config.setProperty("controller1_password", "password1");
    config.setProperty("controller1_type", "etherrain8");

    Irrigator i = new Irrigator(config);
    try {
      i.processControllerConfiguration();
      // Should not hit this line as we are expecting the ConfigurationException.
      fail();
    } catch (ConfigurationException e) {
      assertTrue(e.getMessage().matches("ERROR: Unable to parse IP address of controller1_ip"));
    }
  }

  @Test
  public void testBadIP2ControllerConfiguration() throws ConfigurationException, IOException {
    Properties config = new Properties();
    // Dummy irrigation controller
    config.setProperty("controller1_name", "MY_controller1");
    config.setProperty("controller1_port", "8888");
    config.setProperty("controller1_username", "admin1");
    config.setProperty("controller1_password", "password1");
    config.setProperty("controller1_type", "etherrain8");

    Irrigator i = new Irrigator(config);
    try {
      i.processControllerConfiguration();
      // Should not hit this line as we are expecting the ConfigurationException.
      fail();
    } catch (ConfigurationException e) {
      assertTrue(e.getMessage().matches("ERROR: Unable to parse IP address of controller1_ip"));
    }
  }

  @Test
  public void testBadPort1ControllerConfiguration() throws ConfigurationException, IOException {
    Properties config = new Properties();
    // Dummy irrigation controller
    config.setProperty("controller1_name", "MY_controller1");
    config.setProperty("controller1_ip", "172.17.8.1");
    config.setProperty("controller1_port", "BAD_PORT");
    config.setProperty("controller1_username", "admin1");
    config.setProperty("controller1_password", "password1");
    config.setProperty("controller1_type", "etherrain8");

    Irrigator i = new Irrigator(config);
    try {
      i.processControllerConfiguration();
      // Should not hit this line as we are expecting the ConfigurationException.
      fail();
    } catch (ConfigurationException e) {
      assertTrue(e.getMessage().matches("ERROR: Unable to parse port of controller1_port"));
    }
  }

  @Test
  public void testBadPort2ControllerConfiguration() throws ConfigurationException, IOException {
    Properties config = new Properties();
    // Dummy irrigation controller
    config.setProperty("controller1_name", "MY_controller1");
    config.setProperty("controller1_ip", "172.17.8.1");
    config.setProperty("controller1_username", "admin1");
    config.setProperty("controller1_password", "password1");
    config.setProperty("controller1_type", "etherrain8");

    Irrigator i = new Irrigator(config);
    try {
      i.processControllerConfiguration();
      // Should not hit this line as we are expecting the ConfigurationException.
      fail();
    } catch (ConfigurationException e) {
      assertTrue(e.getMessage().matches("ERROR: Unable to parse port of controller1_port"));
    }
  }

  @Test
  public void testBadWateringDays1Configuration() throws ConfigurationException, IOException {
    Properties config = new Properties();
    config.setProperty("watering_days", "BAD_DAY");

    Irrigator i = new Irrigator(config);

    try {
      i.processWateringDaysConfiguration();
      // Should not hit this line as we are expecting the ConfigurationException.
      fail();
    } catch (ConfigurationException e) {
      assertTrue(e.getMessage().matches(
          "ERROR: Could not read watering_days from configuration file"));
    }
  }

  @Test
  public void testBadWateringDays2Configuration() throws ConfigurationException, IOException {
    Properties config = new Properties();

    Irrigator i = new Irrigator(config);

    try {
      i.processWateringDaysConfiguration();
      // Should not hit this line as we are expecting the ConfigurationException.
      fail();
    } catch (ConfigurationException e) {
      assertTrue(e.getMessage().matches(
          "ERROR: Could not read watering_days from configuration file"));
    }
  }

  @Test
  public void testBadWateringDaysStartTime1Configuration() throws ConfigurationException,
      IOException {
    Properties config = new Properties();
    config.setProperty("watering_start_time", "BAD_TIME");

    Irrigator i = new Irrigator(config);

    try {
      i.processWateringStartTimeConfiguration();
      // Should not hit this line as we are expecting the ConfigurationException.
      fail();
    } catch (ConfigurationException e) {
      assertTrue(e.getMessage().matches("ERROR: Unable to parse watering_start_time value"));
    }
  }

  @Test
  public void testBadWateringDaysStartTime2Configuration() throws ConfigurationException,
      IOException {
    Properties config = new Properties();

    Irrigator i = new Irrigator(config);

    try {
      i.processWateringStartTimeConfiguration();
      // Should not hit this line as we are expecting the ConfigurationException.
      fail();
    } catch (ConfigurationException e) {
      assertTrue(e.getMessage().matches(
          "ERROR: Could not read watering_start_time from configuration file"));
    }
  }

  @Test
  public void testBadZoneDuration1Configuration() throws ConfigurationException, IOException {
    Properties config = new Properties();
    // Dummy irrigation controller
    config.setProperty("controller1_name", "MY_controller1");
    config.setProperty("controller1_ip", "172.17.1.1");
    config.setProperty("controller1_port", "8888");
    config.setProperty("controller1_username", "admin1");
    config.setProperty("controller1_password", "password1");
    config.setProperty("controller1_type", "etherrain8");
    config.setProperty("controller1_zone1_name", "Front Garden");
    config.setProperty("controller1_zone1_id", "1");
    // Unsupported delimiter "d".
    config.setProperty("controller1_zone1_duration", "241d");

    Irrigator i = new Irrigator(config);
    try {
      i.processControllerConfiguration();
      // Should not hit this line as we are expecting the ConfigurationException.
      fail();
    } catch (ConfigurationException e) {
      assertTrue(e.getMessage().matches(
          "ERROR: Unable to parse duration of controller1_zone1_duration"));
    }
  }

  @Test
  public void testBadZoneDuration2Configuration() throws ConfigurationException, IOException {
    Properties config = new Properties();
    // Dummy irrigation controller
    config.setProperty("controller1_name", "MY_controller1");
    config.setProperty("controller1_ip", "172.17.1.1");
    config.setProperty("controller1_port", "8888");
    config.setProperty("controller1_username", "admin1");
    config.setProperty("controller1_password", "password1");
    config.setProperty("controller1_type", "etherrain8");
    config.setProperty("controller1_zone1_name", "Front Garden");
    config.setProperty("controller1_zone1_id", "1");
    config.setProperty("controller1_zone1_duration", "BAD_DURATION");

    Irrigator i = new Irrigator(config);
    try {
      i.processControllerConfiguration();
      // Should not hit this line as we are expecting the ConfigurationException.
      fail();
    } catch (ConfigurationException e) {
      assertTrue(e.getMessage().matches(
          "ERROR: Unable to parse duration of controller1_zone1_duration"));
    }
  }

  @Test
  public void testControllerConfiguration() throws ConfigurationException, IOException {
    Properties config = new Properties();
    // Dummy irrigation controller 1 with 2 zones.
    config.setProperty("controller1_name", "MY_controller1");
    config.setProperty("controller1_ip", "172.17.8.1");
    config.setProperty("controller1_port", "8888");
    config.setProperty("controller1_username", "admin1");
    config.setProperty("controller1_password", "password1");
    config.setProperty("controller1_type", "etherrain8");
    config.setProperty("controller1_zone1_name", "Front Garden");
    config.setProperty("controller1_zone1_id", "1");
    config.setProperty("controller1_zone1_duration", "30s");
    config.setProperty("controller1_zone2_name", "Side Garden");
    config.setProperty("controller1_zone2_id", "2");
    config.setProperty("controller1_zone2_duration", "20M");

    // Dummy irrigation controller 2 with 2 zones.
    config.setProperty("controller2_name", "MY_controller2");
    config.setProperty("controller2_ip", "172.17.8.2");
    config.setProperty("controller2_port", "80");
    config.setProperty("controller2_username", "admin2");
    config.setProperty("controller2_password", "password2");
    config.setProperty("controller2_type", "etherrain8");
    config.setProperty("controller2_zone1_name", "Rear Garden");
    config.setProperty("controller2_zone1_id", "5");
    config.setProperty("controller2_zone1_duration", "3h");
    config.setProperty("controller2_zone2_name", "Pagola");
    config.setProperty("controller2_zone2_id", "6");
    // NOTE: No duration set for zone 2, should default to 0.

    Irrigator i = new Irrigator(config);
    i.processControllerConfiguration();

    // Validate controllers get parsed and installed into the Irrigator.
    assertTrue(i.getControllers().size() == 2);
    assertNotNull(i.getController(config.getProperty("controller1_name")));
    assertNotNull(i.getController(config.getProperty("controller2_name")));
    assertNull(i.getController("non_existant_controller"));

    // Validate all primary fields in the controller configuration.
    // Controller 1
    Controller c1 = i.getController(config.getProperty("controller1_name"));
    assertTrue(config.getProperty("controller1_ip").matches(c1.getIpAddress().getHostAddress()));
    assertTrue(Integer.parseInt(config.getProperty("controller1_port")) == c1.getPort());
    assertTrue(config.getProperty("controller1_username").matches(c1.getUsername()));
    assertTrue(config.getProperty("controller1_password").matches(c1.getPassword()));
    // Controller 1 is an EtherRain controller.
    assertTrue(c1.getClass().equals(EtherRain8Controller.class));

    // Controller 2
    Controller c2 = i.getController(config.getProperty("controller2_name"));
    assertTrue(config.getProperty("controller2_ip").matches(c2.getIpAddress().getHostAddress()));
    assertTrue(Integer.parseInt(config.getProperty("controller2_port")) == c2.getPort());
    assertTrue(config.getProperty("controller2_username").matches(c2.getUsername()));
    assertTrue(config.getProperty("controller2_password").matches(c2.getPassword()));
    // Controller 2 is an EtherRain controller.
    assertTrue(c2.getClass().equals(EtherRain8Controller.class));

    // Validate zones were created from controller configuration.
    assertTrue(c1.getZones().size() == 2);

    // Controller 1 Zone 1
    Zone z1 = c1.getZone(config.getProperty("controller1_zone1_name"));
    assertNotNull(z1);
    assertTrue(z1.getName().matches(config.getProperty("controller1_zone1_name")));
    assertTrue(z1.getController() == c1);
    assertTrue(z1.getId().matches(config.getProperty("controller1_zone1_id")));
    // 30 second configuration timer.
    assertTrue(z1.getDuration() == 30);

    // Controller 1 Zone 2
    Zone z2 = c1.getZone(config.getProperty("controller1_zone2_name"));
    assertNotNull(z2);
    assertTrue(z2.getName().matches(config.getProperty("controller1_zone2_name")));
    assertTrue(z2.getController() == c1);
    assertTrue(z2.getId().matches(config.getProperty("controller1_zone2_id")));
    // 20 minute configuration timer.
    assertTrue(z2.getDuration() == (20 * 60));

    // Controller 2 Zone 1
    Zone z3 = c2.getZone(config.getProperty("controller2_zone1_name"));
    assertNotNull(z3);
    assertTrue(z3.getName().matches(config.getProperty("controller2_zone1_name")));
    assertTrue(z3.getController() == c2);
    assertTrue(z3.getId().matches(config.getProperty("controller2_zone1_id")));
    // 1 hour configuration timer.
    assertTrue(z3.getDuration() == ((3 * 60) * 60));

    // Controller 2 Zone 2
    Zone z4 = c2.getZone(config.getProperty("controller2_zone2_name"));
    assertNotNull(z4);
    assertTrue(z4.getName().matches(config.getProperty("controller2_zone2_name")));
    assertTrue(z4.getController() == c2);
    assertTrue(z4.getId().matches(config.getProperty("controller2_zone2_id")));
    // 0 configuration timer(disabled).
    assertTrue(z4.getDuration() == 0);
  }

  @Test
  public void testEtherRainBadZoneDurationConfiguration() throws ConfigurationException,
      IOException {
    Properties config = new Properties();
    // Dummy irrigation controller
    config.setProperty("controller1_name", "MY_controller1");
    config.setProperty("controller1_ip", "172.17.1.1");
    config.setProperty("controller1_port", "8888");
    config.setProperty("controller1_username", "admin1");
    config.setProperty("controller1_password", "password1");
    config.setProperty("controller1_type", "etherrain8");
    config.setProperty("controller1_zone1_name", "Front Garden");
    config.setProperty("controller1_zone1_id", "1");
    config.setProperty("controller1_zone1_duration", "241m");

    Irrigator i = new Irrigator(config);
    try {
      i.processControllerConfiguration();
      // Should not hit this line as we are expecting the ConfigurationException.
      fail();
    } catch (ConfigurationException e) {
      assertTrue(e.getMessage().matches(
          "ERROR: Zone duration must be between 0 and 240 minutes for EtherRain8 controllers"));
    }
  }

  @Test
  public void testEtherRainBadZoneId1Configuration() throws ConfigurationException, IOException {
    Properties config = new Properties();
    // Dummy irrigation controller
    config.setProperty("controller1_name", "MY_controller1");
    config.setProperty("controller1_ip", "172.17.1.1");
    config.setProperty("controller1_port", "8888");
    config.setProperty("controller1_username", "admin1");
    config.setProperty("controller1_password", "password1");
    config.setProperty("controller1_type", "etherrain8");
    config.setProperty("controller1_zone1_name", "Front Garden");
    config.setProperty("controller1_zone1_id", "9");
    config.setProperty("controller1_zone1_duration", "30s");

    Irrigator i = new Irrigator(config);
    try {
      i.processControllerConfiguration();
      // Should not hit this line as we are expecting the ConfigurationException.
      fail();
    } catch (ConfigurationException e) {
      assertTrue(e.getMessage().matches(
          "ERROR: Zone id must be between 1 and 8 for EtherRain8 controllers"));
    }
  }

  @Test
  public void testEtherRainBadZoneId2Configuration() throws ConfigurationException, IOException {
    Properties config = new Properties();
    // Dummy irrigation controller
    config.setProperty("controller1_name", "MY_controller1");
    config.setProperty("controller1_ip", "172.17.1.1");
    config.setProperty("controller1_port", "8888");
    config.setProperty("controller1_username", "admin1");
    config.setProperty("controller1_password", "password1");
    config.setProperty("controller1_type", "etherrain8");
    config.setProperty("controller1_zone1_name", "Front Garden");
    config.setProperty("controller1_zone1_id", "BAD_ID");
    config.setProperty("controller1_zone1_duration", "30s");

    Irrigator i = new Irrigator(config);
    try {
      i.processControllerConfiguration();
      // Should not hit this line as we are expecting the ConfigurationException.
      fail();
    } catch (ConfigurationException e) {
      assertTrue(e.getMessage().matches(
          "ERROR: Zone id must be between 1 and 8 for EtherRain8 controllers"));
    }
  }

  @Test
  public void testEtherRainMissingZoneIdConfiguration() throws ConfigurationException, IOException {
    Properties config = new Properties();
    // Dummy irrigation controller
    config.setProperty("controller1_name", "MY_controller1");
    config.setProperty("controller1_ip", "172.17.1.1");
    config.setProperty("controller1_port", "8888");
    config.setProperty("controller1_username", "admin1");
    config.setProperty("controller1_password", "password1");
    config.setProperty("controller1_type", "etherrain8");
    config.setProperty("controller1_zone1_name", "Front Garden");
    config.setProperty("controller1_zone1_duration", "30s");

    Irrigator i = new Irrigator(config);
    try {
      i.processControllerConfiguration();
      // Should not hit this line as we are expecting the ConfigurationException.
      fail();
    } catch (ConfigurationException e) {
      assertTrue(e.getMessage().matches(
          "ERROR: Could not find configuration for controller1_zone1_id"));
    }
  }

  @Test
  public void testIsIrrigatingFalse() throws ConfigurationException, IOException {
    Properties config = new Properties();
    // Dummy irrigation controller 1 with 2 zones.
    config.setProperty("controller1_name", "MY_controller1");
    config.setProperty("controller1_ip", "172.17.8.1");
    config.setProperty("controller1_port", "8888");
    config.setProperty("controller1_username", "admin1");
    config.setProperty("controller1_password", "password1");
    config.setProperty("controller1_type", "etherrain8");
    config.setProperty("controller1_zone1_name", "Front Garden");
    config.setProperty("controller1_zone1_id", "1");
    config.setProperty("controller1_zone1_duration", "30s");
    config.setProperty("controller1_zone2_name", "Side Garden");
    config.setProperty("controller1_zone2_id", "2");
    config.setProperty("controller1_zone2_duration", "20M");

    // Dummy irrigation controller 2 with 2 zones.
    config.setProperty("controller2_name", "MY_controller2");
    config.setProperty("controller2_ip", "172.17.8.2");
    config.setProperty("controller2_port", "80");
    config.setProperty("controller2_username", "admin2");
    config.setProperty("controller2_password", "password2");
    config.setProperty("controller2_type", "etherrain8");
    config.setProperty("controller2_zone1_name", "Rear Garden");
    config.setProperty("controller2_zone1_id", "5");
    config.setProperty("controller2_zone1_duration", "3h");
    config.setProperty("controller2_zone2_name", "Pagola");
    config.setProperty("controller2_zone2_id", "6");
    // NOTE: No duration set for zone 2, should default to 0.

    Irrigator i = new Irrigator(config);
    i.processControllerConfiguration();

    assertFalse(i.isIrrigating());
  }

  @Test
  public void testIsIrrigatingTrue() throws ConfigurationException, IOException {
    Properties config = new Properties();
    // Dummy irrigation controller 1 with 2 zones.
    config.setProperty("controller1_name", "MY_controller1");
    config.setProperty("controller1_ip", "172.17.8.1");
    config.setProperty("controller1_port", "8888");
    config.setProperty("controller1_username", "admin1");
    config.setProperty("controller1_password", "password1");
    config.setProperty("controller1_type", "etherrain8");
    config.setProperty("controller1_zone1_name", "Front Garden");
    config.setProperty("controller1_zone1_id", "1");
    config.setProperty("controller1_zone1_duration", "30s");
    config.setProperty("controller1_zone2_name", "Side Garden");
    config.setProperty("controller1_zone2_id", "2");
    config.setProperty("controller1_zone2_duration", "20M");

    // Dummy irrigation controller 2 with 2 zones.
    config.setProperty("controller2_name", "MY_controller2");
    config.setProperty("controller2_ip", "172.17.8.2");
    config.setProperty("controller2_port", "80");
    config.setProperty("controller2_username", "admin2");
    config.setProperty("controller2_password", "password2");
    config.setProperty("controller2_type", "etherrain8");
    config.setProperty("controller2_zone1_name", "Rear Garden");
    config.setProperty("controller2_zone1_id", "5");
    config.setProperty("controller2_zone1_duration", "3h");
    config.setProperty("controller2_zone2_name", "Pagola");
    config.setProperty("controller2_zone2_id", "6");
    // NOTE: No duration set for zone 2, should default to 0.

    Irrigator i = new Irrigator(config);
    i.processControllerConfiguration();

    i.getController("MY_controller2").setIsIrrigating(true);

    assertTrue(i.isIrrigating());
  }

  @Test
  public void testMainArgsParser() throws FileNotFoundException, IOException {
    final String[] args = { "--config=" + TMP_CONFIG_FILE };
    Properties config = new Properties();
    config.setProperty("test", "test");
    // Write the config to file
    config.store(new FileOutputStream(TMP_CONFIG_FILE), null);
    Properties p = Irrigator.parseConfigurationFile(args);
    assertNotNull(p);
    assertFalse(p.isEmpty());
  }

  @Test
  public void testTimeToIrrigateFalse1() throws ConfigurationException, IOException {
    Properties config = new Properties();
    config.setProperty("watering_days", "Monday,saturday");
    config.setProperty("watering_start_time", "23:10");

    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
    // 2nd December 2013 is a Monday.
    DateTime dt = formatter.parseDateTime("02/12/2013 23:11:15");
    DateTimeUtils.setCurrentMillisFixed(dt.getMillis());

    Irrigator i = new Irrigator(config);
    i.processWateringDaysConfiguration();
    i.processWateringStartTimeConfiguration();

    assertFalse(i.timeToIrrigate());
  }

  @Test
  public void testTimeToIrrigateFalse2() throws ConfigurationException, IOException {
    Properties config = new Properties();
    config.setProperty("watering_days", "tuesday,saturday");
    config.setProperty("watering_start_time", "23:11");

    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
    // 2nd December 2013 is a Monday.
    DateTime dt = formatter.parseDateTime("02/12/2013 23:11:15");
    DateTimeUtils.setCurrentMillisFixed(dt.getMillis());

    Irrigator i = new Irrigator(config);
    i.processWateringDaysConfiguration();
    i.processWateringStartTimeConfiguration();

    assertFalse(i.timeToIrrigate());
  }

  @Test
  public void testTimeToIrrigateTrue() throws ConfigurationException, IOException {
    Properties config = new Properties();
    config.setProperty("watering_days", "Monday,saturday");
    config.setProperty("watering_start_time", "23:11");

    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
    // 2nd December 2013 is a Monday.
    DateTime dt = formatter.parseDateTime("02/12/2013 23:11:15");
    DateTimeUtils.setCurrentMillisFixed(dt.getMillis());

    Irrigator i = new Irrigator(config);
    i.processWateringDaysConfiguration();
    i.processWateringStartTimeConfiguration();

    assertTrue(i.timeToIrrigate());
  }

  @Test
  public void testNextIrrigationAt() throws ConfigurationException, IOException {
    Properties config = new Properties();
    config.setProperty("watering_days", "Monday,saturday");
    config.setProperty("watering_start_time", "23:11");

    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
    // 3rd December 2013 is a Tuesday.
    DateTime dt = formatter.parseDateTime("03/12/2013 21:11");
    DateTimeUtils.setCurrentMillisFixed(dt.getMillis());

    Irrigator i = new Irrigator(config);
    i.processWateringDaysConfiguration();
    i.processWateringStartTimeConfiguration();

    assertTrue(formatter.print(i.nextIrrigationAt()).equals("07/12/2013 23:11"));
  }

  @Test
  public void testWateringDaysConfiguration() throws ConfigurationException, IOException {
    Properties config = new Properties();
    config.setProperty("watering_days",
        "sunday ,Monday,tuesday,WEDnesDAY,thursday , FRIDAY,saturday");

    Irrigator i = new Irrigator(config);
    i.processWateringDaysConfiguration();

    assertTrue(i.getWateringDays().contains(Calendar.SUNDAY));
    assertTrue(i.getWateringDays().contains(Calendar.MONDAY));
    assertTrue(i.getWateringDays().contains(Calendar.TUESDAY));
    assertTrue(i.getWateringDays().contains(Calendar.WEDNESDAY));
    assertTrue(i.getWateringDays().contains(Calendar.THURSDAY));
    assertTrue(i.getWateringDays().contains(Calendar.FRIDAY));
    assertTrue(i.getWateringDays().contains(Calendar.SATURDAY));
  }

  @Test
  public void testWateringStartTimeConfiguration() throws ConfigurationException, IOException {
    Properties config = new Properties();
    config.setProperty("watering_start_time", "23:11");

    Irrigator i = new Irrigator(config);
    i.processWateringStartTimeConfiguration();

    assertTrue(i.getWateringStartTime().getHourOfDay() == 23);
    assertTrue(i.getWateringStartTime().getMinuteOfHour() == 11);

    Properties config2 = new Properties();
    config2.setProperty("watering_start_time", "9:09");

    i = new Irrigator(config2);
    i.processWateringStartTimeConfiguration();

    assertTrue(i.getWateringStartTime().getHourOfDay() == 9);
    assertTrue(i.getWateringStartTime().getMinuteOfHour() == 9);
  }

  @Test
  public void testWeatherStationConfiguration() throws ConfigurationException, IOException {
    Properties config = new Properties();
    // Dummy weather underground personal weather station.
    config.setProperty("weatherstation1_name", "MY_weatherstation");
    config.setProperty("weatherstation1_type", "wunderground");
    config.setProperty("weatherstation1_stationid", "IMOUTHAU666");
    config.setProperty("weatherstation1_api", "abcdefgh123456");

    Irrigator i = new Irrigator(config);
    i.processWeatherStationConfiguration();

    // Validate weather station got parsed and installed into the Irrigator.
    assertTrue(i.getWeatherStations().size() == 1);
    assertNotNull(i.getWeatherStation("MY_weatherstation"));
  }

  @Test
  public void testWeatherThresholdConfigurationMetric() throws ConfigurationException, IOException {
    Properties config = new Properties();
    // Dummy weather underground personal weather station.
    config.setProperty("weatherstation1_name", "MY_weatherstation");
    config.setProperty("weatherstation1_type", "wunderground");
    config.setProperty("weatherstation1_stationid", "IMOUTHAU666");
    config.setProperty("weatherstation1_api", "abcdefgh123456");
    // Dummy weather threshold settings.
    config.setProperty("weather_threshold_rain_days_to_look_back", "7");
    config.setProperty("weather_threshold_total_rain_amount", "3mm");
    config.setProperty("weather_threshold_current_rain_amount", "1mm");
    config.setProperty("weather_threshold_rain_days_to_look_ahead", "7");
    config.setProperty("weather_threshold_pop", "40");
    config.setProperty("weather_threshold_wind_speed", "4kph");
    config.setProperty("weather_threshold_max_temp", "30C");
    config.setProperty("weather_threshold_min_temp", "4C");

    Irrigator i = new Irrigator(config);
    i.processWeatherStationConfiguration();
    i.processWeatherThresholdConfiguration();

    assertTrue(i.getRainDaysToLookBack() == 7);
    assertTrue(i.getTotalRainAmountThresholdInMilliMetres() == 3);
    assertTrue(i.getCurrentRainAmountThresholdInMilliMetres() == 1);
    assertTrue(i.getRainDaysToLookAhead() == 7);
    assertTrue(i.getPopThreshold() == 40);
    assertTrue(i.getCurrentWindThresholdInKmph() == 4);
    assertTrue(i.getMaxTempThresholdInCelcius() == 30);
    assertTrue(i.getMinTempThresholdInCelcius() == 4);
  }

  @Test
  public void testWeatherThresholdConfigurationImperial() throws ConfigurationException,
      IOException {
    Properties config = new Properties();
    // Dummy weather underground personal weather station.
    config.setProperty("weatherstation1_name", "MY_weatherstation");
    config.setProperty("weatherstation1_type", "wunderground");
    config.setProperty("weatherstation1_stationid", "IMOUTHAU666");
    config.setProperty("weatherstation1_api", "abcdefgh123456");
    // Dummy weather threshold settings.
    config.setProperty("weather_threshold_rain_days_to_look_back", "7");
    config.setProperty("weather_threshold_total_rain_amount", "3in");
    config.setProperty("weather_threshold_current_rain_amount", "1in");
    config.setProperty("weather_threshold_rain_days_to_look_ahead", "7");
    config.setProperty("weather_threshold_pop", "40");
    config.setProperty("weather_threshold_wind_speed", "4mph");
    config.setProperty("weather_threshold_max_temp", "80F");
    config.setProperty("weather_threshold_min_temp", "4F");

    Irrigator i = new Irrigator(config);
    i.processWeatherStationConfiguration();
    i.processWeatherThresholdConfiguration();

    assertTrue(i.getRainDaysToLookBack() == 7);
    assertTrue(i.getTotalRainAmountThresholdInMilliMetres() == 76.2);
    assertTrue(i.getCurrentRainAmountThresholdInMilliMetres() == 25.4);
    assertTrue(i.getRainDaysToLookAhead() == 7);
    assertTrue(i.getPopThreshold() == 40);
    assertTrue(i.getCurrentWindThresholdInKmph() == 6.4);
    assertTrue(i.getMaxTempThresholdInCelcius() == 26.67);
    assertTrue(i.getMinTempThresholdInCelcius() == -15.56);
  }
}
