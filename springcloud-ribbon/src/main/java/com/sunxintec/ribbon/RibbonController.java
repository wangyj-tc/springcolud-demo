
package com.sunxintec.ribbon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RibbonController {
	@Autowired
	private HelloService helloService;
     @RequestMapping("/hi")
	public String hi(String name) {
		return helloService.hi(name);
	}

}
