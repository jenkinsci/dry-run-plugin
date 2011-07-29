package org.jenkinsci.plugins.dryrun;

import hudson.model.Cause;

/**
 * @author Gregory Boissinot
 */
public class DryRunCause extends Cause {
    @Override
    public String getShortDescription() {
        return "[DRY-RUN]";
    }
}
