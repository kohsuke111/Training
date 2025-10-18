package training;

//ブロックの判定を次回行う
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Main{
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("ブロック崩し練習用");
		CardLayout crd = new CardLayout();
		frame.setBounds(300,200,800,800);//(フレームが作られる時のx座標、y座標、横の長さ、縦の長さ)
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);//ウィンドウの大きさを変えられるかどうか
        frame.setLayout(crd);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//バツ閉じした時の挙動。これだとアプリを落とすってこと
		GamePanel gamePanel = new GamePanel(crd);
		frame.add(gamePanel,"GAME");//gamePanelをフレームに貼り付ける "Game"でレイアウト操作をすることが可能
		EndPanel endPanel = new EndPanel();
		frame.add(endPanel,"END");
		
		crd.show(frame.getContentPane(), "GAME");//パネルの画面確認用(作成完了後削除)
		
		
		frame.setVisible(true);//表示するためのメソッド
		
	}
	
}

class GamePanel extends JPanel implements ActionListener , MouseMotionListener{
	private CardLayout cardLayout;
	private Ball ball;
	private Paddle paddle;
	private Timer timer ;
	private ArrayList<Block> blocks;
	
	
	public GamePanel(CardLayout cardLayout) {
		this.cardLayout = cardLayout ;
		this.setBackground(Color.black);
		this.ball = new Ball(100, 100, 20);
		this.paddle = new Paddle();
		this.timer = new Timer(10,this);
		addMouseMotionListener(this);
		blockSettings();
		timer.start();
	}
//	new Block(0, 0, 80,30);
	private void blockSettings() {
		blocks = new ArrayList<Block>();
		int rows = 5 ; //縦数
		int cols = 8 ; //横数
		int blockWidth = 80 ; //ブロック一つ当たりの横幅
		int blockHeight = 20 ; //ブロック一つ当たりの縦幅
		int startX = 55 ; //一つ目のブロックのx座標
		int startY = 50 ;//一つ目のブロックのy座標
		int sukima = 5 ; //ブロック間の隙間
		
		for(int i = 0 ; i < rows ; i++) {
			for(int j = 0 ; j < cols ; j++) {
				int x = startX + j * (blockWidth + sukima); //iの数が増えていくごと、つまりブロックの数が増えるごとにそのブロックのx座標が変わっていく
				int y = startY + i * (blockHeight + sukima);//jの数が増えていくごと、つまりブロックの数が増えるごとにそのブロックのy座標が変わっていく
				blocks.add(new Block(x,y,blockWidth,blockHeight));
			}
		}
	}
	
	@Override 
	public void paintComponent(Graphics g) { //このメソッド内に各部品の描写について記述する
		super.paintComponent(g); //親クラスJPanelのpaintComponetを使ってる
		ball.drawBall(g);
		paddle.drawPaddle(g);
		for(Block block : blocks) {
			block.drawBlock(g);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ball.move(getWidth(), getHeight());
		
		if(ball.bounds().intersects(paddle.bounds())) {
			ball.reverseY();
		}
		
		if(ball.getGameOver()) {
			timer.stop();;
			JOptionPane.showMessageDialog(this, "ゲームオーバー");
			cardLayout.show(this.getParent(), "END");
//			System.exit(0); //ゲームオーバーと同時に消える
		}
		
		repaint();
	}


	@Override
	public void mouseDragged(MouseEvent e) {

	}


	@Override
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		paddle.moveToMouse(mouseX, getWidth());
		repaint();		
	}
}

class Ball{
	private int x;
	private int y;
	private int size;
	private int moveX = 4;
	private int moveY = 4;
	private boolean gameOver ;
	
	public Ball (int x, int y, int size) {
		this.x = x ;
		this.y = y ;
		this.size = size ;
	}
	

	public void drawBall(Graphics g) {
		g.setColor(Color.white);
		g.fillOval(x,  y, size, size);
	}
	
	//ballの動きと跳ね返る座標の計算
	public void move(int panelWidth,int panelHeight) {
		if(gameOver)return;
		
		x += moveX;
		y += moveY;
		
		if(x<=0 || panelWidth-size < x) {
			reverseX();
		}
		if(y<=0) {
			reverseY();
		}
		
		if(panelHeight < y ) {
			gameOver = true ;
		}
	}
	
	//awt内にある衝突判定を行うクラス(Rectangle)
	public Rectangle bounds(){
		return new Rectangle(x,y,size,size);
		
	}
	
	public boolean getGameOver() {
		return gameOver;
	}

	public void reverseX() {
		moveX = -moveX ;
	}
	public void reverseY() {
		moveY = -moveY ;
	}
 }

class Paddle{
	private int x;
	private int y = 650 ;
	private int paddleWidth = 150;
	private int paddleHeight = 10;
	
	public void drawPaddle(Graphics g) {
		
//		int panelWidth = g.getClipBounds().width; // Graphics から親の JPanel の幅を取得
//		x = (panelWidth - paddleWidth) / 2;
		
		g.setColor(Color.white);
		g.fillRect(x, y, paddleWidth, paddleHeight);
	}
	
	public void moveToMouse(int mouseX,int panelWidth) {
		x = mouseX - paddleWidth / 2 ; //マウスのx座標からオブジェクトの半径を引くことでオブジェクトの真ん中をカーソルに合わせる
		if(x < 0) {
			x = 0 ;
		}
		if(x + paddleWidth > panelWidth) {
			x = panelWidth - paddleWidth ;
		}
	}
	
	public Rectangle bounds(){
		return new Rectangle(x,y,paddleWidth,paddleHeight);
		
	}
	
}

class Block{
	private int x ;
	private int y ;
	private int width;
	private int height;
	private boolean blockDestroyed ;//デフォルトでfall
	
	public Block(int x , int y ,int width ,int height) {
		this.x = x ;
		this.y = y ;
		this.width = width ;
		this.height = height ;
	}
	
	public void drawBlock(Graphics g) {
		g.setColor(Color.red);
		g.fillRect(x, y, width, height);
		g.setColor(Color.white);
		g.drawRect(x, y, width, height);//ブロックの枠
	}
	
	public Rectangle bounds() {
		return new Rectangle(x,y,width,height);
	}
	
	public boolean destroyed() {
		blockDestroyed = true ;
		return blockDestroyed;
	}
}

class EndPanel extends JPanel{
	
	public EndPanel() {
		this.setBackground(Color.blue);
	}
	
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.white);
        g.drawString("ゲーム終了！", 100, 100);
    }
}
