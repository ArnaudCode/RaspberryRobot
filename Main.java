import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.json.JSONObject;

import lejos.pc.comm.NXTConnector;

public class Main {

	public static NXTConnector link;
	public static DataOutputStream outRobot;
	public static Socket socket;

	public static void main(String[] args) {
		connect();

		try {
			socket = new Socket("192.168.1.8", 3000);

			PrintWriter outRegie = new PrintWriter(socket.getOutputStream());
			
			JSONObject json = new JSONObject();
			json.put("idModule", "raspberryRobot");
			json.put("action", "init");
			
			outRegie.println(json);
			outRegie.flush();
						
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			System.out.println("Pret. Attente d'instructions regie");
			String ligne = null;
			while ((ligne = in.readLine()) != null) {
				System.out.println(ligne);
				commande(ligne);
			}
		} catch (Exception e) {
		}

		disconnect();
	}

	public static void connect() {
		link = new NXTConnector();

		if (!link.connectTo("usb://")) {
			System.out.println("\nAucun NXT trouve en USB");
		}

		outRobot = new DataOutputStream(link.getOutputStream());
		System.out.println("\nNXT connecte");
	}

	public static void disconnect() {
		try {
			outRobot.close();
			link.close();
		} catch (IOException ioe) {
			System.out.println("\nIO Exception writing bytes");
		}

		System.out.println("\nFermeture des flux de donnees");
	}

	public static void commande(String ligne) {
		try {
			JSONObject json = new JSONObject(ligne);

			String action = json.get("action").toString();
			String vitesse = json.get("vitesse").toString();

			outRobot.writeUTF(action);
			outRobot.writeUTF(vitesse);
			outRobot.flush();
		} catch (Exception e) {
		}
	}
}
