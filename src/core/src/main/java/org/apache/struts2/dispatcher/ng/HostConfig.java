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
package org.apache.struts2.dispatcher.ng;

import javax.servlet.ServletContext;
import java.util.Iterator;

/**
 * Abstraction for host configuration information such as init params or the servlet context.
 * 为初始化主机配置信息，如初始化参数或servlet上下文的抽象接口
 */
public interface HostConfig {

    /**
     * 根据key值获取到初始化的属性value值
     * @param key The parameter key
     * @return The parameter value
     */
    String getInitParameter(String key);

    /**
     * 获取初始化属性的迭代器
     * @return A list of parameter names
     */
    Iterator<String> getInitParameterNames();

    /**
     * 获取servlet上下文对象
     * @return The servlet context
     */
    ServletContext getServletContext();
}
