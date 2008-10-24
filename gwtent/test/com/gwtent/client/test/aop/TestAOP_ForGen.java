package com.gwtent.client.test.aop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gwtent.client.aop.AspectException;
import com.gwtent.client.aop.intercept.MethodInterceptor;
import com.gwtent.client.aop.intercept.MethodInvocation;
import com.gwtent.client.aop.intercept.impl.MethodInterceptorFinalAdapter;
import com.gwtent.client.aop.intercept.impl.MethodInvocationAdapter;
import com.gwtent.client.aop.intercept.impl.MethodInvocationLinkedAdapter;
import com.gwtent.client.reflection.ClassType;
import com.gwtent.client.reflection.Method;
import com.gwtent.client.reflection.TypeOracle;
import com.gwtent.client.reflection.impl.MethodImpl;
import com.gwtent.client.test.aop.AOPTestCase.Receiver;

public class TestAOP_ForGen extends AOPTestCase.Phone {
	private static class InterceptorMap{
		static Map<Method, String> interceptors = new HashMap<Method, String>();
		
		static {
			interceptors.put(classType.findMethod("call", new String[]{"java.lang.Number"}), "com.gwtent.client.test.aop.TestMatcher");
		}
	}
	
	private static final ClassType classType = TypeOracle.Instance.getClassType(AOPTestCase.Phone.class);
	
	private final MethodInvocationLinkedAdapter Ivn_call;
	
	public TestAOP_ForGen(){
		
		Method method = null;
		//Do loop...
		method = classType.findMethod("call", new String[]{"java.lang.Number"});
		List<MethodInterceptor> interceptors = new ArrayList<MethodInterceptor>();
		interceptors.add(new AOPTestCase.PhoneLoggerInterceptor());
		interceptors.add(new AOPTestCase.PhoneRedirectInterceptor());
		interceptors.add(new MethodInterceptorFinalAdapter());
		Ivn_call = new MethodInvocationLinkedAdapter(method, this, interceptors);
	}
	

	public Receiver call(Number number) {
		if (Ivn_call.getCurrentInterceptor() instanceof MethodInterceptorFinalAdapter){
			return super.call(number);
		}
		
		Object[] args = new Object[]{number};
		Ivn_call.reset(args);
		try {
			return (Receiver) Ivn_call.proceed();
		} catch (Throwable e) {
			throw new AspectException(e);
		}
	}
}
