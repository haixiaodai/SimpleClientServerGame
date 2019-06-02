import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Client {

	Socket s;
	DataInputStream ins;
	static DataOutputStream out;
	Scanner sc;
	
	

	public static void main(String[] args) {
		new Client();

	}

	public Client() {
		try {
			sc = new Scanner(System.in);
			System.out.println("Please enter your first name");
			//Get name of user
			String name = sc.nextLine();
			//Establish connection to server
			s = new Socket("REPLACE WITH YOUR SERVER", 61555);//Replace with your own server address
			out = new DataOutputStream(s.getOutputStream());
			ins = new DataInputStream(s.getInputStream());
			//Send user's name to server
			out.writeUTF(name);
			System.out.println("Connected, waiting to start");
			//Start guess procedure
			guess();
			//Set a timer, if user doesn't input in 30 seconds, then exit the game
			Timer ti2=new Timer();
			ti2.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
					System.out.println("Timeout, exiting game......");
					out.writeUTF("q");
					s.close();
					sc = null;
					System.exit(0);
					}
					catch (IOException e) {
						
					}
					}
				},30000);
			while (sc != null) {
				//After the round finish, ask user to start again or finish the game. 
				System.out.println("Enter p to play again, or q to exit");
				String op = sc.nextLine();
				//If user choose to finish the game
				if (op.equals("q")) {
					out.writeUTF("q");
					try {
						s.close();
						sc.close();
						System.exit(0);
					} catch (IOException e) {
					}
					break;
				}
				//If user choose to start again, send signal to server and waiting for start signal from server
				if (op.equals("p")) {
					ti2.cancel();
					out.writeUTF("p");
					System.out.println("Waiting to start");
					guess();
				}
			}
		} catch (IOException e) {

		}
	}

	public void guess() {
		Timer ti;
		try {
			// Wait for start signal from server
			while (true) {
				if (ins.readUTF().equals("start")) {
					break;
				}
			}
			// Get name of all clients in this round and print on screen
			String anouname = ins.readUTF();
			System.out.println(anouname);
			int times = 0;
			// Keep guessing until number of guess reaches 4 or client choose to exit the game
			while (times < 4) {
				//Set a timer before every time user input a number to guess, if user doesn't input in 30 seconds, then exit the game
				ti=new Timer();
				ti.schedule(new TimerTask() {
					@Override
					public void run() {
						try {
						System.out.println("Timeout, exiting game......");
						out.writeUTF("e");
						s.close();
						sc = null;
						System.exit(0);
						}
						catch (IOException e) {
							
						}
						}
					},30000);
				System.out.println("Please input a number to guess, or enter e to exit the game");
				String guess = sc.next();
				// If user input single letter e, exit the game
				if (guess.equals("e")) {
					out.writeUTF("e");
					s.close();
					sc = null;
					System.exit(0);
					break;
				} else {
					ti.cancel();
					// Send guess to server
					out.writeUTF(guess);
					// Read result from server and print on screen
					String result = ins.readUTF();
					System.out.println(result);
					if (result.equals("Congratulation")) {
						break;
					} else {
						times++;
					}
					if (times >= 4)
						System.out.println("You have reach the maxmium attmpt, the correct answer is " + ins.readUTF());
				}
			}
			if (!s.isClosed()) {
				System.out.println("Waiting for other user to finish");
			}
			//Waiting for other users in this round to finish the game
			while (true) {
				if (ins.readUTF().equals("finish")) {
					//Get the ranking result from server and print on screen
					System.out.println(ins.readUTF());
					break;
				}
			}

		} catch (IOException e) {

		}
	}

}
