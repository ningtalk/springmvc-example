package cn.np.vo;

/**
 * @Description:
 * @Author: ningpeng
 * @CreateDate: 2018/10/30 11:32
 */
public class BaseRespVo<T> {
    /**
     * 返回码
     */
    private String code;
    /**
     * 返回码意思
     */
    private String msg;
    /**
     * 返回数据
     */
    private T data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseRespVo{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
