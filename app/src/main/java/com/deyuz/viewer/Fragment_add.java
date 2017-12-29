package com.deyuz.viewer;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class Fragment_add extends Fragment implements OnClickListener,
		SharedInfo, OnLongClickListener {

	public Button more_bt, newtab_bt, add_bt;
	public int id = 0;// 用于记录当前页序号
	public LinearLayout gallery;
	private OnAddClickListener mListener;
	public int sum = 1;
	private Bitmap bitmap[] = new Bitmap[12];
	public Bitmap bmp;

	@Override
	public View onCreateView(LayoutInflater inflater,
							 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_add, container, false);

		initViews(view);

		return view;
	}

	void initViews(View view) {
		more_bt = (Button) view.findViewById(R.id.more);
		newtab_bt = (Button) view.findViewById(R.id.newtab);
		add_bt = (Button) view.findViewById(R.id.add);
		gallery = (LinearLayout) view.findViewById(R.id.gallery);

		newtab_bt.setBackgroundResource(tabId[0]);
		gallery.setGravity(Gravity.FILL);

		// 为添加新标签页添加按钮
		add_bt.setOnClickListener(this);

		bitmap[0] = bmp;
		gallery.addView(insertImage(0));

		gallery.setPadding(150, 150, 150, 150);

	}

	public View insertImage(Bitmap bmp) {
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setLayoutParams(new LayoutParams(500, 800));
		layout.setGravity(Gravity.CENTER);

		ImageView imageView = new ImageView(getActivity());
		imageView.setLabelFor(id);
		imageView.setImageBitmap(bmp);
		imageView.setOnClickListener(this);
		imageView.setOnLongClickListener(this);
		imageView.setPadding(15, 15, 15, 15);
		layout.addView(imageView);
		return layout;
	}

	public View insertImage(int id) {
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setLayoutParams(new LayoutParams(500, 800));
		layout.setGravity(Gravity.CENTER);

		ImageView imageView = new ImageView(getActivity());
		imageView.setLabelFor(id);
		imageView.setImageBitmap(bitmap[id]);
		imageView.setOnClickListener(this);
		imageView.setOnLongClickListener(this);
		imageView.setPadding(15, 15, 15, 15);
		layout.addView(imageView);
		return layout;
	}

	public interface OnAddClickListener {
		public void OnAddClick(int index);
	}

	@Override
	public void onAttach(android.app.Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnAddClickListener) activity;
		} catch (Exception e) {
			e.printStackTrace();
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.add:
				id++;
				System.out.println("imageId->" + id);
				bitmap[id] = bmp;

				mListener.OnAddClick(-1);

				gallery.addView(insertImage(id));
				break;
			default:
				mListener.OnAddClick(v.getLabelFor());
				for (int i = v.getLabelFor(); i < sum; i++) {

				}
				break;
		}
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getLabelFor()) {
			default:
				System.out.println("start->" + v.getLabelFor() + " end " + sum);
				gallery.removeViews(v.getLabelFor(), sum);
				// gallery.removeViewAt(v.getLabelFor());
				mListener.OnAddClick(-2);
				break;
		}
		return false;
	}
}
