package de.unistuttgart.iste.rss.bugminer.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * A status in the workflow of a bug
 */
@Component
@Scope("prototype")
@Entity
public class BugStatus {
	@ManyToOne
	private Project project;

	private String name;

	/**
	 * Creates an empty {@code BugStatus}
	 */
	public BugStatus() {
		// empty
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
