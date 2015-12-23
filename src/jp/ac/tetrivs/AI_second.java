package jp.ac.tetrivs;

import java.util.LinkedList;
import java.util.Queue;

public class AI_second implements Runnable{
	/* テトリスデータの取得 */
	private Tetris t;
	/* 待機用変数 */
	private long time;
	/* コマンド用キュー
	 * 0:右に一度回転
	 * 1:左に一度回転
	 * 2:右に一つ移動
	 * 3:左に一つ移動
	 * 4:ドロップを行う	 */
	private Queue<Integer> commands = new LinkedList<Integer>();
	
	AI_second(Tetris t){
		this.t = t;
	}
	
	@Override
	public void run() {
		while(t.getGameOver()){
/* ここ以降にAIによるコマンド生成を書いていきます */
		/* ここでは右に3回転,右に3回転,左に3,左に6動かしてからドロップしている */
		for(int i = 0; i < 3; i++) commands.add(0);
		for(int i = 0; i < 3; i++) commands.add(1);
		for(int i = 0; i < 3; i++) commands.add(3);
		for(int i = 0; i < 6; i++) commands.add(2);
		commands.add(4);
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
