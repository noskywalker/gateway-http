package com.baidu.fbu.mtp.util.http;

import org.apache.http.conn.ssl.X509HostnameVerifier;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.security.cert.X509Certificate;

/**
 * Created on 10:20 11/03/2015.
 *
 * @author skywalker
 */
public class NopHostnameVerifier implements X509HostnameVerifier {
    @Override
    public boolean verify(String s, SSLSession sslSession) {
        return true;
    }

    @Override
    public void verify(String host, SSLSocket ssl) throws IOException {

    }

    @Override
    public void verify(String host, X509Certificate cert) throws SSLException {

    }

    @Override
    public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {

    }
}
