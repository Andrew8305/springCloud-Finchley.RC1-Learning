//package cn.lframe.apigateway.filter;
//
//import cn.lframe.apigateway.constant.RedisConstant;
//import cn.lframe.apigateway.util.CookieUtil;
//import com.netflix.zuul.ZuulFilter;
//import com.netflix.zuul.context.RequestContext;
//import com.netflix.zuul.exception.ZuulException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//
//import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_DECORATION_FILTER_ORDER;
//
///**
// * 权限拦截（区分买家和卖家）
// *
// * @author Lframe
// * @create2018 -05 -19 -10:26
// */
//@Component
//public class AuthFilter extends ZuulFilter {
//
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    /**
//     * 下面的枚举常量都是取至FilterConstants类。
//     */
//    @Override
//    public String filterType() {
//        return FilterConstants.PRE_TYPE;
//    }
//
//    /**
//     * 人家说这是官方提倡的写法，以后看看官方文档，
//     *
//     * @return
//     */
//    @Override
//    public int filterOrder() {
//        return PRE_DECORATION_FILTER_ORDER - 1;
//    }
//
//    @Override
//    public boolean shouldFilter() {
//        return true;
//    }
//
//    /**
//     * 这里就是我们实现拦截的逻辑
//     *
//     * @return
//     * @throws ZuulException
//     */
//    @Override
//    public Object run() throws ZuulException {
//        RequestContext requestContext = RequestContext.getCurrentContext();
//        HttpServletRequest httpServletRequest = requestContext.getRequest();
//
//        /**
//         *  /order/create 只能买家访问 (cookie里面有openid)
//         *  /order/finish 只能卖家访问（cookie里面有token，并且对应的redis里面有卖家的openid）
//         *  /product/list 都可访问
//         */
//
//        if ("/order/order/create".equals(httpServletRequest.getRequestURI())) {
//            Cookie cookie = CookieUtil.get(httpServletRequest, "openid");
//            if (cookie == null || StringUtils.isEmpty(cookie.getValue())) {
//                requestContext.setSendZuulResponse(false);
//                requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
//
//            }
//        }
//
//        if ("/order/order/finish".equals(httpServletRequest.getRequestURI())) {
//            Cookie cookie = CookieUtil.get(httpServletRequest, "token");
//            if (cookie == null
//                    || StringUtils.isEmpty(cookie.getValue())
//                    || StringUtils.isEmpty(
//                    stringRedisTemplate.opsForValue().get(
//                            String.format(RedisConstant.TOKEN_TEMPLATE, cookie.getValue())
//                    )
//            )
//                    ) {
//                requestContext.setSendZuulResponse(false);
//                requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
//            }
//        }
//        return null;
//    }
//
//}
