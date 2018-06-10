package cn.lframe.apigateway.filter;

import cn.lframe.apigateway.constant.RedisConstant;
import cn.lframe.apigateway.util.CookieUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_DECORATION_FILTER_ORDER;

/**
 * 权限拦截（区分买家和卖家）
 *
 * @author Lframe
 * @create2018 -05 -19 -10:26
 */
@Component
public class AuthSellerFilter extends ZuulFilter {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 下面的枚举常量都是取至FilterConstants类。
     */
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    /**
     * 人家说这是官方提倡的写法，以后看看官方文档，
     *
     * @return
     */
    @Override
    public int filterOrder() {
        return PRE_DECORATION_FILTER_ORDER - 1;
    }

    /**
     * 这里实现拦截的URL
     * @return
     */
    @Override
    public boolean shouldFilter() {

        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest httpServletRequest = requestContext.getRequest();
        if ("/order/order/finish".equals(httpServletRequest.getRequestURI())) {
            return true;
        }
        return false;
    }

    /**
     * 这里实现拦截后的逻辑
     *
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest httpServletRequest = requestContext.getRequest();

        /**
         *  /order/finish 只能卖家访问（cookie里面有token，并且对应的redis里面有卖家的openid）
         */

        Cookie cookie = CookieUtil.get(httpServletRequest, "token");
        if (cookie == null
                || StringUtils.isEmpty(cookie.getValue())
                || StringUtils.isEmpty(
                stringRedisTemplate.opsForValue().get(
                        String.format(RedisConstant.TOKEN_TEMPLATE, cookie.getValue())
                )
        )
                ) {
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }

        return null;
    }

}
