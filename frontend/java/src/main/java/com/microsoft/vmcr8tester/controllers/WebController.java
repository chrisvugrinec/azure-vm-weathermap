package com.microsoft.vmcr8tester.controllers;

import javax.servlet.http.HttpServletRequest;

import com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.model.RegionResults;
import com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.util.DataCollectorInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@ComponentScan(basePackages = "com.microsoft.vmcr8tester.util")
@Controller
public class WebController {


	@Autowired
	private DataCollectorInterface dataCollector;

	@RequestMapping(value = "/vmcr8tester", method = { RequestMethod.GET, RequestMethod.POST })
	public String gotoTool(ModelMap model, HttpServletRequest httpRequest) {
		model.addAttribute("totalvms",dataCollector.getTotalMachinesBuild());
		model.addAttribute("totalvmstoday",dataCollector.getTotalMachinesBuildToday());

		List<RegionResults> rResults = dataCollector.getMachineResults();
		for(RegionResults rResult : rResults ){
			model.addAttribute(rResult.getRegion(),rResult.getResult());

		}
		return "vmcr8tester";
	}


}
