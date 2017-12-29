package com.deyuz.viewer;

import android.app.Activity;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;

import com.deyuz.viewer.Fragment_add.OnAddClickListener;
import com.deyuz.viewer.Fragment_mark.OnMarksClickListener;
import com.deyuz.viewer.Fragment_set.OnSetClickListener;
import com.deyuz.viewer.Fragment_tab.OnTabsClickListener;

public class MainActivity extends Activity implements OnMarksClickListener,
        OnTabsClickListener, OnAddClickListener, OnSetClickListener, Fragment_history.OnHistoryClickListener, SharedInfo {

    private SQLiteDatabase db = null;
    private Fragment_mark marksfragment;
    private Fragment_tab[] tabsfragment = new Fragment_tab[12];
    private Fragment_add addFragment;
    private Fragment_set setfragmet;
    private Fragment_history historyfragment;
    private int fragmentSum = 0, currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tab);

        initViews();
    }

    private void initViews() {
        tabsfragment[0] = new Fragment_tab();
        getFragmentManager().beginTransaction()
                .add(R.id.activity_main, tabsfragment[0]).commit();
    }

    private String getBookmarksURL(String mname) {
        System.out.println(mname);
        db = openOrCreateDatabase("bookmark", MODE_PRIVATE, null);
        String createCmd = "create table if not exists marks(markname varchar(100),markaddr varchar(100));";
        db.execSQL(createCmd);
        Cursor cursor = db.query("marks", null, "markname=?",
                new String[]{mname}, null, null, null, null);
        cursor.moveToFirst();
        String markaddr = cursor.getString(1);
        db.close();
        return markaddr;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        tabsfragment[currentTab].webView.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        tabsfragment[currentTab].webView.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        System.out.println("exit");
        return super.onKeyDown(keyCode, event);
    }

    @SuppressWarnings("deprecation")
    private Bitmap getShot() {
        View view = this.getWindow().getDecorView();
        view.buildDrawingCache();
        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = this.getWindowManager().getDefaultDisplay();
        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();
        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        // 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);
        // 销毁缓存信息
        view.destroyDrawingCache();

        return bmp;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横向
            tabsfragment[currentTab].search_et.setVisibility(View.GONE);
            tabsfragment[currentTab].newtab_bt.setVisibility(View.GONE);
            tabsfragment[currentTab].more_bt.setVisibility(View.GONE);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 纵向
            tabsfragment[currentTab].search_et.setVisibility(View.VISIBLE);
            tabsfragment[currentTab].newtab_bt.setVisibility(View.VISIBLE);
            tabsfragment[currentTab].more_bt.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void OnMarksClick(String params) {
        // TODO Auto-generated method stub
        params = getBookmarksURL(params);
        // Toast.makeText(MainActivity.this, params, 3000).show();
        getFragmentManager().popBackStack();

        System.out.println(params);
    }

    @Override
    public void OnAddClick(int index) {
        // TODO Auto-generated method stub
        switch (index) {
            case -2:// 当用户长点击某标签页时
                fragmentSum--;
                addFragment.sum = fragmentSum;
                currentTab = 0;
                break;
            case -1:// 点击添加标签页时
                fragmentSum++;
                addFragment.sum = fragmentSum;
                currentTab = fragmentSum;
                tabsfragment[fragmentSum] = new Fragment_tab();
                tabsfragment[fragmentSum].id = fragmentSum;
                getFragmentManager().beginTransaction()
                        .add(R.id.activity_main, tabsfragment[fragmentSum])
                        .commit();
                break;
            default:// 点击已经产生的页面时
                System.out.println("当前进入页面->" + index);
                System.out.println("总页面数->" + fragmentSum);
                currentTab = index;
                for (int i = 0; i < fragmentSum; i++) {
                    if (tabsfragment[i] != null) {
                        getFragmentManager().beginTransaction()
                                .hide(tabsfragment[i]).commit();
                    }
                }

                getFragmentManager().beginTransaction().hide(addFragment).commit();

                if (tabsfragment[index] == null) {
                    tabsfragment[index] = new Fragment_tab();
                    getFragmentManager().beginTransaction()
                            .add(R.id.activity_main, tabsfragment[index]).commit();
                }

                getFragmentManager().beginTransaction().show(tabsfragment[index])
                        .commit();
                break;
        }
    }

    @Override
    public void OnTabClick(int code) {
        // TODO Auto-generated method stub
        switch (code) {
            case 0:// newtab 点击右上角的添加按钮时，进入AddFragment
                System.out.println("getShot");
                if (addFragment == null) {
                    // 在addFragment为空时，创建实例，并显示
                    addFragment = new Fragment_add();
                    // 为新标签页指定快照
                    addFragment.bmp = getShot();
                    getFragmentManager().beginTransaction()
                            .add(R.id.activity_main, addFragment)
                            .addToBackStack("add").commit();
                } else {
                    // 为新标签页指定快照
                    addFragment.bmp = getShot();
                    // 隐藏所有标签页
                    for (int i = 0; i < fragmentSum + 1; i++) {
                        getFragmentManager().beginTransaction()
                                .hide(tabsfragment[i]).commit();
                    }
                    // 出现标签页总数
                    addFragment.sum = fragmentSum;
                    addFragment.newtab_bt.setBackgroundResource(tabId[fragmentSum]);

                    System.out.println("对 " + currentTab + " 设置新图片");
                    addFragment.gallery.removeViewAt(currentTab);
                    addFragment.gallery.addView(addFragment
                            .insertImage(addFragment.bmp));

                    getFragmentManager().beginTransaction().show(addFragment)
                            .addToBackStack("add").commit();
                }
                break;
            case 1:// marked 已添加的书签，位于左下角
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.animator.marked_enter,
                                R.animator.marked_exit, R.animator.marked_enter,
                                R.animator.marked_exit)
                        .add(R.id.activity_main,
                                marksfragment = new Fragment_mark())
                        .addToBackStack("mark").commit();
                break;
            case 2:// history 历史记录，位于右下角
                tabsfragment[currentTab].search_et.requestFocus();
                getFragmentManager()
                        .beginTransaction().setCustomAnimations(R.animator.history_enter,
                        R.animator.history_exit, R.animator.history_enter,
                        R.animator.history_exit)
                        .add(R.id.activity_main, historyfragment = new Fragment_history())
                        .addToBackStack("history").commit();
                break;
            case 3:// more 位于右上角
                // 实例化设置对象
                if (setfragmet == null) {
                    setfragmet = new Fragment_set();
                }
                // 显示Fragment对象
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.animator.set_enter,
                                R.animator.set_exit, R.animator.set_enter,
                                R.animator.set_exit)
                        .add(R.id.activity_main, setfragmet).addToBackStack("set")
                        .commit();

                // new JokeParse().execute();
                // System.out.println("jokes");
                break;
        }
    }

    @Override
    public String[] OnSetClick(int code) {
        // TODO Auto-generated method stub
        switch (code) {
            case 0:
                String params[] = new String[2];
                params[0] = tabsfragment[currentTab].webView.getTitle();
                params[1] = tabsfragment[currentTab].webView.getUrl();

                return params;
            case 1:
                getFragmentManager().popBackStack();
                break;
            case 2:// marked 已添加的书签，位于左下角
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.animator.marked_enter,
                                R.animator.marked_exit, R.animator.marked_enter,
                                R.animator.marked_exit)
                        .add(R.id.activity_main,
                                marksfragment = new Fragment_mark())
                        .addToBackStack("mark").commit();
                break;
            case 3:
                tabsfragment[currentTab].search_et.requestFocus();
                getFragmentManager()
                        .beginTransaction().setCustomAnimations(R.animator.history_enter,
                        R.animator.history_exit, R.animator.history_enter,
                        R.animator.history_exit)
                        .add(R.id.activity_main, historyfragment = new Fragment_history())
                        .addToBackStack("history").commit();
                break;
            default:
                System.out.println("点击其它");

                break;
        }
        return null;
    }

    @Override
    public void OnHistoryClick() {

    }
}
