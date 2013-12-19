package org.bensteele.jirrigate.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import jline.internal.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.bensteele.jirrigate.controller.zone.Zone;

/**
 * An abstract class for an irrigation controller interface. A complete workable implementation of a
 * class that extends this will <i>successfully</i> implement all the abstract methods presented.
 * <p>
 * See {@link EtherRain8Controller} for a reference implementation of this class.
 * 
 * @author Ben Steele (ben@bensteele.org)
 */
public abstract class Controller {

  public enum ControllerType {
    ETHERRAIN8
  }

  private final Logger log;
  private final String controllerName;
  private InetAddress ipAddress;
  private int port;
  private String username = "";
  private String password = "";
  private final Set<Zone> zones = new LinkedHashSet<Zone>();
  private boolean isActive;
  private boolean isIrrigating;
  private final ExecutorService requestExecutor = Executors.newSingleThreadExecutor();
  private final BlockingQueue<IrrigationRequest> irrigationRequests = new ArrayBlockingQueue<IrrigationRequest>(
      100);
  private final BlockingQueue<IrrigationResult> irrigationResults = new LinkedBlockingQueue<IrrigationResult>();
  private final ControllerType controllerType;

  public Controller(final String controllerName, InetAddress ipAddress, int port, String username,
      String password, ControllerType type, final Logger log) throws IOException {
    this.controllerName = controllerName;
    this.ipAddress = ipAddress;
    this.port = port;
    this.username = username;
    this.password = password;
    this.controllerType = type;
    this.log = log;
    this.isActive = true;

    // Process the incoming IrrigationRequests.
    requestExecutor.execute(new Runnable() {
      @Override
      public void run() {
        while (true) {
          IrrigationRequest request;
          try {
            request = irrigationRequests.take();
            isIrrigating = true;
            log.info(controllerName + " starting irrigation request:\n" + request.toString());
            startIrrigation(request);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    });
  }

  public void addZone(Zone z) {
    this.zones.add(z);
  }

  public IrrigationRequest generateDefaultIrrigationRequest() {
    IrrigationRequest request = new IrrigationRequest();
    for (Zone z : zones) {
      request.getZones().put(z, z.getDuration());
    }
    return request;
  }

  public ControllerType getControllerType() {
    return this.controllerType;
  }

  public InetAddress getIpAddress() {
    return this.ipAddress;
  }

  public BlockingQueue<IrrigationRequest> getIrrigationRequests() {
    return irrigationRequests;
  }

  public BlockingQueue<IrrigationResult> getIrrigationResults() {
    return irrigationResults;
  }

  protected Logger getLog() {
    return log;
  }

  public String getName() {
    return this.controllerName;
  }

  public String getPassword() {
    return this.password;
  }

  public int getPort() {
    return this.port;
  }

  public abstract String getStatus();

  public String getUsername() {
    return this.username;
  }

  public Zone getZone(String name) {
    for (Zone z : zones) {
      if (z.getName().matches(name)) {
        return z;
      }
    }
    return null;
  }

  public Set<Zone> getZones() {
    return this.zones;
  }

  public void irrigationRequest(IrrigationRequest request) throws IllegalStateException {
    this.irrigationRequests.add(request);
  }

  public boolean isActive() {
    return isActive;
  }

  public boolean isIrrigating() {
    return isIrrigating;
  }

  /**
   * Generic "helper" method to send a HTTP GET to a specified URL.
   * 
   * @param url
   *          The full URL that you wish to send this request to.
   * @return The body of the reply line by line in a List.
   * @throws IOException
   *           If something goes wrong with the request.
   */
  protected List<String> sendHttpGet(String url) throws IOException {
    // Set the timeout to 5 seconds.
    final int HTTP_TIMEOUT = 5000;
    CloseableHttpClient httpclient = HttpClients
        .custom()
        .setDefaultRequestConfig(
            RequestConfig.custom().setSocketTimeout(HTTP_TIMEOUT)
                .setConnectionRequestTimeout(HTTP_TIMEOUT).setConnectTimeout(HTTP_TIMEOUT).build())
        .build();

    try {
      HttpGet httpget = new HttpGet(url);

      ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

        @Override
        public String handleResponse(final HttpResponse response) throws ClientProtocolException,
            IOException {
          int status = response.getStatusLine().getStatusCode();
          if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
          } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
          }
        }

      };
      String responseBody = httpclient.execute(httpget, responseHandler);

      // Convert the monolithic response body String into a new line delimited List.
      String bodyArray[] = responseBody.split("\\n");
      List<String> response = new ArrayList<String>();
      for (String line : bodyArray) {
        response.add(line);
      }

      return response;

    } catch (Exception e) {
      Log.error(controllerName + " timed out trying to execute URL: " + url);
      return new ArrayList<String>();
    } finally {
      httpclient.close();
    }
  }

  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }

  public void setIpAddress(InetAddress ipAddress) {
    this.ipAddress = ipAddress;
  }

  public void setIsIrrigating(boolean isIrrigating) {
    this.isIrrigating = isIrrigating;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  protected abstract void startIrrigation(IrrigationRequest request);

  public abstract boolean stopIrrigation();

  /**
   * Removes {@link Zone} that have no/zero duration specified in the {@link IrrigationRequest}.
   * 
   * @param request
   *          The {@link IrrigationRequest} to process.
   * @return A Set of {@link Zone} to be irrigated.
   */
  protected Set<Zone> stripNoIrrigationZones(IrrigationRequest request) {
    Set<Zone> strippedZones = new HashSet<Zone>();
    for (Zone z : request.getZones().keySet()) {
      if (request.getZones().get(z) > 0) {
        strippedZones.add(z);
      }
    }
    return strippedZones;
  }

  public void testZone(Zone zone, long duration) {
    IrrigationRequest request = new IrrigationRequest();
    request.getZones().put(zone, duration);
    irrigationRequest(request);
  }

  public void testZones(Set<Zone> zones, long duration) {
    IrrigationRequest request = new IrrigationRequest();
    for (Zone zone : zones) {
      request.getZones().put(zone, duration);
    }
    irrigationRequest(request);
  }
}
