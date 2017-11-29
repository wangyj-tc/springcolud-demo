package com.suxintec.util;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 校验的一些公共类
 * @author dell
 *
 */
public class VerifyUtil {
	
	private static Logger logger = LoggerFactory.getLogger(VerifyUtil.class);
	
	private static ThreadLocal<Boolean> flag = new ThreadLocal<Boolean>(){
		protected Boolean initialValue() {
			return true;
		};
	};

	public static boolean exitsNullParams(Object object) throws Exception {
		Method[] methods = object.getClass().getMethods();
		try {
			if ((methods != null) && (methods.length > 0)) {
				for (Method method : methods) {
					if (method.getName().startsWith("get")) {
						StatusDiscription status = method.getAnnotation(StatusDiscription.class);
						if(status!=null&&status.recursion()){
							if(method.invoke(object, new Object[0])==null){
								flag.set(false);
							}else{
								exitsNullParams(method.invoke(object, new Object[0]));
							}
							
						}
						if (status!=null&&status.notNull()) {
							Object o = method.invoke(object, new Object[0]);
							if (o instanceof java.lang.String) {
								if(StringUtils.isBlank((String)o)){
									logger.error(method.getName()+"参数为空");
									flag.set(false);
								}
							}else{
								if(o==null){
									logger.error(method.getName()+"参数为空");
									flag.set(false);
								}
							}
							
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("{}","验证类参数是否存在空值异常",e);
			throw new Exception("");
		}

		return flag.get();
	}
	

}
