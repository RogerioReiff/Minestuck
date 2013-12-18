package com.mraof.minestuck.network.skaianet;

import static com.mraof.minestuck.network.skaianet.SkaianetHandler.getAssociatedPartner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

import com.mraof.minestuck.Minestuck;

/**
 * Was also an interface for the session system, but now just a data structure representing a session.
 * SessionHandler is the new class for session interface.
 * @author kirderf1
 */
public class Session {
	
	/**
	 * 
	 */
	List<SburbConnection> connections;
	
	/**
	 * If the "connection circle" is whole, unused if globalSession == true.
	 */
	boolean completed;
	
	//Unused, will later be 0 if not yet generated
	int skaiaId;
	int prospitId;
	int derseId;
	
	/**
	 * Checks if the variable completed should be true or false.
	 */
	void checkIfCompleted(){
		if(connections.isEmpty() || SessionHandler.singleSession){
			completed = false;
			return;
		}
		String start = connections.get(0).getClientName();
		String current = start;
		while(true){
			for(SburbConnection c : connections)
				if(c.getServerName().equals(current)){
					current = c.getClientName();
					if(start.equals(current)){
						completed = true;
						return;
					} else continue;
				}
			completed = false;
			return;
		}
	}
	
	Session(){
		connections = new ArrayList<SburbConnection>();
	}
	
	/**
	 * Checks if a certain player is in the connection list.
	 * @param s The username of the player.
	 * @return If the player was found.
	 */
	boolean containsPlayer(String s){
		return getPlayerList().contains(s);
	}
	
	/**
	 * Creates a list with all players in the session.
	 * @return Returns a list with the players usernames.
	 */
	List<String> getPlayerList(){
		List<String> list = new ArrayList();
		for(SburbConnection c : this.connections){
			if(!list.contains(c.getClientName()))
				list.add(c.getClientName());
			if(!list.contains(c.getServerName()))
				list.add(c.getServerName());
		}
		return list;
	}
	
	/**
	 * Writes this session to an nbt tag.
	 * Note that this will only work as long as <code>SkaianetHandler.connections</code> remains unmodified.
	 * @return An NBTTagCompound representing this session.
	 */
	NBTTagCompound write() {
		NBTTagCompound nbt = new NBTTagCompound();
		int[] list = new int[connections.size()];
		for(int i = 0; i < list.length; i++)
			list[i] = SkaianetHandler.connections.indexOf(connections.get(i));
		nbt.setIntArray("connections", list);
		nbt.setInteger("skaiaId", skaiaId);
		nbt.setInteger("derseId", derseId);
		nbt.setInteger("prospitId", prospitId);
		return nbt;
	}
	
	/**
	 * Reads data from the given nbt tag.
	 * @param nbt An NBTTagCompound to read from.
	 * @return This.
	 */
	Session read(NBTTagCompound nbt) {
		if(nbt.getTag("connections") instanceof NBTTagIntArray)	//New nbt format
			for(int i : nbt.getIntArray("connections"))
				connections.add(SkaianetHandler.connections.get(i));
		else {	//old nbt format
			NBTTagList list = nbt.getTagList("connections");
			for(int i = 0; i < list.tagCount(); i++)
				connections.add(new SburbConnection().read((NBTTagCompound) list.tagAt(i)));
			SkaianetHandler.connections.addAll(this.connections);
		}
		checkIfCompleted();
		return this;
	}
	
	SburbConnection getConnection(String client, String server){
		for(SburbConnection c : connections)
			if(c.getClientName().equals(client) && c.getServerName().equals(server))
				return c;
		return null;
	}
	
}
