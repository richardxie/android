package com.corel.android.dao;

import com.corel.android.pinyin.PinYin;

import java.util.List;
import java.util.Set;

public interface IPinYinDAO {
	void open();
	void close();
	/**
	 * get Pinyin according to id
	 * @param id 
	 * @return a pinyin struction
	 */
	PinYin get(int id);
	
	/**
	 * get Pinyin according to chinese
	 * @param chinese
	 * @return a pinyin
	 */
	PinYin get(String chinese);
	
	/**
	 * get all pinyin in a card
	 * @param cardId
	 * @return
	 */
	List<PinYin> getAll(int cardId);
	
	/**
	 * add a pinyin to a card
	 * @param py
	 */
	void add(int card, PinYin py);
	
	/**
	 * add all pinyin to a card
	 * @param py
	 */
	void addAll(int card, Set<PinYin> py);
}