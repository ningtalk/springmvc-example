package cn.np.aop;


import cn.np.util.RespUtils;
import cn.np.vo.BaseReqVo;
import com.github.underscore.Optional;
import com.github.underscore.Predicate;
import com.github.underscore.U;
import com.google.common.base.Strings;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;


@Aspect
@Component
public class ControllerAspect {

    /**
     * 加密检查等
     * @param point
     * @return
     */
    @Around("@annotation(cn.np.aop.EnableEncryption)")
    public Object enableEncryption(ProceedingJoinPoint point) {
    //    throw new RuntimeException("test");
        return RespUtils.ok();
    }


    /**
     * 处理请求参数
     * @param point
     */
    @Before("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void initParameters(JoinPoint point) {
        HttpServletRequest request = getObject(point, HttpServletRequest.class);
        BaseReqVo baseReqVo = getObject(point, BaseReqVo.class);
        if(request == null || baseReqVo == null) {
            return;
        }
        String ip = request.getHeader("X-FORWARDED-FOR");
        if(Strings.isNullOrEmpty(ip)) {
            ip = request.getRemoteAddr();
        }
        baseReqVo.setIp(ip);
    }


    private <T> T getObject(JoinPoint point, final Class<T> c) {
        Object[] args = point.getArgs();
        Optional<Object> optional = U.find(Arrays.asList(args), new Predicate<Object>() {
            @Override
            public boolean test(Object o) {
                return c.isInstance(o);
            }
        });
        if (optional.isPresent()) {
            return c.cast(optional.get());
        }
        return null;
    }
}
