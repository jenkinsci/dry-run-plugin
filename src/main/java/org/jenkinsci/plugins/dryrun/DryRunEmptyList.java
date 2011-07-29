package org.jenkinsci.plugins.dryrun;

import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.util.DescribableList;

/**
 * @author Gregory Boissinot
 */
public class DryRunEmptyList<T extends Describable<T>, D extends Descriptor<T>> extends DescribableList<T, D> {
}
