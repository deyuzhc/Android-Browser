package com.deyuz.viewer;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.select.Elements;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Fragment_tab extends Fragment implements OnClickListener,
        SharedInfo, DownloadListener {

    @SuppressWarnings("unused")
    private boolean inputing = false;

    private TextView backcolor, line;
    private Button marked_bt, history_bt;
    private RelativeLayout bottom_lo;

    private OnTabsClickListener mListener;

    public int id;
    public ImageView logo_iv;
    public EditText search_et;
    public Button newtab_bt, more_bt;
    public WebView webView;

    // 设置 部分的按钮
    private Button mark_bt;

    public Elements elements;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.activity_tab, container, false);

        initViews(view);

        newtab_bt.setBackgroundResource(tabId[id]);
        System.out.println("id->" + id);

        return view;
    }

    // 所有按钮和背景框的监听器
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.backcolor:
                // 小图标，线条设为不可见
                line.setVisibility(View.GONE);
                // 地址栏设为空
                search_et.setText("");
                // 返回原来位置
                if (logo_iv.getVisibility() == View.VISIBLE) {
                    /*Animation anim = AnimationUtils.loadAnimation(getActivity(),
                            R.animator.anim_down);
					search_et.startAnimation(anim);*/
                    //logo_iv.startAnimation(anim);
                    Animator animator = ObjectAnimator.ofFloat(search_et, "translationY", -0.6f, 0.0f);
                    animator.setDuration(120);
                    animator.start();

                    Animator anim = ObjectAnimator.ofFloat(logo_iv, "translationY", -0.6f, 0.0f);
                    anim.setDuration(120);
                    anim.start();

                    @SuppressWarnings("deprecation")
                    RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.FILL_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    rl.addRule(RelativeLayout.ALIGN_TOP, R.id.center);
                    rl.addRule(RelativeLayout.CENTER_HORIZONTAL);

                    rl.setMargins(40, 60, 40, 0);
                    search_et.setLayoutParams(rl);

                    newtab_bt.setVisibility(View.VISIBLE);
                    more_bt.setVisibility(View.VISIBLE);

                    bottom_lo.setVisibility(View.VISIBLE);
                }

                search_et.clearFocus();

                // 退出软键盘
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }

                inputing = false;
                // 回到原位，设置背景色不可见
                backcolor.setVisibility(View.GONE);
                break;
            case R.id.newtab:// 新标签页，位于右上角
                mListener.OnTabClick(0);
                break;
            case R.id.more:// 更多，位于右上角
                // 产生设置模块
                mListener.OnTabClick(3);
                break;
            case R.id.history://历史记录按钮，位于右下角
                mListener.OnTabClick(2);
                break;
            case R.id.marked:// 保存的书签页，位于左下角
                mListener.OnTabClick(1);
                break;
            default:
                break;
        }
    }

    void initViews(View view) {
        search_et = (EditText) view.findViewById(R.id.search_et);
        backcolor = (TextView) view.findViewById(R.id.backcolor);
        marked_bt = (Button) view.findViewById(R.id.marked);
        history_bt = (Button) view.findViewById(R.id.history);
        webView = (WebView) view.findViewById(R.id.webview);
        logo_iv = (ImageView) view.findViewById(R.id.logo);
        line = (TextView) view.findViewById(R.id.line);
        newtab_bt = (Button) view.findViewById(R.id.newtab);
        bottom_lo = (RelativeLayout) view.findViewById(R.id.bottom);
        more_bt = (Button) view.findViewById(R.id.more);

        configWebView();

        search_et.setOnKeyListener(OnKey);
        search_et.setOnFocusChangeListener(OnFocusChange);
        backcolor.setOnClickListener(this);
        newtab_bt.setOnClickListener(this);
        marked_bt.setOnClickListener(this);
        more_bt.setOnClickListener(this);
        history_bt.setOnClickListener(this);


    }

    public interface OnTabsClickListener {
        public void OnTabClick(int code);
    }

    OnFocusChangeListener OnFocusChange = new OnFocusChangeListener() {

        @SuppressWarnings("deprecation")
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            // TODO Auto-generated method stub
            if (hasFocus) {
                // 播放搜索框上升动画

                ObjectAnimator animator = ObjectAnimator.ofFloat(search_et, "translationY", 0.5f, 0.0f);
                animator.setDuration(300);
                animator.start();

                // 搜索栏内容设为网址
                search_et.setText(webView.getUrl());
                // 使搜索栏光标可见
                search_et.setCursorVisible(true);
                // 设置搜索栏在顶部的格式
                search_et.setLayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.FILL_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT));
                // 使背景色可见
                backcolor.setVisibility(View.VISIBLE);
                // 底部的 书签栏 和 搜索历史 设为不可见

                bottom_lo.setVisibility(View.GONE);

                // 右上角的两个按钮
                more_bt.setVisibility(View.GONE);
                newtab_bt.setVisibility(View.GONE);
                // 输入状态为真
                inputing = true;
            }
        }
    };

    // 对应于搜索栏的监听器
    OnKeyListener OnKey = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // TODO Auto-generated method stub
            if (keyCode == 66 || keyCode == 84) {
                System.out.println("on Search");
                String Url = search_et.getText().toString();
                configViewsAfterSearch(Url);
            }
            return false;
        }
    };

    // 按下搜索后对控件进行的配置
    void configViewsAfterSearch(String Url) {

        // 解析 URL 路径
        new Urlparse().execute(Url);
        // 更新webView
        System.out.println("更新webView");
        // webView.setVisibility(0);
        // LOGO 与背景灰色消失
        logo_iv.setVisibility(View.GONE);
        backcolor.setVisibility(View.GONE);
        // 清除搜索栏焦点
        search_et.clearFocus();
        // 设置顶点两个按钮可见
        more_bt.setVisibility(View.VISIBLE);
        newtab_bt.setVisibility(View.VISIBLE);

        @SuppressWarnings("deprecation")
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        rl.addRule(RelativeLayout.LEFT_OF, R.id.newtab);
        rl.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        search_et.setLayoutParams(rl);

        // 退出软键盘
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getApplicationContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);

        // 线条，进度条，小图标，网页设为可见
        line.setVisibility(View.VISIBLE);
        webView.setVisibility(View.VISIBLE);
        System.out.println("初始化完成");
    }

    @SuppressWarnings("deprecation")
    void configWebView() {

        // 使页面支持缩放
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDefaultZoom(ZoomDensity.FAR);
        webView.getSettings().setBuiltInZoomControls(true);
        // 使网页自适应大小
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        // 隐藏网页上的缩放按钮
        webView.getSettings().setDisplayZoomControls(false);

        // 保存表单数据和密码
        webView.getSettings().setSavePassword(true);
        webView.getSettings().setSaveFormData(true);

        // 调用系统下载
        webView.setDownloadListener(this);

        // 播放视频
        webView.getSettings().setPluginState(PluginState.ON_DEMAND);
        // 支持多窗口
        webView.getSettings().supportMultipleWindows();
        // webView.loadUrl("http://cn.bing.com/");
        webView.requestFocus();
        // 支持手势获取焦点
        webView.requestFocusFromTouch();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }

            // 处理https协议
            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {
                // TODO Auto-generated method stub
                handler.proceed();
            }

            @Override
            @Deprecated
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // TODO Auto-generated method stub
                switch (errorCode) {
                    case 404:
                        view.loadUrl("file:///android_assets/error_handle.html");
                        break;
                }
            }

        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                System.out.println("pro->" + newProgress);
                if (newProgress != 100) {
                    search_et.setText(webView.getUrl());
                } else {
                    search_et.setText(webView.getTitle());
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        try {
            mListener = (OnTabsClickListener) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Urlparse extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                URL url = new URL(params[0]);
                params[0] = url.toString();
                System.out.println("出口  1-》" + params[0]);
                return params[0];
            } catch (MalformedURLException e) {
                try {
                    URL surl = new URL("http://" + params[0]);
                    @SuppressWarnings("unused")
                    InputStream in = surl.openStream();
                    System.out.println("出口  2-》" + params[0]);
                    return surl.toString();
                } catch (IOException e1) {
                    String query = "http://cn.bing.com/search?q=";
                    query += params[0];
                    System.out.println("出口  3-》" + params[0]);
                    return query.toString();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            webView.loadUrl(result);
        }

    }

    @Override
    public void onDownloadStart(String url, String userAgent,
                                String contentDisposition, String mimetype, long contentLength) {
        // TODO Auto-generated method stub
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }
}
