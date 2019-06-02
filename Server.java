
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class Server {
	ServerSocket ss;
	Socket s;
	private DataInputStream ins;
	private String name;
	public static ArrayList<Thread> list = new ArrayList<Thread>();
	static boolean isstart;
	public static ArrayList<Thread> round = new ArrayList<Thread>();
	public static Integer num;
	public static Timer timer = null;
	public static boolean roundfinish = false;
	public static String ranking = "Round finish. Ranking: ";
	public static ArrayList<rankitem> rank = new ArrayList<rankitem>();

	public static void main(String[] args) {
		new Server();
	}

	public Server() {
		isstart = false;
		try {
			ss = new ServerSocket(61555);
			// Loop to accept clients' connections and add clients to the queue
			while (true) {
				s = ss.accept();
				//Write client info to log when new client connect to server
				savecomlog(s.getInetAddress().toString()+":"+s.getPort()+"   Connected"+'\n');
				ins = new DataInputStream(s.getInputStream());
				name = ins.readUTF();
				// Once accept client, create a new ServerThread but don't run the thread
				Thread tt = new Thread(new ServerThread(s, name));
				tt.setDaemon(true);
				tt.setName(name);
				list.add(tt);
				// Let the server to judge whther to start the game or not
				judge();
			}
		} catch (IOException | NullPointerException e) {

		} finally {
			try {
				s.close();
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	// Method to start a new round
	public static void startgame() {
		timer = null;
		roundfinish = false;
		rank.clear();
		ranking = "Round finish. Ranking: ";
		// Generate a random number
		num = ThreadLocalRandom.current().nextInt(0, 10);
		System.out.println(num);
		isstart = true;
		// Let all clients in the round list to start game
		for (int i = 0; i < round.size(); i++) {
			Server.round.get(i).start();
			// Remove client from queue if the client has been put in the round list and
			// start game
			Server.list.remove(0);
		}
		// Wait until all the client finish or exit game
		while (true) {
			System.out.print("");
			if (rank.size() == round.size()) {
				System.out.println("Round finish");
				break;
			}
		}
		// Ranking and sorting clients by the number of times guessed
		Collections.sort(rank, new Comparator<rankitem>() {
			public int compare(rankitem r1, rankitem r2) {
				return r1.getNumofguess() - r2.getNumofguess();
			}
		});
		// Generating the ranking result as a string
		for (int i = 0; i < Server.rank.size(); i++) {
			if (Server.rank.get(i).getNumofguess() != -1) {
				ranking = ranking + Server.rank.get(i).getName() + "-" + Server.rank.get(i).getNumofguess() + ", ";
			}
		}
		// Round finish
		savegamelog();
		isstart=false;
		roundfinish = true;
		round.clear();
	}

	// Method to judge whether to start the game or not
	public static void judge() {
		// When the number of client in the queue is less than 3, set a timer for 60
		// seconds to start new round
		if ((list.size() == 1 || list.size() == 2) && !isstart && timer == null) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					// Put all the client in the queue to the round list
					for (int i = 0; i < list.size(); i++) {
						round.add(list.get(i));
					}
					if (round.size() > 0) {
						startgame();
					}
				}
			}, 60000);
		}
		// When the number of client in the queue reaches 3
		if (list.size() >= 3 && isstart == false) {
			try {
				// cancel any timer set before and start new round
				timer.cancel();
			} catch (NullPointerException e) {

			}
			// Put the first three client to the round list
			for (int i = 0; i < 3; i++) {
				round.add(list.get(i));
			}
			startgame();
		}

	}

	// Method to judge all clients have finish or exit the game.
	public static void judge2() {
		boolean interrupted = true;
		boolean allalive = false;
		// If the client finish the game normally, status should be interrupted
		for (int i = 0; i < round.size(); i++) {
			if (!round.get(i).isInterrupted()) {
				interrupted = false;
			}
		}
		// If the client choose to exit the game, the thread should be died
		for (int i = 0; i < round.size(); i++) {
			if (round.get(i).isAlive()) {
				allalive = true;
			}
		}
		//If there is only one user in the round the the user has exit the game
		if(Server.list.size()==0||Server.list==null) {
			isstart=false;
			round.clear();
		}
		// If all clients have finish or exit the game, clear the round list and let the server to judge whether to start new round or not
		if (interrupted || !allalive) {
			round.clear();
			isstart = false;
			judge();
		}

	}
	
	//Method to generate game log
	public static void savegamelog() {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
		String path = new File("").getAbsolutePath();
		File file = new File(path +"/gamelog.txt");
		if(!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw=new FileWriter(file,true); 
		fw.write(dateFormat.format(date)+'\n');
		fw.write(ranking+'\n');
		fw.close();
	}
		catch(IOException e) {
		}
	}
	
	//Method to generate communication log
	public static void savecomlog(String address) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
		String path = new File("").getAbsolutePath();
		File file = new File(path +"/comlog.txt");
		if(!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw=new FileWriter(file,true); 
		fw.write(dateFormat.format(date)+'\n');
		fw.write(address);
		fw.close();
		
	}
		catch(IOException e) {
		}
}
}

//Item that store clients name and number of guessed
class rankitem {
	private String name;
	private Integer numofguess;

	public rankitem(String name, Integer numofguess) {
		this.name = name;
		this.numofguess = numofguess;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNumofguess() {
		return numofguess;
	}

	public void setNumofguess(Integer numofguess) {
		this.numofguess = numofguess;
	}

}
