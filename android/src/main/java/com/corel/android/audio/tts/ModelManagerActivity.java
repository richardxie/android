/**
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.corel.android.audio.tts;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.model.BasicHandler;
import com.baidu.tts.client.model.Conditions;
import com.baidu.tts.client.model.DownloadHandler;
import com.baidu.tts.client.model.LibEngineParams;
import com.baidu.tts.client.model.ModelBags;
import com.baidu.tts.client.model.ModelFileBags;
import com.baidu.tts.client.model.ModelInfo;
import com.baidu.tts.client.model.ModelManager;
import com.baidu.tts.client.model.OnDownloadListener;
import com.corel.android.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * @author liweigao 2015年10月26日
 */
public class ModelManagerActivity extends Activity implements OnClickListener {
    @Bind(R.id.getServerList) Button mGetServerList;
    @Bind(R.id.getLocalList) Button mGetLocalList;
    @Bind(R.id.getServerListAvailable) Button mGetServerListAvailable;
    @Bind(R.id.getLocalListAvailable) Button mGetLocalListAvailable;
    @Bind(R.id.getServerFileInfo) Button mGetServerFileInfo;
    @Bind(R.id.getLocalFileInfo) Button mGetLocalFileInfo;
    @Bind(R.id.getModelDefault) Button mGetModelDefault;
    @Bind(R.id.getModelFilePath) Button mGetModelFilePath;
    @Bind(R.id.downloadList) Button mDownloadList;
    @Bind(R.id.stop) Button mStop;
    @Bind(R.id.useModel) Button mUseModel;
    @Bind(R.id.speak) Button mSpeak;
    @Bind(R.id.input) EditText mInput;
    @Bind(R.id.listview) ListView mListView;

    private ModelManager mModelManager;
    private SpeechSynthesizer mSpeechSynthesizer;
    private ConcurrentHashMap<String, DownloadHandler> mDownloadHandlers =
            new ConcurrentHashMap<String, DownloadHandler>();

    private static final int PRINT = 0;
    private static final int UPDATE_PROGRESS = 1;
    private static final int UPDATE_STATE = 2;
    private static final String TAG = "ModelManagerActivity";

    public static final String KEY_MODEL_ID = "modelId";
    public static final String KEY_DOWNLOAD_BYTES = "downloadBytes";
    public static final String KEY_TOTAL_BYTES = "totalBytes";
    public static final String KEY_STATE = "state";

    /*
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modelmanager);
        initialView();

        initialModelManager();
    }

    private void initialView() {
        ButterKnife.bind(this);
    }

    private void initialModelManager() {
        this.mModelManager = new ModelManager(this);
        this.mSpeechSynthesizer = SpeechSynthesizer.getInstance();
    }

    /*
     * @param v
     */
    @Override
    @OnClick({R.id.getServerList, R.id.getLocalList, R.id.getServerListAvailable, R.id.getLocalListAvailable, R.id.getServerFileInfo,
            R.id.getLocalFileInfo, R.id.getModelDefault, R.id.getModelFilePath, R.id.downloadList, R.id.stop, R.id.useModel, R.id.speak})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.getServerList:
                getServerList();
                break;
            case R.id.getLocalList:
                getLocalList();
                break;
            case R.id.getServerListAvailable:
                getServerListAvailable();
                break;
            case R.id.getLocalListAvailable:
                getLocalListAvailable();
                break;
            case R.id.getServerFileInfo:
                getServerFileInfo();
                break;
            case R.id.getLocalFileInfo:
                getLocalFileInfos();
                break;
            case R.id.getModelDefault:
                getModelDefault();
                break;
            case R.id.getModelFilePath:
                getModelFilePath();
                break;
            case R.id.downloadList:
                downloadList();
                break;
            case R.id.stop:
                stop();
                break;
            case R.id.useModel:
                useModel();
                break;
            case R.id.speak:
                speak();
                break;

            default:
                break;
        }
    }

    private void speak() {
        String text = mInput.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            int result = mSpeechSynthesizer.speak(text);
            toPrint("speak result=" + result);
        }
    }

    private void useModel() {
        // AvailableConditions conditions = new AvailableConditions();
        // conditions.appendGender("male");
        BasicHandler<ModelBags> handler = mModelManager.getLocalModelsAvailable(null);
        ModelBags bags = handler.get();
        if (bags != null) {
            if (!bags.isEmpty()) {
                List<ModelInfo> infos = bags.getModelInfos();
                ModelInfo mi = infos.get(0);
                String modelId = mi.getServerId();
                if (mModelManager.isModelValid(modelId)) {
                    String textFilePath = mModelManager.getTextModelFileAbsPath(modelId);
                    String speechFilePath = mModelManager.getSpeechModelFileAbsPath(modelId);
                    int result = mSpeechSynthesizer.loadModel(speechFilePath, textFilePath);
                    toPrint("loadModel result=" + result);
                }
            }
        }
    }

    private void stop() {
        mModelManager.stop();
    }

    private void downloadList() {
        BasicHandler<ModelBags> handler = mModelManager.getServerModelsAvailable(null);
        ModelBags bags = handler.get();
        if (bags != null) {
            String str = bags.toJson().toString();
            toPrint(str);
            List<ModelInfo> list = bags.getModelInfos();
            DownloadListAdapter adapter = new DownloadListAdapter(list, this);
            mListView.setAdapter(adapter);
        }
    }

    private void getModelFilePath() {
        String modelId = "4";
        String textFilePath = mModelManager.getTextModelFileAbsPath(modelId);
        String speechFilePath = mModelManager.getSpeechModelFileAbsPath(modelId);
        if (textFilePath != null) {
            toPrint("text=" + textFilePath);
        }
        if (speechFilePath != null) {
            toPrint("speech=" + speechFilePath);
        }
        LibEngineParams engineParams = mModelManager.getEngineParams();
        toPrint(engineParams.getResult());
    }

    private void getModelDefault() {
        BasicHandler<ModelBags> handler = mModelManager.getServerDefaultModels();
        ModelBags bags = handler.get();
        if (bags != null) {
            String str = bags.toJson().toString();
            toPrint(str);
        }
    }

    private void getLocalFileInfos() {
        Set<String> set = new HashSet<String>();
        set.add("4");
        set.add("5");
        BasicHandler<ModelFileBags> handler = mModelManager.getLocalModelFileInfos(set);
        ModelFileBags bags = handler.get();
        if (bags != null) {
            String str = bags.toJson().toString();
            toPrint(str);
        }
    }

    private void getServerFileInfo() {
        Set<String> set = new HashSet<String>();
        set.add("4");
        set.add("5");
        BasicHandler<ModelFileBags> handler = mModelManager.getServerModelFileInfos(set);
        ModelFileBags bags = handler.get();
        if (bags != null) {
            String str = bags.toJson().toString();
            toPrint(str);
        }
    }

    private void getLocalListAvailable() {
        // AvailableConditions conditions = new AvailableConditions();
        // conditions.appendGender("female");
        BasicHandler<ModelBags> handler = mModelManager.getLocalModelsAvailable(null);
        ModelBags bags = handler.get();
        if (bags != null) {
            String str = bags.toJson().toString();
            toPrint(str);
        }
    }

    private void getServerListAvailable() {
        // AvailableConditions conditions = new AvailableConditions();
        // conditions.appendGender("female");
        BasicHandler<ModelBags> handler = mModelManager.getServerModelsAvailable(null);
        ModelBags bags = handler.get();
        if (bags != null) {
            String str = bags.toJson().toString();
            toPrint(str);
        }
    }

    private void getLocalList() {
        Conditions conditions = new Conditions();
        conditions.appendGender("male");
        BasicHandler<ModelBags> handler = mModelManager.getLocalModels(conditions);
        ModelBags bags = handler.get();
        if (bags != null) {
            String str = bags.toJson().toString();
            toPrint(str);
        }
    }

    private void getServerList() {
        Conditions conditions = new Conditions();
        conditions.appendGender("female");
        BasicHandler<ModelBags> handler = mModelManager.getServerModels(conditions);
        ModelBags bags = handler.get();
        if (bags != null) {
            toPrint(bags.toJson().toString());
        }
    }

    @OnItemClick(R.id.listview)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DownloadListAdapter adapter = (DownloadListAdapter) parent.getAdapter();
            String modelId = adapter.getModelId(position);
            DownloadHandler downloadHandler = mModelManager.download(modelId, new OnDownloadListener() {

                @Override
                public void onStart(String modelId) {
                    toPrint("onStart--modelId=" + modelId);
                    sendStateMsg(modelId, "开始");
                }

                @Override
                public void onProgress(String modelId, long downloadBytes, long totalBytes) {
                    // toPrint("onProgress--modelId=" + modelId + "--db=" + downloadBytes + "--tb=" + totalBytes);
                    sendProgressMsg(modelId, downloadBytes, totalBytes);
                }

                @Override
                public void onFinish(String modelId, int code) {
                    toPrint("onFinish--modelId=" + modelId + "--code=" + code);
                    String state = null;
                    if (code == 0) {
                        state = "下载完成";
                    } else if (code == -1005) {
                        state = "已下载";
                    } else {
                        state = "下载失败";
                    }
                    sendStateMsg(modelId, state);
                }
            });
            mDownloadHandlers.put(modelId, downloadHandler);
    }

    private Handler mHandler = new Handler() {

        /*
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case PRINT:
                    print(msg);
                    break;
                case UPDATE_PROGRESS:
                    updateProgress(msg);
                    break;
                case UPDATE_STATE:
                    updateState(msg);
                    break;

                default:
                    break;
            }
        }

    };

    private void sendStateMsg(String modelId, String state) {
        Message message = Message.obtain();
        message.what = UPDATE_STATE;
        Bundle bundle = new Bundle();
        bundle.putString(KEY_MODEL_ID, modelId);
        bundle.putString(KEY_STATE, state);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    private void sendProgressMsg(String modelId, long downloadBytes, long totalBytes) {
        Message message = Message.obtain();
        message.what = UPDATE_PROGRESS;
        Bundle bundle = new Bundle();
        bundle.putString(KEY_MODEL_ID, modelId);
        bundle.putLong(KEY_DOWNLOAD_BYTES, downloadBytes);
        bundle.putLong(KEY_TOTAL_BYTES, totalBytes);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    private void updateState(Message msg) {
        DownloadListAdapter adapter = (DownloadListAdapter) mListView.getAdapter();
        Bundle bundle = msg.getData();
        String modelId = bundle.getString(KEY_MODEL_ID);
        String state = bundle.getString(KEY_STATE);
        adapter.updateState(modelId, state);
        adapter.notifyDataSetChanged();
    }

    private void updateProgress(Message msg) {
        DownloadListAdapter adapter = (DownloadListAdapter) mListView.getAdapter();
        Bundle bundle = msg.getData();
        String modelId = bundle.getString(KEY_MODEL_ID);
        long downloadBytes = bundle.getLong(KEY_DOWNLOAD_BYTES);
        long totalBytes = bundle.getLong(KEY_TOTAL_BYTES);
        adapter.updateProgress(modelId, downloadBytes, totalBytes);
        adapter.notifyDataSetChanged();
    }

    public void onModelStop(String modelId) {
        DownloadHandler handler = mDownloadHandlers.get(modelId);
        if (handler != null) {
            handler.stop();
        }
        DownloadListAdapter adapter = (DownloadListAdapter) mListView.getAdapter();
        String state = "停止";
        adapter.updateState(modelId, state);
        adapter.notifyDataSetChanged();
    }

    private void toPrint(String str) {
        Message msg = Message.obtain();
        msg.obj = str;
        this.mHandler.sendMessage(msg);
    }

    private void print(Message msg) {
        String message = (String) msg.obj;
        if (message != null) {
            Log.e(TAG, message);
        }
    }

}
