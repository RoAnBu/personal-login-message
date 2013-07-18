package com.gmail.fantasticskythrow.other;

public class MessageData {

	public String message;
	public String[] channels;
	public SectionTypes type = null;
	public SectionSubTypes subType = null;

	public MessageData(String message, String[] channels) {
		this.message = message;
		this.channels = channels;
	}

	public MessageData(String message, String[] channels, SectionTypes type, SectionSubTypes subType) {
		this.message = message;
		this.channels = channels;
		this.type = type;
		this.subType = subType;
	}
}
