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

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

@Named("pullViewer")
@ApplicationScoped
public class PullViewer {
    private Pull rateLimited = new Pull("RATELIMITED", "RATELIMITED", "ERROR", "RATE LIMITED WARNING CACHED DATA");

    private List<String> urls = Arrays.asList(new String[] {
            "https://api.github.com/repos/jbosstm/narayana/pulls",
            "https://api.github.com/repos/jbosstm/quickstart/pulls",
            "https://api.github.com/repos/jbosstm/documentation/pulls",
            "https://api.github.com/repos/jbosstm/jboss-transaction-spi/pulls",
            "https://api.github.com/repos/jbosstm/jboss-as/pulls",
            "https://api.github.com/repos/jbosstm/jboss-transaction-spi/pulls",
            "https://api.github.com/repos/jbosstm/narayana.io/pulls",
            "https://api.github.com/repos/jbosstm/performance/pulls",
            "https://api.github.com/repos/jboss-dockerfiles/narayana/pulls",
            "https://api.github.com/repos/web-servers/narayana-tomcat/pulls",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Awildfly/wildfly+type%3Apr+author%3Azhfeng",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Awildfly/wildfly+type%3Apr+author%3Ammusgrov",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Awildfly/wildfly+type%3Apr+author%3Aochaloup",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Awildfly/wildfly+type%3Apr+author%3Atomjenkinson",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Ajboss-openshift/cct_module+type%3Apr+author%3Azhfeng",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Ajboss-openshift/cct_module+type%3Apr+author%3Ammusgrov",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Ajboss-openshift/cct_module+type%3Apr+author%3Aochaloup",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Ajboss-openshift/cct_module+type%3Apr+author%3Atomjenkinson"
    });

    private List<Pull> lastPulls = new ArrayList<Pull>();
    private SimpleDateFormat sdf;
    private long lastChecked;
    private String basic;

    @PostConstruct
    public void init () {
        sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/London"));
    }

    // JSF will call this multiple times so only attempt if older than 10 seconds
    public synchronized List<Pull> getPulls() throws IOException {

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastChecked < 10000) {
            return lastPulls;
        }
        lastChecked = currentTime;
        try {
            List<Pull> pulls = new ArrayList<Pull>();
            for (String project : urls) {
                URL url = new URL( project);
                URLConnection connection = url.openConnection();
                if (basic == null) {
                    File file = new File("creds");
                    if (file.exists()) {
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String username = reader.readLine();
                        String password = reader.readLine();
                        basic = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());
                    } else {
                        System.out.println("Still looking for: " + file.getAbsolutePath());
                    }
                }
                if (basic != null) {
                    connection.setRequestProperty ("Authorization", basic);
                }

                String rateReset = connection.getHeaderField("X-RateLimit-Reset");
                Date reset = new Date(Integer.parseInt(rateReset) * 1000L);
                rateLimited.setDescription("RATE LIMITED WARNING CACHED DATA - rate will reset at " + sdf.format(reset));

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
                JSON json = JSONSerializer.toJSON(string);
                if (!json.isArray())
                {
                    json = (JSON) ((JSONObject)json).get("items");
                }
                Iterator iterator = ((JSONArray)json).iterator();
                while (iterator.hasNext()) {
                    JSONObject next = (JSONObject) iterator.next();
                    String title = next.getString("title");
                    String pullUrl = next.getString("url").replace(
                            "api.github.com/repos/" + project + "/pulls",
                            "github.com/" + project + "/pull");
                    String jiraUrl = "https://issues.jboss.org/browse/"
                            + title.substring(0, Math.min(
                            title.indexOf(' ') > 0 ? title.indexOf(' ')
                                    : title.length(),
                            title.indexOf('.') > 0 ? title.indexOf('.')
                                    : title.length()));
                    String description = title.substring(title.indexOf(" ") + 1);
                    pulls.add(new Pull(project, pullUrl, jiraUrl, description));

                }
            }
            lastPulls.clear();
            lastPulls.addAll(pulls);
        } catch (Throwable t) {
            basic = null;
            t.printStackTrace(); // This is probably rate limit
            if (!lastPulls.contains(rateLimited)) {
                lastPulls.add(0, rateLimited);
            }
        }

        return lastPulls;
    }

//    public static void main (String[] args) throws IOException {
//        PullViewer viewer = new PullViewer();
//        viewer.init();
//        List<Pull> pulls = viewer.getPulls();
//        for (Pull pull : pulls) {
//            System.out.println(pull.getDescription());
//        }
//    }
}
