package de.unistuttgart.iste.rss.bugminer.model.entities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.unistuttgart.iste.rss.bugminer.model.BaseEntity;

/**
 * A bug report imported from an issue tracker
 */
@Component
@Scope("prototype")
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"issue_tracker_id", "key"}))
public class Bug extends BaseEntity {
	@ManyToOne
	@JsonIgnore
	private Project project;

	@ManyToMany
	private Collection<Label> labels;

	@OneToMany
	@JoinColumn(name = "bug_id")
	private Collection<Event> events;

	@OneToMany
	@JoinColumn(name = "bug_id")
	@JsonIgnore
	private Collection<LineChange> lineChanges;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "bug_id")
	private Collection<Classification> classifications;

	@OneToMany
	@JoinColumn(name = "bug_id")
	private Collection<BugParticipant> participants;

	@ManyToOne
	private IssueTracker issueTracker;

	private String key;

	private boolean isFixed;

	private String jsonDetails;

	private Instant reportTime;

	private Instant closeTime;

	private String title;

	@Column(length = 1000000)
	private String description;

	/**
	 * Creates a new empty {@code Bug}
	 */
	public Bug() {
		labels = new ArrayList<>();
		events = new ArrayList<>();
		lineChanges = new ArrayList<>();
		classifications = new ArrayList<>();
		participants = new ArrayList<>();
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Collection<Label> getLabels() {
		return labels;
	}

	public void setLabels(Collection<Label> labels) {
		this.labels = labels;
	}

	public Collection<Event> getEvents() {
		return events;
	}

	public void setEvents(Collection<Event> events) {
		this.events = events;
	}

	public Collection<LineChange> getLineChanges() {
		return lineChanges;
	}

	public void setLineChanges(Collection<LineChange> lineChanges) {
		this.lineChanges = lineChanges;
	}

	public Collection<Classification> getClassifications() {
		return classifications;
	}

	public void setClassifications(Collection<Classification> classifications) {
		this.classifications = classifications;
	}

	public Collection<BugParticipant> getParticipants() {
		return participants;
	}

	public void setParticipants(Collection<BugParticipant> participants) {
		this.participants = participants;
	}

	public IssueTracker getIssueTracker() {
		return issueTracker;
	}

	public void setIssueTracker(IssueTracker issueTracker) {
		this.issueTracker = issueTracker;
	}

	public boolean isFixed() {
		return isFixed;
	}

	public void setFixed(boolean isFixed) {
		this.isFixed = isFixed;
	}

	public String getJsonDetails() {
		return jsonDetails;
	}

	public void setJsonDetails(String jsonDetails) {
		this.jsonDetails = jsonDetails;
	}

	public Instant getReportTime() {
		return reportTime;
	}

	public void setReportTime(Instant reportTime) {
		this.reportTime = reportTime;
	}

	public Instant getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(Instant closeTime) {
		this.closeTime = closeTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
