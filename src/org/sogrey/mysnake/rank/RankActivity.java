package org.sogrey.mysnake.rank;

import java.util.LinkedList;

import org.sogrey.mysnake.R;
import org.sogrey.mysnake.db.DBWrapper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Sogrey
 * 
 */
public class RankActivity extends Activity {
	
	protected LinkedList<DBWrapper.data> mData;
	protected LstAdapter myAdapter;
	protected ListView mLstRank;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rank);
		initDatas();
		initViews();
	}

	private void initDatas() {
		mData = DBWrapper.getInstance(this).rawQueryDB();
		myAdapter = new LstAdapter();
		myAdapter.notifyDataSetChanged();
	}

	private void initViews() {
		mLstRank=(ListView) findViewById(R.id.lst_rank);
		mLstRank.setAdapter(myAdapter);
	}
	
	class LstAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return mData.get(position).hashCode();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View  view  = convertView;
			ViewHolder holder;
			if (convertView==null) {
				holder=new ViewHolder();
				view = getLayoutInflater().inflate(R.layout.item_of_rank_list, null);
				holder.txtName = (TextView) view.findViewById(R.id.item_name_of_lst_rank);
				holder.txtScores = (TextView) view.findViewById(R.id.item_scores_of_lst_rank);
				holder.txtDate = (TextView) view.findViewById(R.id.item_date_of_lst_rank);
				view.setTag(holder);
			}else {
				holder=(ViewHolder) view.getTag();
			}
//			holder.txtName.setText(mData.get(position).getName());
			holder.txtName.setText(mData.get(position).name);
//			holder.txtScores.setText(mData.get(position).getScores());
			holder.txtScores.setText(mData.get(position).scores+"");
//			holder.txtDate.setText(mData.get(position).getDate());
			holder.txtDate.setText(mData.get(position).date);
			return view;
		}
		
	}
	
	class ViewHolder{
		TextView txtName,txtScores,txtDate;
	}
}
