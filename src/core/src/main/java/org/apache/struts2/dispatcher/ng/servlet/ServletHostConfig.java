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
package org.apache.struts2.dispatcher.ng.servlet;

import org.apache.struts2.util.MakeIterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Iterator;

import org.apache.struts2.dispatcher.ng.HostConfig;

/**
 * Host configuration that wraps a ServletConfig
 * 包装ServletConfig对象的host配置类
 */
public class ServletHostConfig implements HostConfig {

    /**
     * ServletConfig对象，
     * 一个servlet对应一个ServletConfig对象,
     * web容器在创建servlet实例对象时，会自动将这些初始化参数封装到一个ServletConfig对象中，
     * 并在调用servlet的init方法时，将ServletConfig对象作为参数传递给servlet
     * 通过操作ServletConfig对象就可以得到当前servlet的初始化参数信息
     */
    private ServletConfig config;

    /**
     * 构造方法
     * @param config
     */
    public ServletHostConfig(ServletConfig config) {
        this.config = config;
    }

    /**
     * 根据key值获取到servlet配置的初始化的属性value值
     * @param key The parameter key
     * @return
     */
    public String getInitParameter(String key) {
        return config.getInitParameter(key);
    }

    /**
     * 获取servlet配置的初始化属性的迭代器
     * @return
     */
    public Iterator<String> getInitParameterNames() {
        return MakeIterator.convert(config.getInitParameterNames());
    }

    /**
     * 获取servlet对应的ServletConfig对象对应的serlvet上线文（一个web应用只有一个servlet上下文）
     * @return
     */
    public ServletContext getServletContext() {
        return config.getServletContext();
    }
}
