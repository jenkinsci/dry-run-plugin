package org.jenkinsci.plugins.dryrun;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * @author Gregory Boissinot
 */
public class DryRunJobProperty extends JobProperty {

    private boolean on;

    private boolean enableRunListeners;

    private boolean enableSCM;

    private boolean enableTriggers;

    private boolean enableBuildWrappers;

    private boolean enableBuilders;

    private boolean enablePublishers;

    @DataBoundConstructor
    public DryRunJobProperty() {
    }

    public boolean isOn() {
        return on;
    }

    public boolean isEnableRunListeners() {
        return enableRunListeners;
    }

    public boolean isEnableSCM() {
        return enableSCM;
    }

    public boolean isEnableTriggers() {
        return enableTriggers;
    }

    public boolean isEnableBuildWrappers() {
        return enableBuildWrappers;
    }

    public boolean isEnableBuilders() {
        return enableBuilders;
    }

    public boolean isEnablePublishers() {
        return enablePublishers;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    @SuppressWarnings("unused")
    public void setEnableRunListeners(boolean enableRunListeners) {
        this.enableRunListeners = enableRunListeners;
    }

    @SuppressWarnings("unused")
    public void setEnableSCM(boolean enableSCM) {
        this.enableSCM = enableSCM;
    }

    @SuppressWarnings("unused")
    public void setEnableTriggers(boolean enableTriggers) {
        this.enableTriggers = enableTriggers;
    }

    @SuppressWarnings("unused")
    public void setEnableBuildWrappers(boolean enableBuildWrappers) {
        this.enableBuildWrappers = enableBuildWrappers;
    }

    @SuppressWarnings("unused")
    public void setEnableBuilders(boolean enableBuilders) {
        this.enableBuilders = enableBuilders;
    }

    @SuppressWarnings("unused")
    public void setEnablePublishers(boolean enablePublishers) {
        this.enablePublishers = enablePublishers;
    }

    @Extension
    @SuppressWarnings("unused")
    public static class DescriptorImpl extends JobPropertyDescriptor {

        @Override
        public String getDisplayName() {
            return "Configure a Dry-Run";
        }

        @Override
        public JobProperty<?> newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            Object onObject = formData.get("on");
            if (onObject instanceof JSONObject) {
                JSONObject onJSONObject = (JSONObject) onObject;
                DryRunJobProperty dryRunJobProperty = new DryRunJobProperty();
                req.bindJSON(dryRunJobProperty, onJSONObject);
                dryRunJobProperty.setOn(true);
                return dryRunJobProperty;
            }
            return null;
        }

        @Override
        public boolean isApplicable(Class<? extends Job> jobType) {
            return true;
        }

        @Override
        public String getHelpFile() {
            return "/plugin/dry-run/help.html";
        }
    }
}
