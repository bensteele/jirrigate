package org.bensteele.jirrigate.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.bensteele.jirrigate.controller.IrrigationResult.Result;
import org.bensteele.jirrigate.controller.zone.EtherRain8Zone;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * Tests for the {@link EtherRain8Controller}.
 * 
 * @author Ben Steele (ben@bensteele.org)
 */
public class EtherRain8ControllerTest {

  private final static String BUSY_STATUS_RESPONSE = "<html>\n <head>\n </head>\n <body>\n EtherRain Device Status <br>\n "
      + " un:EtherRain 8\n ma: 01.00.44.03.0A.01 <br>\n ac: <br>\n os: BZ <br>\n cs: OK <br>\n"
      + " rz: UK <br>\n ri: 0 <br>\n rn: 0 <br>\n </body>\n</html>";

  private final static String OK_STATUS_RESPONSE = "<html>\n <head>\n </head>\n <body>\n EtherRain Device Status <br>\n "
      + " un:EtherRain 8\n ma: 01.00.44.03.0A.01 <br>\n ac: <br>\n os: RD <br>\n cs: OK <br>\n"
      + " rz: OK <br>\n ri: 0 <br>\n rn: 0 <br>\n </body>\n</html>";

  private final static String LOGIN_OK_RESPONSE = "<html>\n<head>\n</head>\n<body>\nConfiguration: EtherRain 8 <br>\n ur: admin <br>\n"
      + "un: EthernRain-8 <br>\n hp: 80 <br>\n dh: 1 <br>\n ip: 192.168.1.204 <br>\n"
      + " nm: 255.255.255.0 <br>\n gw: 192.168.1.1 <br>\n dn: 68.94.156.1 <br>\n ma: 0:4:A3:1:0:0 <br>\n"
      + " sh: www.lawncheck.com <br>\n ac: 12-3456-789 <br>\n sv: 3.41 <br>\n av: 1.21 <br>\n"
      + " om: P <br>\n si: 23.13.123.78 <br>\n </body>\n</html>";

  private Controller c;

  @ClassRule
  public static WireMockRule wireMockRule = new WireMockRule(8888);

  @Before
  public void setUp() throws Exception {
    c = new EtherRain8Controller("Test Controller", InetAddress.getByName("127.0.0.1"), 8888,
        "admin", "pw", Logger.getLogger("test logger"));
    c.addZone(new EtherRain8Zone(c, "Test Zone 1", 100, "1"));
    c.addZone(new EtherRain8Zone(c, "Test Zone 2", 200, "2"));
    c.addZone(new EtherRain8Zone(c, "Test Zone 3", 300, "3"));
    c.addZone(new EtherRain8Zone(c, "Test Zone 4", 0, "4"));
    c.addZone(new EtherRain8Zone(c, "Test Zone 5", 300, "5"));
    c.addZone(new EtherRain8Zone(c, "Test Zone 6", 300, "6"));
    c.addZone(new EtherRain8Zone(c, "Test Zone 7", 300, "7"));
    c.addZone(new EtherRain8Zone(c, "Test Zone 8", 3000, "8"));

    stubFor(get(urlEqualTo("/ergetcfg.cgi?lu=admin&lp=pw")).willReturn(
        aResponse().withStatus(200).withBody(LOGIN_OK_RESPONSE)));

    stubFor(get(urlEqualTo("/result.cgi?xs")).willReturn(
        aResponse().withStatus(200).withBody(OK_STATUS_RESPONSE)));

    stubFor(get(urlEqualTo("/result.cgi?xi=0:1:3:5:0:5:5:5:50")).willReturn(
        aResponse().withStatus(200).withBody(OK_STATUS_RESPONSE)));

    Thread.sleep(500);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testgetStatus() throws UnknownHostException, InterruptedException {
    assertTrue(c.getStatus().matches("Device is ready"));
    stubFor(get(urlEqualTo("/result.cgi?xs")).willReturn(
        aResponse().withStatus(200).withBody(BUSY_STATUS_RESPONSE)));
    assertTrue(c.getStatus().matches("Device is busy"));
  }

  @Test(timeout = 20000)
  public void testStartIrrigationBusyFailureEtherRain() throws UnknownHostException,
      InterruptedException {
    IrrigationRequest request = c.generateDefaultIrrigationRequest();
    assertTrue(request.getZones().size() == 8);

    stubFor(get(urlEqualTo("/result.cgi?xs")).willReturn(
        aResponse().withStatus(200).withBody(BUSY_STATUS_RESPONSE)));

    long testTime = System.currentTimeMillis();
    c.irrigationRequest(request);

    IrrigationResult result = c.getIrrigationResults().take();

    assertTrue(result.getStartTime() >= testTime);
    assertTrue(result.getResult() == Result.FAIL);
    assertTrue(result.getMessage().matches("ERROR: Device is busy"));
    assertFalse(c.isIrrigating());
  }

  @Test(timeout = 20000)
  public void testStartIrrigationEtherRain() throws UnknownHostException, InterruptedException {
    IrrigationRequest request = c.generateDefaultIrrigationRequest();
    assertTrue(request.getZones().size() == 8);

    long testTime = System.currentTimeMillis();
    c.irrigationRequest(request);
    Thread.sleep(500);
    assertTrue(c.isIrrigating());

    IrrigationResult result = c.getIrrigationResults().take();

    assertTrue(result.getStartTime() >= testTime);
    assertTrue(result.getZones().size() == 7);
    assertTrue(result.getCommandSent().equals(
        "http://127.0.0.1:8888/result.cgi?xi=0:1:3:5:0:5:5:5:50"));
    assertTrue(result.getResult() == Result.SUCCESS);
    assertFalse(c.isIrrigating());
  }

  @Test(timeout = 20000)
  public void testStartIrrigationLoginFailureEtherRain() throws UnknownHostException,
      InterruptedException {
    stubFor(get(urlEqualTo("/ergetcfg.cgi?lu=admin&lp=pw")).willReturn(
        aResponse().withStatus(200).withBody("FAILED LOGIN")));

    IrrigationRequest request = c.generateDefaultIrrigationRequest();

    c.irrigationRequest(request);

    IrrigationResult result = c.getIrrigationResults().take();

    assertTrue(result.getResult() == Result.FAIL);
    assertTrue(result.getMessage().matches("ERROR: Login failed"));
    assertFalse(c.isIrrigating());
  }

  @Test(timeout = 20000)
  public void testStopIrrigationEtherRain() throws UnknownHostException, InterruptedException {
    IrrigationRequest request = c.generateDefaultIrrigationRequest();

    assertTrue(request.getZones().size() == 8);

    c.irrigationRequest(request);
    Thread.sleep(500);
    assertTrue(c.isIrrigating());

    stubFor(get(urlEqualTo("/result.cgi?xr")).willReturn(
        aResponse().withStatus(200).withBody(BUSY_STATUS_RESPONSE)));

    boolean stopped = c.stopIrrigation();
    assertTrue(stopped);
    assertFalse(c.isIrrigating());

    IrrigationResult result = c.getIrrigationResults().take();

    assertTrue(result.getResult() == Result.INTERRUPTED);
    assertTrue(result.getMessage().matches("INFO: Irrigation interrupted by stop request"));
    assertFalse(c.isIrrigating());
  }
}
