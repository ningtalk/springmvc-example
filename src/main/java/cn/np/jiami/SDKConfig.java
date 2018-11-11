package cn.np.jiami;

/**
 * @author np
 * @date 2018/11/11
 */
public class SDKConfig {

    public static String signMethod = "01";
    public static String signCertPath = "D:/certs/acp_test_sign.pfx";
    public static String signCertPwd = "000000";
    public static String signCertType = "PKCS12";
    public static String middleCertPath = "D:/certs/acp_test_middle.cer";
    public static String rootCertPath = "D:/certs/acp_test_root.cer";
    public static String encryptCertPath = "d:/certs/acp_test_enc.cer";
    public static String encryptTrackKeyModulus = "d:/certs/acp_test_enc.cer";
    public static boolean ifValidateRemoteCert = false;

}
