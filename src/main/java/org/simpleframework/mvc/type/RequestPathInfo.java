package org.simpleframework.mvc.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 存储http请求路径和请求方法
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestPathInfo {
    private String httpMethod;
    private String httpPath;
}
