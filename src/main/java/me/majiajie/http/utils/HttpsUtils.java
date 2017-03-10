package me.majiajie.http.utils;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;


public class HttpsUtils
{
    private HttpsUtils(){}

    /**
     * 生成SSLSocketFactory
     * @param certificates  证书，不可为空
     * @return  {@link SSLSocketFactory}
     */
    public static SSLSocketFactory getSslSocketFactory(InputStream certificates) {

        return getSslSocketFactory(certificates,null,null);
    }

    /**
     * 生成SSLSocketFactory
     * @param certificates  证书，不可为空
     * @param key           keystore
     * @param keyPassword   keystore密码
     * @return  {@link SSLSocketFactory}
     */
    public static SSLSocketFactory getSslSocketFactory
    (InputStream certificates, InputStream key, String keyPassword)
    {
        SSLContext sslContext = null;

        Certificate ca = getCertificate(certificates);
        if(ca == null){return null;}

        try
        {
            TrustManagerFactory tmf = getTrustManagerFactory(ca);

            KeyManagerFactory kmf = null;
            if(key != null && keyPassword != null)
            {
                kmf = getKeyManagerFactory(key,keyPassword);
            }

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf == null ? null:kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return sslContext != null? sslContext.getSocketFactory():null;
    }

    /**
     * 获取信任所有证书的SSLSocketFactory
     */
    public static SSLSocketFactory getTrustAllHttpsSSLSocketFactory(){
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }

    /**
     * 获得证书实体
     * @param certificate 证书
     * @return  {@link Certificate},如果输入流错误就返回null
     */
    private static Certificate getCertificate(InputStream certificate)
    {
        Certificate ca = null;
        try
        {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            try {
                ca = certificateFactory.generateCertificate(certificate);
            } finally {
                certificate.close();
            }
        }
        catch (CertificateException | IOException e)
        {
            e.printStackTrace();
        }

        return ca;
    }

    /**
     * 获取{@link TrustManagerFactory}（信任证书管理）
     * @param ca 证书实体
     * @return {@link TrustManagerFactory},如果输入流错误就返回null
     */
    private static TrustManagerFactory getTrustManagerFactory(Certificate ca)
    {
        TrustManagerFactory tmf = null;
        try {
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null,null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tmf;
    }

    /**
     * 获取{@link KeyManagerFactory}（Keystote管理）
     * @param key           keystore文件流（.jks）
     * @param keyPassword   keystore密码
     * @return  {@link KeyManagerFactory},如果输入流错误或密码错误就返回null
     */
    private static KeyManagerFactory getKeyManagerFactory(InputStream key, String keyPassword)
    {
        KeyManagerFactory kmf = null;

        try {
            String keyStoreType = "JKS";
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(key, keyPassword.toCharArray());

            String kmfAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
            kmf = KeyManagerFactory.getInstance(kmfAlgorithm);
            kmf.init(keyStore,keyPassword.toCharArray());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return kmf;
    }

    public static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
    }
}
