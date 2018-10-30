package cn.np.exception;

import cn.np.util.RespUtils;
import cn.np.vo.BaseRespVo;
import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 统一处理异常
 * @Author: ningpeng
 * @CreateDate: 2018/10/30 11:43
 */
@Component
public class GlobalExceptionHandler implements HandlerExceptionResolver, Ordered {
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        ModelAndView mv = new ModelAndView();
        BaseRespVo vo = RespUtils.exception(e);
        FastJsonJsonView view = new FastJsonJsonView();
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("code", vo.getCode());
        attributes.put("msg", vo.getMsg());
        attributes.put("data", vo.getData());
        view.setAttributesMap(attributes);
        mv.setView(view);
        return mv;
    }
}
