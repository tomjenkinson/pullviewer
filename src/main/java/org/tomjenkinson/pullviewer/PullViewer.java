package org.tomjenkinson.pullviewer;

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

@Named("pullViewer")
@ApplicationScoped
public class PullViewer {

	public synchronized List<Pull> getPulls() throws IOException {

		List<Pull> pulls = new ArrayList<Pull>();

		for (String project : new String[] { "jbosstm/narayana", "jbosstm/quickstart", "jbosstm/documentation", "jbosstm/jboss-transaction-spi", "jbosstm/jboss-as", "jbosstm/jboss-transaction-spi", "jbosstm/narayana.io", "jbosstm/performance", "jboss-dockerfiles/narayana" }) {
			URL url = new URL("https://api.github.com/repos/" + project + "/pulls");
			URLConnection connection = url.openConnection();
			connection.connect();
			InputStream is = connection.getInputStream();
			int ptr = 0;
			StringBuffer buffer = new StringBuffer();
			List<String> ids = new ArrayList<String>();
			while ((ptr = is.read()) != -1) {
				buffer.append((char) ptr);
			}
			is.close();
			String string = buffer.toString();
			JSONArray json = (JSONArray) JSONSerializer.toJSON(string);
			Iterator iterator = json.iterator();
			while (iterator.hasNext()) {
				JSONObject next = (JSONObject) iterator.next();
				String title = next.getString("title");
				String pullUrl = next.getString("url").replace(
						"api.github.com/repos/jbosstm/" + project + "/pulls",
						"www.github.com/jbosstm/" + project + "/pull");
				String jiraUrl = "https://issues.jboss.org/browse/"
						+ title.substring(0, Math.min(
								title.indexOf(' ') > 0 ? title.indexOf(' ')
										: title.length(),
								title.indexOf('.') > 0 ? title.indexOf('.')
										: title.length()));
				String description = title.substring(title.indexOf(' '));
				pulls.add(new Pull(pullUrl, jiraUrl, description));

			}
		}

		return pulls;
	}
}
