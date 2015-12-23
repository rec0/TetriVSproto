package jp.ac.tetrivs;

/* マルチプラットホームにするためのシステムだったりします、今回は関係ありません */
public interface GameInterface {
	/* 画面の更新をサブスレッドから指示できるようにする必要がある */
	abstract void updateScrean();
}
