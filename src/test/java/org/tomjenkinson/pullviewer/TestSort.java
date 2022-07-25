package org.tomjenkinson.pullviewer;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

public class TestSort {
    @Test
    @Ignore
    public void testSort() throws IOException {
        PullViewer pv = new PullViewer();
        List<String> urls = Arrays.asList(new String[]{
                "https://api.github.com/repos/jbosstm/narayana/pulls",
                "https://api.github.com/repos/jbosstm/narayana.io/pulls"});
        pv.init();
        List<Pull> pulls = pv.getPulls(false, urls);
        String previous = null;
        for (Pull pull : pulls) {
            System.out.println(pull.getPullUrl());
            if (previous != null)
                if (previous.compareTo(pull.getPullUrl()) < 0) {
                    fail("Out of order");
                }
        }
    }
}
