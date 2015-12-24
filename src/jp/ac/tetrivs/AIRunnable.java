package jp.ac.tetrivs;

import java.util.LinkedList;
import java.util.Queue;

public abstract class AIRunnable implements Runnable {
	/* テトリスデータの取得 */
	protected Tetris t;
	/* 待機用変数 */
	protected long time;
	/* コマンド用キュー
	 * 0:右に一度回転
	 * 1:左に一度回転
	 * 2:右に一つ移動
	 * 3:左に一つ移動
	 * 4:ドロップを行う	 */
	protected Queue<Integer> commands = new LinkedList<Integer>();
	
	AIRunnable(Tetris t){
		this.t = t;
	}
	
	abstract void commandsMaker();
	
	@Override
	public void run() {
		while(t.getGameOver()){
			/* ここ以降にAIによるコマンド生成を書いていきます */
			commandsMaker();
			/* コマンド生成ここまで */
			
			/* 生成したコマンドを実行する */
			while(!commands.isEmpty()){
				switch(commands.poll()){
				case 0: t.setSpinRight(true); break;
				case 1: t.setSpinLeft(true); break;
				case 2: t.setRight(true); break;
				case 3: t.setLeft(true); break;
				case 4: t.setDrop(true); break;
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
		time = System.currentTimeMillis();
	}
}
