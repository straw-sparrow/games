package games;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
 
public class Reversi extends JPanel {
	
	/**　マス目の大きさ　*/
	private final static int cellSize = 100;
	/**　マス目と駒の比率　*/
	private final static int masuyohaku = (int) (cellSize * 0.1);
	/**　上部余白　*/
	private final static int upBlank = cellSize;
	/**　左部余白　*/
	private final static int leftBlank = cellSize;
	
	/**　
	 * 白黒ターン<br>
	 * true:白 / false:黒　
	 */
	private boolean bwTurn = true;
	
	/**　8方向走査y　*/
    private int dr[] = {-1,-1,-1, 0, 0, 1, 1, 1};
	/**　8方向走査x　*/
    private int dc[] = {-1, 0, 1,-1, 1,-1, 0, 1};
	
	
	private String[][] board = {
			{" ","1","2","3","4","5","6","7","8"," "},
			{"1","・","・","・","・","・","・","・","・"," "},
			{"2","・","・","・","・","・","・","・","・"," "},
			{"3","・","・","・","・","・","・","・","・"," "},
			{"4","・","・","・","○","●","・","・","・"," "},
			{"5","・","・","・","●","○","・","・","・"," "},
			{"6","・","・","・","・","・","・","・","・"," "},
			{"7","・","・","・","・","・","・","・","・"," "},
			{"8","・","・","・","・","・","・","・","・"," "},
			{" "," "," "," "," "," "," "," "," "," "}
	};
	/**
	 * コンストラクタ
	 */   
	public Reversi() {
        setPreferredSize(new Dimension(cellSize * 10, cellSize * 10));
        addMouseListener(new MouseProc());
    }
    
	/**
	 * 画面描画
	 */
    public void paintComponent(Graphics g) {
    	// 盤面描画
    	initBoardDraw(g);
		// 駒描画
    	pieceDraw(g);
    	
    }

	/**
	 * 盤面描画
	 */
    private void initBoardDraw(Graphics g) {
        // 背景
        g.setColor(Color.black);
        g.fillRect(0, 0, cellSize * 10, cellSize * 10);
        g.setColor(new Color(0, 170, 0));
        g.fillRect(cellSize, cellSize, cellSize * 8, cellSize * 8);

        // 横線
        g.setColor(Color.black);
		for (int i = 0; i < 9; i++) {
			g.drawLine(cellSize, cellSize * (i + 1), cellSize * 9, cellSize * (i + 1));
		}
		// 縦線
		for (int i = 0; i < 9; i++) {
			g.drawLine(cellSize * (i + 1), cellSize, cellSize * (i + 1), cellSize * 9);
		}
		
		// 点
		g.setColor(Color.black);
		int pointSize = (int) (cellSize * 0.1);
		g.fillOval(cellSize * 3 - pointSize / 2, cellSize * 3 - pointSize / 2, pointSize, pointSize);
		g.fillOval(cellSize * 7 - pointSize / 2, cellSize * 3 - pointSize / 2, pointSize, pointSize);
		g.fillOval(cellSize * 3 - pointSize / 2, cellSize * 7 - pointSize / 2, pointSize, pointSize);
		g.fillOval(cellSize * 7 - pointSize / 2, cellSize * 7 - pointSize / 2, pointSize, pointSize);

	}

	/**
	 * 初期駒描画
	 */
    private void pieceDraw(Graphics g) {
		for (int iy = 0; iy < board.length; iy++) {
			for (int jx = 0; jx < board[iy].length; jx++) {
				if(board[iy][jx] == "○") {
					// 白丸
					putWhitePiece(jx +"," + iy , g);
				} else if(board[iy][jx] == "●") {
					// 黒丸
					putBlackPiece(jx +"," + iy , g);
				}
			}
		}
    }
    
	/**
	 * 駒配置
	 */
    private void putPiece(int x, int y, Color c, Graphics g){
		g.setColor(c);
        g.fillOval(x + masuyohaku, y + masuyohaku, cellSize - masuyohaku * 2, cellSize - masuyohaku * 2);
    }


	/**
	 * 白駒配置
	 */
    private void putWhitePiece(String cellAddress, Graphics g){
    	String[] address = cellAddress.split(",");
    	int x = Integer.parseInt(address[0]);
    	int y = Integer.parseInt(address[1]);
		putPiece(cellSize * x, cellSize * y, Color.white, g);
    }
    
	/**
	 * 黒駒配置
	 */    
    private void putBlackPiece(String cellAddress, Graphics g){
    	String[] address = cellAddress.split(",");
    	int x = Integer.parseInt(address[0]);
    	int y = Integer.parseInt(address[1]);
		putPiece(cellSize * x, cellSize * y, Color.black, g);
    }
    
    // クリックされた時の処理用のクラス
    class MouseProc extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            // 盤の外側がクリックされたときは何もしないで終了
			if (x < leftBlank) {
				return;
			} else if (x >= leftBlank + cellSize * 8) {
				return;
			} else if (y < upBlank) {
				return;
			} else if (y >= upBlank + cellSize * 8) {
				return;
			}
			// クリックされたマスを特定
			int cellx = (x - leftBlank) / cellSize + 1;
			int celly = (y - upBlank) / cellSize + 1;
			if (canPut(cellx, celly, bwTurn)) {
				put(cellx, celly, bwTurn);
				bwTurn = !bwTurn;
			}

			// 再描画
			repaint();
		}
	}

    public int countFlip(int row, int col, boolean bwturn, int dr, int dc) {
        int count = 0;
        int r = row + dr;
        int c = col + dc;
        // 盤からはみ出さない間繰り返す
        while (0 <= r && r < board[0].length && 0 <= c && c < board.length) {
            if (board[c][r] == puttablePiece(!bwTurn)){
                // 相手の石なら countを1増やす
                count++;
            } else if (board[c][r] == puttablePiece(bwTurn)) {
                // 自分の石なら count を返して終了
                return count;
            } else {
                // 空マスなら 0 を返して終了
                return 0;
            }
            // 次のマスへ移動
            r += dr;
            c += dc;
        }
        // 盤からはみ出したら0を返す
        return 0;
    }
    
	private boolean canPut(int row, int col, boolean bwTurn) {
		boolean result = false;
        // その場所が空でなかった置けない
		if (board[col][row] == "・") {
			// どこかの方向でひっくりかせれば OK
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
        // 各方向について，ひっくり返せる石をひっくり返す
        for (int i = 0; i < 8; i++) {
            int count = countFlip(row, col, bwTurn, dr[i], dc[i]);
            if (count > 0){
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
    	return piece=="●" ? "○" : "●";
    }

	private String puttablePiece(boolean bwTurn) {
		if (bwTurn) {
			// 白ターン
			return "○";
		} else {
			// 黒ターン
			return "●";
		}
	}
    
	/**
	 * 起動
	 */
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.getContentPane().setLayout(new FlowLayout());
        f.getContentPane().add(new Reversi());
        f.pack();
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}