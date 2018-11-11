package cn.np.jiami;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static cn.np.jiami.SdkConstants.param_signMethod;

/**
 * @author np
 * @date 2018/11/11
 */
public class SdkUtil {


    public static boolean sign(Map<String, String> data, String encoding) {
        if (isEmpty(encoding)) {
            encoding = "UTF-8";
        }
        String signMethod = data.get(param_signMethod);
        String version = data.get(SdkConstants.param_version);

        if (isEmpty(version)) {
            return false;
        }

        if (SdkConstants.VERSION_5_1_0.equals(version)) {
            // 设置签名证书序列号
            data.put(SdkConstants.param_certId, CertUtil.getSignCertId());
            // 将Map信息转换成key1=value1&key2=value2的形式
            String stringData = coverMap2String(data);
            byte[] byteSign = null;
            String stringSign = null;
            try {
                // 通过SHA256进行摘要并转16进制
                byte[] signDigest = SecureUtil
                        .sha256X16(stringData, encoding);
                byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft256(
                        CertUtil.getSignCertPrivateKey(), signDigest));
                stringSign = new String(byteSign);
                // 设置签名域值
                data.put(SdkConstants.param_signature, stringSign);
                return true;
            } catch (Exception e) {
                System.out.println("Sign Error" + e);
                return false;
            }

        } else if (SdkConstants.SIGNMETHOD_SHA256.equals(signMethod)) {
        //    return signBySecureKey(data, SdkConfig.secureKey, encoding);

        }

        return false;

    }

    /**
     * 过滤请求报文中的空字符串
     * @param contentData
     * @return
     */
    public static Map<String, String> fliterBlank(Map<String, String> contentData) {
        if (contentData == null) {
            return null;
        }

        Map<String, String> submitFormData = new HashMap<>();
        Set<String> keyset =  contentData.keySet();
        for (String key : keyset) {
            String value = contentData.get(key);
            // 可以用工具类替换
            if (value != null && value.trim().length() != 0) {
                submitFormData.put(key, value.trim());
            }
        }
        return submitFormData;

    }

    /**
     * 判断字符串是否为NULL或空
     *
     * @param s
     *            待判断的字符串数据
     * @return 判断结果 true-是 false-否
     */
    public static boolean isEmpty(String s) {
        return null == s || "".equals(s.trim());
    }

    /**
     * 将Map中的数据转换成key1=value1&key2=value2的形式 不包含签名域signature
     *
     * @param data
     *            待拼接的Map数据
     * @return 拼接好后的字符串
     */
    public static String coverMap2String(Map<String, String> data) {
        TreeMap<String, String> tree = new TreeMap<String, String>();
        Iterator<Map.Entry<String, String>> it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> en = it.next();
            if (SdkConstants.param_signature.equals(en.getKey().trim())) {
                continue;
            }
            tree.put(en.getKey(), en.getValue());
        }
        it = tree.entrySet().iterator();
        StringBuffer sf = new StringBuffer();
        while (it.hasNext()) {
            Map.Entry<String, String> en = it.next();
            sf.append(en.getKey() + SdkConstants.EQUAL + en.getValue()
                    + SdkConstants.AMPERSAND);
        }
        return sf.substring(0, sf.length() - 1);
    }

    /**
     * 通过传入的证书绝对路径和证书密码读取签名证书进行签名并返回签名值<br>
     *
     * @param data
     *            待签名数据Map键值对形式
     * @param encoding
     *            编码
     * @return 签名值
     */
    public static boolean signBySecureKey(Map<String, String> data, String secureKey,
                                          String encoding) {

        if (isEmpty(encoding)) {
            encoding = "UTF-8";
        }
        if (isEmpty(secureKey)) {
            System.out.println("secureKey is empty");
            return false;
        }
        String signMethod = data.get(param_signMethod);
        if (isEmpty(signMethod)) {
            System.out.println("signMethod must Not null");
            return false;
        }

        if (SdkConstants.SIGNMETHOD_SHA256.equals(signMethod)) {
            // 将Map信息转换成key1=value1&key2=value2的形式
            String stringData = coverMap2String(data);
            System.out.println("待签名请求报文串:[" + stringData + "]");
            String strBeforeSha256 = stringData
                    + SdkConstants.AMPERSAND
                    + SecureUtil.sha256X16Str(secureKey, encoding);
            String strAfterSha256 = SecureUtil.sha256X16Str(strBeforeSha256,
                    encoding);
            // 设置签名域值
            data.put(SdkConstants.param_signature, strAfterSha256);
            return true;
        } else if (SdkConstants.SIGNMETHOD_SM3.equals(signMethod)) {
            String stringData = coverMap2String(data);
            System.out.println("待签名请求报文串:[" + stringData + "]");
            String strBeforeSM3 = stringData
                    + SdkConstants.AMPERSAND
                    + SecureUtil.sm3X16Str(secureKey, encoding);
            String strAfterSM3 = SecureUtil.sm3X16Str(strBeforeSM3, encoding);
            // 设置签名域值
            data.put(SdkConstants.param_signature, strAfterSM3);
            return true;
        }
        return false;
    }


    /**
     * 将形如key=value&key=value的字符串转换为相应的Map对象
     *
     * @param result
     * @return
     */
    public static Map<String, String> convertResultStringToMap(String result) {
        Map<String, String> map =null;
        try {

            if (result != null && result.trim().length() > 0) {
                if (result.startsWith("{") && result.endsWith("}")) {
                    result = result.substring(1, result.length() - 1);
                }
                map = parseQString(result);
            }

        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage() + e);
        }
        return map;
    }

    /**
     * 解析应答字符串，生成应答要素
     *
     * @param str
     *            需要解析的字符串
     * @return 解析的结果map
     * @throws UnsupportedEncodingException
     */
    public static Map<String, String> parseQString(String str)
            throws UnsupportedEncodingException {

        Map<String, String> map = new HashMap<String, String>();
        int len = str.length();
        StringBuilder temp = new StringBuilder();
        char curChar;
        String key = null;
        boolean isKey = true;
        boolean isOpen = false;//值里有嵌套
        char openName = 0;
        if(len>0){
            for (int i = 0; i < len; i++) {// 遍历整个带解析的字符串
                curChar = str.charAt(i);// 取当前字符
                if (isKey) {// 如果当前生成的是key

                    if (curChar == '=') {// 如果读取到=分隔符
                        key = temp.toString();
                        temp.setLength(0);
                        isKey = false;
                    } else {
                        temp.append(curChar);
                    }
                } else  {// 如果当前生成的是value
                    if(isOpen){
                        if(curChar == openName){
                            isOpen = false;
                        }

                    }else{//如果没开启嵌套
                        if(curChar == '{'){//如果碰到，就开启嵌套
                            isOpen = true;
                            openName ='}';
                        }
                        if(curChar == '['){
                            isOpen = true;
                            openName =']';
                        }
                    }
                    if (curChar == '&' && !isOpen) {// 如果读取到&分割符,同时这个分割符不是值域，这时将map里添加
                        putKeyValueToMap(temp, isKey, key, map);
                        temp.setLength(0);
                        isKey = true;
                    }else{
                        temp.append(curChar);
                    }
                }

            }
            putKeyValueToMap(temp, isKey, key, map);
        }
        return map;
    }

    private static void putKeyValueToMap(StringBuilder temp, boolean isKey,
                                         String key, Map<String, String> map)
            throws UnsupportedEncodingException {
        if (isKey) {
            key = temp.toString();
            if (key.length() == 0) {
                throw new RuntimeException("QString format illegal");
            }
            map.put(key, "");
        } else {
            if (key.length() == 0) {
                throw new RuntimeException("QString format illegal");
            }
            map.put(key, temp.toString());
        }
    }

}
