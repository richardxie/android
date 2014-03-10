package com.corel.android.dao;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.corel.android.pinyin.PinYin;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Singleton
public class PinYinJSONDAO implements IPinYinDAO {

	private static final String TAG = "PinYinJsonDAO";

	public PinYinJSONDAO() {
	}

	@Inject
	public PinYinJSONDAO(Context context) {
		this.mContext = context;
	}

	@Override
	public void open() {

	}

	@Override
	public void close() {

	}

	@Override
	public PinYin get(int id) {
		Log.i(TAG, "get pinyin in Json DAO for id:" + id);
		return null;
	}

	@Override
	public PinYin get(String chinese) {
		Log.i(TAG, "get pinyin in Json DAO for word:" + chinese);
		InputStream input = null;
		PinYin pinyin = null;
		getJSONArray();
		try {
			input = getInputStream();//mContext.openFileInput(JSON_FILE);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(input));
			StringBuilder content = new StringBuilder();
			String readContent = null;
			while ((readContent = reader.readLine()) != null) {
				content.append(readContent);
			}
			JSONArray jsonArray = new JSONArray(content.toString());
			for (int i = 0; i < jsonArray.length(); i++) {
				 JSONObject jsonObject = (JSONObject)jsonArray.opt(i);
				 System.out.println(jsonObject.getString(PinYin.PinYinColumns.CARD));
				 JSONArray pinyinArray = jsonObject.getJSONArray(PinYin.PINYIN_TABLE_NAME);
				 for (int j = 0; j < pinyinArray.length(); j++) {
					 JSONObject pinyinObj = pinyinArray.getJSONObject(j);
					 String cName = pinyinObj.getString(PinYin.PinYinColumns.CHINESE);
					 if(cName.equals(chinese)) {
						 String eName =  pinyinObj.getString(PinYin.PinYinColumns.ENGLISH);
						 String p = pinyinObj.getString(PinYin.PinYinColumns.PINYIN);
						 pinyin = new PinYin(cName, eName, p);
					 }
				 }
				 

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}finally {
			IOUtils.closeQuietly(input);
		}

		return pinyin;
	}

	@Override
	public List<PinYin> getAll(int cardId) {
		Log.i(TAG, "get all pinyin in Json DAO for card:" + cardId);
		JSONObject obj = getJSONObject(cardId);
		if(obj != null) {
			List<PinYin> py = new ArrayList<PinYin>();
			try {
				JSONArray pinyinArr = obj.getJSONArray(PinYin.PINYIN_TABLE_NAME);
				for(int i = 0; i < pinyinArr.length(); i++) {
					JSONObject pinyinObj = pinyinArr.getJSONObject(i);
					py.add(fromJson(pinyinObj));
				}
			}catch(JSONException e) {
				
			}
			return py;
		}else {
			return null;
		}
	}

	@Override
	public void add(int card, PinYin py) {
		Log.i(TAG, "add pinyin in Json DAO! " + py);
		JSONArray jsonArray = getJSONArray();
		JSONObject obj = getJSONObject(jsonArray, card);
		try {
			JSONArray pinyinArr = obj.getJSONArray(PinYin.PINYIN_TABLE_NAME);
			JSONObject pinyinObj = toJson(py);
			pinyinArr.put(pinyinObj);
			writeJSONFile(jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addAll(int card, Set<PinYin> py) {
		Log.i(TAG, "add all pinyin in Json DAO!");
		//this.mCurrentCard = card;
		
		JSONArray jsonArray = getJSONArray();
		if(jsonArray == null) {
			writeAll(card, py);
		}
		else {
			JSONObject jsonObject = getJSONObject(jsonArray, card);
			if(jsonObject == null) {
				try{
					jsonObject = new JSONObject();
					jsonObject.put(PinYin.PinYinColumns.CARD, card);
					JSONArray arr = new JSONArray();
					writeToJson(arr, py);
					jsonObject.put(PinYin.PINYIN_TABLE_NAME, arr);
				}catch(JSONException e) {
					
				}
			}
			else {
				try{
					JSONArray myArray = jsonObject.getJSONArray(PinYin.PINYIN_TABLE_NAME);
					writeToJson(myArray, py);
				}catch(JSONException e) {
					Log.e(TAG, "write JSON error");
				}
			}
			jsonArray.put(jsonObject);
			writeJSONFile(jsonArray);
		}
	}
	
	private InputStream getInputStream() throws FileNotFoundException {
		InputStream input = null;
//		input = mContext.openFileInput(JSON_FILE);
		File f = new File(Environment.getExternalStorageDirectory(), "PinYin/" + JSON_FILE);
		input = new FileInputStream(f);
		return input;
	}
	
	private OutputStream openOutputStream () throws FileNotFoundException {
		OutputStream output = null;
		//output = mContext.openFileOutput(JSON_FILE, Context.MODE_PRIVATE);
		File f = new File(Environment.getExternalStorageDirectory(), "PinYin/" + JSON_FILE);
		output = new FileOutputStream(f);
		return output;
	}
	
	
	private static void writeToJson(final JSONArray array, final Set<PinYin> py) throws JSONException {
		for(PinYin p: py) {
			array.put(toJson(p));
		}
	}
	
	private static JSONObject toJson(PinYin pinyin) {
		JSONObject obj = new JSONObject();
		try {
			obj.put(PinYin.PinYinColumns.CHINESE, pinyin.getChinese());
			obj.put(PinYin.PinYinColumns.ENGLISH, pinyin.getEnglish());
			obj.put(PinYin.PinYinColumns.PINYIN, pinyin.getPinyin());
		}catch(JSONException e) {
			;
		}
		return obj;
	}
	private static PinYin fromJson(JSONObject obj) {
		PinYin py = null;
		try {
			String chinese = obj.getString(PinYin.PinYinColumns.CHINESE);
			String english = obj.getString(PinYin.PinYinColumns.ENGLISH);
			String pinyin = obj.getString(PinYin.PinYinColumns.PINYIN);
			py = new PinYin(chinese, english, pinyin);
		}catch(JSONException e) {
			;
		}
		return py;
	}
	
	private JSONArray getJSONArray() {
		StringBuilder content = null;
		InputStream input = null;
		JSONArray jsonArray = null;
		try {
			input = getInputStream();//mContext.openFileInput(JSON_FILE);
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String readContent = null;
			content = new StringBuilder();
			while ((readContent = reader.readLine()) != null) {
				content.append(readContent);
			}
			jsonArray = new JSONArray(content.toString());
		}catch(IOException e) {
			Log.i(TAG, "get PINYIN IO error");
		}catch (JSONException e) {
			Log.i(TAG, "get PINYIN JSON error");
		}finally {
			IOUtils.closeQuietly(input);
		}
		return jsonArray;
	}
	
	private JSONObject getJSONObject(JSONArray jsonArray, int card) {
		JSONObject jsonObj = null;
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject pinyinObj = jsonArray.getJSONObject(i);
				 if(card == pinyinObj.getInt(PinYin.PinYinColumns.CARD)) {
					 jsonObj = pinyinObj;
					 break;
				 }
				
			}
		}catch(JSONException e) {
			Log.i(TAG, "get JSON Object error");
		}
		return jsonObj;
	}
	
	private JSONObject getJSONObject(int card) {
		JSONObject obj = null;
		JSONArray jsonArray = getJSONArray();
		if(jsonArray != null)
			obj = getJSONObject(jsonArray, card);
		
		return obj;
	}

	public void writeAll(int card, Set<PinYin> py) {
		OutputStream output = null;
		InputStream input = null;
		try {
			output = openOutputStream(); // mContext.openFileOutput(JSON_FILE, Context.MODE_PRIVATE);
			JSONArray array = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put(PinYin.PinYinColumns.CARD, card);
			JSONArray arr = new JSONArray();
			for(PinYin p: py) {
				JSONObject pinyinObj = new JSONObject();
				pinyinObj.put(PinYin.PinYinColumns.CHINESE, p.getChinese());
				pinyinObj.put(PinYin.PinYinColumns.ENGLISH, p.getEnglish());
				pinyinObj.put(PinYin.PinYinColumns.PINYIN, p.getPinyin());
				arr.put(pinyinObj);
			}
			obj.put(PinYin.PINYIN_TABLE_NAME, arr);
			array.put(obj);
			input = new ByteArrayInputStream(array.toString().getBytes("UTF-8"));
			IOUtils.copy(input, output);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(input);
		}
	}
	
	private void writeJSONFile(final JSONArray array) {
		InputStream input = null;
		OutputStream output = null;
		try{
			input = new ByteArrayInputStream(array.toString().getBytes("UTF-8"));
			output = openOutputStream();//mContext.openFileOutput(JSON_FILE, Context.MODE_PRIVATE);
			IOUtils.copy(input, output);
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(input);
		}
	}


	@Inject
	private Context mContext;
	
	//Cache for performance
	//TODO
	//private int mCurrentCard;
	
	//private JSONArray mCurrentPinYin;

	@Inject @Named("JSON_FILE")
	private String JSON_FILE;
}
