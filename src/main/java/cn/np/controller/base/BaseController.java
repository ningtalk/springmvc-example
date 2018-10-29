package cn.np.controller.base;


import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public class BaseController {
    @Autowired
    private HttpServletRequest request;

    public HttpServletRequest getRequest() {
        return request;
    }
}
