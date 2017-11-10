package com.microsoft.vmcr8tester;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.util.DataCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.microsoft.vmcr8tester.auth.AdalFilter;

@SpringBootApplication
public class Application {

	private @Autowired AutowireCapableBeanFactory beanFactory;
	private static final Logger logger = Logger.getLogger(Application.class.getName());

	@Value("${authenticatedpaths}")
	private String authenticatedpaths;

	@Value("${cosmoDBserviceEndpoint}")
	private String cosmoDBserviceEndpoint;

	@Value("${cosmoDBmasterkey}")
	private String cosmoDBmasterkey;


	public static void main(String[] args) throws Throwable {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public FilterRegistrationBean adalFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		AdalFilter adalFilter = new AdalFilter();
		beanFactory.autowireBean(adalFilter);
		registration.setFilter(adalFilter);
		for (String path : authenticatedpaths.split(",")) {
			registration.addUrlPatterns(path);
			logger.log(Level.INFO, "adding path: {0} to path", path);
		}
		registration.setOrder(1);
		return registration;
	}

	@Bean
	public DataCollector dataCollector() {
		return new DataCollector(cosmoDBserviceEndpoint,cosmoDBmasterkey);
	}



}
