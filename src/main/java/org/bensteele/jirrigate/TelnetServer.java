package org.bensteele.jirrigate;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.TreeSet;

import jline.console.ConsoleReader;
import jline.console.completer.StringsCompleter;

public class TelnetServer implements Runnable {

  private final int serverPort;

  public TelnetServer(int port) {
    this.serverPort = port;
  }

  @Override
  public void run() {
    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(this.serverPort);
    } catch (IOException e) {
      throw new RuntimeException("ERROR: Cannot open port " + serverPort, e);
    }

    while (true) {
      Socket clientSocket = null;
      try {
        clientSocket = serverSocket.accept();
        processClientRequest(clientSocket);
      } catch (IOException e) {
        // TODO: log
      }
    }
  }

  private void processClientRequest(Socket clientSocket) throws IOException {
    ConsoleReader reader = new ConsoleReader(clientSocket.getInputStream(),
        clientSocket.getOutputStream());

    reader.setPrompt("jirrigate> ");

    // test auto-complete
    Set<String> commands = new TreeSet<String>();
    commands.add("show controller all info");
    commands.add("show controller all status");
    reader.addCompleter(new StringsCompleter(commands));

    String line;
    while ((line = reader.readLine()) != null) {

      if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
        break;
      }
    }
    clientSocket.close();
  }
}
