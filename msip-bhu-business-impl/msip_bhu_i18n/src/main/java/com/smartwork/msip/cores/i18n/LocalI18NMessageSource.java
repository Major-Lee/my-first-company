package com.smartwork.msip.cores.i18n;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class LocalI18NMessageSource {
	MessageSource messageSource;
	
	private static LocalI18NMessageSource instance;
	private LocalI18NMessageSource() {
		//instance = this;
	}
	
	public static LocalI18NMessageSource getInstance(){
		if(instance == null){
			instance = new LocalI18NMessageSource();
		}
        return instance;
    }
	
	public String getMessage(String messageKey,Object[] texts, Locale locale){
		//Locale.CHINESE
		return messageSource.getMessage(messageKey, texts, locale);//LocaleContextHolder.getLocale());
	}
	
	public String getMessage(String messageKey,Object[] texts){
		//Locale.CHINESE
		return messageSource.getMessage(messageKey, texts, Locale.CHINA);//LocaleContextHolder.getLocale());
	}
	
	public String getMessage(String messageKey){
		return getMessage(messageKey,null);
	}
	public MessageSource getMessageSource() {
		return messageSource;
	}
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public static void main(String[] argv){
		System.out.println(Locale.CHINA.toString());
		System.out.println(Locale.CHINESE.toString());
		System.out.println(LocaleContextHolder.getLocale().toString());
		
	}
}
