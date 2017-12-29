package com.deyuz.viewer;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class Fragment_mark extends Fragment implements OnClickListener,
        OnLongClickListener {

    private SQLiteDatabase db = null;
    private ArrayList<String> bookmarks;
    private LinearLayout mark_layout;
    private OnMarksClickListener mListener;
    private Button newtab, snewtab, edit, delete;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View view = inflater.inflate(R.layout.activity_mark, container, false);

        initViews(view);

        return view;
    }

    void initViews(View view) {
        mark_layout = (LinearLayout) view.findViewById(R.id.bookmarks);
        bookmarks = queryBookmarks();

        for (int i = 0; i < bookmarks.size(); i++) {
            Button bt = new Button(getActivity());
            bt.setText("☆    " + bookmarks.get(i));
            bt.setBackgroundResource(R.drawable.selector_item);
            bt.setOnClickListener(this);
            bt.setOnLongClickListener(this);
            bt.setGravity(Gravity.START);
            bt.setLabelFor(i);
            mark_layout.addView(bt);
        }

    }

    ArrayList<String> queryBookmarks() {
        db = getActivity().openOrCreateDatabase("bookmark",
                Context.MODE_PRIVATE, null);
        String createCmd = "create table if not exists marks(markname varchar(100),markaddr varchar(100));";
        db.execSQL(createCmd);
        Cursor cursor = db.query("marks", null, null, null, null, null, null);
        ArrayList<String> result = new ArrayList<String>();
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {

                result.add(cursor.getString(0));
                cursor.moveToNext();
            }
        }
        db.close();
        return result;
    }

    public interface OnMarksClickListener {
        public void OnMarksClick(String showText);
    }

    @Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMarksClickListener) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ;

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int index = v.getLabelFor();
        if (index != -1) {
            mListener.OnMarksClick(bookmarks.get(index));
        } else {
            System.out.println("长点击操作");
        }
    }

    @Override
    public boolean onLongClick(View v) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.activity_edit,
                (ViewGroup) v.findViewById(R.id.activity_edit));

        newtab = (Button) layout.findViewById(R.id.edit_newtab);
        snewtab = (Button) layout.findViewById(R.id.edit_snewtab);
        edit = (Button) layout.findViewById(R.id.edit_edit);
        delete = (Button) layout.findViewById(R.id.edit_delete);

        newtab.setOnClickListener(this);
        snewtab.setOnClickListener(this);
        edit.setOnClickListener(this);
        delete.setOnClickListener(this);

        layout.setOnClickListener(this);
        new AlertDialog.Builder(getActivity()).setView(layout).create().show();
        return false;
    }
}
