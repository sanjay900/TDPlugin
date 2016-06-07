package com.sanjay900.TD;

import java.util.LinkedHashMap;

import org.bukkit.entity.Player;

import com.dsh105.holoapi.HoloAPI;

public class HoloVisible implements com.dsh105.holoapi.api.visibility.Visibility{

	@Override
	public boolean isVisibleTo(Player player, String hologramId) {
		if (HoloAPI.getManager().getHologram(hologramId) != null) {

		return (player.getLocation().distance(HoloAPI.getManager().getHologram(hologramId).getDefaultLocation()) < 10);
		}
		return true;
	}

	@Override
	public LinkedHashMap<String, Object> getDataToSave() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSaveKey() {
		// TODO Auto-generated method stub
		return null;
	}

}
