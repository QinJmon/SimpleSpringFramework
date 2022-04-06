package com.imooc;

import com.imooc.entity.bo.HeadLine;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Slf4j
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {

        System.out.println("初始化Servlet...");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("我是入口，我执行了doget方法");
        doGet(req, resp);

    }

    //final static Logger log= LoggerFactory.getLogger(HelloServlet.class);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name="简易的spring框架";
        log.debug("name is"+name);
        req.setAttribute("name",name);
        req.getRequestDispatcher("/WEB-INF/jsp/hello.jsp").forward(req,resp);
        HeadLine headLine=new HeadLine();

    }

    @Override
    public void destroy() {

        System.out.println("销毁");
    }
}




