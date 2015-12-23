package jp.ac.tetrivs;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/* テトリスの処理及び各種データを保持するクラスです */
public class Tetris {
	
	/* 処理用のフィールド値を宣言 */
	private int[][] Putted = makeDefaultPutted();
	private int[][] closedStates = makeDefaultPutted();
	private Queue<Integer> nextMinos = new LinkedList<Integer>();
	private int[][] moving = Minoes[selectNextMino()];
	/* ホールドしているミノの保持 */
	private int[][] hold = makeDefaultMoving();
	/* x,y座標および処理用フィールド値 */
	private int x = 4, y = 0, attack = 0, dropY = 0;
	/* ネクストミノ生成用キュー */
	private Queue<Integer> damage = new LinkedList<Integer>();
	
	/* 落下予測地点を表すフィールド値
	 * status: 	int値:	落下予測距離
	 * 			-1	:  	落下禁止		*/
	private int[] guessedDropYs = {0,0,0,0,0,0,0,0,0,0};
	
	/* ドロップの遊びを計るのとミノが落ちる速度とミノが落ちたかどうかを判定するフィールド値 */
	private int dropSpace = 0, downSpace = 0, dropMinoVelocity = 60;
	boolean droped = false;
	/* 処理入力用の判定値のフィールド値 */
	private boolean drop = false, down = false, left = false, right = false, spinLeft = false, spinRight = false, updateScrean = false;
	private boolean ScreanWait = false, damager = false;
	private boolean moveFlag = false, gameOverFlag = true;
	
	/* 下左右へのあたり判定 */
	public void moveLeftJadge(){
		moveFlag = true;
		for(int j=0; j<4; j++)
			for(int i=0; i<4; i++)
				if((moving[j][i]!=0)&&(Putted[j+y][i-1+x]!=0)) moveFlag = false;
		if(moveFlag) x = x - 1;
	}
	public void moveRightJadge(){
		moveFlag = true;
		for(int i=0; i<4; i++)
			for(int j=0; j<4; j++)
				if((moving[j][i]!=0)&&(Putted[j+y][i+1+x]!=0)) moveFlag = false;
		if(moveFlag) x = x + 1;
	}
	public void moveDownJadge(){
		moveFlag = true;
		for(int i=0; i<4; i++)
			for(int j=0; j<4; j++)
				if((moving[j][i]!=0)&&(Putted[j+1+y][i+x]!=0)) moveFlag = false;
		if(moveFlag){
			y = y + 1;
			downSpace = 0;
		}
	}
	
	/* 右回転の判定をする関数 */
	public void spinRightMoveJadge(){
		int[][] spinJadge = makeDefaultMoving();
		
		spinJadge[0][0] = moving[2][0];
		spinJadge[0][1] = moving[1][0];
		spinJadge[0][2] = moving[0][0];
		spinJadge[1][0] = moving[2][1];
		spinJadge[1][1] = moving[1][1];
		spinJadge[1][2] = moving[0][1];
		spinJadge[2][0] = moving[2][2];
		spinJadge[2][1] = moving[1][2];
		spinJadge[2][2] = moving[0][2];
		spinJadge[1][3] = moving[3][1];
		spinJadge[3][1] = moving[1][3];

		boolean nextJadger = true;
		nextJadger = spinJadger(0,0,spinJadge);
		if(nextJadger)nextJadger = spinJadger(1,0,spinJadge);
		if(nextJadger)nextJadger = spinJadger(-1,0,spinJadge);
		if(nextJadger)nextJadger = spinJadger(-2,0,spinJadge);
		if(nextJadger)nextJadger = spinJadger(-1,1,spinJadge);
		if(nextJadger)nextJadger = spinJadger(-2,1,spinJadge);
		if(nextJadger)nextJadger = spinJadger(-1,2,spinJadge);
	}
	
	/* 左回転の判定をする関数 */
	public void spinLeftMoveJadge(){
		int[][] spinJadge = makeDefaultMoving();
		
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

		boolean nextJadger = true;
		nextJadger = spinJadger(0,0,spinJadge);
		if(nextJadger)nextJadger = spinJadger(1,0,spinJadge);
		if(nextJadger)nextJadger = spinJadger(-1,0,spinJadge);
		if(nextJadger)nextJadger = spinJadger(-2,0,spinJadge);
		if(nextJadger)nextJadger = spinJadger(1,1,spinJadge);
		if(nextJadger)nextJadger = spinJadger(2,1,spinJadge);
		if(nextJadger)nextJadger = spinJadger(1,2,spinJadge);
	}
	
	/* 回転の当たり判定をする関数 */
	private boolean spinJadger(int xx, int yy, int[][] spinJadge){
		boolean res = true;
		moveFlag = true;
		for(int i=0; i<4; i++) for(int j=0; j<4; j++) if( (i+y+yy < 22) && (j+x+xx < 12) && (i+y+yy >= 0) && (j+x+xx >= 0)){
			if((spinJadge[i][j] != 0)&&(Putted[i+y+yy][j+x+xx] != 0)) moveFlag = false;
		}
		if(moveFlag){
			for(int i=0; i<4; i++) for(int j=0; j<4; j++)this.moving[i][j] = spinJadge[i][j];
			x += xx;
			y += yy;
			res = false;
		}
		return res;
	}
	
	/* 直代入用横方向判定 */
	public boolean moveHorizonJadge(int x){
		moveFlag = true;
		for(int j=0; j<4; j++)
			for(int i=0; i<4; i++)
				if(i+x >= 0 && i+x < 12) { if((moving[j][i]!=0)&&(Putted[j+y][i+x]!=0)) moveFlag = false; }
		return moveFlag;
	}
	
	/* ドロップの判定をする関数 */
	public void dropMoveJadge(){
		y = guessDropPosition(this.x);
		this.dropedMino();
	}
	
	/* ドロップの落下地点をすべてのxの場合について予測するための関数 */
	public void guessDropPositions(){
		for(int i=0; i<this.guessedDropYs.length; i++){
			this.guessedDropYs[i] = guessDropPosition(i);
			if(this.guessedDropYs[i] == this.y) this.guessedDropYs[i] = -1;
		}
	}
	
	/* ドロップの落下地点の予測をするための関数 */
	public int guessDropPosition(int x){
		moveFlag = true;
		int i = 0;
		for(i=y; (i<=20)&&moveFlag; i++)
		{
			for(int j=0; j<4; j++) for(int k=0; k<4; k++) if(j+1+i < 22)if(k+x < 12)if((moving[j][k]!=0)&&(Putted[j+1+i][k+x]!=0)) 
				moveFlag = false;
		}
		return i-1;
	}
	
	/* ミノの自由落下 */
	public void downMino(){
		/* 自由落下処理 */
		moveFlag = true;
		for(int i=0; i<4; i++) for(int j=0; j<4; j++) if((moving[j][i]!=0)&&(Putted[j+1+y][i+x]!=0)) moveFlag = false;
		if(moveFlag){
			downSpace++;
			if(downSpace >= dropMinoVelocity){
				y++;
				this.setUpdateScrean(true);
				downSpace = 0;
			}
		}
		/* 地面に設置した際の遊びの処理 */
		if(!moveFlag){
			dropSpace ++;
			if(dropSpace >= 30) droped = true;
		}else dropSpace = 0;
		/* 落ちた時に発生する処理 */
		if(droped){
			this.dropedMino();
			this.setUpdateScrean(true);
			droped = false;
		}
	}
	
	/* ミノが落ちたときの動作 */
	public void dropedMino(){
		/* movingをPuttedに格納 */
		for(int i = 0; i < moving.length; i++)for(int j = 0; j < moving[0].length; j++)
			if(moving[i][j]!=0 && j+y >= 0 && j+x < 12 && i+y >=0)Putted[i+y][j+x] = moving[i][j];
		/* 列がそろっているか確認するとともにダメージ判定を行う */
		attack = 0;
		for(int i=20; i>1; )
		{
			if((Putted[i][1]!=0)&&(Putted[i][2]!=0)&&(Putted[i][3]!=0)&&(Putted[i][4]!=0)&&(Putted[i][5]!=0)&&
					(Putted[i][6]!=0)&&(Putted[i][7]!=0)&&(Putted[i][8]!=0)&&(Putted[i][9]!=0)&&(Putted[i][10]!=0))
			{
				attack++;
				for(int j=i; j>1; j--)
				{
					for(int k=1; k<11; k++)
					{
						Putted[j][k] = Putted[j-1][k];
					}
				}
				for(int k=1; k<11; k++)
				{
					Putted[1][k] = 0;
				}
			}
			else i--;
		}
		if(attack-1 > 1) this.setDamage(attack - 1);
		if(this.getDamager()) damaged(this.getDamage());
		moving = Minoes[selectNextMino()];
		x = 4;
		y = 0;
		if(Putted[0][4] != 0 || Putted[0][5] != 0) setGameOver();
	}
	
	/* 次のミノを選択するための関数 */
	private int selectNextMino(){
		if(nextMinos.isEmpty()){
			List<Integer> nexts = new ArrayList<Integer>();
			for(int i=0; i<7; i++) nexts.add(i);
			Collections.shuffle(nexts);
			nextMinos.addAll(nexts);
		}
		int next = nextMinos.poll();
		return next;
	}
	
	/* 動いているミノを置いた状態の盤面を予測する関数 */
	private int[][] guessPuttedMino(int putted[][], int moving[][], int movingX, int movingY){
		int[][] res = makeDefaultPutted();
		for(int i = 0; i < putted.length; i++)for(int j = 0; j < putted[0].length; j++){
			res[i][j] = putted[i][j];
		}
		for(int i = 0; i < moving.length; i++)for(int j = 0; j < moving[0].length; j++)
			if(moving[i][j]!=0 && j+movingX >= 0 && j+movingX < 12 && i+movingY >=0){
			res[i+movingY][j+movingX] = moving[i][j];
		}
		return res;
	}
	private int[][] guessPuttedMino(){
		return guessPuttedMino(this.Putted, this.moving, this.x, this.y);
	}
	
	/* ダメージを受けた際の動作 */
	private Random rand = new Random();
	private void damaged(int damage){
		int space = rand.nextInt(9) + 1;
		for(int j = damage; j < 22; j++)for(int i = 1; i <= 10; i++)Putted[j-damage][i] = Putted[j][i];
		for(int j = 0; j < damage; j++)for(int i = 1; i <= 10; i++){
			if(i != space) Putted[20-j][i] = 9;
			else Putted[20-j][i] = 0;
		}
		if(this.damage.isEmpty()) this.setDamager(false);
	}
	
	/* 置かれたミノのデータを渡す */
	public int getMino(int i, int j){
		return this.Putted[i][j];
	}
	
	/* x軸方向のデータを渡す */
	public int getX(){
		return this.x;
	}
	
	/* y軸方向のデータを渡す */
	public int getY(){
		return this.y;
	}
	
	/* y軸方向の予測された落下地点のデータを渡す */
	public int getDropY(){
		return this.dropY;
	}
	
	/* 動いているミノのデータを渡す */
	public int getMove(int i, int j){
		return this.moving[i][j];
	}
		
	/* ミノの形の保持 */
	static final int[][][] Minoes = {
		{	{1,1,0,0},
			{0,1,1,0},
			{0,0,0,0},
			{0,0,0,0}	},
		{	{0,0,0,0},
			{2,2,2,0},
			{2,0,0,0},
			{0,0,0,0},	},
		{	{0,0,0,0},
			{3,3,3,0},
			{0,0,3,0},
			{0,0,0,0},	},
		{	{0,0,0,0},
			{4,4,4,4},
			{0,0,0,0},
			{0,0,0,0}	},
		{
			{0,5,0,0},
			{5,5,5,0},
			{0,0,0,0},
			{0,0,0,0}	},
		{
			{0,6,6,0},
			{6,6,0,0},
			{0,0,0,0},
			{0,0,0,0}	},
		{
			{7,7,0,0},
			{7,7,0,0},
			{0,0,0,0},
			{0,0,0,0}	
		}
	};	
	/* 処理変数に処理タスクをいれる */
	public void setDrop(boolean b){
		drop = b;
	}
	public void setDown(boolean b){
		down = b;
	}
	public void setLeft(boolean b){
		left = b;
	}
	public void setRight(boolean b){
		right = b;
	}
	public void setSpinLeft(boolean b){
		spinLeft = b;
	}
	public void setSpinRight(boolean b){
		spinRight = b;
	}
	public void setUpdateScrean(boolean b){
		updateScrean = b;
	}
	public void setGameOver(){
		gameOverFlag = false;
	}
	public void setDamage(int d){
		if(d != -1)this.damage.add(d);
		else setGameOver();
	}
	public void setAttack(int a){
		this.attack = a;
	}
	public void setDamager(boolean b){
		this.damager = b;
	}
	public void setX(int x){
		if(x>=0 && x<=11 && moveHorizonJadge(x)) this.x = x;
	}
	/* 処理変数を返す */
	public boolean getDrop(){
		return drop;
	}
	public boolean getDown(){
		return down;
	}
	public boolean getLeft(){
		return left;
	}
	public boolean getRight(){
		return right;
	}
	public boolean getSpinLeft(){
		return spinLeft;
	}
	public boolean getSpinRight(){
		return spinRight;
	}
	public boolean getUpdateScrean(){
		return updateScrean;
	}
	public boolean getGameOver(){
		return gameOverFlag;
	}
	public int getDamage(){
		return damage.poll();
	}
	public int getAttack(){
		return attack;
	}
	public boolean getDamager(){
		return damager;
	}
	
	/* 処理に使う関数関係 */
	/* デフォルトのputtedミノを与える関数 */
	private int[][] makeDefaultPutted(){
		int[][] d = {
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,0,0,0,0,0,0,0,0,0,0,8},
				{8,8,8,8,8,8,8,8,8,8,8,8},
			};
		return d;
	}
	/* デフォルトのmovingミノを与える関数 */
	private int[][] makeDefaultMoving(){
		int[][] m = {
				{0,0,0,0},
				{0,0,0,0},
				{0,0,0,0},
				{0,0,0,0},	
		};
		return m;
	}
}