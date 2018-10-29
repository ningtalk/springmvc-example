package cn.np.vo;

import java.io.Serializable;

/**
 * 基础请求，存放基本信息，如机型信息，用户信息等
 */
public class BaseReqVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
