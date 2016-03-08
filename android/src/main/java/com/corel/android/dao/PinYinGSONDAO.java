package com.corel.android.dao;

import android.content.Context;
import android.util.Log;

import com.corel.android.pinyin.PinYin;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PinYinGSONDAO implements IPinYinDAO {

	private static final String TAG = "PinYinGSONDAO";

	public PinYinGSONDAO() {
	}

	@Inject
	public PinYinGSONDAO(Context context) {
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
		try {
			FileInputStream in = mContext.openFileInput(JSON_FILE);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			StringBuilder content = new StringBuilder();
			String readContent = null;
			while ((readContent = reader.readLine()) != null) {
				content.append(readContent);
			}
			JSONArray jsonArray = new JSONArray(content.toString());
			for (int i = 0; i < jsonArray.length(); i++) {
				 JSONObject jsonObject = (JSONObject)jsonArray.opt(i);
				 System.out.println(jsonObject.getString("card"));

			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public List<PinYin> getAll(int cardId) {
		Log.i(TAG, "get all pinyin in Json DAO for card:" + cardId);
		return readJsonStream(cardId);
	}

	@Override
	public void add(int card, PinYin py) {
		Log.i(TAG, "add pinyin in Json DAO! " + py);
	//	Gson gson = new Gson();
	//	String s = gson.toJson(py);

	}

	@Override
	public void addAll(int card, Set<PinYin> py) {
		Log.i(TAG, "add all pinyin in Json DAO!");
	//	this.mCurrentCard = card;
		try {
			FileInputStream in = mContext.openFileInput(JSON_FILE);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder content = new StringBuilder();
			String readContent = null;
			while ((readContent = reader.readLine()) != null) {
				content.append(readContent);
			}
			JSONArray jsonArray = new JSONArray(content.toString());
			for (int i = 0; i < jsonArray.length(); i++) {
				 JSONObject jsonObject = (JSONObject)jsonArray.opt(i);
				 System.out.println(jsonObject.getString("card"));

			}

		} catch (FileNotFoundException e) {
			//writeJsonStream(card, py);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//writeJsonStream(card, py);

	}

	private List<PinYin> readJsonStream(int card) {
		JsonReader reader = null;
		try {
			FileInputStream in = mContext.openFileInput(JSON_FILE);
			reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
			return readJsonArray(reader, card);
		} catch (FileNotFoundException e) {
			;
		} catch (UnsupportedEncodingException e) {
			;
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
			}
		}
		return null;
	}

	private List<PinYin> readJsonArray(JsonReader reader, int card) {
		List<PinYin> py = null;
		try {
			reader.beginArray();
			while (reader.hasNext()) {
				reader.beginObject();
				while (reader.hasNext()) {
					String cardName = reader.nextName();
					int cardID = 1;
					if (cardName.equals(PinYin.PinYinColumns.CARD)) {
						cardID = reader.nextInt();
					}
					String pinyin = reader.nextName();
					if (cardID == card && pinyin.equals("PinYin")) {
						py = readJson(reader);
					}
				}
				reader.endObject();
			}
			reader.endArray();
		} catch (IOException e) {

		}
		// TODO Auto-generated method stub
		return py;
	}

	private List<PinYin> readJson(JsonReader reader) {
		List<PinYin> py = new ArrayList<PinYin>();
		String chinese = null;
		String english = null;
		String pinyin = null;
		try {
			reader.beginArray();
			while (reader.hasNext()) {
				reader.beginObject();
				while (reader.hasNext()) {
					String name = reader.nextName();
					if (name.equals(PinYin.PinYinColumns.CHINESE))
						chinese = reader.nextString();
					else if (name.equals(PinYin.PinYinColumns.ENGLISH))
						english = reader.nextString();
					else if (name.equals(PinYin.PinYinColumns.PINYIN))
						pinyin = reader.nextString();
					else
						assert (false);
				}
				py.add(new PinYin(chinese, english, pinyin));
				reader.endObject();
			}
			reader.endArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return py;
	}

	private void writeJsonStream(int card, final List<PinYin> py) {
		JsonWriter writer = null;
		try {
			FileOutputStream out = mContext.openFileOutput(JSON_FILE,
					Context.MODE_PRIVATE | Context.MODE_APPEND);
			writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
			writeJsonArray(writer, py);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}

	}

	private void writeJsonArray(final JsonWriter writer, final List<PinYin> py) {
		try {
			writer.beginArray();
			writer.beginObject();
			writer.name("card").value(1);
			writer.name("PinYin");
			writer.beginArray();
			for (PinYin p : py) {
				writeJson(writer, p);
			}
			writer.endArray();
			writer.endObject();
			writer.endArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void writeJson(final JsonWriter writer, final PinYin p) {
		try {
			writer.beginObject();
			writer.name(PinYin.PinYinColumns.CHINESE).value(p.getChinese());
			writer.name(PinYin.PinYinColumns.ENGLISH).value(p.getEnglish());
			writer.name(PinYin.PinYinColumns.PINYIN).value(p.getPinyin());
			writer.endObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Inject Context mContext;
	
	//private int mCurrentCard;

	private String JSON_FILE = "PINYIN_JSON";
}
