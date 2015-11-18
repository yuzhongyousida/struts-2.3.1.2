/*
 * $Id: DefaultActionSupport.java 651946 2008-04-27 13:41:38Z apetrelli $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.dispatcher.ng.filter;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.ng.ExecuteOperations;
import org.apache.struts2.dispatcher.ng.InitOperations;
import org.apache.struts2.dispatcher.ng.PrepareOperations;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Handles both the preparation and execution phases of the Struts dispatching process.  This filter is better to use
 * when you don't have another filter that needs access to action context information, such as Sitemesh.
 * 译文：处理的准备和执行阶段的struts调度程序.
 *      当你没有另一个需要访问动作上下文信息的filter时，用filter来实现更佳，比如：sitemesh.
 *
 */
public class StrutsPrepareAndExecuteFilter implements StrutsStatics, Filter {
    protected PrepareOperations prepare;//为请求做准备的操作容器实例对象
    protected ExecuteOperations execute;//执行请求的操作容器实例对象
	protected List<Pattern> excludedPatterns = null;//存储不由struts2处理的路径对应的Pattern对象的List（StrutsConstants.STRUTS_ACTION_EXCLUDE_PATTERN的属性配置值）

    /**
     * filter的初始化方法
     * @param filterConfig
     * @throws ServletException
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        //1、----------包含struts初始化操作的类
        InitOperations init = new InitOperations();
        try {

            //2、----------hostConfig包装类
            FilterHostConfig config = new FilterHostConfig(filterConfig);

            //3、----------初始化struts的内部日志
            init.initLogging(config);

            //4、----------创建和初始化dispatcher对象
            Dispatcher dispatcher = init.initDispatcher(config);

            //5、----------用struts filter的配置初始化静态内容加载器
            init.initStaticContentLoader(config, dispatcher);

            //6、----------准备操作容器对象和执行操作容器对象的初始化（这就是StrutsPrepareAndExecuteFilter优于老版本FilterDispatcherListener的地方）
            prepare = new PrepareOperations(filterConfig.getServletContext(), dispatcher);
            execute = new ExecuteOperations(filterConfig.getServletContext(), dispatcher);

            //7、----------根据StrutsConstants.STRUTS_ACTION_EXCLUDE_PATTERN的属性配置值，获取不由struts2处理的路径对应的Pattern对象的List，赋值给excludedPatterns属性
			this.excludedPatterns = init.buildExcludedPatternsList(dispatcher);

            //8、----------初始化后的回掉
            postInit(dispatcher, filterConfig);
        } finally {
            init.cleanup();
        }

    }

    /**
     * Callback for post initialization
     */
    protected void postInit(Dispatcher dispatcher, FilterConfig filterConfig) {
    }

    /**
     * 过滤器主体逻辑
     * @param req
     * @param res
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        //1、----------转换request和response对象
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        try {
            //2、----------设置请求准备操作的编码方式和Local
            prepare.setEncodingAndLocale(request, response);

            //3、----------创建action上下文环境对象
            prepare.createActionContext(request, response);

            //4、----------将Dispatcher对象赋值给当前线程
            prepare.assignDispatcherToThread();

            //5、----------判断请求URL是否属于配置的不由struts2处理的URL中的一个
			if ( excludedPatterns != null && prepare.isUrlExcluded(request, excludedPatterns)) {
				chain.doFilter(request, response);
			} else {
                //6、----------将request对象包装成StrutsRequestWrapper类型或其子类型MultiPartRequestWrapper
				request = prepare.wrapRequest(request);

                //7、----------获取请求action对应的ActionMapping对象
				ActionMapping mapping = prepare.findActionMapping(request, response, true);

                //8、----------若找不到请求action对应的ActionMapping对象，则判断一下是否是请求静态资源
                if (mapping == null) {
					boolean handled = execute.executeStaticResourceRequest(request, response);
					if (!handled) {
						chain.doFilter(request, response);
					}
				} else {
                    //9、----------若找的到对应的ActionMapping对象，则进入对应的action中进行业务处理逻辑
					execute.executeAction(request, response, mapping);
				}
			}
        } finally {
            //10、----------清理PrepareOperations本地线程中的request对象（为下次请求做准备）
            prepare.cleanupRequest(request);
        }
    }

    public void destroy() {
        prepare.cleanupDispatcher();
    }
}
