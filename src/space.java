import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;


public class space extends JLabel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Color (0 red, 1 yellow, 2 empty)
	private int color = 2;
	// Number (identifier)
	private int number = 0;
	
	public space() {
		addMouseListener((MouseListener) new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				System.out.println("Clicked " + number);
				
				// If it is your turn
				if (ConnectFour.boolTurnHost.get()) {
					// If the game is active
					if (ConnectFour.boolGameActive) {
						try {
							// Make sure the space isn't already clicked
							if (color == 2) {
								// Number to output
								int outNumber = number;
								
								// The number falls down while the space below it is empty
								try {
									while (ConnectFour.spaces[outNumber + 7].getColor() == 2) {
										outNumber = outNumber + 7;
									}
								} catch (Exception e2) {
									// Exception for calculations
								}
								
								// Output the number you clicked on
								ConnectFour.out.writeInt(outNumber);
								// Flush...
								ConnectFour.out.flush();
								
								// Update space accordingly
								if (ConnectFour.isHost.get()) {
									ConnectFour.spaces[outNumber].setColor(0);
								} else {
									ConnectFour.spaces[outNumber].setColor(1);
								}
								ConnectFour.spaces[outNumber].update();
								
								// Check for winner
								ConnectFour.checkWinnerVertical();
								ConnectFour.checkWinnerDiagonal();
								ConnectFour.checkWinnerHorizontal();
								
								// It's now not your turn
								ConnectFour.boolTurnHost.set(false);
							} else {
								System.out.println("Space already clicked");
							}
						} catch (Exception e1) {
							JOptionPane.showMessageDialog(null, "Exception occured. The game has been set to inactive. The program will terminate");
							System.out.println("Exception occured. The game has been set to inactive.");
							ConnectFour.boolGameActive = false;
							
							System.exit(0);
						}
					}
				} else {
					System.out.println("It isn't your turn >_<");
				}
				
			}
		});
	}
	
	/**
	 * This method updates the square's image.
	 * 0 == red, 1 == yellow, 2 == transparent
	 */
	public void update() {
		if (color == 0) {
			try {
				setIcon(new ImageIcon(ImageIO.read(new File("resources\\red.png"))));
			} catch (IOException e) {
				try {
					setIcon(new ImageIcon(ImageIO.read(new File("resources/red.png"))));
				} catch (IOException e2) {
					System.out.println("red.png not found.");
				}
			}
		} else if (color == 1) {
			try {
				setIcon(new ImageIcon(ImageIO.read(new File("resources\\yellow.png"))));
			} catch (IOException e) {
				try {
					setIcon(new ImageIcon(ImageIO.read(new File("resources/yellow.png"))));
				} catch (IOException e2) {
					System.out.println("yellow.png not found.");
				}
			}
		} else if (color == 2) {
			setIcon(null);
		}
	}

	
	// Setters and getters
	@Override
	public String toString() {
		return ("Space: " + number + " | Color: " + color);
	}
	
	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
	
}
