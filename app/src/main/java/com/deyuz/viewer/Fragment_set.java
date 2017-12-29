package com.deyuz.viewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Fragment_set extends Fragment implements OnClickListener {

    private OnSetClickListener mListener;
    private Button forward_bt, mark_bt, joke_bt, refresh_bt, back_bt;
    private Button newtab_bt, snewtab_bt, marked_bt, history_bt, desktop_bt,
            set_bt, feedback_bt;
    private SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.activity_set, container, false);

        initViews(view);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        try {
            mListener = (OnSetClickListener) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addBookmarks(String name, String url) {
        db = getActivity().openOrCreateDatabase("bookmark",
                Context.MODE_PRIVATE, null);
        String createCmd = "create table if not exists marks(markname varchar(100),markaddr varchar(100));";
        db.execSQL(createCmd);
        String cmd = "insert into marks values('" + name + "','" + url + "')";
        db.execSQL(cmd);
        db.close();
    }

    void initViews(View view) {
        //背景按钮
        back_bt = (Button) view.findViewById(R.id.back_bt);
        // 上部的四个按钮
        forward_bt = (Button) view.findViewById(R.id.set_forward);
        mark_bt = (Button) view.findViewById(R.id.set_mark);
        joke_bt = (Button) view.findViewById(R.id.set_joke);
        refresh_bt = (Button) view.findViewById(R.id.set_refresh);
        // 下方的七个按钮
        newtab_bt = (Button) view.findViewById(R.id.function_newtab);
        snewtab_bt = (Button) view.findViewById(R.id.function_snewtab);
        desktop_bt = (Button) view.findViewById(R.id.function_desktop);
        feedback_bt = (Button) view.findViewById(R.id.function_feedback);
        history_bt = (Button) view.findViewById(R.id.function_history);
        marked_bt = (Button) view.findViewById(R.id.function_mark);
        set_bt = (Button) view.findViewById(R.id.function_set);
        // 为所有控件绑定监听器
        forward_bt.setOnClickListener(this);
        mark_bt.setOnClickListener(this);
        joke_bt.setOnClickListener(this);
        refresh_bt.setOnClickListener(this);
        newtab_bt.setOnClickListener(this);
        snewtab_bt.setOnClickListener(this);
        desktop_bt.setOnClickListener(this);
        feedback_bt.setOnClickListener(this);
        history_bt.setOnClickListener(this);
        marked_bt.setOnClickListener(this);
        set_bt.setOnClickListener(this);
        back_bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.set_forward:
                break;
            case R.id.set_joke://笑话按钮，对应代码 1 操作为，在主Activity中，将设置Fragment取消
                new JokeParse(v).execute();
                break;
            case R.id.set_mark:
                // 监听器向Activity发送获取Title和Url的请求，然后由监听器回传
                final String echo[] = mListener.OnSetClick(0);
                addBookmarks(echo[0], echo[1]);
                Toast.makeText(getActivity(), "添加书签" + echo[0], Toast.LENGTH_LONG).show();
                break;
            case R.id.set_refresh:
                break;
            case R.id.function_desktop:
                break;
            case R.id.function_feedback:
                break;
            case R.id.function_history:
                mListener.OnSetClick(1);//取消设置Fragment
                mListener.OnSetClick(3);//出现历史Fragment
                break;
            case R.id.function_mark:
                mListener.OnSetClick(2);
                break;
            case R.id.function_newtab:
                break;
            case R.id.function_set:
                break;
            case R.id.function_snewtab:
                break;
            case R.id.back_bt:
                mListener.OnSetClick(1);
                break;
            default:
                break;
        }
    }

    class JokeParse extends AsyncTask<Void, Void, Elements> {
        View view;

        public JokeParse(View v) {
            view = v;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            Toast.makeText(getActivity(), "解析中...", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected Elements doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                // 解析Url获取Document对象
                Document document = Jsoup.connect(
                        "http://xhkong.com/list-1-1.html").get();
                // 获取网页源码文本内容

                // 获取指定class的内容指定tag的元素
                Elements liElements = document.getElementsByClass("main")
                        .get(0).getElementsByTag("p");

                return liElements;
            } catch (IOException e) {
                System.out.println("解析出错！");
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final Elements result) {
            // TODO Auto-generated method stub
            //弹出设置Fragment
            mListener.OnSetClick(1);
            //对应处理笑话功能
            final ArrayList<String> jokes = new ArrayList<String>();

            for (int i = 0; i < result.size(); i++) {
                jokes.add(i + 1 + ". " + result.get(i));
            }
            // String joke = result.get(0).text();
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View layout = inflater.inflate(R.layout.activity_jokes, (ViewGroup) view.findViewById(R.id.activity_jokes));

            final TextView joke = (TextView) layout.findViewById(R.id.jokes);
            joke.setText("1. " + result.get(0).text());

            OnClickListener OnJokeClick = new OnClickListener() {
                int jokeId = 0;

                @Override
                public void onClick(View v) {
                    if (jokeId < result.size() - 1) {
                        jokeId++;
                        joke.setText(1 + jokeId + ". " + result.get(jokeId).text());
                    } else {
                        joke.setText("1. " + result.get(0).text());
                        jokeId = 0;
                    }
                }
            };

            Button next = (Button) layout.findViewById(R.id.next);
            next.setOnClickListener(OnJokeClick);


            new AlertDialog.Builder(getActivity())
                    .setView(layout)
                    .create().show();
        }

    }

    public interface OnSetClickListener {
        public String[] OnSetClick(int code);
    }
}
