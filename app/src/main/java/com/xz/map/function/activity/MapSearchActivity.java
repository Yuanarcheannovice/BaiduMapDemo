package com.xz.map.function.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.xcoder.lib.annotation.ContentView;
import com.xcoder.lib.annotation.ViewInject;
import com.xcoder.lib.annotation.event.OnClick;
import com.xcoder.lib.utils.Utils;
import com.xz.map.R;
import com.xz.map.app.BaseActivity;
import com.xz.map.function.adapter.MapSearchAdapter;
import com.xz.map.util.AppStaticVariable;
import com.xz.xadapter.XRvPureAdapter;

import java.util.Iterator;
import java.util.List;

/**
 * 聊天地图查询
 * Created by xz on 2016/10/18 0018.
 * @author xz
 */
@ContentView(R.layout.activity_map_search)
public class MapSearchActivity extends BaseActivity {

    @ViewInject(R.id.ams_rv)
    private RecyclerView mRecyclerView;

    @ViewInject(R.id.ams_et)
    private EditText mEditText;

    public SuggestionSearch mSuggestionSearch;

    private MapSearchAdapter mMapSearchAdapter;

    @Override
    public void onXCoderCreate(Bundle savedInstanceState) {
        initView();
        initSearch();
        InputMethodManager inputManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(mEditText, 0);
    }

    /**
     * 搜索
     */
    public void initSearch() {
        //关键词搜索
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                //未找到相关结果
                if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
                    return;
                }
                List<SuggestionResult.SuggestionInfo> ssList = suggestionResult.getAllSuggestions();

                //关键搜索时，数据有时候没有经纬度，和地址信息,需要剔除
                Iterator<SuggestionResult.SuggestionInfo> itParent = ssList.iterator();
                while (itParent.hasNext()) {
                    SuggestionResult.SuggestionInfo ss = itParent.next();
                    if (ss.pt == null || TextUtils.isEmpty(ss.district)) {
                        itParent.remove();
                    }
                }
                mMapSearchAdapter.setDatas(ssList, true);
            }
        });
    }

    /**
     * 对搜索框进行监听
     */
    public void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mMapSearchAdapter = new MapSearchAdapter();
        mMapSearchAdapter.setOnItemClickListener(new XRvPureAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                SuggestionResult.SuggestionInfo ss = mMapSearchAdapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra(AppStaticVariable.MAP_SEARCH_LONGITUDE, ss.pt.longitude);
                intent.putExtra(AppStaticVariable.MAP_SEARCH_LATITUDE, ss.pt.latitude);
                intent.putExtra(AppStaticVariable.MAP_SEARCH_ADDRESS, ss.city + ss.district + ss.key);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        mRecyclerView.setAdapter(mMapSearchAdapter);


        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 这里的s表示改变之前的内容，通常start和count组合，可以在s中读取本次改变字段中被改变的内容。而after表示改变后新的内容的数量。
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 这里的s表示改变之后的内容，通常start和count组合，可以在s中读取本次改变字段中新的内容。而before表示被改变的内容的数量。
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 表示最终内容
                String mapInput = mEditText.getText().toString().trim();
                if (!Utils.isEmpty(mapInput)) {
                    //搜索关键词
                    mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                            .keyword(mapInput).city("长沙")
                    );
                }
            }
        };
        mEditText.addTextChangedListener(tw);

        findViewById(R.id.ams_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @OnClick({R.id.ams_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ams_back:
                finish();
                break;
            default:
                break;
        }
    }


    @Override
    public void closeActivity() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放POI检索实例；
        if (mSuggestionSearch != null) {
            mSuggestionSearch.destroy();
        }
    }
}
