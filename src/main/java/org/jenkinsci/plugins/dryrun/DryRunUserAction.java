package org.jenkinsci.plugins.dryrun;

import hudson.model.AbstractProject;
import hudson.model.Item;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author Gregory Boissinot
 */
public class DryRunUserAction implements Serializable {

    private AbstractProject project;

    public DryRunUserAction(AbstractProject project) {
        this.project = project;
    }

    @SuppressWarnings("unused")
    public void doDynamic(String token, StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
        project.checkPermission(Item.BUILD);
        project.scheduleBuild(0, new DryRunCause(), new DryRunActivateListenerAction(), new DryRunBuildBadgeAction());
        rsp.forwardToPreviousPage(req);
    }
}
