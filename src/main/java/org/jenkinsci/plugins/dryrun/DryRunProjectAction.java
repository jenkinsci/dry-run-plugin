package org.jenkinsci.plugins.dryrun;

import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Item;
import org.acegisecurity.AccessDeniedException;
import org.kohsuke.stapler.StaplerProxy;

/**
 * @author Gregory Boissinot
 */
public class DryRunProjectAction implements Action, StaplerProxy {

    private AbstractProject target;

    public DryRunProjectAction(AbstractProject target) {
        this.target = target;
    }

    public String getIconFileName() {
        return "clock.gif";
    }

    public String getDisplayName() {
        return "Dry Run";
    }

    public String getUrlName() {
        try {
            target.checkPermission(Item.BUILD);
            return "dryRun";
        } catch (AccessDeniedException ae) {
            return null;
        }
    }

    public Object getTarget() {
        return new DryRunUserAction(target);
    }
}
