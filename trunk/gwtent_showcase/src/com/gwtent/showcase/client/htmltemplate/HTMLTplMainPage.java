package com.gwtent.showcase.client.htmltemplate;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.DeckPanel;
import com.gwtent.client.template.HTMLEvent;
import com.gwtent.client.template.HTMLTemplatePanel;
import com.gwtent.client.template.HTMLWidget;
import com.gwtent.showcase.client.ContentWidget;
import com.gwtent.showcase.client.SubContent;
import com.gwtent.showcase.client.domain.UserFactory;

@MyHTMLTemplate("HtmlTemplate.html")
public class HTMLTplMainPage extends HTMLTemplatePanel {

	public HTMLTplMainPage(String html) {
		super(html);
	}

	@HTMLWidget
	protected SubContent subContentTpl = new SubContent();
	
	
	@HTMLEvent(value = {"linkTplOverview"})
	protected void doHomeClick(){
		
	}
	
	
	@HTMLEvent(value = {"linkTplBasic"})
	protected void doHTMLTemplateClick(){
		if (basicPage == null){
			basicPage = GWT.create(HTMLTplBasicPage.class);
			subContentTpl.addShowCase(basicPage);
		}
		
		subContentTpl.showShowCase(basicPage);
	}
	
	@HTMLEvent(value = {"linkTplUIBind"})
	protected void doHTMLTemplateUIBindClick(){
		if (uibindPage == null){
			uibindPage = GWT.create(HTMLTplUIBindPage.class);
			subContentTpl.addShowCase(uibindPage);
		}
		
		//Get some object from server, and refresh the UI
		uibindPage.setUser(UserFactory.getInstance().getJames());
		uibindPage.modelChanged();
		
		subContentTpl.showShowCase(uibindPage);
	}
	
	
	private HTMLTplBasicPage basicPage;
	private HTMLTplUIBindPage uibindPage;
}