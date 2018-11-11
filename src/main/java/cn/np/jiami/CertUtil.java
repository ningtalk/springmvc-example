package cn.np.jiami;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import static cn.np.jiami.SdkUtil.isEmpty;

/**
 * @author np
 * @date 2018/11/11
 */
public class CertUtil {

    /** 证书容器，存储对商户请求报文签名私钥证书. */
    private static KeyStore keyStore = null;
    /** 验签中级证书 */
    private static X509Certificate middleCert = null;

    /** 验签根证书 */
    private static X509Certificate rootCert = null;

    /** 敏感信息加密公钥证书 */
    private static X509Certificate encryptCert = null;

    static {
        init();
    }

    /**
     * 初始化所有证书.
     */
    private static void init() {
        try {
            addProvider();//向系统添加BC provider
            initSignCert();//初始化签名私钥证书
            initMiddleCert();//初始化验签证书的中级证书
            initRootCert();//初始化验签证书的根证书
            initEncryptCert();//初始化加密公钥
        } catch (Exception e) {
            System.out.println("init失败。（如果是用对称密钥签名的可无视此异常。）" + e);
        }
    }

    /**
     * 添加签名，验签，加密算法提供者
     */
    private static void addProvider(){
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        } else {
            Security.removeProvider("BC"); //解决eclipse调试时tomcat自动重新加载时，BC存在不明原因异常的问题。
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }
        // printSysInfo();
    }

    /**
     * 获取配置文件acp_sdk.properties中配置的签名私钥证书certId
     *
     * @return 证书的物理编号
     */
    public static String getSignCertId() {
        try {
            Enumeration<String> aliasenum = keyStore.aliases();
            String keyAlias = null;
            if (aliasenum.hasMoreElements()) {
                keyAlias = aliasenum.nextElement();
            }
            X509Certificate cert = (X509Certificate) keyStore
                    .getCertificate(keyAlias);
            return cert.getSerialNumber().toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 用配置文件acp_sdk.properties中配置的私钥路径和密码 加载签名证书
     */
    private static void initSignCert() {
        if(!"01".equals(SDKConfig.signMethod)){
            System.out.println("非rsa签名方式，不加载签名证书。");
            return;
        }
        if (SDKConfig.signCertPath == null
                || SDKConfig.signCertPwd == null
                || SDKConfig.signCertType == null) {
            return;
        }
        if (null != keyStore) {
            keyStore = null;
        }
        try {
            keyStore = getKeyInfo(SDKConfig.signCertPath,
                    SDKConfig.signCertPwd, SDKConfig
                            .signCertType);
            System.out.println("InitSignCert Successful. CertId=["
                    + getSignCertId() + "]");
        } catch (IOException e) {
            System.out.println("InitSignCert Error" + e);
        }
    }

    /**
     * 将签名私钥证书文件读取为证书存储对象
     *
     * @param pfxkeyfile
     *            证书文件名
     * @param keypwd
     *            证书密码
     * @param type
     *            证书类型
     * @return 证书对象
     * @throws IOException
     */
    private static KeyStore getKeyInfo(String pfxkeyfile, String keypwd,
                                       String type) throws IOException {
        System.out.println("加载签名证书==>" + pfxkeyfile);
        FileInputStream fis = null;
        try {
            KeyStore ks = KeyStore.getInstance(type, "BC");
            System.out.println("Load RSA CertPath=[" + pfxkeyfile + "],Pwd=["+ keypwd + "],type=["+type+"]");
            fis = new FileInputStream(pfxkeyfile);
            char[] nPassword = null;
            nPassword = null == keypwd || "".equals(keypwd.trim()) ? null: keypwd.toCharArray();
            if (null != ks) {
                ks.load(fis, nPassword);
            }
            return ks;
        } catch (Exception e) {
            System.out.println("getKeyInfo Error" + e);
            return null;
        } finally {
            if(null!=fis)
                fis.close();
        }
    }


    /**
     * 用配置文件acp_sdk.properties配置路径 加载敏感信息加密证书
     */
    private static void initMiddleCert() {
        System.out.println("加载中级证书==>"+SDKConfig.middleCertPath);
        if (!isEmpty(SDKConfig.middleCertPath)) {
            middleCert = initCert(SDKConfig.middleCertPath);
            System.out.println("Load MiddleCert Successful");
        } else {
            System.out.println("WARN: acpsdk.middle.path is empty");
        }
    }

    /**
     * 通过证书路径初始化为公钥证书
     * @param path
     * @return
     */
    private static X509Certificate initCert(String path) {
        X509Certificate encryptCertTemp = null;
        CertificateFactory cf = null;
        FileInputStream in = null;
        try {
            cf = CertificateFactory.getInstance("X.509", "BC");
            in = new FileInputStream(path);
            encryptCertTemp = (X509Certificate) cf.generateCertificate(in);
            // 打印证书加载信息,供测试阶段调试
            System.out.println("[" + path + "][CertId="
                    + encryptCertTemp.getSerialNumber().toString() + "]");
        } catch (CertificateException e) {
            System.out.println("InitCert Error" + e);
        } catch (FileNotFoundException e) {
            System.out.println("InitCert Error File Not Found" + e);
        } catch (NoSuchProviderException e) {
            System.out.println("LoadVerifyCert Error No BC Provider" + e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
            }
        }
        return encryptCertTemp;
    }

    /**
     * 用配置文件acp_sdk.properties配置路径 加载敏感信息加密证书
     */
    private static void initRootCert() {
        System.out.println("加载根证书==>"+SDKConfig.rootCertPath);
        if (!isEmpty(SDKConfig.rootCertPath)) {
            rootCert = initCert(SDKConfig.rootCertPath);
            System.out.println("Load RootCert Successful");
        } else {
            System.out.println("WARN: acpsdk.rootCert.path is empty");
        }
    }

    /**
     * 用配置文件acp_sdk.properties配置路径 加载银联公钥上级证书（中级证书）
     */
    private static void initEncryptCert() {
        System.out.println("加载敏感信息加密证书==>"+SDKConfig.encryptCertPath);
        if (!isEmpty(SDKConfig.encryptCertPath)) {
            encryptCert = initCert(SDKConfig.encryptCertPath);
            System.out.println("Load EncryptCert Successful");
        } else {
            System.out.println("WARN: acpsdk.encryptCert.path is empty");
        }
    }

    /**
     * 通过keyStore 获取私钥签名证书PrivateKey对象
     *
     * @return
     */
    public static PrivateKey getSignCertPrivateKey() {
        try {
            Enumeration<String> aliasenum = keyStore.aliases();
            String keyAlias = null;
            if (aliasenum.hasMoreElements()) {
                keyAlias = aliasenum.nextElement();
            }
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias,
                    SDKConfig.signCertPwd.toCharArray());
            return privateKey;
        } catch (KeyStoreException e) {
            System.out.println("getSignCertPrivateKey Error" + e);
            return null;
        } catch (UnrecoverableKeyException e) {
            System.out.println("getSignCertPrivateKey Error" + e);
            return null;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("getSignCertPrivateKey Error" + e);
            return null;
        }
    }



}
