package com.jessehu.jhhttp.http;

import android.support.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * HTTPS证书工具
 *
 * @author JesseHu
 * @date 2018/11/29
 */
public class CertManager {
    /**
     * 获取指定证书的SSLSocketFactory
     *
     * @param sslContext SSLContext
     * @return SSLSocketFactory
     */
    public static SSLSocketFactory getSocketFactory(SSLContext sslContext) {
        if (sslContext != null) {
            return sslContext.getSocketFactory();
        }
        return null;
    }

    /**
     * 获取指定证书的SSLSocketFactory
     *
     * @param der 证书byte[]
     * @return SSLSocketFactory
     */
    public static SSLSocketFactory getSocketFactory(byte[] der) {
        SSLContext sslContext = getSSLContext(der);
        if (sslContext != null) {
            return sslContext.getSocketFactory();
        }
        return null;
    }

    /**
     * 获取指定证书的SSLSocketFactory
     *
     * @param der 证书String
     * @return SSLSocketFactory
     */
    public static SSLSocketFactory getSocketFactory(String der) {
        SSLContext sslContext = getSSLContext(der);
        if (sslContext != null) {
            return sslContext.getSocketFactory();
        }
        return null;
    }

    /**
     * 获取指定证书的SSLContext
     *
     * @param der 证书String
     * @return SSLContext
     */
    @Nullable
    public static SSLContext getSSLContext(String der) {
        byte[] derBytes = der.getBytes();
        return getSSLContext(derBytes);
    }

    /**
     * 获取指定证书的SSLContext
     *
     * @param der 证书byte[]
     * @return SSLContext
     */
    @Nullable
    public static SSLContext getSSLContext(byte[] der) {
        try {
            KeyStore trustStore = getKeyStore(der);
            KeyManager[] keyManagers = getKeyManagers(trustStore);
            TrustManager[] trustManagers = getTrustManagers(trustStore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagers, new SecureRandom());
            //sslContext.init(可为null, trustManagers, 可为null);
            return sslContext;
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取X509TrustManager
     *
     * @param der 证书
     * @return X509TrustManager
     */
    public static X509TrustManager getX509TrustManager(byte[] der) {
        KeyStore trustStore = getKeyStore(der);
        TrustManager[] trustManagers = getTrustManagers(trustStore);
        if (trustManagers == null || trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }

    /**
     * 获取X509TrustManager
     *
     * @param der 证书
     * @return X509TrustManager
     */
    public static X509TrustManager getX509TrustManager(String der) {
        byte[] derBytes = der.getBytes();
        return getX509TrustManager(derBytes);
    }

    /**
     * 获取信任证书
     *
     * @param keyStore KeyStore
     * @return TrustManager[]
     */
    public static TrustManager[] getTrustManagers(KeyStore keyStore) {
        TrustManagerFactory tmf = null;
        try {
            //实例化信任库
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            //初始化信任库
            tmf.init(keyStore);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        if (tmf != null) {
            return tmf.getTrustManagers();
        }
        return null;
    }

    /**
     * 获取信任证书
     *
     * @param der 证书String
     * @return TrustManager[]
     */
    public static TrustManager[] getTrustManagers(String der) {
        KeyStore keyStore = getKeyStore(der);
        return getTrustManagers(keyStore);
    }

    /**
     * 获取信任证书
     *
     * @param der byte[]
     * @return TrustManager[]
     */
    public static TrustManager[] getTrustManagers(byte[] der) {
        KeyStore keyStore = getKeyStore(der);
        return getTrustManagers(keyStore);
    }

    /**
     * 证书秘钥管理工具
     *
     * @param keyStore 证书秘钥
     * @return KeyManager[]
     */
    public static KeyManager[] getKeyManagers(KeyStore keyStore) {
        KeyManagerFactory kmf = null;
        try {
            //实例化证书库
            kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            //初始化证书库
            kmf.init(keyStore, null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        if (kmf != null) {
            return kmf.getKeyManagers();
        }
        return null;
    }

    /**
     * 证书秘钥管理工具
     *
     * @param der 证书String
     * @return KeyManager[]
     */
    public static KeyManager[] getKeyManagers(String der) {
        KeyStore keyStore = getKeyStore(der);
        return getKeyManagers(keyStore);
    }

    /**
     * 证书秘钥管理工具
     *
     * @param der 证书byte[]
     * @return KeyManager[]
     */
    public static KeyManager[] getKeyManagers(byte[] der) {
        KeyStore keyStore = getKeyStore(der);
        return getKeyManagers(keyStore);
    }

    /**
     * 获取秘钥
     *
     * @param der 证书
     * @return KeyStore
     */
    public static KeyStore getKeyStore(String der) {
        byte[] derBytes = der.getBytes();
        return getKeyStore(derBytes);
    }

    /**
     * 获取秘钥
     *
     * @param der 证书
     * @return KeyStore
     */
    public static KeyStore getKeyStore(byte[] der) {
        KeyStore trustStore = null;
        try {
            ByteArrayInputStream derInputStream = new ByteArrayInputStream(der);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            //读取证书
            X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(derInputStream);
            String alias = cert.getSubjectX500Principal().getName();
            // Create keystore and add to ssl context
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null);
            trustStore.setCertificateEntry(alias, cert);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return trustStore;
    }

    /**
     * 获取信任所有证书的SSLSocketFactory
     *
     * @return SSLSocketFactory
     */
    public static SSLSocketFactory getAllSocketFactory() {
        return getAllSSLContext().getSocketFactory();
    }

    /**
     * 获取信任所有证书的SSLContext
     *
     * @return SSLContext
     */
    @Nullable
    public static SSLContext getAllSSLContext() {
        try {
            //信任所有证书 （官方不推荐使用）
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }
            }}, new SecureRandom());
            return sslContext;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class MyHostnameVerifier implements HostnameVerifier {
        private String mHostname;

        public MyHostnameVerifier(String mHostname) {
            this.mHostname = mHostname;
        }

        @Override
        public boolean verify(String hostname, SSLSession session) {
            if (mHostname != null) {
                return hostname.equals(mHostname);
            }
            return true;
        }
    }
}
