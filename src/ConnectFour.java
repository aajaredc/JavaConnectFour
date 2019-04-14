/*
 * ConnectFour
 * 
 * Written by Jared Caruso
 * 11/9/2018
 * 
 * It's connect four.
 * 2/7/19: It's actually connect three now
 */

import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.*;

public class ConnectFour extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	
	public static space spaces[] = new space[42];
	public static String ip = "localhost";
	public static int port = 1116;
	public static DataOutputStream out;
	public static DataInputStream in;
	public static AtomicBoolean boolTurnHost = new AtomicBoolean(false);
	public static AtomicBoolean accepted = new AtomicBoolean(false);
	public static boolean boolGameActive = true;
	public static ServerSocket host;
	public static Socket socket;
	public static AtomicBoolean isHost = new AtomicBoolean(true);
	
	public static Thread gameLoop;
	
	public static JLabel lblWaitingForPeer = new JLabel("Waiting for peer...");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConnectFour frame = new ConnectFour();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		// Start a new thread for use with Swing
		// (networking must be in a different thread)
		gameLoop = new Thread() {
			public void run() {
				// While true...
				while (true) {
					// If the game is active...
					if (boolGameActive) {
						
						// If it's not the host's turn...
						if (!boolTurnHost.get()) {
							
							// Try to get the input from the other player
							try {
								// Read input
								int inRead = in.readInt();
								// Debug
								System.out.println(inRead);
								
								// Update space accordingly
								if (ConnectFour.isHost.get()) {
									spaces[inRead].setColor(1);
								} else {
									spaces[inRead].setColor(0);
								}
								spaces[inRead].update();
								ConnectFour.checkWinnerVertical();
								ConnectFour.checkWinnerDiagonal();
								ConnectFour.checkWinnerHorizontal();
								
								// When the host receive's the input, it's now his/her turn
								boolTurnHost.set(true);
							} catch (IOException e) {
								// Error has occurred...
								System.out.println("Exception occured. The game has been set to inactive. The program will terminate");
								JOptionPane.showMessageDialog(null, "Exception occured. The game has been set to inactive. The program will terminate");
								boolGameActive = false;
								
								System.exit(0);
							}
							
							
						}
						
						// If a player has not be accepted, listen
						if (!accepted.get()) {
							listen();
						}
						
						
					}
				
					
				}
			}
		};
		
		// If not connected...
		if (!connected()) {
			// Then you're the host!
			try {
				// Assign the ServerSocket (host)
				host = new ServerSocket(port, 8, InetAddress.getByName(ip));
			} catch (Exception e) {
				e.printStackTrace();
			}
			// It's the host's turn first
			boolTurnHost.set(true);
			
			isHost.set(true);
			
		} else {
			isHost.set(false);
		}
		
		// Start the game loop
		gameLoop.start();
	}
	
	// Networking
	/**
	 * Check if a player is connected
	 * @return
	 */
	private static boolean connected() {
		try {
			// Create the socket
			socket = new Socket(ip, port);
			// Out stream
			out = new DataOutputStream(socket.getOutputStream());
			// in Stream
			in = new DataInputStream(socket.getInputStream());
			// if it all worked out, mark that the player was accepted
			accepted.set(true);
			lblWaitingForPeer.setVisible(false);
		} catch (IOException e) {
			// It failed
			System.out.println("Searching for other player...");
			return false;
		}
		// they're connected!!
		System.out.println("Successfully connected to the host");
		return true;
	}
	
	/**
	 * This method checks for a request from another player
	 */
	private static void listen() {
		// Temporary socket
		Socket temp = null;
		try {
			// Check if the host has accepted, using temporary socket
			temp = host.accept();
			// Debug
			System.out.println("Found request");
			// Streams
			out = new DataOutputStream(temp.getOutputStream());
			in = new DataInputStream(temp.getInputStream());
			// Accepted
			accepted.set(true);
			System.out.println("Accepted request");
			lblWaitingForPeer.setVisible(false);
		} catch (IOException e) {
			System.out.println("Request failed");
			e.printStackTrace();
		}
	}

	/**
	 * Check for winner
	 */
	public static void checkWinnerVertical() {
		for (int i = 0; i < spaces.length; i++) {
			try {
				// Check for red
				if ((spaces[i].getColor() == 0 &&
					spaces[i + 7].getColor() == 0 &&
					spaces[(i + 7) + 7].getColor() == 0)) {
					
					redWins();
							
				}
				// Check for yellow
				if ((spaces[i].getColor() == 1 &&
					spaces[i + 7].getColor() == 1 &&
					spaces[(i + 7) + 7].getColor() == 1)) {
					
					yellowWins();
					
							
				}
			} catch(Exception e) {
				
			}
			
		}
	}
	
	/**
	 * Check for winner diagonally
	 *
	 */
	public static void checkWinnerDiagonal() {
		for (int i = 0; i < spaces.length; i++) {
			try {
				// Check for red
				if ((spaces[i].getColor() == 0 &&
						spaces[i - 6].getColor() == 0 &&
						spaces[(i - 6) - 6].getColor() == 0)) {
					redWins();
								
				}
				if ((spaces[i].getColor() == 0 &&
						spaces[i - 8].getColor() == 0 &&
						spaces[(i - 8) - 8].getColor() == 0)) {
						redWins();
								
					}
				
				// Check for yellow
				if ((spaces[i].getColor() == 1 &&
						spaces[i - 6].getColor() == 1 &&
						spaces[(i - 6) - 6].getColor() == 1)) {
						
					yellowWins();
								
				}
				if ((spaces[i].getColor() == 1 &&
						spaces[i - 8].getColor() == 1 &&
						spaces[(i - 8) - 8].getColor() == 1)) {
						
						yellowWins();
								
					}
			} catch(Exception e) {
				
			}
			
		}
	}
	
	/**
	 * Check for winner horizontally
	 */
	public static void checkWinnerHorizontal() {

		for (int i = 0; i < spaces.length; i++) {
			try {
				if ((spaces[i].getColor() == 0 &&
						spaces[i + 1].getColor() == 0 &&
						spaces[(i - 1)].getColor() == 0) && 
						((i + 1) % 7 != 0)){
					redWins();
				} else if ((spaces[i].getColor() == 1 &&
						spaces[i + 1].getColor() == 1 &&
						spaces[(i - 1)].getColor() == 1) && 
						((i + 1) % 7 != 0)){
					yellowWins();
				}
			} catch (Exception e){
				
			}
		}
	}
	
	/**
	 * Red wins method
	 */
	public static void redWins() {
		// Set the game to inactive
		ConnectFour.boolGameActive = false;
		
		// Debug
		System.out.println("Winner: red");
		JOptionPane.showMessageDialog(null, "Winner: Red!!!");
		
		// Reset the game
		resetGame();
	}
	/**
	 * Yellow wins method
	 */
	public static void yellowWins() {
		ConnectFour.boolGameActive = false;
		
		System.out.println("Winner: yellow");
		JOptionPane.showMessageDialog(null, "The winner is yellow! uWu");
		
		resetGame();
	}
	
	/**
	 * Reset the game
	 */
	public static void resetGame() {
		for (int s = 0; s < spaces.length; s++) {
			spaces[s].setColor(2);
			spaces[s].update();
			
			boolGameActive = true;
		}
	}
	
	/**
	 * Create the frame.
	 */
	public ConnectFour() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 508);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setTitle("Caruso - Exam1");
		
		// Waiting for peer label
		lblWaitingForPeer.setForeground(Color.RED);
		lblWaitingForPeer.setFont(new Font("Dialog", Font.BOLD, 26));
		lblWaitingForPeer.setBounds(176, 164, 290, 59);
		contentPane.add(lblWaitingForPeer);
		
		// Load the board
		JLabel lblBoard = new JLabel("board");
		lblBoard.setBounds(0, 0, 640, 480);
		try {
			lblBoard.setIcon(new ImageIcon(ImageIO.read(new File("resources\\board.png"))));
		} catch (IOException e) {
			try {
				lblBoard.setIcon(new ImageIcon(ImageIO.read(new File("resources/board.png"))));
			} catch (IOException e2) {
				System.out.println("board.png not found.");
				//System.exit(0);
			}
		}
		contentPane.add(lblBoard);
		
		// Create the spaces
		int x = 15, y = 6; // Top left position of the square
		for (int i = 0; i < spaces.length; i++) {
			// Create a new space
			spaces[i] = new space();
			// Set the bounds
			spaces[i].setBounds(x,y,70,70);
			// Set the number (identifier) of the square
			spaces[i].setNumber(i);
			
			// Debug
			System.out.println(spaces[i].toString());
			
			// Add the label to the pane
			contentPane.add(spaces[i]);
			
			// Increment the X position
			x = x + 90;
			
			// Every 7 spaces, reset the X position and move down 80 units
			if ((i + 1 ) % 7 == 0 && i != 0) {
				x = 15;
				y = y + 80;
			}
			
		}
		
	}
}
