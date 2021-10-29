package com.baidu.fbu.mtp.common;

import static com.baidu.fbu.mtp.common.type.ConfigFileType.CONFIG_PROPERTY;
import static com.baidu.fbu.mtp.common.ConfigUtil.getInt;

import com.baidu.fbu.mtp.common.handler.DefaultHttpRequestRetryHandler;
import com.baidu.fbu.mtp.service.SpringContextHolder;
import com.baidu.fbu.mtp.util.http.IdleConnectionMonitorThread;
import com.baidu.fbu.mtp.util.http.NoRedirectStrategy;
import com.baidu.fbu.mtp.util.http.NopHostnameVerifier;
import com.baidu.fbu.mtp.util.http.TrustAllStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.SSLContext;

/**
 * Created on 14:46 12/02/2015.
 *
 * @author skywalker
 */
public class HttpClientFactory {
    private static final PoolingHttpClientConnectionManager CM = new PoolingHttpClientConnectionManager(registry());

    static {
        initConnectionManager(CM);
    }

    private static final CloseableHttpClient HTTP_CLIENT = HttpClients.custom()
            .setConnectionManager(CM)
            .setRetryHandler(new DefaultHttpRequestRetryHandler())
            .setRedirectStrategy(new NoRedirectStrategy())
            .setDefaultRequestConfig(defaultRequestConfig())
            // 不要在这设置
            // .setMaxConnPerRoute(10)
            // .setMaxConnTotal(50)
            .build();

    private HttpClientFactory() {
    }

    public static CloseableHttpClient getInstance() {
        return HTTP_CLIENT;
    }

    private static void initConnectionManager(PoolingHttpClientConnectionManager cm) {
        cm.setDefaultSocketConfig(SocketConfig.custom()
                .setSoTimeout(getInt(CONFIG_PROPERTY, "http.soTimeout", 2000))
                .build());
        cm.setDefaultMaxPerRoute(getInt(CONFIG_PROPERTY, "http.defaultMaxPerRoute", 500));
        cm.setMaxTotal(getInt(CONFIG_PROPERTY, "http.maxTotal", 1000));
    }

    private static RequestConfig defaultRequestConfig() {
        return RequestConfig.custom()
                .setStaleConnectionCheckEnabled(true)
                // 分配的 socket 的 soTimeout, 后续处理过程使用这个超时时间
                .setSocketTimeout(getInt(CONFIG_PROPERTY, "http.socketTimeout", 20000))
                // socket 建立网络连接, 超时时间
                .setConnectTimeout(getInt(CONFIG_PROPERTY, "http.connectTimeout", 10000))
                // 从连接池获取连接, 最长的等待时间
                .setConnectionRequestTimeout(getInt(CONFIG_PROPERTY, "http.connectionRequestTimeout", 8000))
                .build();
    }

    private static SSLContext sslContext() {
        try {
            return SSLContexts.custom().loadTrustMaterial(null, new TrustAllStrategy()).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Registry<ConnectionSocketFactory> registry() {
        return RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", new SSLConnectionSocketFactory(sslContext(), new NopHostnameVerifier()))
                .register("http", PlainConnectionSocketFactory.getSocketFactory()) // thread-safe
                .build();
    }

    static {
        IdleConnectionMonitorThread t =
                (IdleConnectionMonitorThread) SpringContextHolder.getBean("httpConnMonitorService");
        t.setConnMgr(CM);
        t.setDaemon(true);
        t.start();
    }
}
