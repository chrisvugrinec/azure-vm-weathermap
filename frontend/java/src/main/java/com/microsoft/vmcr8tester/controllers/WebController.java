package com.microsoft.vmcr8tester.controllers;

import javax.servlet.http.HttpServletRequest;

import com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.model.RegionResults;
import com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.util.CosmoDBDataCollectService;
import com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.util.CosmoDBInterface;
import com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.util.RedisCacheInterface;
import com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.util.RedisDataCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

@ComponentScan(basePackages = "com.microsoft.vmcr8tester.util")
@Controller
public class WebController {


	@Autowired
	private CosmoDBInterface cosmoDBDataCollectService;

	@Autowired
	private RedisCacheInterface redisDataCollectService;



	@RequestMapping(value = "/vmcr8tester", method = { RequestMethod.GET, RequestMethod.POST })
	public String gotoTool(ModelMap model, HttpServletRequest httpRequest) {
		model.addAttribute("totalvms",cosmoDBDataCollectService.getTotalMachinesBuild());
		model.addAttribute("totalvmstoday",cosmoDBDataCollectService.getTotalMachinesBuildToday());

		List<RegionResults> rResults = cosmoDBDataCollectService.getMachineResults();
		for(RegionResults rResult : rResults ){
			model.addAttribute(rResult.getRegion(),rResult.getResult());

		}
		model.addAttribute("buildQ",redisDataCollectService.getQueue());

		return "vmcr8tester";
	}


}
