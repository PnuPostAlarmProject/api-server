package com.ppap.ppap._core.utils;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@Component
public class UrlFactory {

    public URL makeURL(String url) throws MalformedURLException {
        return new URL(url);
    }

    public InputStream getInputStream(String url, int connectionTimeout, int readTimeout) throws IOException {
        URL urlInstance = new URL(url);
        URLConnection connection = urlInstance.openConnection();
        connection.setConnectTimeout(connectionTimeout);
        connection.setReadTimeout(readTimeout);
        return connection.getInputStream();
    }
}
