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


package com.gwtent.client.ui.transition;

import com.gwtent.client.reflection.ClassType;
import com.gwtent.client.reflection.Reflection;
import com.gwtent.client.ui.model.Domain;
import com.gwtent.client.ui.model.value.ValueFactoryImpl;

public class POJOToModelImpl implements POJOToModel {
	
	public Domain createModel(Object pojo, ClassType classType) {
		if (pojo instanceof Reflection){
			POJOToModel transition = new ReflectionToModel(ValueFactoryImpl.getInstance());
			return transition.createModel(pojo, classType);
		}else{
			throw new TransitionException("Cann't create fields of class, dose this class implement Refection or UIFriendly interface?");
		}
	}

}
