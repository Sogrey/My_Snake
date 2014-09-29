package org.sogrey.mysnake.game;

import android.Manifest.permission;

/**
 * @author Sogrey
 * 
 */
public class Tile {

	// 该方块的中心的x坐标
	private int mCenterX;
	// 该方块的中心的y座标
	private int mCenterY;

	public Tile(int x, int y) {
		mCenterX = x;
		mCenterY = y;
	}

	public int getCenterX() {
		return mCenterX;
	}

	public void setCenterX(int mCenterX) {
		this.mCenterX = mCenterX;
	}

	public int getCenterY() {
		return mCenterY;
	}

	public void setCenterY(int mCenterY) {
		this.mCenterY = mCenterY;
	}

	/** 判断是否是同一个 */
	public boolean isSameAs(Tile tile) {
		if (mCenterX == tile.getCenterX()
				&& mCenterY == tile.getCenterY()) {
			return true;
		} else {
			return false;
		}
	}
}
