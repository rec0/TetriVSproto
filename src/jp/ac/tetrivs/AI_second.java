package jp.ac.tetrivs;

public class AI_second extends AIRunnable{
	
	AI_second(Tetris t) {
		super(t);
	}
	
	/* テトリスデータを取得するためのフィールド値 */
	int[][] putted = Tetris.makeDefaultPutted();
	int[][] moving = Tetris.makeDefaultMoving();
	int[][] spinJadge = Tetris.makeDefaultMoving();
	
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
		/* Putted,Movingミノの取得を行う */
		getPutted();
		getMoving();
		
		/* 一番下における位置を検出 */
		int depth = 0;
		int depthIndex = 0;
		for(int l = 0; l < 4; l++){
			for(int k = 0; k < 10; k++){
				int depthIter = guessDropDepth(k, t.getY(), this.moving, this.putted);
				if(depth < depthIter){ depth = depthIter; depthIndex = k + l*10; }
			}
			spinJadge[2][0] = moving[0][0];
			spinJadge[1][0] = moving[0][1];
			spinJadge[0][0] = moving[0][2];
			spinJadge[2][1] = moving[1][0];
			spinJadge[1][1] = moving[1][1];
			spinJadge[0][1] = moving[1][2];
			spinJadge[2][2] = moving[2][0];
			spinJadge[1][2] = moving[2][1];
			spinJadge[0][2] = moving[2][2];
			spinJadge[1][3] = moving[3][1];
			spinJadge[3][1] = moving[1][3];
			for(int i = 0; i < 4; i++) for(int j = 0; j < 4; j++) moving[i][j] = spinJadge[i][j];
		}
		
		/* 検出した一番下の位置へ移動するようにコマンドを入力 */
		for(int i = 0; i < depthIndex/10; i++) commands.add(0);
		if((depthIndex%10 -t.getX() ) <= 0 ) for(int i = 0; i < ( t.getX() - depthIndex%10); i++) commands.add(3);
		else for(int i = 0; i < (depthIndex%10 - t.getX()); i++) commands.add(2);
		commands.add(4);
	} 
	
	/* Puttedミノを取得する関数 */
	private void getPutted(){
		for(int i = 0; i < this.putted[0].length; i++) for(int j = 0; j < this.putted.length; j++) this.putted[j][i] = t.getPutted(i, j);
	}
	
	/* movingミノを取得する関数 */
	private void getMoving(){
		for(int i = 0; i < this.moving[0].length; i++)for(int j = 0; j < this.moving.length; j++) this.moving[j][i] = t.getMoving(i, j);
	}
	
	/* ドロップの落下地点の予測をするための関数 */
	public int guessDropDepth(int x, int y, int[][] m, int[][] p){
		boolean moveFlag = true;
		int i = 0;
		for(i=y; (i<=20)&&moveFlag; i++)
		{
			for(int j=0; j<4; j++) for(int k=0; k<4; k++) if(j+1+i < 22)if(k+x < 12)if((m[j][k]!=0)&&(p[j+1+i][k+x]!=0)) 
				moveFlag = false;
		}
		return i-1;
	}
}
