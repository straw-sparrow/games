package games;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class Reversi extends JPanel {

	/** }XΪΜε«³ */
	private final static int cellSize = 100;
	/** }XΪΖξΜδ¦ */
	private final static int masuyohaku = (int) (cellSize * 0.1);
	/** γ] */
	private final static int upBlank = cellSize;
	/** Ά] */
	private final static int leftBlank = cellSize;

	/**
	 * ^[<br>
	 * true: / false:
	 */
	private boolean bwTurn = true;

	/** 8ϋόΈy */
	private int dr[] = { -1, -1, -1, 0, 0, 1, 1, 1 };
	/** 8ϋόΈx */
	private int dc[] = { -1, 0, 1, -1, 1, -1, 0, 1 };

	private String[][] board = { 
			{ " ", "1", "2", "3", "4", "5", "6", "7", "8", " " },
			{ "1", "E", "E", "E", "E", "E", "E", "E", "E", " " }, 
			{ "2", "E", "E", "E", "E", "E", "E", "E", "E", " " },
			{ "3", "E", "E", "E", "E", "E", "E", "E", "E", " " }, 
			{ "4", "E", "E", "E", "", "", "E", "E", "E", " " },
			{ "5", "E", "E", "E", "", "", "E", "E", "E", " " }, 
			{ "6", "E", "E", "E", "E", "E", "E", "E", "E", " " },
			{ "7", "E", "E", "E", "E", "E", "E", "E", "E", " " }, 
			{ "8", "E", "E", "E", "E", "E", "E", "E", "E", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " ", " ", " " } };

	private int puttableAreaCount = 0;
	private int blankAreaCount = 0;
	private int blackCount = 0;
	private int whiteCount = 0;
	
	/**
	 * RXgN^
	 */
	public Reversi() {
		setPreferredSize(new Dimension(cellSize * 10, cellSize * 10));
		addMouseListener(new MouseProc());
	}

	/**
	 * ζΚ`ζ
	 */
	public void paintComponent(Graphics g) {
		// ΥΚ`ζ
		initBoardDraw(g);
		// ξ`ζ
		pieceDraw(g);

		// qg}X
		takeHintArea(g);

		// Ά\¦
		Font font = new Font("HGPnppΞί―ΜίΜ", Font.ITALIC, 40);
		g.setFont(font);
		g.setColor(Color.white);

		System.out.println("‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘");
		
		blankAreaCount = 0;
		whiteCount = 0;
		blackCount = 0;
		for (String[] a : board) {
			for(String b:a) {
				System.out.print(b);
				if("E".equals(b)) {
					blankAreaCount++;
				} else if("".equals(b)) {
					whiteCount++;
				} else if("".equals(b)) {
					blackCount++;
				}
			}
			System.out.println();
		}
		
		if(puttableAreaCount == 0) {
			if(blankAreaCount == 0) {
				g.drawString(whiteCount == blackCount ? "ψ«ͺ―" : (whiteCount > blackCount ? "" : "") + "ΜΏI", cellSize * 4, cellSize/2);
			} else {
				bwTurn = !bwTurn;
				repaint();
			}
		} else {
			g.drawString((bwTurn ? "" : "") + "Μ^[", cellSize * 4, cellSize/2);
		}

	}

	/**
	 * qg`ζ
	 */
	private void takeHintArea(Graphics g) {
		puttableAreaCount = 0;
		for (int iy = 0; iy < board.length; iy++) {
			for (int jx = 0; jx < board[iy].length; jx++) {
				if (board[jx][iy] == "E") {
					for (int i = 0; i < 8; i++) {
						int r = countFlip(iy, jx, bwTurn, dr[i], dc[i]);
						if (r > 0) {
							showCanPutArea(iy + "," + jx, g);
							puttableAreaCount++;
						}
					}
				}
			}
		}

	}

	/**
	 * ΥΚ`ζ
	 */
	private void initBoardDraw(Graphics g) {
		// wi
		g.setColor(Color.black);
		g.fillRect(0, 0, cellSize * 10, cellSize * 10);
		g.setColor(new Color(0, 170, 0));
		g.fillRect(cellSize, cellSize, cellSize * 8, cellSize * 8);

		// ‘ό
		g.setColor(Color.black);
		for (int i = 0; i < 9; i++) {
			g.drawLine(cellSize, cellSize * (i + 1), cellSize * 9, cellSize * (i + 1));
		}
		// cό
		for (int i = 0; i < 9; i++) {
			g.drawLine(cellSize * (i + 1), cellSize, cellSize * (i + 1), cellSize * 9);
		}

		// _
		g.setColor(Color.black);
		int pointSize = (int) (cellSize * 0.1);
		g.fillOval(cellSize * 3 - pointSize / 2, cellSize * 3 - pointSize / 2, pointSize, pointSize);
		g.fillOval(cellSize * 7 - pointSize / 2, cellSize * 3 - pointSize / 2, pointSize, pointSize);
		g.fillOval(cellSize * 3 - pointSize / 2, cellSize * 7 - pointSize / 2, pointSize, pointSize);
		g.fillOval(cellSize * 7 - pointSize / 2, cellSize * 7 - pointSize / 2, pointSize, pointSize);

	}

	/**
	 * ξ`ζ
	 */
	private void pieceDraw(Graphics g) {
		for (int iy = 0; iy < board.length; iy++) {
			for (int jx = 0; jx < board[iy].length; jx++) {
			if ("".equals(board[iy][jx])) {
					// Ϋ
					putWhitePiece(jx + "," + iy, g);
				} else if ("".equals(board[iy][jx])) {
					// Ϋ
					putBlackPiece(jx + "," + iy, g);
				}
			}
		}
	}

	/**
	 * ξzu
	 */
	private void putPiece(int x, int y, Color c, Graphics g) {
		g.setColor(c);
		g.fillOval(x + masuyohaku, y + masuyohaku, cellSize - masuyohaku * 2, cellSize - masuyohaku * 2);
	}

	/**
	 * qg}X
	 */
	private void showCanPutArea(String cellAddress, Graphics g) {
		String[] address = cellAddress.split(",");
		int x = Integer.parseInt(address[0]);
		int y = Integer.parseInt(address[1]);
		putPiece(cellSize * x, cellSize * y, new Color(0, 158, 210), g);
	}

	/**
	 * ξzu
	 */
	private void putWhitePiece(String cellAddress, Graphics g) {
		String[] address = cellAddress.split(",");
		int x = Integer.parseInt(address[0]);
		int y = Integer.parseInt(address[1]);
		putPiece(cellSize * x, cellSize * y, Color.white, g);
	}

	/**
	 * ξzu
	 */
	private void putBlackPiece(String cellAddress, Graphics g) {
		String[] address = cellAddress.split(",");
		int x = Integer.parseInt(address[0]);
		int y = Integer.parseInt(address[1]);
		putPiece(cellSize * x, cellSize * y, Color.black, g);
	}

	// NbN³κ½ΜpΜNX
	class MouseProc extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			// ΥΜO€ͺNbN³κ½Ζ«Ν½ΰ΅Θ’ΕIΉ
			if (x < leftBlank) {
				return;
			} else if (x >= leftBlank + cellSize * 8) {
				return;
			} else if (y < upBlank) {
				return;
			} else if (y >= upBlank + cellSize * 8) {
				return;
			}
			// NbN³κ½}XπΑθ
			int cellx = (x - leftBlank) / cellSize + 1;
			int celly = (y - upBlank) / cellSize + 1;
			if (canPut(cellx, celly, bwTurn)) {
				put(cellx, celly, bwTurn);
				bwTurn = !bwTurn;
			}

			// Δ`ζ
			repaint();
		}
	}

	public int countFlip(int row, int col, boolean bwturn, int dr, int dc) {
		int count = 0;
		int r = row + dr;
		int c = col + dc;
		// Υ©ηΝέo³Θ’ΤJθΤ·
		while (0 <= r && r < board[0].length && 0 <= c && c < board.length) {
			if (board[c][r].equals(puttablePiece(!bwTurn))) {
				// θΜΞΘη countπ1β·
				count++;
			} else if (board[c][r].equals(puttablePiece(bwTurn))) {
				// ©ͺΜΞΘη count πΤ΅ΔIΉ
				return count;
			} else {
				// σ}XΘη 0 πΤ΅ΔIΉ
				return 0;
			}
			// Μ}XΦΪ?
			r += dr;
			c += dc;
		}
		// Υ©ηΝέo΅½η0πΤ·
		return 0;
	}

	private boolean canPut(int row, int col, boolean bwTurn) {
		boolean result = false;
		// »ΜκͺσΕΘ©Α½u―Θ’
		if ("E".equals(board[col][row])) {
			// Η±©ΜϋόΕΠΑ­θ©ΉκΞ OK
			for (int i = 0; i < 8; i++) {
				if (countFlip(row, col, bwTurn, dr[i], dc[i]) > 0) {
					return true;
				}
			}
		}
		return result;
	}

	private void put(int row, int col, boolean bwTurn) {
		board[col][row] = puttablePiece(bwTurn);
		// eϋόΙΒ’ΔCΠΑ­θΤΉιΞπΠΑ­θΤ·
		for (int i = 0; i < 8; i++) {
			int count = countFlip(row, col, bwTurn, dr[i], dc[i]);
			if (count > 0) {
				flip(row, col, dr[i], dc[i], count);
			}
		}
	}

	public void flip(int row, int col, int dr, int dc, int count) {
		int r = row + dr;
		int c = col + dc;
		for (int i = 0; i < count; i++) {
			board[c][r] = opponent(board[c][r]);
			r += dr;
			c += dc;
		}
	}

	private String opponent(String piece) {
		return "".equals(piece) ? "" : "";
	}

	private String puttablePiece(boolean bwTurn) {
		return bwTurn ? "" : "";
	}

	/**
	 * N?
	 */
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setTitle("reversi");
		f.getContentPane().setLayout(new FlowLayout());

		JPanel jp = new Reversi();
		f.getContentPane().add(jp);
		f.pack();
		f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}