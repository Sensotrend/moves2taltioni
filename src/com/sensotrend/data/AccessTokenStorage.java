package com.sensotrend.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.AccessToken;

public class AccessTokenStorage {
	
	private static final AccessTokenStorage instance = new AccessTokenStorage();

	private final Map<String, Tokens> tokens = new HashMap<String, Tokens>();
	
	/**
	 *  Private constructor for the singleton instance.
	 */
	private AccessTokenStorage() {
		
	}
	
	public static AccessTokenStorage getInstance() {
		return instance;
	}

	public String storeToken(String token) {
		if (token == null) {
			throw new NullPointerException();
		}
		if (tokens.containsValue(token)) {
			// issue a warning! this should not happen, and is a slow operation.
			Iterator<Map.Entry<String, Tokens>> iterator = tokens.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Tokens> entry = iterator.next();
				if (token.equals(entry.getValue())) {
					return entry.getKey();
				}
			}
			// if we end up here, someone just removed the entry, let's add it back
		}
		String key = UUID.randomUUID().toString();
		
		Tokens t = new Tokens(token);
		tokens.put(key, t);
		return key;
	}
	
	public String getTokenString(String key) {
		return tokens.get(key).taltioniToken.getAccessToken();
	}
	
	public AccessToken getToken(String key) {
		return tokens.get(key).taltioniToken;
	}
	
	public boolean containsTokenFor(String key) {
		if (tokens.containsKey(key)) {
			return true;
		}
		return false;
	}
	
	public void setMovesToken(String key, String movesToken) {
	    tokens.get(key).movesToken = movesToken;
	}
	
	public String getMovesToken(String key) {
	    return tokens.get(key).movesToken;
	}
	
	private class Tokens {
	    final AccessToken taltioniToken;
	    String movesToken = null;
	    
	    public Tokens(String taltioniTokenString) {
	        taltioniToken = new AccessToken();
	        taltioniToken.setAccessToken(taltioniTokenString);
	    }
	}
}
