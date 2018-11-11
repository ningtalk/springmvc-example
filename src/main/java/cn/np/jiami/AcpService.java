package cn.np.jiami;


import java.util.HashMap;
import java.util.Map;

/**
 * @author np
 * @date 2018/11/11
 */
public class AcpService {

    public static Map<String, String> sign(Map<String, String> reqData, String encoding) {
        reqData = SdkUtil.fliterBlank(reqData);
        SdkUtil.sign(reqData, encoding);
        return reqData;
    }

    /**
     * 功能：后台交易提交请求报文并接收同步应答报文<br>
     * @param reqData 请求报文<br>
     * @param reqUrl  请求地址<br>
     * @param encoding<br>
     * @return 应答http 200返回true ,其他false<br>
     */
    public static Map<String,String> post(
            Map<String, String> reqData,String reqUrl,String encoding) {
        Map<String, String> rspData = new HashMap<String,String>();
        System.out.println("请求银联地址:" + reqUrl);
        //发送后台请求数据
        HttpClient hc = new HttpClient(reqUrl, 30000, 30000);
        try {
            int status = hc.send(reqData, encoding);
            if (200 == status) {
                String resultString = hc.getResult();
                if (null != resultString && !"".equals(resultString)) {
                    // 将返回结果转换为map
                    Map<String,String> tmpRspData  = SdkUtil.convertResultStringToMap(resultString);
                    rspData.putAll(tmpRspData);
                }
            }else{
                System.out.println("返回http状态码["+status+"]，请检查请求报文或者请求地址是否正确");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + e);
        }
        return rspData;
    }
}
