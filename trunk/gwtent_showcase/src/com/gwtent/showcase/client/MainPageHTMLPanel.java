package com.gwtent.showcase.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Label;
import com.gwtent.client.template.HTMLEvent;
import com.gwtent.client.template.HTMLTemplate;
import com.gwtent.client.template.HTMLTemplatePanel;
import com.gwtent.client.template.HTMLWidget;
import com.gwtent.showcase.client.aop.AOPMainPage;
import com.gwtent.showcase.client.htmltemplate.HTMLTplMainPage;
import com.gwtent.showcase.client.reflection.ReflectionPage;
import com.gwtent.showcase.client.uibinder.UIBinderPage;

@HTMLTemplate("MainPageHTMLPanel.html")
public class MainPageHTMLPanel extends HTMLTemplatePanel {

	public MainPageHTMLPanel(String html) {
		super(html);
	}

	@HTMLWidget
	protected DeckPanel content = new DeckPanel();
	
	
	@HTMLEvent(value = {"linkHome", "linkValidation"})
	protected void doHomeClick(){
		Window.alert("Not finish yet");
	}
	
	@HTMLEvent(value = {"linkEnglish"})
	protected void dolinkEnglishClick(){
		Window.alert("English");
	}
	
	@HTMLEvent(value = {"linkReflection"})
	protected void doReflectionClick(){
		if (tplReflection == null){
			tplReflection = GWT.create(ReflectionPage.class);
			content.add(tplReflection);
		}
		
		content.showWidget(content.getWidgetIndex(tplReflection));
	}
	
	@HTMLEvent(value = {"linkUIBinding"})
	protected void doUIBindingClick(){
		if (tplUIBinder == null){
			tplUIBinder = GWT.create(UIBinderPage.class);
			content.add(tplUIBinder);
		}
		
		content.showWidget(content.getWidgetIndex(tplUIBinder));
	}
	
	@HTMLEvent(value = {"linkHTMLTemplate"})
	protected void doHTMLTemplateClick(){
		if (tplMainPage == null){
			tplMainPage = GWT.create(HTMLTplMainPage.class);
			content.add(tplMainPage);
		}
		
		content.showWidget(content.getWidgetIndex(tplMainPage));
	}
	
	@HTMLEvent(value = {"linkAOP"})
	protected void doAOPClick(){
		if (aopMainPage == null){
			aopMainPage = GWT.create(AOPMainPage.class);
			content.add(aopMainPage);
		}
		
		content.showWidget(content.getWidgetIndex(aopMainPage));
	}

	private HTMLTplMainPage tplMainPage;
	private ReflectionPage tplReflection;
	private UIBinderPage tplUIBinder;
	private AOPMainPage aopMainPage;
}