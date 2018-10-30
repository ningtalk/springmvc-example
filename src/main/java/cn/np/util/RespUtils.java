package cn.np.util;

import cn.np.vo.BaseRespVo;
import com.alibaba.fastjson.JSON;

/**
 * @Description:
 * @Author: ningpeng
 * @CreateDate: 2018/10/30 11:35
 */
public class RespUtils {

    public final static Object DEFAULT_DATA = JSON.toJSON(new Object());

    public final static String SUCCESS_CODE = "0";

    public final static String SUCCESS_MSG = "成功";

    public static <T> BaseRespVo<T> ok() {
        return make(SUCCESS_CODE, SUCCESS_MSG, null);
    }

    public static <T> BaseRespVo<T> make(String code, String msg, T t) {
        if(t==null) {
            BaseRespVo<Object> resp = new BaseRespVo<Object>();
            resp.setCode(code);
            resp.setMsg(msg);
            resp.setData(DEFAULT_DATA);
            return (BaseRespVo<T>) resp;
        }
        BaseRespVo<T> resp = new BaseRespVo<>();
        resp.setCode(code);
        resp.setMsg(msg);
        resp.setData(t);
        return  resp;
    }

    public static BaseRespVo exception(Exception ex) {
        return RespUtils.make("xxx", "111", DEFAULT_DATA);
    }
}
