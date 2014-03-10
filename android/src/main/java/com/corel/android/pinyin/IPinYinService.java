package com.corel.android.pinyin;

public interface IPinYinService {
	/**
	 * load service for card
	 * 
	 * @param card
	 *            a card Number
	 */
	void load(int card);

	/**
	 * release service
	 */
	void release();

	/**
	 * save current service states
	 * 
	 * @return
	 */
	boolean save();

	/**
	 * check stats when card load
	 * 
	 * @param card
	 *            a card Number
	 * @return true if service info is ready
	 */
	boolean preLoadCheck(int card);

	/**
	 * card number for current audio service
	 * 
	 * @return
	 */
	int getCurrentCard();

}
