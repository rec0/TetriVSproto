package jp.ac.tetrivs;

/* 実際にテトリスの処理を行うスレッドです */
public class TetrisThread implements Runnable {
	/* テトリスデータ格納とループ用フィールド値 */
	Tetris t;
	GameInterface screan;
	/* コンストラクタの宣言 */
	TetrisThread(Tetris t, GameInterface screan){
		this.t = t;
		this.screan = screan;
	}
	/* フレームレート固定用のフィールド値 */
	long time;
	
	/* 処理のメイン文 */
	@Override
	public void run() {
		try {
			this.suspend();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		while(t.getGameOver()){
			time = System.currentTimeMillis();
			
			if(t.getDown()) 		t.moveDownJadge();		t.setDown(false);		t.setUpdateScrean(true);
			if(t.getLeft()) 		t.moveLeftJadge();		t.setLeft(false);		t.setUpdateScrean(true);
			if(t.getRight()) 		t.moveRightJadge();		t.setRight(false);		t.setUpdateScrean(true);
			if(t.getSpinLeft())		t.spinLeftMoveJadge();	t.setSpinLeft(false);	t.setUpdateScrean(true);
			if(t.getSpinRight())	t.spinRightMoveJadge();	t.setSpinRight(false);	t.setUpdateScrean(true);
			if(t.getDrop()) 		t.dropMoveJadge();		t.setDrop(false);		t.setUpdateScrean(true);
			t.downMino();
			
			/* 画面へ描写の指示 */
			if(t.getUpdateScrean())	screan.updateScrean();	t.setUpdateScrean(false);
			
			if(System.currentTimeMillis() < (time + 16) ){
				try {
					Thread.sleep( 16 - (System.currentTimeMillis() - time) );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(!t.getGameOver()) screan.updateScrean();
		}
	}
	
	public synchronized void suspend() throws InterruptedException{
		this.wait();
	}
	
	public synchronized void gameStart(){
		this.notifyAll();
	}
}
