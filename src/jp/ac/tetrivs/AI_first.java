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
	 * 4:ドロップを行う
	 * 5:ホールドを行う	*/
	@Override
	void commandsMaker() {
		/* ここではホールドを行い,右に3回転,左に3回転,右に3,左に6動かしてからドロップしている */
		commands.add(5);
		for(int i = 0; i < 3; i++) hold();
		for(int i = 0; i < 3; i++) leftSpin();
		for(int i = 0; i < 3; i++) right();
		for(int i = 0; i < 6; i++) left();
		drop();
	} 
}
