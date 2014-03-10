package com.corel.android.test;

import android.test.AndroidTestCase;

import com.corel.android.dao.PinYinJSONDAO;
import com.corel.android.pinyin.PinYin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PinYinJsonTestCase extends AndroidTestCase {
	PinYinJSONDAO dao = null;
	@Override
	protected void setUp() throws Exception {
		dao = new PinYinJSONDAO(getContext());
	}

	@Override
	protected void tearDown() throws Exception {
	}
	
	public void testInert() {
		Set<PinYin> py = new HashSet<PinYin>();
		py.add(new PinYin("1", "1", "1"));
		py.add(new PinYin("2", "2", "2"));
		dao.addAll(1, py);
		
		py.clear();
		py.add(new PinYin("chinese", "chinese", "1"));
		py.add(new PinYin("english", "english", "2"));
		dao.addAll(2, py);
		
		PinYin p = dao.get("chinese");
		assertNotNull(p);
		assertEquals("chinese", p.getChinese());
		
		dao.add(1, new PinYin("好","Good", "hao"));
		assertEquals(3, dao.getAll(1).size());

	}
	
	public void testRead() {
		List<PinYin> py = dao.getAll(1);
		assertEquals(3, py.size());
		assertEquals("1", py.get(0).getChinese());
		assertEquals("2", py.get(1).getChinese());
	}
}
