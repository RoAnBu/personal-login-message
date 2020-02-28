package com.gmail.fantasticskythrow.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class ExtendedYamlConfiguration extends YamlConfiguration {

	public static YamlConfiguration loadConfiguration(File file) throws IllegalStateException {
		Validate.notNull(file, "File cannot be null");

		YamlConfiguration config = new YamlConfiguration();

		try {
			config.load(file);
		} catch (FileNotFoundException ex) {
			// TODO error handling
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		} catch (InvalidConfigurationException ex) {
			throw new IllegalStateException(ex);
		}

		return config;
	}
}
