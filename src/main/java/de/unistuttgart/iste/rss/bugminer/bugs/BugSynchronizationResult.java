package de.unistuttgart.iste.rss.bugminer.bugs;

import java.util.Collection;

import de.unistuttgart.iste.rss.bugminer.model.Bug;

public class BugSynchronizationResult {

	private Collection<Bug> newBugs;
	private Collection<Bug> updatedBugs;

	public BugSynchronizationResult(Collection<Bug> newBugs, Collection<Bug> updatedBugs) {
		this.newBugs = newBugs;
		this.updatedBugs = updatedBugs;
	}

	public Collection<Bug> getNewBugs() {
		return newBugs;
	}

	public Collection<Bug> getUpdatedBugs() {
		return updatedBugs;
	}
}
