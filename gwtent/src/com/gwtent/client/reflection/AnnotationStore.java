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


/**
 * FileName: Annotation.java
 * Author:		JamesLuo.au@gmail.com
 * purpose:
 * 
 * History:
 * 
 */


package com.gwtent.client.reflection;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface AnnotationStore {
  Class<? extends Annotation> annotationType();
  
  /**
   * Return the value of an annotation
   * @param name the name of function name
   * @return the value of this function, if not point in annotation, it's default value
   */
  public String getValue(String name);
  
  /**
   * Get annotation value as ClassType, this annotation must returns a class
   * For example: Class<? extends Constraint> value();
   * @param name the function name
   * @return the ClassType of the class
   */
  public ClassType getAsClassType(String name);
  
  /**
   * Get annotation value as Array of String, this annotation must returns an array
   * For exmpale: @Target({METHOD, FIELD}) will return {"METHOD", "FIELD"}
   * @param name
   * @return
   */
  public String[] getAsStringArray(String name);
  
  
  public Map<String, String> allValues(); 
  
}