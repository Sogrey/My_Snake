package org.sogrey.mysnake.game;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.sogrey.mysnake.R;
import org.sogrey.mysnake.db.DBWrapper;
import org.sogrey.mysnake.game.GameView.OnWinListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.EditText;

/**
 * @author Sogrey
 * 
 */
public class GamePlay extends Activity implements OnWinListener  {

	/** ��Ϸ�����Ի���ID */
	public static final int DIALOG_END = 0x400;
	public static float mWidth;// �õ����
	public static float mHeight;// �õ��߶�

	public int mScore;// ����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getResolution();
		setContentView(R.layout.activity_game);
		((GameView)findViewById(R.id.gameView)).setOnWinListener(this);
	}

	/**
	 * ��ȡ��Ļ�ֱ���
	 */
	private void getResolution() {
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);
		mWidth = displayMetrics.widthPixels;// �õ����
		mHeight = displayMetrics.heightPixels;// �õ��߶�
	}

	
	@Override
	public void onWin(int score) {
		mScore = score;
		showDialog(DIALOG_END);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_END:// ��Ϸ�����Ի���ID
			return CreateEndDialog();
		default:
			return super.onCreateDialog(id);
		}
	}

	private Dialog CreateEndDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.tit_gameover);
		builder.setMessage(getString(R.string.msg_gameover, mScore));
		final EditText edt = new EditText(this);
		builder.setView(edt);
		builder.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String name = edt.getText().toString();
						if (TextUtils.isEmpty(name)) {
							name=getString(R.string.txt_no_name);
						}
						insertData(name, mScore);
						finish();
					}
				});
		return builder.create();
	}

	/** �������ݵ����ݿ� */
	protected void insertData(String name, int score) {
		DBWrapper wrapper = DBWrapper.getInstance(this);
		/* ���ڸ�ʽ */
		String patternDate = "yyyy-MM-dd";
		/* new ��SimpleDateFormat ����sdf */
		SimpleDateFormat sdfDate = new SimpleDateFormat(patternDate);
		/* ��ʽ����ǰʱ�丳�� date */
		String date = sdfDate.format(new Date());
		wrapper.insertDB(name, score, date);
	}
}
