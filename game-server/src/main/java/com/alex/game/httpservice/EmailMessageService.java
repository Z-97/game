package com.alex.game.httpservice;

import java.util.List;

import com.alex.game.server.http.HttpService;
import com.alex.game.server.http.HttpServiceRes;

public class EmailMessageService implements HttpService {

	@Override
	public String getName() {
		return "EmailMessageService";
	}

	@Override
	public HttpServiceRes service(List<String> args) {
		return null;
	}

}
