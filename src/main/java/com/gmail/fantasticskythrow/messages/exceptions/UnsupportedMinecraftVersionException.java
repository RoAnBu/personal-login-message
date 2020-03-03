package com.gmail.fantasticskythrow.messages.exceptions;

public class UnsupportedMinecraftVersionException extends RuntimeException
{
	public UnsupportedMinecraftVersionException() {
		super("Only versions newer than 1.7.8 are supported");
	}

	public UnsupportedMinecraftVersionException(String actualVersion) {
		super("Only versions newer than 1.7.8 are supported, got version: " + actualVersion);
	}
}
