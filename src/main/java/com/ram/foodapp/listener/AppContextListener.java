package com.ram.foodapp.listener;

import com.ram.foodapp.config.AppConfig;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        sce.getServletContext().setAttribute("springContext", context);
    }
}
