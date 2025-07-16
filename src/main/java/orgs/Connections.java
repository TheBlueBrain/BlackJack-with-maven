package orgs;

import java.io.*;
import java.net.*;

public class Connections {
	final int PORT = 20000;
	private ServerSocket serverSocket;

	public void startServer() throws IOException {
		serverSocket = new ServerSocket(PORT);
		System.out.println("Server started on port " + PORT);
	}

	public Socket acceptClient() throws IOException {
		return serverSocket.accept();
	}

	static public void sendMessage(Socket clientSocket, String message) throws IOException {
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		out.println(message);
	}
	static public String getMessage(Socket clientSocket) throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		return reader.readLine().trim();
	}

	public void stopServer() throws IOException {
		if (serverSocket != null && !serverSocket.isClosed()) {
			serverSocket.close();
		}
	}
}