package com.wd.tech.utils;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Created by mumu on 2018/12/20.
 */

public class HttpsUtils {

    //服务器主机名字，用来验证服务器域名,这里需要根据自己的主机域名进行配置
    private static final String HOST_NAME = "172.17.8.100";

    //证书名字，需要手动配置，证书放在asset文件夹下
    private static String CERTIFICATE_NAME = "server.crt";

    //证书
    private X509Certificate x509Certificate;

    //需要配置给ok的SSLSocketFactory
    private SSLSocketFactory mSslSocketFactory;

    private SSLContext sslContext;

    //证书管理者
    private MyTrustManager mTrustManager;


    public HttpsUtils(Context context) {
        initHttps(context);
    }


    private void initHttps(Context context) {
        try {
            sslContext = SSLContext.getInstance("TLS");
            //信任证书管理,这个是由我们自己生成的,信任我们自己的服务器证书
            x509Certificate = readCert(context, CERTIFICATE_NAME);
            mTrustManager = new MyTrustManager(x509Certificate);
            sslContext.init(null, new TrustManager[]{
                    mTrustManager
            }, null);
            mSslSocketFactory = sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

    }

    /**
     * 支持证书和域名认证
     */
    public OkHttpClient.Builder setCertificateForOkhttp(OkHttpClient.Builder builder) {
        return builder.sslSocketFactory(mSslSocketFactory, mTrustManager)
                .hostnameVerifier(hostnameVerifier);
    }


    /**
     * 实现了 X509TrustManager
     * 通过此类中的 checkServerTrusted 方法来确认服务器证书是否正确
     */
    private static final class MyTrustManager implements X509TrustManager {
        X509Certificate cert;

        MyTrustManager(X509Certificate cert) {
            this.cert = cert;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // 我们在客户端只做服务器端证书校验。
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // 确认服务器端证书和代码中 hard code 的 CRT 证书相同。
            if (chain[0].equals(this.cert)) {
                Log.i("tag", "checkServerTrusted Certificate from server is valid!");
                return;
            }
            throw new CertificateException("checkServerTrusted No trusted server cert found!");
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /**
     * 服务器域名验证
     */
    private static final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return hostname.equals(HOST_NAME);
        }
    };

    /**
     * 根据asset下证书的名字取出证书 ps：
     */
    private static X509Certificate readCert(Context context, String assetName) {
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(assetName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        X509Certificate cert = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) cf.generateCertificate(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Throwable ex) {
            }
        }
        return cert;
    }

}
