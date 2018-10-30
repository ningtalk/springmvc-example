package cn.np.controller.test;

import cn.np.aop.EnableEncryption;
import cn.np.controller.base.BaseController;
import cn.np.vo.account.UserInfoVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/test")
public class TestController extends BaseController {

    @RequestMapping("userinfo")
    @ResponseBody
    @EnableEncryption
    public String userinfo(UserInfoVo userInfoVo, HttpServletRequest request) {
        System.out.println(userInfoVo);
        System.out.println(userInfoVo.getIp());
        return userInfoVo.toString();
    }
}
