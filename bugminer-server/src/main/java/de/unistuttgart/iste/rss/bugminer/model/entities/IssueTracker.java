package de.unistuttgart.iste.rss.bugminer.model.entities;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.unistuttgart.iste.rss.bugminer.bugs.IssueTrackerStrategy;
import de.unistuttgart.iste.rss.bugminer.model.BaseEntity;
import de.unistuttgart.iste.rss.bugminer.strategies.StrategyFactory;

/**
 * The representation of an issue tracker software
 */
@Component
@Scope("prototype")
@Entity
public class IssueTracker extends BaseEntity {
	@Autowired
	@Transient
	private StrategyFactory strategyFactory;

	@ManyToOne
	@JsonIgnore
	private Project project;

	@OneToMany
	private Collection<Bug> bugs;

	private URI uri;

	private String provider;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Creates an empty {@code IssueTracker}
	 */
	public IssueTracker() {
		bugs = new ArrayList<>();
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Collection<Bug> getBugs() {
		return bugs;
	}

	public void setBugs(Collection<Bug> bugs) {
		this.bugs = bugs;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
}
