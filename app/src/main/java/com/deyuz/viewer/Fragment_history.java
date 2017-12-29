package com.deyuz.viewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by deyuz on 2016/8/22.
 */
public class Fragment_history extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private OnHistoryClickListener mListener;
    private LinearLayout history_part;
    private ArrayList<String> history;
    private Button newtab_bt, snewtab_bt, mark_bt, delete_bt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_history, container, false);

        initViews(view);

        return view;
    }

    void initViews(View view) {
        history_part = (LinearLayout) view.findViewById(R.id.history_part);
        history = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            history.add("cn.bing.com");
        }


        for (int i = 0; i < history.size(); i++) {
            Button bt = new Button(getActivity());
            bt.setText("︽    " + history.get(i));
            bt.setGravity(Gravity.START);
            bt.setBackgroundResource(R.drawable.selector_item);
            bt.setLabelFor(i);
            bt.setOnClickListener(this);
            bt.setOnLongClickListener(this);
            history_part.addView(bt);
        }
    }


    public interface OnHistoryClickListener {
        public void OnHistoryClick();
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        try {
            mListener = (OnHistoryClickListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {


    }

    @Override
    public boolean onLongClick(View v) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.activity_edit, (ViewGroup) v.findViewById(R.id.activity_edit));

        newtab_bt = (Button) layout.findViewById(R.id.edit_newtab);
        snewtab_bt = (Button) layout.findViewById(R.id.edit_snewtab);
        mark_bt = (Button) layout.findViewById(R.id.edit_edit);
        delete_bt = (Button) layout.findViewById(R.id.edit_delete);

        mark_bt.setText("存为书签");
        delete_bt.setText("删除记录");

        newtab_bt.setOnClickListener(this);
        snewtab_bt.setOnClickListener(this);
        mark_bt.setOnClickListener(this);
        delete_bt.setOnClickListener(this);
        layout.setOnClickListener(this);

        new AlertDialog.Builder(getActivity()).setView(layout).create().show();
        return false;
    }
}
