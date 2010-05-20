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


package com.gwtent.ui.client.generator;

import com.google.gwt.user.client.ui.Widget;
import com.gwtent.ui.client.model.Domain;


/**
 * UIGenerator auto generator ui for model
 * @author James Luo
 * 2007-12-21 上午11:05:13
 *
 */
public interface UIGenerator {
	public Widget generator(Domain fields);
}