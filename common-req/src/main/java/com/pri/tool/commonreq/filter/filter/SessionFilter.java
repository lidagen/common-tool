package com.pri.tool.commonreq.filter.filter;

import com.alibaba.fastjson.JSONObject;
import com.pri.tool.commonreq.filter.bean.PublicParam;
import com.pri.tool.commonreq.filter.req.RequestWrapper;
import com.pri.tool.commonreq.filter.session.SessionUtil;
import com.pri.tool.commonreq.filter.util.RequestBodyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.logging.LogRecord;

/**
 * @author wang.song
 * @date 2020-12-09 17:30
 * @Desc
 */
@Order(2)
@WebFilter(filterName = "sessionFilter", urlPatterns = "/*")
@Slf4j
@Component
public class SessionFilter implements Filter {
    private static final String POST = "POST";
    private static final String REQUEST = "REQUEST";
    private static final String REQUEST_DATA ="REQUEST_DATA";


    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if (request instanceof HttpServletRequest) {
            requestWrapper = new RequestWrapper((HttpServletRequest) request);
        }
        if (null == requestWrapper) {
            chain.doFilter(request, response);
        } else {
            //如果是进行包装后，那么就可以使用原request对象来进行操作
            PublicParam publicParam = getBaseReqVoByRequest(requestWrapper);
            if (null != publicParam) {
                //存入本地线程
                SessionUtil.set(publicParam);
            }
            try {
                chain.doFilter(requestWrapper, response);
            }catch (Exception e){
                log.error("执行出错");
                throw  e;
            }finally {
                //销毁数据
                SessionUtil.remove();
            }
        }
    }

    public PublicParam getBaseReqVoByRequest(ServletRequest request) throws IOException {
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            //得到目标路径
            HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(httpServletRequest);
            if(Objects.isNull(handlerExecutionChain)){
                return null;
            }
            HandlerMethod handlerMethod = (HandlerMethod) handlerExecutionChain.getHandler();
            //请求地址
            String sendMethod = handlerMethod.getMethod().toString();
            //得到访问路径
            String urlQueryString = httpServletRequest.getRequestURI();

            if (POST.equals(httpServletRequest.getMethod())) {
                inputStreamReader = new InputStreamReader(request.getInputStream());
                reader = new BufferedReader(inputStreamReader);
                String body = RequestBodyUtils.read(reader);
                //得到了body

                PublicParam publicParam = getBaseReqVoByJsonStr(body);
                return publicParam;
            } else {
                //其他的直接打印出请求url和请求参数
                if (Objects.nonNull(urlQueryString) && (Objects.nonNull(httpServletRequest.getQueryString()))) {
                    urlQueryString = urlQueryString + "?" + httpServletRequest.getQueryString();
                }

            }

        } catch (Exception e) {
            log.error("{}",e);
        } finally {
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (reader != null) {
                inputStreamReader.close();
            }
        }
        return null;
    }

    /**
     * 得到BaseReqVo的公参
     *
     * @param jsonStr
     * @return
     */
    public PublicParam getBaseReqVoByJsonStr(String jsonStr) {
        if (Objects.isNull(jsonStr)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        if (null != jsonObject && jsonObject.containsKey(REQUEST)) {
            //确定为openapi格式
            JSONObject reqJson = jsonObject.getJSONObject("REQUEST").getJSONObject("REQUEST_DATA");
            PublicParam publicParam = reqJson.toJavaObject(PublicParam.class);
            //得到了公参
            return publicParam;
        }
        return null;
    }


}
