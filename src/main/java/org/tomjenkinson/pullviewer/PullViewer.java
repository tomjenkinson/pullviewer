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

//import javax.annotation.PostConstruct;
//import javax.enterprise.context.ApplicationScoped;
//import javax.inject.Named;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

//@Named("pullViewer")
//@ApplicationScoped
public class PullViewer {
    private Pull rateLimited = new Pull("pullviewer", "http://github.com/jbosstm/narayana/pulls/", "http://issues.jboss.org/browse/JBTM", "pullViewer", "RATE LIMITED WARNING CACHED DATA");

    List<String> urls = Arrays.asList(new String[]{
            "https://api.github.com/repos/jbosstm/narayana/pulls",
            "https://api.github.com/repos/jbosstm/quickstart/pulls",
            "https://api.github.com/repos/jbosstm/documentation/pulls",
            "https://api.github.com/repos/jbosstm/jboss-transaction-spi/pulls",
            "https://api.github.com/repos/jbosstm/jboss-as/pulls",
            "https://api.github.com/repos/jbosstm/jboss-transaction-spi/pulls",
            "https://api.github.com/repos/jbosstm/narayana.io/pulls",
            "https://api.github.com/repos/jbosstm/performance/pulls",
            "https://api.github.com/repos/jbosstm/conferences/pulls",
            "https://api.github.com/repos/jboss-dockerfiles/narayana/pulls",
            "https://api.github.com/repos/jbosstm/narayana-tomcat/pulls",
//            "https://api.github.com/repos/web-servers/narayana-tomcat/pulls",
//            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Awildfly/wildfly-capabilities+type%3Apr+author%3Azhfeng",
//            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Awildfly/wildfly-capabilities+type%3Apr+author%3Ammusgrov",
//            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Awildfly/wildfly-capabilities+type%3Apr+author%3Aochaloup",
//            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Awildfly/wildfly-capabilities+type%3Apr+author%3Atomjenkinson",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Awildfly/wildfly-proposals+type%3Apr+author%3Ammusgrov",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Awildfly/wildfly-proposals+type%3Apr+author%3Aochaloup",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Awildfly/wildfly-proposals+type%3Apr+author%3Atomjenkinson",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Awildfly/wildfly+type%3Apr+author%3Ammusgrov",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Awildfly/wildfly+type%3Apr+author%3Aochaloup",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Awildfly/wildfly+type%3Apr+author%3Atomjenkinson",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Ajboss-openshift/cct_module+type%3Apr+author%3Ammusgrov",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Ajboss-openshift/cct_module+type%3Apr+author%3Aochaloup",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Ajboss-openshift/cct_module+type%3Apr+author%3Atomjenkinson",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Ajboss-openshift/application-templates+type%3Apr+author%3Azhfeng",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Ajboss-openshift/application-templates+type%3Apr+author%3Ammusgrov",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Ajboss-openshift/application-templates+type%3Apr+author%3Aochaloup",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Ajboss-openshift/application-templates+type%3Apr+author%3Atomjenkinson",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Ajboss-container-images/jboss-eap-modules+type%3Apr+author%3Aochaloup",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Ajboss-container-images/jboss-eap-modules+type%3Apr+author%3Ammusgrov",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Ajboss-container-images/jboss-eap-modules+type%3Apr+author%3Atomjenkinson",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Ajboss-container-images/jboss-eap-modules+type%3Apr+author%3Azhfeng",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Aapache/commons-dbcp+type%3Apr+author%3Ammusgrov",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Aapache/commons-dbcp+type%3Apr+author%3Aochaloup",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Aapache/commons-dbcp+type%3Apr+author%3Atomjenkinson",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Aquarkusio/quarkus+type%3Apr+author%3Ammusgrov",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Aquarkusio/quarkus+type%3Apr+author%3Aochaloup",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Aquarkusio/quarkus+type%3Apr+author%3Atomjenkinson",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Awildfly/wildfly-cekit-modules+type%3Apr+author%3Ammusgrov",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Awildfly/wildfly-cekit-modules+type%3Apr+author%3Aochaloup",
            "https://api.github.com/search/issues?q=state%3Aopen+repo%3Awildfly/wildfly-cekit-modules+type%3Apr+author%3Atomjenkinson"
    });

    private List<Pull> lastPulls = new ArrayList<Pull>();
    private SimpleDateFormat sdf;
    private long lastChecked;
    private String basic;

    public static void main(String[] args) throws IOException {
        PullViewer viewer = new PullViewer();
        viewer.init();
        List<Pull> pulls = viewer.getPulls();
        File file = new File("pulls.html");
        FileWriter writer = new FileWriter(file);
        writer.append("<html>\n");
        writer.append("  <body>\n");
        writer.append("  <h1>pullViewer</h1>\n");
        writer.append("  <table>\n");
        writer.append("    <tbody>\n");
        for (Pull pull : pulls) {
            writer.append("    <tr>\n");
            writer.append("      <td><a href=\""+pull.getPullUrl()+"\"a>"+pull.getProject()+"</a></td>"+"<td><a href=\""+pull.getJiraUrl()+"\"a>"+pull.getJiraUrl()+"</a></td>"+"<td>"+pull.getAuthor() +"</td><td>" +pull.getDescription()+"</td>\n");
            writer.append("    </tr>\n");
        }
        writer.append("    </tbody>\n");
        writer.append("  </table>\n");
        writer.append("  <p>That is all, to change the list of repos please click <a href=\"https://github.com/tomjenkinson/pullviewer/blob/master/src/main/java/org/tomjenkinson/pullviewer/PullViewer.java#L50\">here</a></p>\n");
        writer.append("</body>\n");
        writer.append("</html>");
        writer.flush();
        writer.close();
    }

//    @PostConstruct
    public void init() {
        sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/London"));
    }

    // JSF will call this multiple times so only attempt if older than 10 seconds
    public synchronized List<Pull> getPulls() throws IOException {
        if (basic == null) {
            File file = new File("config/creds");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String username = reader.readLine();
                //String password = reader.readLine();
                basic = "token " + username;
                //javax.xml.bind.DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());
                System.out.println("Read from: " + file.getAbsolutePath());
            } else {
                throw new RuntimeException("Looking for: " + file.getAbsolutePath());
            }
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastChecked < 10000) {
            return lastPulls;
        }
        lastChecked = currentTime;
        final List<Pull> pulls = new ArrayList<Pull>();
        List<Thread> threads = new ArrayList<Thread>();
        for (final String project : urls) {
            Thread thread = new Thread() {
                public void run() {
                    try {
                        URL url = new URL(project);
                        URLConnection connection = url.openConnection();
                        if (basic != null) {
                            connection.setRequestProperty("Authorization", basic);
                        }

                        String rateReset = connection.getHeaderField("X-RateLimit-Reset");
                        Date reset = new Date(Integer.parseInt(rateReset) * 1000L);
                        rateLimited.setDescription("RATE LIMITED WARNING CACHED DATA - rate ("+connection.getHeaderField("X-RateLimit-Remaining")+"/"+connection.getHeaderField("X-RateLimit-Limit")+") will reset at " + sdf.format(reset));

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
                        if (!json.isArray()) {
                            json = (JSON) ((JSONObject) json).get("items");
                        }
                        Iterator iterator = ((JSONArray) json).iterator();
                        while (iterator.hasNext()) {
                            JSONObject next = (JSONObject) iterator.next();
                            String title = next.getString("title");
                            String author = "unknown";
                            try {
                                author = next.getJSONObject("user").getString("login");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String pullUrl = next.getString("url").replace(
                                    "api.github.com/repos/",
                                    "github.com/").replace("/pulls/", "/pull/");
                            String jiraUrl = "https://issues.jboss.org/browse/"
                                    + title.substring(0, Math.min(
                                    title.indexOf(' ') > 0 ? title.indexOf(' ')
                                            : title.length(),
                                    title.indexOf('.') > 0 ? title.indexOf('.')
                                            : title.length())).replace("[", "").replace("]", "");
                            String description = title.substring(title.indexOf(" ") + 1);
                            pulls.add(new Pull(next.getString("url").replace(
                                    "https://api.github.com/repos/",
                                    "").replace("/pulls/", "-").replace("/issues/", "-"), pullUrl, jiraUrl, author, description));
                        }
                    } catch (Throwable t) {
                        basic = null;
                        t.printStackTrace(); // This is probably rate limit
                        System.out.println("Could not connect for" + project);
                        if (!pulls.contains(rateLimited)) {
                            pulls.add(0, rateLimited);
                        }
                    }
                }
            };
            thread.start();
            threads.add(thread);
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        pulls.sort(new Comparator<Pull>() {
            @Override
            public int compare(Pull o1, Pull o2) {
                return o1.getPullUrl().compareTo(o2.getPullUrl());
            }
        });

        // Keep the existing list of pulls due to being rate limited
        if (!pulls.contains(rateLimited)) {
            lastPulls.clear();
            lastPulls.addAll(pulls);
        } else {
            if (!lastPulls.contains(rateLimited)) {
                lastPulls.add(0, rateLimited);
            }
        }
            
        return lastPulls;
    }
}
