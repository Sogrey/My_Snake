package org.sogrey.mysnake.game;

import android.Manifest.permission;

/**
 * @author Sogrey
 * 
 */
public class Tile {

	// �÷�������ĵ�x����
	private int mCenterX;
	// �÷�������ĵ�y����
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

	/** �ж��Ƿ���ͬһ�� */
	public boolean isSameAs(Tile tile) {
		if (mCenterX == tile.getCenterX()
				&& mCenterY == tile.getCenterY()) {
			return true;
		} else {
			return false;
		}
	}
}
