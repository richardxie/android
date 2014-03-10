package com.corel.android.pinyin;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AssetWordLoder implements IWordLoader{

	@Override
	public String getWords(Context context, int cardNum) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(context
					.getAssets().open("table.txt")));
			for (int i = 0; i < cardNum - 1; i++) {
				reader.readLine(); // word
			}
			return reader.readLine();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

}
