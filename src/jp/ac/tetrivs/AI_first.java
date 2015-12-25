package jp.ac.tetrivs;

public class AI_first extends AIRunnable {
	
	AI_first(Tetris t) {
		super(t);
	}
	
	/* ここにAIの処理を書いていく
	 * 処理はcommandsにaddする形式で増やしていく 
	 * コマンド用キュー
	 * 0:右に一度回転
	 * 1:左に一度回転
	 * 2:右に一つ移動
	 * 3:左に一つ移動
	 * 4:ドロップを行う*/
	@Override
	void commandsMaker() {
		/* ここでは右に3回転,左に3回転,右に3,左に6動かしてからドロップしている */
		for(int i = 0; i < 3; i++) commands.add(0);
		for(int i = 0; i < 3; i++) commands.add(1);
		for(int i = 0; i < 3; i++) commands.add(2);
		for(int i = 0; i < 6; i++) commands.add(3);
		commands.add(4);
	} 
}
