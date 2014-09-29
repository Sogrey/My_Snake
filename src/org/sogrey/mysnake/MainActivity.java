package org.sogrey.mysnake;

import org.sogrey.mysnake.game.GamePlay;
import org.sogrey.mysnake.rank.RankActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	protected Button mBtnNewGame,mBtnRankingList,mBtnHelp,mBtnExitGame;
	private static final int DIALOG_EXIT=400;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
	}

	private void initViews() {
	mBtnNewGame=(Button) findViewById(R.id.btn_newgame_main);
	mBtnRankingList=(Button) findViewById(R.id.btn_rankinglist_main);
	mBtnHelp=(Button) findViewById(R.id.btn_help_main);
	mBtnExitGame=(Button) findViewById(R.id.btn_exitgame_main);
	mBtnNewGame.setOnClickListener(this);
	mBtnRankingList.setOnClickListener(this);
	mBtnHelp.setOnClickListener(this);
	mBtnExitGame.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		Intent intent;
		int id = view.getId();
		switch (id) {
		case R.id.btn_newgame_main://新游戏
			intent=new Intent(this, GamePlay.class);
			startActivity(intent);
			break;
		case R.id.btn_rankinglist_main://排行榜
			intent=new Intent(this, RankActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_help_main://帮助
			//TODO 
			break;
		case R.id.btn_exitgame_main://退出游戏
			showDialog(DIALOG_EXIT);
			break;

		default:
			break;
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_EXIT://退出游戏
			AlertDialog.Builder builder =new AlertDialog.Builder(this);
			builder.setTitle(R.string.btn_exitgame_main);
			builder.setMessage(R.string.msg_exitgame_main);
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					System.exit(0);
//					finish();
				}
			});
			builder.setNegativeButton(R.string.no, null);
			return builder.create();

		default:
			return super.onCreateDialog(id);
		}
	}
}
