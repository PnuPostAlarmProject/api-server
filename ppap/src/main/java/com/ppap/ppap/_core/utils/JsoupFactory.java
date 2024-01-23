package com.ppap.ppap._core.utils;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class JsoupFactory {

	public Document getDocumentData(String url) throws IOException {
		return Jsoup.connect(url).get();
	}

}
