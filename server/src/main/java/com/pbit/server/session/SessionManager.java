package com.pbit.server.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
	private Map<String,ISession> _SessionMap = new HashMap<String, ISession>();
	
	public void addSession(ISession session){
		_SessionMap.put(session.getId(),session);
	}
	public ISession getSession(String key){
		return _SessionMap.get(key);
	}
	
	
}
