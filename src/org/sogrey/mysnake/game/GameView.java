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

	public int mStatus_mode = STATUS_RUNNING; // ��ʼ״̬-���У�׼����
	public static final int STATUS_PAUSE = 0x00;// ״̬-��ͣ
	public static final int STATUS_READY = 0x01;// ״̬-׼��
	public static final int STATUS_RUNNING = 0x02;// ״̬-��Ϸ��
	public static final int STATUS_LOSE = 0x03;// ״̬-����

	private int mDirection = DIRECTION_RIGHT;// ��ʼ����-��
	private int mNextDirection = DIRECTION_RIGHT;// ��ʼ��һ����-��
	private static final int DIRECTION_UP = 0x10;// ����-��
	private static final int DIRECTION_DOWN = 0x12;// ����-��
	private static final int DIRECTION_RIGHT = 0x13;// ����-��
	private static final int DIRECTION_LEFT = 0x14;// ����-��

	private static final int TILE_WIDTH = 20;// ������

	private int score = 0;// ����
	private boolean mIsRun;// �Ƿ�������

	private Paint mPaint;// ����

	private GamePlay mGamePlay;
	private float mWidth, mHeight;// ��Ϸ������
	private Rect mPlayRect;// ��Ϸ������

	private int mWallLeft, mWallTop, mWallRight, mWallBottom;// ǽ�ڱ߿�����
	private Tile mApple;// ƻ��
	private int mApplePreX, mApplePreY;// ƻ������
	private int mOffsetLX, mOffsetTY;// ��Ϸ�������Ͻ�ƫ��
	private int mOffsetRX, mOffsetBY;// ��Ϸ�������½�ƫ��

	private boolean mIsEated;// �Ե�ƻ���ˣ�
	private int mScore = 0;// ����
	
	OnWinListener mOnWinListener;// ����һ��OnWinListener����ʤ��������

	private LinkedList<Tile> mLinkedList;

	public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initGameView();// ��ʼ������
		getHolder().addCallback(this);
	}

	public GameView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GameView(Context context) {
		this(context, null);
	}

	// ��ʼ����Ϸ����
	private void initGameView() {
		// TODO Auto-generated method stub
		mPaint = new Paint();// �½�����
		mGamePlay = new GamePlay();
		// getResolution();
		mWidth = mGamePlay.mWidth;
		mHeight = mGamePlay.mHeight;
		Log.d("��Ļ�ߴ�", mWidth + "*" + mHeight);
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
		Log.d("��Ϸ����ߴ�", (mOffsetRX - mOffsetLX) + "*" + (mOffsetBY - mOffsetTY));
		mLinkedList = new LinkedList<Tile>();
		initSnake();
	}

	/** ��ʼ������ */
	private void initSnake() {
		Tile tile = null;
		for (int i = 0; i < 3; i++) {// ��ʼ���ȣ�3
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
		mIsRun = true;// ��Ϸ��ʼ
		// Thread thread=new Thread();
		// thread.start();
		new Thread(new Runnable() {
			public void run() {
				while (mIsRun) {
					switch (mStatus_mode) {
					case STATUS_RUNNING:// ��ʽ��Ϸʱ���߳�����
						try {
							touch();// �����¼��߳�
							Thread.sleep(3000);// ����100����
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
					}
				}
			}
		}).start();
		new Thread(this).start();// �����߳�
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mIsRun = false;// ��Ϸ��ʼ
	}

	@Override
	public void run() {
		while (mIsRun) {
			try {
				switch (mStatus_mode) {
				case STATUS_RUNNING:// ��ʽ��Ϸʱ���߳�����
					checkResult();// �����
					Thread.sleep(1000);// ����1500����
					break;
				}
				refreshView();// ��ͼ����Ϸ����ͼƬ�����
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/** ˢ�½��� */
	private void refreshView() {
		// ����ˢ���¼�
		Canvas canvas = null;
		try {
			canvas = getHolder().lockCanvas();// ��ȡ������������
			refreshView(canvas);// ��ͼ�¼���
		} catch (Exception e) {
		} finally {
			if (canvas != null)
				getHolder().unlockCanvasAndPost(canvas);// �����������
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
	 * ��״̬
	 * 
	 * @param canvas
	 */
	private void drawLose(Canvas canvas) {
		switch (mStatus_mode) {
		case STATUS_LOSE: {
			mPaint.reset();
			mPaint.setColor(0xfff9a41f);
			mPaint.setTextSize(30);
			String string = "��Ϸ������";
			canvas.drawText(string, 0, string.length(), 500, 40, mPaint);
			if (mOnWinListener != null) {// ��������Ϊ��
				post(new Runnable() {// ���߳������̷߳���winDialogue��ʾ����

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
			String string = "��Ϸ��...";
			canvas.drawText(string, 0, string.length(), 500, 40, mPaint);
		}
			break;

		default:
			break;
		}
	}
	/**
	 * ������
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

	/** ������ */
	private void drawBackground(Canvas canvas) {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.background);

		RectF dst = new RectF(0, 0, mWidth, mHeight);
		canvas.drawBitmap(bitmap, null, dst, mPaint);
	}

	/**
	 * ��ǽ��
	 * 
	 * @param canvas
	 */
	private void drawWall(Canvas canvas) {
		mPaint.reset();
		mPaint.setColor(0xfff9a41f);
		mPaint.setStrokeWidth(TILE_WIDTH);
		mPaint.setStyle(Style.STROKE);// ���ķ����ʽ
		canvas.drawRect(mPlayRect, mPaint);
	}

	/**
	 * ����
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
				case DIRECTION_UP:// ��
					if (mDirection != DIRECTION_DOWN) {
						tile = swipTop(tile, tileLast);
						mDirection = mNextDirection;
					} else {
						tile = swipBottom(tile, tileLast);
					}

					break;
				case DIRECTION_DOWN:// ��
					if (mDirection != DIRECTION_UP) {
						tile = swipBottom(tile, tileLast);
						mDirection = mNextDirection;
					} else {
						tile = swipTop(tile, tileLast);
					}
					break;
				case DIRECTION_LEFT:// ��
					if (mDirection != DIRECTION_RIGHT) {
						tile = swipLeft(tile, tileLast);
						mDirection = mNextDirection;
					} else {
						tile = swipRight(tile, tileLast);
					}
					break;
				case DIRECTION_RIGHT:// ��
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
	 * ��ƻ��
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

	/** �����¼����� */
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

	/** �����Ӯ */
	private boolean checkWin() {
		// ײǽ
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

		// ײ�Լ�
		for (int i = 0; i < mLinkedList.size() - 1; i++) {// -1��Ϊ�˲����Լ��Ƚ�
			if (mLinkedList.getLast().isSameAs(mLinkedList.get(i)))
				return true;
		}
		return false;
	}

	private void checkResult() {
		if (mIsRun && checkWin()) {
			mStatus_mode = STATUS_LOSE;
			Log.d("win", "��Ϸ������");
		}
	}
	
	/** ʤ�������ӿ� */
	interface OnWinListener {
		public void onWin(int score);
	}
	/**
	 * ʤ����������
	 * */
	public void setOnWinListener(OnWinListener winListener) {
		mOnWinListener = winListener;
	}
}
