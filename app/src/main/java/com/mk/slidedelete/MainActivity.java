package com.mk.slidedelete;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.lv_contact);

        ListAdapter adapter = new MyAdapter();
        listView.setAdapter(adapter);



        /*slideFrameLayout.setOnStateChangedListener(new SlideFrameLayout.OnStateChangedListener() {
            @Override
            public void onOpen() {
                ToastUtil.showToast(MainActivity.this,"打开");
            }

            @Override
            public void onClose() {
                ToastUtil.showToast(MainActivity.this,"关闭");
            }

            @Override
            public void onStartOpen() {
                ToastUtil.showToast(MainActivity.this,"正在打开");
            }

            @Override
            public void onStartClose() {
                ToastUtil.showToast(MainActivity.this,"正在关闭");
            }
        });*/
    }

    private class MyAdapter extends BaseAdapter {
        //private SlideFrameLayout slideFrameLayout;
private ArrayList<SlideFrameLayout> list;
        public MyAdapter() {
            //slideFrameLayout = null;
            list = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return Cheeses.NAMES.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = View.inflate(getApplicationContext(), R.layout.item, null);
            }
            SlideFrameLayout sfl = (SlideFrameLayout) view;
            sfl.setOnStateChangedListener(new SlideFrameLayout.OnStateChangedListener() {
                @Override
                public void onOpen(SlideFrameLayout sfl) {
                    //slideFrameLayout = sfl;
                    list.add(sfl);
                }

                @Override
                public void onClose(SlideFrameLayout sfl) {
                    //slideFrameLayout = null;
                    list.remove(sfl);
                }

                @Override
                public void onStartOpen(SlideFrameLayout sfl) {
                    //slideFrameLayout.slowClose();
                    //slideFrameLayout = null;
                    for (SlideFrameLayout s : list) {
                        s.slowClose();
                    }
                    list.clear();
                }

                @Override
                public void onStartClose(SlideFrameLayout sfl) {

                }
            });
            TextView tv = (TextView) view.findViewById(R.id.tv_name);
            tv.setText(Cheeses.NAMES[i]);
            return view;
        }
    }
}
