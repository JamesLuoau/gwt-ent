package com.gwtent.test.aspectj;

import java.lang.reflect.Method;

import com.gwtent.aop.matcher.ClassMethodMatcher;
import com.gwtent.aop.matcher.Matcher;
import com.gwtent.aop.matcher.Matchers;
import com.gwtent.client.test.aop.Phone;

public class TestMatcher implements ClassMethodMatcher {

	public Matcher<? super Class<?>> getClassMatcher() {
		return Matchers.subclassesOf(Phone.class);
	}

	public Matcher<? super Method> getMethodMatcher() {
		return Matchers.returns(Matchers.only(Phone.Receiver.class));
		//return Matchers.any();
	}

}