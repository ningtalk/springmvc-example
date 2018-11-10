package cn.np.jiami;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: ningpeng
 * @CreateDate: 2018/11/10 19:47
 * https://my.oschina.net/jiangli0502/blog/171263
 * 非对称加密一般都用于加密对称加密算法的密钥,而不是直接加密内容
 */
public class RSAUtils {
    public static final String KEY_ALGORTHM="RSA";//
    public static final String SIGNATURE_ALGORITHM="MD5withRSA";

    public static final String PUBLIC_KEY = "RSAPublicKey";//公钥
    public static final String PRIVATE_KEY = "RSAPrivateKey";//私钥

    public static Map<String,Object> initKey()throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORTHM);
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        //公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        //私钥
        RSAPrivateKey privateKey =  (RSAPrivateKey) keyPair.getPrivate();
        Map<String,Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);

        return keyMap;
    }

    /**
     * 取得公钥，并转化为String类型
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap)throws Exception{
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Coder.encryptBASE64(key.getEncoded());
    }

    /**
     * 取得私钥，并转化为String类型
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap) throws Exception{
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Coder.encryptBASE64(key.getEncoded());
    }
}
