package org.jenkinsci.plugins.dryrun;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Project;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.Builder;
import hudson.tasks.Publisher;
import hudson.util.DescribableList;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Gregory Boissinot
 */
@Extension
public class DryRunListener extends RunListener<Run> {

    private DescribableList<Builder, Descriptor<Builder>> builders;
    private DescribableList<Publisher, Descriptor<Publisher>> publishers;
    private DescribableList<BuildWrapper, Descriptor<BuildWrapper>> buildWrappers;


    @Override
    public void onStarted(Run run, TaskListener listener) {
        if (run.getAction(DryRunActivateListenerAction.class) != null) {

            Project jenkinsProject = (Project) run.getParent();
            try {

                //Process buildWrappers
                Field buildWrappersField = Project.class.getDeclaredField("buildWrappers");
                buildWrappersField.setAccessible(true);
                buildWrappers = (DescribableList<BuildWrapper, Descriptor<BuildWrapper>>) buildWrappersField.get(jenkinsProject);
                Map<Descriptor<BuildWrapper>, BuildWrapper> mapBuildWrappers = buildWrappers.toMap();
                for (Descriptor<BuildWrapper> desc : mapBuildWrappers.keySet()) {
                    listener.getLogger().println("Executing BuildWrapper [" + desc.getDisplayName() + "] - OFF");
                }
                buildWrappersField.set(jenkinsProject, new DryRunEmptyList());

                //Process builders
                Field buildersField = Project.class.getDeclaredField("builders");
                buildersField.setAccessible(true);
                builders = (DescribableList<Builder, Descriptor<Builder>>) buildersField.get(jenkinsProject);
                Map<Descriptor<Builder>, Builder> mapBuilders = builders.toMap();
                for (Descriptor<Builder> desc : mapBuilders.keySet()) {
                    listener.getLogger().println("Executing Builder [" + desc.getDisplayName() + "] - OFF");
                }
                buildersField.set(jenkinsProject, new DryRunEmptyList());

                //Process publishers
                Field publishersField = Project.class.getDeclaredField("publishers");
                publishersField.setAccessible(true);
                publishers = (DescribableList<Publisher, Descriptor<Publisher>>) publishersField.get(jenkinsProject);
                Map<Descriptor<Publisher>, Publisher> mapPublishers = publishers.toMap();
                for (Descriptor<Publisher> desc : mapPublishers.keySet()) {
                    listener.getLogger().println("Executing Publisher [" + desc.getDisplayName() + "] - OFF");
                }
                publishersField.set(jenkinsProject, new DryRunEmptyList());


            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCompleted(Run run, TaskListener listener) {
        if (run.getAction(DryRunActivateListenerAction.class) != null) {
            Project jenkinsProject = (Project) run.getParent();
            try {

                //Restore buildWrappers
                Field buildWrappersFiled = Project.class.getDeclaredField("buildWrappers");
                buildWrappersFiled.setAccessible(true);
                buildWrappersFiled.set(jenkinsProject, buildWrappers);

                //Restore builders
                Field buildersFiled = Project.class.getDeclaredField("builders");
                buildersFiled.setAccessible(true);
                buildersFiled.set(jenkinsProject, builders);

                //Restore publishers
                Field publishersFiled = Project.class.getDeclaredField("publishers");
                publishersFiled.setAccessible(true);
                publishersFiled.set(jenkinsProject, publishers);

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
