package jp.ac.tetrivs;

public class AI_second extends AIRunnable{
	
	AI_second(Tetris t) {
		super(t);
	}
	
	/* テトリスデータを取得するためのフィールド値 */
	int[][] putted = Tetris.makeDefaultPutted();
	int[][] moving = Tetris.makeDefaultMoving();
	
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
		/* Putted,Movingミノの取得を行う */
		getPutted();
		getMoving();
	} 
	
	/* Puttedミノを取得する関数 */
	private void getPutted(){
		for(int i = 0; i < this.putted[0].length; i++) for(int j = 0; j < this.putted.length; j++) this.putted[j][i] = t.getPutted(i, j);
	}
	
	/* movingミノを取得する関数 */
	private void getMoving(){
		for(int i = 0; i < this.moving[0].length; i++)for(int j = 0; j < this.moving.length; j++) this.moving[j][i] = t.getMoving(i, j);
	}
}
