package org.jenkinsci.plugins.dryrun;

import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.listeners.RunListener;
import org.kohsuke.stapler.StaplerOverridable;
import org.kohsuke.stapler.StaplerProxy;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;

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
        return "dryRun";
    }

    public Object getTarget() {
        return new DryRunUserAction(target);
    }
}
