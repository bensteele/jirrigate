package org.bensteele.jirrigate.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.bensteele.jirrigate.controller.IrrigationResult.Result;
import org.bensteele.jirrigate.controller.zone.Zone;

/**
 * An EtherRain8 implementation of a {@link Controller}.
 * <p>
 * {@link http://www.quicksmart.com/qs_etherrain.html}
 * 
 * @author Ben Steele (ben@bensteele.org)
 */
public class EtherRain8Controller extends Controller {

  /**
   * <pre>
   * ER - Command had a formatting error.
   * NA - Command came from an unauthorized IP address.
   * OK - Command was accepted.
   * </pre>
   */
  private enum CommandStatus {
    ER("Command had a formatting error"), NA("Command came from an unauthorized IP address"), OK(
      "Command was accepted");

    private final String text;

    private CommandStatus(final String text) {
      this.text = text;
    }

    @Override
    public String toString() {
      return text;
    }
  }

  /**
   * <pre>
   * NC - Irrigation did not complete.
   * OK - Irrigation completed with no issues. 
   * RN - Irrigation was interrupted by the rain indicator.
   * SH - Irrigation was interrupted by the detection of a short in the valve wiring.
   * </pre>
   */
  private enum OperatingResult {
    NC("Irrigation did not complete"), OK("Irrigation completed with no issues"), RN(
      "Irrigation was interrupted by the rain indicator"), SH(
      "Irrigation was interrupted by the detection of a short in the valve wiring");

    private final String text;

    private OperatingResult(final String text) {
      this.text = text;
    }

    @Override
    public String toString() {
      return text;
    }
  }

  /**
   * <pre>
   * BZ - Device is busy.
   * RD - Device is ready.
   * WT - Device is waiting(delay before start).
   * </pre>
   */
  private enum OperatingStatus {
    BZ("Device is busy"), RD("Device is ready"), WT("Device is waiting(delay before start)");

    private final String text;

    private OperatingStatus(final String text) {
      this.text = text;
    }

    @Override
    public String toString() {
      return text;
    }
  }

  private final AtomicBoolean irrigationStopRequest = new AtomicBoolean();
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private IrrigationResult result;

  public EtherRain8Controller(String controllerName, InetAddress ipAddress, int port,
      String username, String password, Logger log) throws IOException {
    super(controllerName, ipAddress, port, username, password, ControllerType.ETHERRAIN8, log);
  }

  private void addIrrigationResult(IrrigationResult result) {
    this.getIrrigationResults().add(result);
  }

  private String generateIrrigationUrl(IrrigationRequest request) {
    long valve1 = 0;
    long valve2 = 0;
    long valve3 = 0;
    long valve4 = 0;
    long valve5 = 0;
    long valve6 = 0;
    long valve7 = 0;
    long valve8 = 0;

    for (Zone z : request.getZones().keySet()) {
      // EtherRain8 takes minutes by default, convert from seconds.
      if (z.getId().matches("1")) {
        valve1 = request.getZones().get(z) / 60;
      } else if (z.getId().matches("2")) {
        valve2 = request.getZones().get(z) / 60;
      } else if (z.getId().matches("3")) {
        valve3 = request.getZones().get(z) / 60;
      } else if (z.getId().matches("4")) {
        valve4 = request.getZones().get(z) / 60;
      } else if (z.getId().matches("5")) {
        valve5 = request.getZones().get(z) / 60;
      } else if (z.getId().matches("6")) {
        valve6 = request.getZones().get(z) / 60;
      } else if (z.getId().matches("7")) {
        valve7 = request.getZones().get(z) / 60;
      } else if (z.getId().matches("8")) {
        valve8 = request.getZones().get(z) / 60;
      }
    }

    String startIrrigationUrl = getUrlPrefix() + "/result.cgi?xi=";

    return startIrrigationUrl + "0:" + valve1 + ":" + valve2 + ":" + valve3 + ":" + valve4 + ":"
        + valve5 + ":" + valve6 + ":" + valve7 + ":" + valve8;
  }

  private CommandStatus getCommandStatus(List<String> response) throws ClientProtocolException,
      IOException {
    for (String s : response) {
      // cs = CommandStatus
      if (s.contains("cs: ")) {
        if (s.contains("ER")) {
          return CommandStatus.ER;
        } else if (s.contains("NA")) {
          return CommandStatus.NA;
        } else if (s.contains("OK")) {
          return CommandStatus.OK;
        }
      }
    }
    return null;
  }

  @Override
  protected Logger getLog() {
    return super.getLog();
  }

  private String getLoginUrl() {
    return getUrlPrefix() + "/ergetcfg.cgi?lu=" + this.getUsername() + "&lp=" + this.getPassword();
  }

  private OperatingResult getOperatingResult() throws ClientProtocolException, IOException {
    login();
    String getStatusUrl = getUrlPrefix() + "/result.cgi?xs";
    List<String> response = this.sendHttpGet(getStatusUrl);
    for (String s : response) {
      // rz = OperatingResult
      if (s.contains("rz: ")) {
        if (s.contains("NC")) {
          return OperatingResult.NC;
        } else if (s.contains("OK")) {
          return OperatingResult.OK;
        } else if (s.contains("RN")) {
          return OperatingResult.RN;
        } else if (s.contains("SH")) {
          return OperatingResult.SH;
        }
      }
    }
    return null;
  }

  private OperatingStatus getOperatingStatus() throws ClientProtocolException, IOException {
    login();
    String getStatusUrl = getUrlPrefix() + "/result.cgi?xs";
    List<String> response = this.sendHttpGet(getStatusUrl);
    for (String s : response) {
      // os = OperatingStatus
      if (s.contains("os: ")) {
        if (s.contains("BZ")) {
          return OperatingStatus.BZ;
        } else if (s.contains("RD")) {
          return OperatingStatus.RD;
        } else if (s.contains("WT")) {
          return OperatingStatus.WT;
        }
      }
    }
    return null;
  }

  @Override
  public String getStatus() {
    try {
      if (login()) {
        return getOperatingStatus().toString();
      }
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "ERROR: Could not login to controller";
  }

  private String getUrlPrefix() {
    return "http://" + this.getIpAddress().getHostAddress() + ":" + this.getPort();
  }

  private boolean login() throws ClientProtocolException, IOException {
    List<String> response = this.sendHttpGet(getLoginUrl());
    for (String s : response) {
      // ur will be set to username if succesfully logged in.
      if (s.contains("ur: ")) {
        if (s.contains(this.getUsername())) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public void startIrrigation(final IrrigationRequest request) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        try {
          // Reset any previous results.
          result = new IrrigationResult(null, null, 0);
          long startTime = System.currentTimeMillis();

          Set<Zone> irrigatedZones = stripNoIrrigationZones(request);

          if (!login()) {
            setIsIrrigating(false);
            result = new IrrigationResult("No irrigation command sent", irrigatedZones, startTime);
            result.setResult(Result.FAIL);
            result.setMessage("ERROR: Login failed");
            result.setEndTime(System.currentTimeMillis());
            addIrrigationResult(result);
            getLog().info("Irrigation Result: \n" + result.toString());
            return;
          }

          OperatingStatus os = getOperatingStatus();
          if (os != OperatingStatus.RD) {
            setIsIrrigating(false);
            result = new IrrigationResult("No irrigation command sent", irrigatedZones, startTime);
            result.setResult(Result.FAIL);
            result.setMessage("ERROR: " + os.toString());
            result.setEndTime(System.currentTimeMillis());
            addIrrigationResult(result);
            getLog().info("Irrigation Result: \n" + result.toString());
            return;
          }

          String commandSent = generateIrrigationUrl(request);
          startTime = System.currentTimeMillis();
          List<String> response = sendHttpGet(commandSent);

          CommandStatus cs = getCommandStatus(response);
          result = new IrrigationResult(commandSent, irrigatedZones, startTime);
          if (cs == CommandStatus.OK) {
            while (true) {

              try {
                Thread.sleep(5000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }

              if (irrigationStopRequest.get()) {
                irrigationStopRequest.set(false);
                setIsIrrigating(false);
                result.setResult(Result.INTERRUPTED);
                result.setMessage("INFO: Irrigation interrupted by stop request");
                result.setZones(irrigatedZones);
                result.setEndTime(System.currentTimeMillis());
                addIrrigationResult(result);
                getLog().info("Irrigation Result: \n" + result.toString());
                return;
              }
              if (getOperatingStatus() == OperatingStatus.RD) {
                // Irrigation finished.
                break;
              }
            }
          } else {
            setIsIrrigating(false);
            result.setResult(Result.FAIL);
            result.setMessage("ERROR: " + cs.toString());
            result.setEndTime(System.currentTimeMillis());
            addIrrigationResult(result);
            getLog().info("Irrigation Result: \n" + result.toString());
            return;
          }

          OperatingResult or = getOperatingResult();

          if (or != OperatingResult.OK) {
            result.setResult(Result.FAIL);
            result.setMessage("ERROR: " + or.toString());
          } else {
            result.setResult(Result.SUCCESS);
            result.setMessage("INFO: " + or.toString());
          }
          result.setZones(irrigatedZones);
          result.setEndTime(System.currentTimeMillis());
          setIsIrrigating(false);
          addIrrigationResult(result);
          getLog().info("Irrigation Result: \n" + result.toString());

        } catch (ClientProtocolException e) {
          getLog().error(e.getMessage());
        } catch (IOException e) {
          getLog().error(e.getMessage());
        } finally {
          setIsIrrigating(false);
        }
      }
    });
  }

  @Override
  public boolean stopIrrigation() {
    getLog().info("Stop request received for " + this.getName());
    // If it's not currently Irrigating then there is nothing to stop.
    if (!isIrrigating()) {
      getLog().warn(this.getName() + " not currently irrigating, aborting stop request");
      return true;
    }
    irrigationStopRequest.set(true);
    while (irrigationStopRequest.get()) {
      // Wait for the Thread running the irrigation on this controller to acknowledge and set this
      // to false.
    }
    List<String> response;
    try {
      if (!login()) {
        return false;
      }
      String stopIrrigationUrl = getUrlPrefix() + "/result.cgi?xr";
      response = this.sendHttpGet(stopIrrigationUrl);
      if (getCommandStatus(response) == CommandStatus.OK) {
        getLog().info("Stop request successful for " + this.getName());
        return true;
      }
    } catch (ClientProtocolException e) {
      getLog().error(e.getMessage());
    } catch (IOException e) {
      getLog().error(e.getMessage());
    }
    getLog().info(
        "Stop request unsuccessful for " + this.getName() + " controller says: " + getStatus());
    return false;
  }
}
