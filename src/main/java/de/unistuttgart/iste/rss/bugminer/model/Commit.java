package de.unistuttgart.iste.rss.bugminer.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Commit {
	@ManyToOne
	private CodeRepo codeRepo;

	private String commitId;
}
