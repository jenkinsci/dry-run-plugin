package org.jenkinsci.plugins.dryrun;

import hudson.Extension;
import hudson.Launcher;
import hudson.matrix.MatrixProject;
import hudson.maven.MavenModuleSet;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.Builder;
import hudson.tasks.Publisher;
import hudson.util.DescribableList;
import org.jenkinsci.lib.dryrun.DryRun;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Gregory Boissinot
 */
@Extension
public class DryRunListener extends RunListener<Run> {

    private DescribableList<Builder, Descriptor<Builder>> builders;
    private DescribableList<Publisher, Descriptor<Publisher>> publishers;
    private DescribableList<BuildWrapper, Descriptor<BuildWrapper>> buildWrappers;

    private Class<? extends AbstractProject> jobClass;
    private AbstractProject jobObject;

    @Override
    public Environment setUpEnvironment(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        if (build.getAction(DryRunActivateListenerAction.class) != null) {

            try {
                dryRun(build, launcher, listener);
            } catch (NoSuchFieldException nse) {
                listener.getLogger().println("SEVERE ERROR occurs: " + nse.getMessage());
                throw new Run.RunnerAbortedException();
            } catch (IllegalAccessException iae) {
                listener.getLogger().println("SEVERE ERROR occurs: " + iae.getMessage());
                throw new Run.RunnerAbortedException();
            } catch (NoSuchMethodException nsme) {
                listener.getLogger().println("SEVERE ERROR occurs: " + nsme.getMessage());
                throw new Run.RunnerAbortedException();
            } catch (InvocationTargetException ite) {
                listener.getLogger().println("SEVERE ERROR occurs: " + ite.getMessage());
                throw new Run.RunnerAbortedException();
            }
        }

        return new Environment() {
            @Override
            public void buildEnvVars(Map<String, String> env) {
            }
        };
    }


    private void dryRun(AbstractBuild build, Launcher launcher, BuildListener listener) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        listener.getLogger().println("Starting a dry-run.");
        populateJobsObjects(build);
        dryRunBuildWrappers(listener);
        dryRunBuilders(build, launcher, listener);
        dryRunPublishers(listener);
        listener.getLogger().println("Dry-run end.");
    }

    private void populateJobsObjects(AbstractBuild build) {
        Job job = build.getParent();
        if (job instanceof MavenModuleSet) {
            jobClass = MavenModuleSet.class;
            jobObject = (MavenModuleSet) job;
        } else if (job instanceof MatrixProject) {
            jobClass = MatrixProject.class;
            jobObject = (MatrixProject) job;

        } else {
            jobClass = Project.class;
            jobObject = (Project) job;
        }
    }

    private void dryRunBuildWrappers(BuildListener listener) throws NoSuchFieldException, IllegalAccessException {
        Field buildWrappersField = jobClass.getDeclaredField("buildWrappers");
        buildWrappersField.setAccessible(true);
        buildWrappers = (DescribableList<BuildWrapper, Descriptor<BuildWrapper>>) buildWrappersField.get(jobObject);
        Map<Descriptor<BuildWrapper>, BuildWrapper> mapBuildWrappers = buildWrappers.toMap();
        for (Descriptor<BuildWrapper> desc : mapBuildWrappers.keySet()) {
            listener.getLogger().println("Executing BuildWrapper [" + desc.getDisplayName() + "]");
        }
        buildWrappersField.set(jobObject, new DryRunEmptyList());
    }

    private void dryRunBuilders(AbstractBuild build, Launcher launcher, BuildListener listener) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Field buildersField = jobClass.getDeclaredField("builders");
        buildersField.setAccessible(true);
        builders = (DescribableList<Builder, Descriptor<Builder>>) buildersField.get(jobObject);
        Map<Descriptor<Builder>, Builder> mapBuilders = builders.toMap();

        for (Map.Entry<Descriptor<Builder>, Builder> entry : mapBuilders.entrySet()) {
            Descriptor<Builder> desc = entry.getKey();
            listener.getLogger().println("Executing Builder [" + desc.getDisplayName() + "]");
            Builder builder = entry.getValue();
            if (isDryRun(builder.getClass())) {
                Method method = builder.getClass().getMethod("performDryRun", AbstractBuild.class, Launcher.class, BuildListener.class);
                method.invoke(builder, build, launcher, listener);
            }
        }
        buildersField.set(jobObject, new DryRunEmptyList());
    }

    private void dryRunPublishers(BuildListener listener) throws NoSuchFieldException, IllegalAccessException {
        Field publishersField = jobClass.getDeclaredField("publishers");
        publishersField.setAccessible(true);
        publishers = (DescribableList<Publisher, Descriptor<Publisher>>) publishersField.get(jobObject);
        Map<Descriptor<Publisher>, Publisher> mapPublishers = publishers.toMap();
        for (Descriptor<Publisher> desc : mapPublishers.keySet()) {
            listener.getLogger().println("Executing Publisher [" + desc.getDisplayName() + "]");
        }
        publishersField.set(jobObject, new DryRunEmptyList());
    }

    private boolean isDryRun(Class objectClass) {
        Class[] interfaces = objectClass.getInterfaces();
        for (Class interfaceClass : interfaces) {
            if (interfaceClass.getName().equals(DryRun.class.getName())) {
                return true;
            }
        }
        return false;
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

            } catch (NoSuchFieldException nse) {
                listener.getLogger().println("SEVERE ERROR occurs: " + nse.getMessage());
                throw new Run.RunnerAbortedException();
            } catch (IllegalAccessException iae) {
                listener.getLogger().println("SEVERE ERROR occurs: " + iae.getMessage());
                throw new Run.RunnerAbortedException();
            }
        }
    }
}
