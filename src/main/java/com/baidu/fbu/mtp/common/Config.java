package com.baidu.fbu.mtp.common;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created on 00:25 08/24/2015.
 *
 * @author skywalker
 */
public class Config extends SleepyTask {

    private static final Logger log = LoggerFactory.getLogger(Config.class);
    private static final int CHECK_INTERVAL = 60000;
    private static final Charset DEF_CHARSET = Charsets.UTF_8;

    private static final LoadingCache<String, Config> CACHE = CacheBuilder.newBuilder()
            .maximumSize(1024)
            .expireAfterAccess(30, TimeUnit.MINUTES).build(new CacheLoader<String, Config>() {

                @Override
                public Config load(String key) throws Exception {
                    URL path = Thread.currentThread().getContextClassLoader().getResource(key);
                    if (path == null) {
                        throw new RuntimeException("can't FIND resource file: " + key);
                    }
                    File file = new File(path.getPath());
                    if (!file.canRead()) {
                        throw new RuntimeException("can't READ resource file: " + key);
                    }
                    return new Config(file.getCanonicalFile(), DEF_CHARSET);
                }
            });

    public static Config get(String name) {
        try {
            return CACHE.get(name);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    public static Config getOrNull(String name) {
        try {
            return CACHE.get(name);
        } catch (Exception e) {
            return null;
        }
    }

    private final ArrayList<ChangeListener> listeners = new ArrayList<>(1);

    private final File file;
    private final Charset charset;

    private volatile Map<String, String> props = Collections.emptyMap();
    private volatile long lastChecked = 0;
    private volatile long lastModified = Long.MIN_VALUE;

    public Config(File file, Charset charset) {
        this.file = file;
        this.charset = charset;
        runTask();
    }

    public Config(File file, String charset) {
        this(file, Charset.forName(charset));
    }

    @Override
    protected void runTask() {
        if (! file.canRead()) {
            return;
        }

        if (lastModified > 0) {
            log.info("file modification detected {}, reload resource file: {}",
                    new Timestamp(lastModified), file);
        } else {
            log.info("load resource file: {}", file);
        }

        try {
            props = reload();
            lastModified = file.lastModified();
        } catch (Exception e) {
            log.warn("resource file load error: " + file, e);
            return;
        }

        for (ChangeListener listener : listeners) {
            exector.execute(() -> listener.onChange(Config.this));
        }
    }

    private Map<String, String> reload() throws Exception {
        if (file.getPath().endsWith(".xml")) {
            return reloadXML();
        }

        return reloadProperties();
    }

    private Map<String, String> reloadProperties() throws IOException {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), charset)) {
            Properties p = new Properties();
            p.load(reader);

            Map<String, String> map = Maps.newHashMapWithExpectedSize(p.size());
            for (String key : p.stringPropertyNames()) {
                map.put(key, p.getProperty(key));
            }
            return map;
        }
    }

    private Map<String, String> reloadXML() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), charset)) {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(new InputSource(reader));
            XPathFactory factory = XPathFactory.newInstance();
            NodeList nodes = (NodeList) factory.newXPath().compile("/properties/property")
                    .evaluate(doc, XPathConstants.NODESET);

            Map<String, String> map = Maps.newHashMap();
            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                String name = el.getAttribute("name");
                String attrValue = el.getAttribute("value");
                String value = ! Strings.isNullOrEmpty(attrValue) ? attrValue : el.getTextContent();
                map.put(name, value);
            }
            return map;
        }
    }

    public void checkUpdate(boolean async) {
        final long current = System.currentTimeMillis();
        if (lastChecked + CHECK_INTERVAL >= current) {
            return;
        }

        lastChecked = current;
        if (! file.canRead() || lastModified >= file.lastModified()) {
            return;
        }

        if (async) {
            wakeup();
        } else {
            runTask();
        }
    }

    public String getProperty(String name) {
        checkUpdate(true);
        return props.get(name);
    }

    public String getString(String name, String def) {
        String v = getProperty(name);
        return v == null ? def : v;
    }

    public int getInt(String name, int def) {
        String propName = getProperty(name);
        return ! Strings.isNullOrEmpty(propName) ? Integer.valueOf(propName) : def;
    }

    public long getLong(String name, long def) {
        String propName = getProperty(name);
        return ! Strings.isNullOrEmpty(propName) ? Long.valueOf(propName) : def;
    }

    public float getFloat(String name, float def) {
        String propName = getProperty(name);
        return ! Strings.isNullOrEmpty(propName) ? Float.valueOf(propName) : def;
    }

    public double getDouble(String name, double def) {
        String propName = getProperty(name);
        return ! Strings.isNullOrEmpty(propName) ? Double.valueOf(propName) : def;
    }

    public boolean getBoolean(String name, boolean def) {
        String propName = getProperty(name);
        return ! Strings.isNullOrEmpty(propName) ? Boolean.parseBoolean(propName) : def;
    }

    interface ChangeListener {
        void onChange(Config target);
    }

    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }

    public Map<String, String> getAll() {
        return Collections.unmodifiableMap(props);
    }
}

