package com.gwtent.client.test.uibinder;

import com.google.gwt.user.client.ui.TextBox;
import com.gwtent.client.template.HTMLTemplate;
import com.gwtent.client.template.HTMLTemplatePanel;
import com.gwtent.client.template.HTMLWidget;
import com.gwtent.client.template.UIBind;
import com.gwtent.client.uibinder.GWTUIBinderRegister;
import com.gwtent.client.uibinder.ModelRootAccessor;
import com.gwtent.client.uibinder.UIBinderGWTFactory;
import com.gwtent.client.uibinder.UIBinderManager;
import com.gwtent.client.uibinder.UIBinderManager.ModelCallback;

@HTMLTemplate("testhtml.html")
public class EditScreenUIBind extends HTMLTemplatePanel {

  //for now, models in path must can be accessed by sub class
  TestModel testModel = null;

  public EditScreenUIBind(String html) {
    super(html);
    
    GWTUIBinderRegister.register();
    
    testModel = new TestModel();
    testModel.setFirstName("first name set by code");
    
  }
  
  @HTMLWidget
  @UIBind(path="testModel.firstName")
  TextBox txtFirstName = new TextBox();
  
  @HTMLWidget
  @UIBind(path="varToBind")
  TextBox txtBindToVariable = new TextBox();
  
  public TextBox getTxtBindToVariable() {
    return txtBindToVariable;
  }

  public void setTxtBindToVariable(TextBox txtBindToVariable) {
    this.txtBindToVariable = txtBindToVariable;
  }

  String varToBind = "abc";

  public TextBox getTxtFirstName() {
    return txtFirstName;
  }

  public void setTxtFirstName(TextBox txtFirstName) {
    this.txtFirstName = txtFirstName;
  }

  public void setTestModel(TestModel testModel) {
    this.testModel = testModel;
  }

  public TestModel getTestModel() {
    return testModel;
  }

  public void setVarToBind(String varToBind) {
    this.varToBind = varToBind;
  }

  public String getVarToBind() {
    return varToBind;
  }

}