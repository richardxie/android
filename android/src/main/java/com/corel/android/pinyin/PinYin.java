/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.corel.android.pinyin;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Convenience definitions for NotePadProvider
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
public final class PinYin implements Parcelable{
    public static final String AUTHORITY = "com.corel.provider.PinYin";
    public static final String AUDIO_PR = ".wav";
    public static final String PINYIN_TABLE_NAME = "pinyin";

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(chinese);
		dest.writeString(english);
		dest.writeString(pinyin);
	}
	
	private String chinese;
	private String english;
	private String pinyin;
    
	public static final Parcelable.Creator<PinYin> CREATOR = new Parcelable.Creator<PinYin>() {
		public PinYin createFromParcel(Parcel in) {
			return new PinYin(in);
		}

		public PinYin[] newArray(int size) {
			return new PinYin[size];
		}
	};

	private PinYin(Parcel in) {
		chinese = in.readString();
		english = in.readString();
		pinyin = in.readString();
	}

    /**
     * Notes table
     */
    public static final class PinYinColumns implements BaseColumns {
        // This class cannot be instantiated
        private PinYinColumns() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/py");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.corel.py";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.corel.py";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        public static final String CARD = "card";
        /**
         * The Chinese
         * <P>Type: TEXT</P>
         */
        public static final String CHINESE = "chinese";
        
        /**
         * The Chinese
         * <P>Type: TEXT</P>
         */
        public static final String ENGLISH = "english";

        /**
         * The note itself
         * <P>Type: TEXT</P>
         */
        public static final String PINYIN = "pinyin";

    }
}
