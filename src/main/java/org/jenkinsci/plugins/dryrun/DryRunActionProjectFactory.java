package org.jenkinsci.plugins.dryrun;

import hudson.Extension;
import hudson.maven.MavenModuleSet;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.TransientProjectActionFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Gregory Boissinot
 */
@Extension
public class DryRunActionProjectFactory extends TransientProjectActionFactory {

    @Override
    public Collection<? extends Action> createFor(AbstractProject target) {
        if (!(target instanceof MavenModuleSet)) {
            ArrayList<DryRunProjectAction> ta = new ArrayList<DryRunProjectAction>();
            ta.add(new DryRunProjectAction(target));
            return ta;
        }
        return new ArrayList<Action>();
    }
}
