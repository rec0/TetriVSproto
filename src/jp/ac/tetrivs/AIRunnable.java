package jp.ac.tetrivs;

import java.util.LinkedList;
import java.util.Queue;

public abstract class AIRunnable implements Runnable {
	/* テトリスデータの取得 */
	private Tetris t;
	/* 待機用変数 */
	protected long time;
	/* コマンド作成時間処理用変数 */
	protected double countTime;
	/* テトリスデータを取得するためのフィールド値 */
	protected int[][] putted = Tetris.makeDefaultPutted();
	protected int[][] moving = Tetris.makeDefaultMoving();
	protected int[][] next = Tetris.makeDefaultMoving();
	protected int[][] hold = Tetris.makeDefaultMoving();
	protected int movingX = 0;
	protected int movingY = 0;
	/* fps計算用変数 */
	protected double fpsTimer = 0;
	/* コマンド用キュー
	 * 0:右に一度回転
	 * 1:左に一度回転
	 * 2:右に一つ移動
	 * 3:左に一つ移動
	 * 4:ドロップを行う	 
	 * 5:ホールドを行う	*/
	protected Queue<Integer> commands = new LinkedList<Integer>();
	
	/* コマンドの指示をわかりやすいように関数化 */
	public void rightSpin(){
		commands.add(0);
	}
	public void leftSpin(){
		commands.add(1);
	}
	public void right(){
		commands.add(2);
	}
	public void left(){
		commands.add(3);
	}
	public void drop(){
		commands.add(4);
	}
	public void hold(){
		commands.add(5);
	}
	
	AIRunnable(Tetris t){
		this.t = t;
	}
	
	abstract void commandsMaker();
	
	@Override
	public void run() {
		while(t.getGameOver()){
			
			countTime = System.currentTimeMillis();
			/* ここ以降にAIによるコマンド生成を書いていきます */
			commandsMaker();
			/* コマンド生成ここまで */
			t.setAiTime(System.currentTimeMillis() - countTime);
			
			/* 生成したコマンドを実行する */
			while(!commands.isEmpty()){
				switch(commands.poll()){
				case 0: t.setSpinRight(true); break;
				case 1: t.setSpinLeft(true); break;
				case 2: t.setRight(true); break;
				case 3: t.setLeft(true); break;
				case 4: t.setDrop(true); break;
				case 5: t.setHoldMove(true); break;
				default:
				}
				waiter(3);
			}
			waiter(3);
		}
	}
	
	/* iフレーム分ウエイトする */
	private void waiter(int i){
		if(System.currentTimeMillis() < (time + 16*i) ) try {
			Thread.sleep( 16*i - (System.currentTimeMillis() - time) );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		/* fpsの計算と格納 */
		t.setAiTimerCounter( t.getAiTimerCounter() + i );
		if(t.getAiTimerCounter() > 30){
			t.setAiTimer( ( ( System.currentTimeMillis() - fpsTimer ) / 500 ) * 60);
			t.setAiTimerCounter(0);
			fpsTimer = System.currentTimeMillis();
		}
		
		time = System.currentTimeMillis();
	}
	/* Puttedミノを取得する関数 */
	protected void getPutted(){
		for(int i = 0; i < this.putted[0].length; i++) for(int j = 0; j < this.putted.length; j++) this.putted[j][i] = t.getPutted(i, j);
	}
	
	/* movingミノを取得する関数 */
	protected void getMoving(){
		for(int i = 0; i < this.moving[0].length; i++)for(int j = 0; j < this.moving.length; j++) this.moving[j][i] = t.getMoving(i, j);
	}
	
	/* nextミノを取得する関数 */
	protected void getNext(){
		for(int i = 0; i < this.next[0].length; i++)for(int j = 0; j < this.next.length; j++) this.next[j][i] = t.getNext(i, j);
	}
	/* holdミノを取得する関数 */
	protected void getHold(){
		for(int i = 0; i < this.hold[0].length; i++)for(int j = 0; j < this.hold.length; j++) this.hold[j][i] = t.getHold(i, j);
	}
	/* movingXを取得する関数 */
	protected void getMovingX(){
		this.movingX = t.getX();
	}
	/* movingYを取得する関数 */
	protected void getMovingY(){
		this.movingY = t.getY();
	}
}
