package org.jenkinsci.plugins.dryrun;

import hudson.model.BuildBadgeAction;

/**
 * @author Gregory Boissinot
 */
public class DryRunBuildBadgeAction implements BuildBadgeAction {

    public String getDescription() {
        return "DRY-RUN";
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public String getUrlName() {
        return null;
    }
}
