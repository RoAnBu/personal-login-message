package com.gmail.fantasticskythrow.other;

import org.bukkit.plugin.Plugin;

import uk.org.whoami.geoip.GeoIPLookup;
import uk.org.whoami.geoip.GeoIPTools;

import com.gmail.fantasticskythrow.PLM;

public class PLMPluginConnector {

	private PLM plm;
	private Plugin essentials;
	private GeoIPLookup geoIPLookup;

	public PLMPluginConnector(PLM plm) {
		this.plm = plm;
		initPlugins();

	}

	private void initPlugins() {
		essentials = plm.getServer().getPluginManager().getPlugin("Essentials");
		Plugin gipt = plm.getServer().getPluginManager().getPlugin("GeoIPTools");
		if (gipt != null) {
			geoIPLookup = ((GeoIPTools) gipt).getGeoIPLookup();
		} else {
			geoIPLookup = null;
		}
	}

	public Plugin getEssentials() {
		return essentials;
	}

	/**
	 * Tries to find the plugin "GeoIPTools" and returns an GeoIPLookup Object which provides public methods for country and city information
	 * @return A GeoIPLookup object if GeoIPTools works. Otherwise it'll return null
	 */
	public GeoIPLookup getGeoIPLookup() {
		return geoIPLookup;
	}
}
