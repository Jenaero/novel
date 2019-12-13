package com.xiaokedou.novel.spider.storage;

import com.xiaokedou.novel.spider.storage.impl.BxwxNovelStorageImpl;
import com.xiaokedou.novel.spider.storage.impl.TestNovelStorageImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

public class Storage {

	public static void main(String[] args) throws FileNotFoundException {
//		if(args.length==0)
//			System.exit(0);
//		Processor processor = new BxwxNovelStorageImpl();
		Processor processor = new TestNovelStorageImpl();
		processor.process();
	}
}
