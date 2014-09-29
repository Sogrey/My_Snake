/**
 * 
 */
package org.sogrey.mysnake.game;

import java.util.LinkedList;
import java.util.Random;

import org.sogrey.mysnake.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * @author Sogrey
 * 
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {

	public int mStatus_mode = STATUS_RUNNING; // 初始状态-运行（准备）
	public static final int STATUS_PAUSE = 0x00;// 状态-暂停
	public static final int STATUS_READY = 0x01;// 状态-准备
	public static final int STATUS_RUNNING = 0x02;// 状态-游戏中
	public static final int STATUS_LOSE = 0x03;// 状态-结束

	private int mDirection = DIRECTION_RIGHT;// 初始方向-右
	private int mNextDirection = DIRECTION_RIGHT;// 初始下一方向-右
	private static final int DIRECTION_UP = 0x10;// 方向-上
	private static final int DIRECTION_DOWN = 0x12;// 方向-下
	private static final int DIRECTION_RIGHT = 0x13;// 方向-右
	private static final int DIRECTION_LEFT = 0x14;// 方向-左

	private static final int TILE_WIDTH = 20;// 蛇身宽度

	private int score = 0;// 分数
	private boolean mIsRun;// 是否在运行

	private Paint mPaint;// 画笔

	private GamePlay mGamePlay;
	private float mWidth, mHeight;// 游戏区域宽高
	private Rect mPlayRect;// 游戏区域宽高

	private int mWallLeft, mWallTop, mWallRight, mWallBottom;// 墙壁边框坐标
	private Tile mApple;// 苹果
	private int mApplePreX, mApplePreY;// 苹果坐标
	private int mOffsetLX, mOffsetTY;// 游戏区域左上角偏移
	private int mOffsetRX, mOffsetBY;// 游戏区域右下角偏移

	private boolean mIsEated;// 吃到苹果了？
	private int mScore = 0;// 分数
	
	OnWinListener mOnWinListener;// 声明一个OnWinListener对象（胜利监听）

	private LinkedList<Tile> mLinkedList;

	public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initGameView();// 初始化界面
		getHolder().addCallback(this);
	}

	public GameView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GameView(Context context) {
		this(context, null);
	}

	// 初始化游戏界面
	private void initGameView() {
		// TODO Auto-generated method stub
		mPaint = new Paint();// 新建画笔
		mGamePlay = new GamePlay();
		// getResolution();
		mWidth = mGamePlay.mWidth;
		mHeight = mGamePlay.mHeight;
		Log.d("屏幕尺寸", mWidth + "*" + mHeight);
		// mWallLeft = (int) mWidth / 22;
		// mWallTop = (int) mHeight / 6;
		// mWallRight = ((int) mWidth * 21 / 22 - mWallLeft-10) % 10 > 0 ?
		// ((int) mWidth * 21 / 22 - ((int) mWidth * 21 / 22 - mWallLeft-10) %
		// 10)
		// : (int) mWidth * 21 / 22;
		// mWallBottom = ((int) mHeight * 29 / 30 - mWallTop-10) % 20 > 0 ?
		// ((int) mHeight * 29 / 30 - ((int) mHeight * 29 / 30 - mWallTop-10) %
		// 20)
		// : (int) mHeight * 29 / 30;

		mWallLeft = ((int) mWidth / 22 + 5) % 10 == 0 ? (int) mWidth / 22
				: ((int) mWidth / 22) - ((int) mWidth / 22 + 5) % 10;
		mWallTop = ((int) mHeight / 6 + 5) % 10 == 0 ? (int) mHeight / 6
				: ((int) mHeight / 6) - ((int) mHeight / 6 + 5) % 10;
		mWallRight = ((int) mWidth * 21 / 22 - mWallLeft - 10) % 20 > 0 ? ((int) mWidth * 21 / 22 - ((int) mWidth
				* 21 / 22 - mWallLeft - 10) % 20)
				: (int) mWidth * 21 / 22;
		mWallBottom = ((int) mHeight * 29 / 30 - mWallTop - 10) % 20 > 0 ? ((int) mHeight * 29 / 30 - ((int) mHeight
				* 29 / 30 - mWallTop - 10) % 20)
				: (int) mHeight * 29 / 30;

		mPlayRect = new Rect(mWallLeft, mWallTop, mWallRight, mWallBottom);
		mOffsetLX = mWallLeft + 10;
		mOffsetTY = mWallTop + 10;
		mOffsetRX = mWallRight - 10;
		mOffsetBY = mWallBottom - 10;
		Log.d("游戏区域尺寸", (mOffsetRX - mOffsetLX) + "*" + (mOffsetBY - mOffsetTY));
		mLinkedList = new LinkedList<Tile>();
		initSnake();
	}

	/** 初始化蛇身 */
	private void initSnake() {
		Tile tile = null;
		for (int i = 0; i < 3; i++) {// 初始长度：3
			int x = mOffsetLX + TILE_WIDTH * 5  + TILE_WIDTH * i;
			int y = (mOffsetBY - mOffsetTY) % TILE_WIDTH == 0 ? (mOffsetBY - mOffsetTY) / 2
					: ((mOffsetBY - mOffsetTY) - (mOffsetBY - mOffsetTY)
							% TILE_WIDTH) / 2;
			tile = new Tile(x, y);
			mLinkedList.addLast(tile);
		}

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mIsRun = true;// 游戏开始
		// Thread thread=new Thread();
		// thread.start();
		new Thread(new Runnable() {
			public void run() {
				while (mIsRun) {
					switch (mStatus_mode) {
					case STATUS_RUNNING:// 正式游戏时候线程休眠
						try {
							touch();// 触摸事件线程
							Thread.sleep(3000);// 休眠100毫秒
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
					}
				}
			}
		}).start();
		new Thread(this).start();// 开启线程
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mIsRun = false;// 游戏开始
	}

	@Override
	public void run() {
		while (mIsRun) {
			try {
				switch (mStatus_mode) {
				case STATUS_RUNNING:// 正式游戏时候线程休眠
					checkResult();// 检查结果
					Thread.sleep(1000);// 休眠1500毫秒
					break;
				}
				refreshView();// 画图（游戏区，图片块矩阵）
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/** 刷新界面 */
	private void refreshView() {
		// 界面刷新事件
		Canvas canvas = null;
		try {
			canvas = getHolder().lockCanvas();// 获取画布，并锁定
			refreshView(canvas);// 绘图事件集
		} catch (Exception e) {
		} finally {
			if (canvas != null)
				getHolder().unlockCanvasAndPost(canvas);// 解除画布锁定
		}
	}

	private void refreshView(Canvas canvas) {
		drawBackground(canvas);
		drawWall(canvas);
		drawApple(canvas);
		drawSnake(canvas);
		drawScore(canvas);
		drawLose(canvas);
	}
	/**
	 * 画状态
	 * 
	 * @param canvas
	 */
	private void drawLose(Canvas canvas) {
		switch (mStatus_mode) {
		case STATUS_LOSE: {
			mPaint.reset();
			mPaint.setColor(0xfff9a41f);
			mPaint.setTextSize(30);
			String string = "游戏结束了";
			canvas.drawText(string, 0, string.length(), 500, 40, mPaint);
			if (mOnWinListener != null) {// 监听器不为空
				post(new Runnable() {// 子线程向主线程发送winDialogue显示请求

					@Override
					public void run() {
						mOnWinListener
								.onWin(mScore);
					}
				});
			}
		}
			break;
		case STATUS_RUNNING: {
			mPaint.reset();
			mPaint.setColor(0xfff9a41f);
			mPaint.setTextSize(30);
			String string = "游戏中...";
			canvas.drawText(string, 0, string.length(), 500, 40, mPaint);
		}
			break;

		default:
			break;
		}
	}
	/**
	 * 画分数
	 * 
	 * @param canvas
	 */
	private void drawScore(Canvas canvas) {
		mPaint.reset();
		mPaint.setColor(0xfff9a41f);
		mPaint.setTextSize(30);
		String score = "Score:" + mScore;
		canvas.drawText(score, 0, score.length(), 200, 40, mPaint);
		
	}

	/** 画背景 */
	private void drawBackground(Canvas canvas) {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.background);

		RectF dst = new RectF(0, 0, mWidth, mHeight);
		canvas.drawBitmap(bitmap, null, dst, mPaint);
	}

	/**
	 * 画墙壁
	 * 
	 * @param canvas
	 */
	private void drawWall(Canvas canvas) {
		mPaint.reset();
		mPaint.setColor(0xfff9a41f);
		mPaint.setStrokeWidth(TILE_WIDTH);
		mPaint.setStyle(Style.STROKE);// 空心风格样式
		canvas.drawRect(mPlayRect, mPaint);
	}

	/**
	 * 画蛇
	 * 
	 * @param canvas
	 */
	private void drawSnake(Canvas canvas) {
		mPaint.reset();
		mPaint.setColor(0xff794ef4);
		Tile tile = null;
		Tile tileLast = mLinkedList.getLast();
//		if (!checkWin()) {
			switch (mStatus_mode) {
			case STATUS_RUNNING:
				// if (mDirection==mNextDirection) return;
				switch (mNextDirection) {
				case DIRECTION_UP:// 上
					if (mDirection != DIRECTION_DOWN) {
						tile = swipTop(tile, tileLast);
						mDirection = mNextDirection;
					} else {
						tile = swipBottom(tile, tileLast);
					}

					break;
				case DIRECTION_DOWN:// 下
					if (mDirection != DIRECTION_UP) {
						tile = swipBottom(tile, tileLast);
						mDirection = mNextDirection;
					} else {
						tile = swipTop(tile, tileLast);
					}
					break;
				case DIRECTION_LEFT:// 左
					if (mDirection != DIRECTION_RIGHT) {
						tile = swipLeft(tile, tileLast);
						mDirection = mNextDirection;
					} else {
						tile = swipRight(tile, tileLast);
					}
					break;
				case DIRECTION_RIGHT:// 右
					if (mDirection != DIRECTION_LEFT) {
						tile = swipRight(tile, tileLast);
						mDirection = mNextDirection;
					} else {
						tile = swipLeft(tile, tileLast);
					}
					break;

				default:
					break;
				}
				mLinkedList.addLast(tile);
				if (!mIsEated) {
					mLinkedList.removeFirst();
				} else {
					mIsEated = false;
				}
				break;
			default:
				break;
			}
//		}

		for (Tile tileItem : mLinkedList) {
			canvas.drawCircle(tileItem.getCenterX(), tileItem.getCenterY(),
					TILE_WIDTH / 2, mPaint);
		}
	}

	/**
	 * 画苹果
	 * 
	 * @param canvas
	 */
	private void drawApple(Canvas canvas) {
		mPaint.reset();
		mPaint.setColor(0xff0af99c);
		if (mApple == null) {
			mApple = new Tile(mLinkedList.getLast().getCenterX(), mLinkedList
					.getLast().getCenterY() + TILE_WIDTH * 5);
			mApplePreX = mPlayRect.centerX();
			mApplePreY = mPlayRect.centerY();
		} else if (Math.abs(mLinkedList.getLast().getCenterX() - mApplePreX) < 20
				&& (Math.abs(mLinkedList.getLast().getCenterY() - mApplePreY) < 20)) {
			mIsEated = true;
			mScore += 10;
			Random random;
			Tile tile;
			Key: for (Tile item : mLinkedList) {
				random = new Random();
				mApplePreX = random.nextInt((int) (mOffsetRX - mOffsetLX)
						/ TILE_WIDTH)
						* TILE_WIDTH + mOffsetLX + 10;
				mApplePreY = random.nextInt((int) (mOffsetBY - mOffsetTY)
						/ TILE_WIDTH)
						* TILE_WIDTH + mOffsetTY + 5;
				tile = new Tile(mApplePreX, mApplePreY);
				if (item.isSameAs(tile))
					continue Key;
			}
		}
		canvas.drawCircle(mApplePreX, mApplePreY, TILE_WIDTH / 2, mPaint);
	}

	/** 触摸事件处理 */
	private void touch() {
		setOnTouchListener(new View.OnTouchListener() {

			private float startX, startY, offsetX, offsetY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = event.getX();
					startY = event.getY();
					break;
				case MotionEvent.ACTION_UP:
					offsetX = event.getX() - startX;
					offsetY = event.getY() - startY;

					if (Math.abs(offsetX) > Math.abs(offsetY)) {
						if (offsetX < -5) {
							Log.e("Touch", "Left");
							mNextDirection = DIRECTION_LEFT;
						} else if (offsetX > 5) {
							Log.e("Touch", "Right");
							mNextDirection = DIRECTION_RIGHT;
						}
					} else {
						if (offsetY < -5) {
							Log.e("Touch", "Up");
							mNextDirection = DIRECTION_UP;
						} else if (offsetY > 5) {
							Log.e("Touch", "Down");
							mNextDirection = DIRECTION_DOWN;
						}
					}
					break;
				}
				return true;
			}

		});
	}

	private Tile swipLeft(Tile tile, Tile tileLast) {
		return tile = new Tile(tileLast.getCenterX() - TILE_WIDTH,
				tileLast.getCenterY());

	}

	private Tile swipRight(Tile tile, Tile tileLast) {
		return tile = new Tile(tileLast.getCenterX() + TILE_WIDTH,
				tileLast.getCenterY());
	}

	private Tile swipTop(Tile tile, Tile tileLast) {
		return tile = new Tile(tileLast.getCenterX(), tileLast.getCenterY()
				- TILE_WIDTH);
	}

	private Tile swipBottom(Tile tile, Tile tileLast) {
		return tile = new Tile(tileLast.getCenterX(), tileLast.getCenterY()
				+ TILE_WIDTH);
	}

	/** 检查输赢 */
	private boolean checkWin() {
		// 撞墙
		if (mLinkedList.getLast().getCenterX() - mWallLeft < 20
				&& mDirection == DIRECTION_LEFT)
			return true;
		else if (mWallRight - mLinkedList.getLast().getCenterX() < 20
				&& mDirection == DIRECTION_RIGHT)
			return true;
		else if (mLinkedList.getLast().getCenterY() - mWallTop < 20
				&& mDirection == DIRECTION_UP)
			return true;
		else if (mWallBottom - mLinkedList.getLast().getCenterY() < 20
				&& mDirection == DIRECTION_DOWN)
			return true;

		// 撞自己
		for (int i = 0; i < mLinkedList.size() - 1; i++) {// -1是为了不与自己比较
			if (mLinkedList.getLast().isSameAs(mLinkedList.get(i)))
				return true;
		}
		return false;
	}

	private void checkResult() {
		if (mIsRun && checkWin()) {
			mStatus_mode = STATUS_LOSE;
			Log.d("win", "游戏结束了");
		}
	}
	
	/** 胜利监听接口 */
	interface OnWinListener {
		public void onWin(int score);
	}
	/**
	 * 胜利监听方法
	 * */
	public void setOnWinListener(OnWinListener winListener) {
		mOnWinListener = winListener;
	}
}
