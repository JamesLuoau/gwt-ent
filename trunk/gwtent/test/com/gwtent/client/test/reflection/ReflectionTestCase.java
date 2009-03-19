/*******************************************************************************
 *  Copyright 2001, 2007 JamesLuo(JamesLuo.au@gmail.com)
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 * 
 *  Contributors:
 *******************************************************************************/

package com.gwtent.client.test.reflection;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

import com.gwtent.client.reflection.ClassType;
import com.gwtent.client.reflection.Constructor;
import com.gwtent.client.reflection.Field;
import com.gwtent.client.reflection.Method;
import com.gwtent.client.reflection.Reflection;
import com.gwtent.client.reflection.TypeOracle;
import com.gwtent.client.reflection.impl.ClassTypeImpl;
import com.gwtent.client.test.annotations.Entity;
import com.gwtent.client.test.annotations.Id;
import com.gwtent.client.test.annotations.Table;

public class ReflectionTestCase extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.gwtent.GwtEntTest";
  }
  
  public void testCreateTypeOracle(){
	  ClassType classType = TypeOracle.Instance.getClassType(TestReflection.class);
	  assertNotNull(classType);
  }

  public void testObject(){
  	ClassType classType = TypeOracle.Instance.getClassType(Object.class);
  	assertNotNull(classType);
  	assertNotNull(classType.invoke(new Object(), "getClass", null));
  }
  
  public void testSuperClass(){
  	ClassType ctTestReflection = TypeOracle.Instance.getClassType(TestReflection.class);
  	ClassType ctObject = TypeOracle.Instance.getClassType(Object.class);
  	assertTrue(ctTestReflection.getSuperclass() == ctObject);
  }
  
  public void testImplementsInterfaces(){
  	ClassType ctTestReflection = TypeOracle.Instance.getClassType(TestReflection.class);
  	ClassType ctReflection = TypeOracle.Instance.getClassType(Reflection.class);
  	ClassType[] types = ctTestReflection.getImplementedInterfaces();
  	boolean found = false;
  	for (ClassType type : types){
  		if (type == ctReflection){
  			found = true;
  			break;
  		}
  	}
  	assertTrue(found);
  	assertTrue(types.length == 1);
  }
  
  private boolean fieldExists(String fieldName, Field[] fields){
  	for (Field field : fields){
  		if (field.getName().equals(fieldName))
  			return true;
  	}
  	
  	return false;
  }
  
  public void testFields() {
    TestReflection test = new TestReflection();
    test.setString("username");
    assertTrue(test.getString().equals("username"));
    
    ClassType classType = TypeOracle.Instance.getClassType(TestReflection.class);
    Field[] fields = classType.getFields();
    assertTrue(fieldExists("t", fields));
    assertTrue(fieldExists("names", fields));
    assertTrue(fieldExists("bool", fields));
    assertTrue(fieldExists("sets", fields));
    //assertTrue(classType.findField("bool").getType().getSimpleSourceName().equals("boolean"));
  }

  public void testAnnotations(){
    Annotation annotation = null;
    assertTrue(annotation.annotationType() == null);
    
    TestReflection test = new TestReflection();
    test.setString("username");
    assertTrue(test.getString().equals("username"));
    
    ClassType classType = TypeOracle.Instance.getClassType(TestReflection.class);
    //Class Annotations
    assertNotNull(classType.getAnnotation(Entity.class));
    assertTrue(classType.getAnnotation(Entity.class).getValue("name").equals("TestReflection"));
    assertTrue(classType.getAnnotation(Table.class).getValue("name").equals("Table_Test"));
    
    //Method Annotations
    assertNotNull(classType.findMethod("getId", new String[]{}).getAnnotation(Id.class));
    
    //Field Annotations
    assertNotNull(classType.findField("id").getAnnotation(Id.class));
  }

  private boolean hasMethod(String methodName, Method[] methods){
  	for (Method method : methods){
  		if (method.getName().equals(methodName))
  			return true;
  	}
  	
  	return false;
  }
  
  public void testMethods() {
  	ClassType classType = TypeOracle.Instance.getClassType(TestReflection.class);
  	Method[] methods = classType.getMethods();
  	for (Method method : methods)
  		System.out.println(method.toString());
  	System.out.println(methods.length);
  	
  	assertTrue(hasMethod("setT", methods));
  	assertTrue(hasMethod("getT", methods));
  	assertTrue(hasMethod("getString", methods));
  	assertTrue(hasMethod("getNames", methods));
  }

  public void testInvokeMethod() {
    TestReflection<Date> account = new TestReflection<Date>();
    account.setString("username");
    assertTrue(account.getString().equals("username"));
    
    ClassType classType = TypeOracle.Instance.getClassType(TestReflection.class);
    assertTrue(classType.invoke(account, "getString", null).equals("username"));
    classType.invoke(account, "setString", new String[]{"username set by reflection"});
    assertTrue(account.getString().equals("username set by reflection"));
    
    //Invoke generic functions 
    List<String> names = new ArrayList<String>();
    names.add("test1");
    names.add("test2");
    classType.invoke(account, "setNames", new Object[]{names});
    assertTrue(account.getNames().get(1).equals("test2"));
    
    Set<String> sets = new HashSet<String>();
    String string = "test1";
    sets.add(string);
    classType.invoke(account, "setSets", new Object[]{sets});
    assertTrue(account.getSets().contains(string));
    
    //Invoke generic functions which class declared
    assertTrue(account.getT() == null);
    Date date = new Date();
    classType.invoke(account, "setT", new Object[]{date});
    assertTrue(account.getT() == date);
  }

  public void testInheritance(){
  	
  }
  
  public void testConstructor(){
  	ClassType classType = TypeOracle.Instance.getClassType(TestReflection.class);
  	Constructor constructor = classType.findConstructor(new String[]{});
  	assertNotNull(constructor);
  	TestReflection obj = (TestReflection)constructor.newInstance();
  	obj.setId("test1");
  	
  	System.out.println(obj.getClass().getName());
  	System.out.println(TestReflection.class.getName());
  	assertTrue(obj.getClass().getName().equals(TestReflection.class.getName()));
  	assertTrue(obj.getId().equals("test1"));
  }
  
  public void testCustomTextBox(){
    ClassType classType = TypeOracle.Instance.getClassType(TextBox.class);
    TextBox t = new TextBox();
    t.setText("SetByCode");
    System.out.println(classType.getName());
    assertTrue(classType.getName().equals(TextBox.class.getName()));
    assertTrue(classType.invoke(t, "getText", null).equals("SetByCode"));
  }
  
  public void testGWTClass(){
  	Class c = TextBox.class;
  	ClassType type1= TypeOracle.Instance.getClassType(c);
  	assertTrue(type1.getName().equals(c.getName()));
  }
  
}
