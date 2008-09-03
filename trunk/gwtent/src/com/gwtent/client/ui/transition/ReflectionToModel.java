/*
 * GwtEnt - Gwt ent library.
 * 
 * Copyright (c) 2007, James Luo(JamesLuo.au@gmail.com)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.gwtent.client.ui.transition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.gwtent.client.reflection.ClassType;
import com.gwtent.client.reflection.Field;
import com.gwtent.client.reflection.HasMetaData;
import com.gwtent.client.reflection.Method;
import com.gwtent.client.reflection.Reflection;
import com.gwtent.client.reflection.Type;
import com.gwtent.client.ui.ClassTypeHelper;
import com.gwtent.client.ui.Utils;
import com.gwtent.client.ui.model.Action;
import com.gwtent.client.ui.model.Domain;
import com.gwtent.client.ui.model.Value;
import com.gwtent.client.ui.model.impl.ActionImpl;
import com.gwtent.client.ui.model.impl.DomainImpl;
import com.gwtent.client.ui.model.impl.FieldImpl;
import com.gwtent.client.ui.model.value.ValueFactory;

/**
 * Improve reflection support to pojo
 * 
 * 1, Class level
 *
 * 
 * @author James Luo
 * 2007-12-27 下午08:50:52
 *
 */
public class ReflectionToModel implements POJOToModel {
	
	private ValueFactory valueFactory;
	private Object pojo = null;
	
	public ReflectionToModel(ValueFactory valueFactory){
		this.valueFactory = valueFactory;
	}

	public ValueFactory getValueFactory() {
		return valueFactory;
	}


	public void setValueFactory(ValueFactory valueFactory) {
		this.valueFactory = valueFactory;
	}


	public Domain createModel(Object pojo, ClassType classType) throws TransitionException {
		this.pojo = pojo;
		
		if (pojo instanceof Reflection){
			//pojo.getClass()
			//ClassType classType = (ClassType)GWT.create(clasz);
			Domain domain = new DomainImpl();
			domain.setInstance(pojo);
			domain.setCaption(ClassTypeHelper.getAllMetaData(classType, ClassTypeHelper.CLASS_CAPTION_METADATA));
			addFields(domain, classType);
			addActions(domain, classType);
			return domain;
		}else{
			throw new TransitionException("pojo must reflectable when use ReflectionToModel");
		}
	}
	
	protected void addActions(Domain domain, ClassType classType){
		List actionNames = new ArrayList();
		Utils.addListByStrIFNotExists(getActionNamesByClassMetaData(classType), actionNames);
		Utils.addListByStrIFNotExists(getActionNamesByMethodMetaData(classType), actionNames);
		
		Iterator iterator = actionNames.iterator();
		while (iterator.hasNext()){
			String actionName = (String)iterator.next();
			
			Action action = new ActionImpl(classType, domain, actionName);
			domain.addAction(action);
		}
	}
	
	
	protected void addFields(Domain domain, ClassType classType){
		List fieldNames = new ArrayList();
		Utils.addListByStrIFNotExists(getFieldNamesByClassMetaData(classType), fieldNames);
		Utils.addListByStrIFNotExists(getFieldNamesByFieldMetaData(classType), fieldNames);
		
		Iterator iterator = fieldNames.iterator();
		while (iterator.hasNext()){
			String fieldName = (String)iterator.next();
			addField(domain, classType, fieldName);
		}
	}
	
	protected com.gwtent.client.ui.model.Field addField(Domain domain, ClassType classType, String fieldName){
		HasMetaData metaData = null;
		String typeName = "";
		Field srcField = classType.findField(fieldName);
		if (srcField != null){
			metaData = srcField;
			typeName = srcField.getTypeName();
		}else{  
			Method srcMethod = classType.findMethod(fieldName, new Type[]{});
			if (srcMethod != null){
				metaData = srcMethod;
				typeName = srcMethod.getReturnTypeName();
			}
		}
		
		if (metaData != null){
			Value value = valueFactory.factory(pojo, classType, fieldName, typeName);
			if (value == null){
				throw new TransitionException("value factory return null, is type:" + typeName + "have correct value type.");
			}
			com.gwtent.client.ui.model.Field destField = new FieldImpl();
			updateFieldByMetaData(destField, classType, metaData, fieldName);
			//TODO destField.setRequire(false); set require by validate
			destField.setRequire(false);
			destField.setValue(value);
			domain.addField(destField);
			return destField;
		}else{
			return null;
		}
	}
	
	protected void updateFieldByMetaData(com.gwtent.client.ui.model.Field field, ClassType classType, HasMetaData metaData, String name){
		String caption = ClassTypeHelper.getCaption(classType, pojo, metaData);
		if (caption.length() <= 0) caption = name;
		field.setCaption(caption);
		
		String desc = ClassTypeHelper.getDesc(classType, pojo, metaData);
		field.setDesc(desc);
	}


	private List getFieldNamesByClassMetaData(ClassType classType) {
		List result = new ArrayList();
		Utils.addListByStrIFNotExists(classType.getMetaDataMerge(ClassTypeHelper.CLASS_FIELD_LIST_METADATA), result);
		return result;
	}
	
	private List getActionNamesByClassMetaData(ClassType classType) {
		List result = new ArrayList();
		Utils.addListByStrIFNotExists(classType.getMetaDataMerge(ClassTypeHelper.CLASS_ACTION_LIST_METADATA), result);
		return result;
	}
	
	
	private List getFieldNamesByFieldMetaData(ClassType classType) {
		List result = new ArrayList();
		Field[] fields = classType.getFields();
		for (int i = 0; i < fields.length; i++){
			Field field = fields[i];
			if (ClassTypeHelper.queryHaveMetaData(field, ClassTypeHelper.FIELD_FLAG_METADATA)){
				result.add(field.getName());
			} 
		}
		
		Method[] methods = classType.getMethods();
		for (int i = 0; i < methods.length; i++){
			Method method = methods[i];
			if (ClassTypeHelper.queryHaveMetaData(method, ClassTypeHelper.FIELD_FLAG_METADATA)){
				result.add(method.getName());
			} 
		}
		
		return result;
	}
	
	private List getActionNamesByMethodMetaData(ClassType classType) {
		List result = new ArrayList();
		
		Method[] methods = classType.getMethods();
		for (int i = 0; i < methods.length; i++){
			Method method = methods[i];
			if (ClassTypeHelper.queryHaveMetaData(method, ClassTypeHelper.ACTION_FLAG_METADATA)){
				result.add(method.getName());
			} 
		}
		
		return result;
	}

}
