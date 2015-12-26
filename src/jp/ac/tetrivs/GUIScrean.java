package jp.ac.tetrivs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

/* テトリスを表示するためのクラスです */
public class GUIScrean extends JFrame implements ActionListener , KeyListener , MouseListener, MouseMotionListener, MouseWheelListener, GameInterface{
	/* テトリスのデータを扱うためのフィールド値 */
	Tetris t = new Tetris();
	GUIScrean me = this;
	private boolean swichGameScreans = true;
	//Panelのインスタンスの宣言
	private JPanel c;
	private JPanel connectScrean;
	private JPanel gameScrean;
	private JPanel[] game = new JPanel[2];
	private JPanel p1,p2,nextP,holdP;
	
	//CardLayoutのフィールド値
	private CardLayout cardLayout;
	
	/* JButtonのインスタンスの作成 */
	private JButton connect;
	
	/* ゲーム画面表示用の配列の作成 */
	private JLabel[][][] minos = new JLabel[2][22][12];
	private JLabel[][] next = new JLabel[4][4];
	private JLabel[][] hold = new JLabel[4][4];
	
	/* ステータス表示用のラベルの作成 */
	private JLabel threadTimerPrinter = new JLabel("  Tetris Thread: " + t.getThreadTimer() + " fps");
	private JLabel aiTimerPrinter = new JLabel("  Ai Thread: " + t.getAiTimer() + " fps");
	private JLabel aiTimePrinter = new JLabel("  Ai Time: " + t.getAiTime() + " ms");
	
	/* 接続用のテキストボックス */
	private JComboBox<String> player;
	
	/* テトリス表示本体部分 */
	public GUIScrean(){
		//ウィンドのタイトルの設定
		this.setTitle("VStetris");
		
		//×ボタンを押したときの動作
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//ウインドウの初期位置とサイズの設定
		this.setBounds(100, 100, 600, 400);
		
		//パネルを作成
		this.c = new JPanel();
		connectScrean = new JPanel();
		connectScrean.setLayout(new BoxLayout(connectScrean,3));
		gameScrean = new JPanel();
		for(int i=0; i < 2; i++) game[i] = new JPanel();
		p1 = new JPanel();
		p2 = new JPanel();
		nextP = new JPanel();
		holdP = new JPanel();
		//コンテナに貼り付ける方向の指定
		this.c.setLayout(new GridLayout(1,2));
		this.nextP.setLayout(new GridLayout(4,4));
		this.holdP.setLayout(new GridLayout(4,4));
		Container container = this.getContentPane();	
		container.add(c);
		c.add(p1);
		c.add(p2);
		/* カードレイアウトを設定する */
		cardLayout = new CardLayout();
		this.p1.setLayout(cardLayout);
		this.gameScrean.setLayout(cardLayout);
		p1.add(connectScrean,"connect");
		p1.add(gameScrean,"gameScrean");
		for(int i = 0; i < 2; i++)gameScrean.add(game[i],"game" + i);
		/* スタート画面 */
		//サーバーの仕様にあわせて先に接続するか後に接続するかを選択
		String[] players = {"ScoreAttack", "AI_1", "AI_2"};
		player = new JComboBox<String>(players);
		/* スタートボタン */
		connect = new JButton("start");
		this.connect.addActionListener(this);
		/* 各要素をはりつけ */
		this.connectScrean.add(player);
		this.connectScrean.add(connect);
		p2.setLayout(new GridLayout(3,2));
		p2.add(nextP);
		p2.add(threadTimerPrinter);
		p2.add(holdP);
		p2.add(aiTimerPrinter);
		p2.add(aiTimePrinter);
		/* ゲーム画面 */
		for(int i=0; i < 2; i++) this.game[i].setLayout(new GridLayout(22,12));
		
		/* パネルにラベルを追加 */
		EtchedBorder border = new EtchedBorder(EtchedBorder.RAISED, Color.lightGray, Color.BLACK);
		for(int k = 0; k < 2; k++){
			for(int i = 0; i < 22; i++)for(int j = 0; j < 12; j++){
				if(t.getMino(i,j) != 0){
					this.minos[k][i][j] = new JLabel(" ");
					this.minos[k][i][j].setOpaque(true);
					this.minos[k][i][j].setBorder(border);
					this.minos[k][i][j].setBackground(Color.BLACK);
				}
				else{
					this.minos[k][i][j] = new JLabel("　");
					this.minos[k][i][j].setOpaque(true);
					this.minos[k][i][j].setBorder(border);
					this.minos[k][i][j].setBackground(Color.WHITE);
				}
			}
			for(int i = 0; i < 4; i++)for(int j = 0; j < 4; j++){
				if(t.getMoving(j,i) != 0) this.minos[k][i+t.getY()][j+t.getX()].setBackground(Color.GRAY);
			}
			for(int i = 0; i < 22; i++)for(int j = 0; j < 12; j++){
				game[k].add(minos[k][i][j]);
			}
		}
		for(int i = 0; i < 4; i ++)for(int j = 0; j < 4; j++){
			this.next[i][j] = new JLabel(" ");
			this.next[i][j].setOpaque(true);
			this.next[i][j].setBorder(border);
			this.next[i][j].setBackground(Color.BLACK);
			nextP.add(next[i][j]);
			this.hold[i][j] = new JLabel(" ");
			this.hold[i][j].setOpaque(true);
			this.hold[i][j].setBorder(border);
			this.hold[i][j].setBackground(Color.BLACK);
			holdP.add(hold[i][j]);
		}
		
		/* キーボード入力を受け付ける */
		addKeyListener(this);
		/* マウス入力を受け付ける */
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}
	
	/* 改行用 */
	public JSeparator getHr(int width, int hight) {
		JSeparator sp = new JSeparator(SwingConstants.HORIZONTAL);
		sp.setPreferredSize(new Dimension(width, hight));
		return sp;
	}
	
	/* ゲームの処理 */
	public static void main(String[] args){
		
		/* 画面表示の画面の設定 */
		GUIScrean screan = new GUIScrean();
		screan.setResizable(false);
		
		/* 画面を表示する */
		screan.setVisible(true);
		screan.requestFocusInWindow();
	}
	
	@Override
	//何かイベントを検知したときに走る文
	public void actionPerformed(ActionEvent event){
		//eventの発生源を取得
		Object source = event.getSource();
		
		/* 開始処理を行う */
		if(source == this.connect){
			t = new Tetris();
			TetrisThread tetris = new TetrisThread(t,me);
			Thread thread = new Thread(tetris);
			thread.start();
			cardLayout.next(p1);
			cardLayout.first(gameScrean);
			if(((String)player.getSelectedItem()).equals("ScoreAttack")){
				/* キーボード入力を受け付ける */
				addKeyListener(this);
				/* マウス入力を受け付ける */
				addMouseListener(this);
				addMouseMotionListener(this);
				addMouseWheelListener(this);
				try {
					Thread.sleep(1001);
					tetris.gameStart();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else if(((String)player.getSelectedItem()).equals("AI_1")){
				/* キーボード入力を受け付けない */
				removeKeyListener(this);
				/* マウス入力を受け付けない */
				removeMouseListener(this);
				removeMouseMotionListener(this);
				removeMouseWheelListener(this);
				/* AIの実体を作成する */
				AI_first ai = new AI_first(t);
				Thread th = new Thread(ai);
				try {
					Thread.sleep(1001);
					tetris.gameStart();
					th.start();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else if(((String)player.getSelectedItem()).equals("AI_2")){
				/* キーボード入力を受け付けない */
				removeKeyListener(this);
				/* マウス入力を受け付けない */
				removeMouseListener(this);
				removeMouseMotionListener(this);
				removeMouseWheelListener(this);
				/* AIの実体を作成する */
				AI_second ai = new AI_second(t);
				Thread th = new Thread(ai);
				try {
					Thread.sleep(1001);
					tetris.gameStart();
					th.start();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		/* キー入力の為にフォーカスをウィンドウに戻しておく */
		requestFocusInWindow();
	}

	/* 画面を更新する:カードレイアウトを使用して裏画面描写後、表画面として映す */
	@Override
	public void updateScrean(){
		if(t.getGameOver()){
			if(swichGameScreans){
				printScrean(1);
				swichGameScreans = false;
			}else{
				printScrean(0);
				swichGameScreans = true;
			}
		}
		else{
			for(int k = 0; k < 2; k++)for(int i = 0; i < 22; i++)for(int j = 0; j < 12; j++)  this.minos[k][i][j].setBackground(Color.black);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			cardLayout.first(p1);
		}
		threadTimerPrinter.setText("  Tetris Thread: " + String.format("%.3f", t.getThreadTimer()) + " fps");
		aiTimerPrinter.setText("  Ai Thread: " + String.format("%.3f",t.getAiTimer()) + " fps");
		aiTimePrinter.setText("  Ai Time: " + String.format("%.3f",t.getAiTime()) + " ms");
	}
	
	/* 画面の描画をする関数（上記画面更新関数で使用） 
	 * index : 描画する画面番号  				*/
	public void printScrean(int index){
		/* 置かれているミノの描画 */
		for(int i = 0; i < 22; i++)for(int j = 0; j < 12; j++){
			if(t.getMino(i,j) == 8){}
			else if(t.getMino(i, j) != 0) this.minos[index][i][j].setBackground(Color.GRAY);
			else this.minos[index][i][j].setBackground(Color.WHITE);
		}
		for(int i = 0; i < 4; i++)for(int j = 0; j < 4; j++){
			if(t.getMoving(j,i) != 0){
				/* 現在のドロップ予測位置の描写 */
				this.minos[index][i+t.getDropY()][j+t.getX()].setBackground(Color.BLUE);
				/* 動いているミノの描画 */
				this.minos[index][i+t.getY()][j+t.getX()].setBackground(Color.GRAY);
			}
			/* ホールドミノの描画 */
			if(t.getHold(j, i) != 0) this.hold[i][j].setBackground(Color.gray);
			else this.hold[i][j].setBackground(Color.white);
			/* ネクストミノの描画 */
			if(t.getNext(j, i) != 0) this.next[i][j].setBackground(Color.gray);
			else this.next[i][j].setBackground(Color.WHITE);
	}
		
		/* 裏画面と表画面の反転 */
		cardLayout.next(gameScrean);
	}
	
	
	/* キーボードからの入力で操作する */
	@Override
	public void keyPressed(KeyEvent e) {
		int keycode = e.getKeyCode();
		if (keycode == KeyEvent.VK_LEFT) t.setLeft(true);
		else if(keycode == KeyEvent.VK_RIGHT) t.setRight(true);
		else if(keycode == KeyEvent.VK_UP) t.setSpinRight(true);
		else if(keycode == KeyEvent.VK_DOWN) t.setDown(true);
		else if(keycode == KeyEvent.VK_SPACE) t.setDrop(true);
		else if(keycode == KeyEvent.VK_SHIFT) t.setHoldMove(true);
		else if(keycode == KeyEvent.VK_ESCAPE) t.setGameOver();
	}
	
	/* マウスを動かした時にミノを動かす */
	@Override
	public void mouseMoved(MouseEvent e) {
		if(e.getX() > 25 && e.getX() < 275) t.setX(( (e.getX()-25) / 25) % 10);
	}
	/* マウスからの入力で操作する（ボタン操作ではない） */
	@Override
	public void mouseClicked(MouseEvent e) {
		int btn = e.getButton();
		if(btn == MouseEvent.BUTTON1){
			t.setSpinLeft(true);
		}else if(btn == MouseEvent.BUTTON2){
			t.setDrop(true);
		}else if(btn == MouseEvent.BUTTON3){
			t.setSpinRight(true);
		}
	}
	/* マウスホイールによる落下 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(e.getWheelRotation() > 0) t.setDown(true);
	}
	
	/* インターフェースの使わない部分 */
	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {	}
	@Override
	public void mousePressed(MouseEvent e) { }
	@Override
	public void mouseReleased(MouseEvent e) { }
	@Override
	public void mouseEntered(MouseEvent e) { }
	@Override
	public void mouseExited(MouseEvent e) { }
	@Override
	public void mouseDragged(MouseEvent e) { }
}
