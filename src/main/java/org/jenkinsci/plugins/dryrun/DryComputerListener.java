package org.jenkinsci.plugins.dryrun;

import hudson.Extension;
import hudson.model.Computer;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hudson.slaves.ComputerListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
@Extension
public class DryComputerListener extends ComputerListener {

    @Override
    public void onOnline(Computer c, TaskListener listener) throws IOException, InterruptedException {

        //Order RunListener in order to male DryRunListener on top.
        if (c != null && c.getName() != null) {
            List<RunListener> runListeners = RunListener.all();
            List<RunListener> sortedRunListeners = new ArrayList<RunListener>();
            for (int k = 0; k < runListeners.size(); k++) {
                RunListener runListener = runListeners.get(k);
                if (DryRunListener.class.isAssignableFrom(runListener.getClass())) {
                    sortedRunListeners.add(0, runListener);
                } else {
                    sortedRunListeners.add(runListener);
                }
                runListeners.remove(k);
            }
            runListeners.addAll(sortedRunListeners);
        }

    }
}
