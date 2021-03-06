package de.unistuttgart.iste.rss.bugminer.model.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import de.unistuttgart.iste.rss.bugminer.model.BaseEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * An addition or removal of a line as part of a bugfix
 */
@Component
@Scope("prototype")
@Entity
public class LineChange extends BaseEntity {
	@ManyToOne
	private Bug bug;

	@ManyToOne
	private CodeRepo codeRepo;

	private String fileName;

	@Column(length = 10000000)
	private String lineText;

	private int oldLineNumber;

	@Basic(optional = true)
	private Integer newLineNumberIndex;

	private LineChangeKind kind;

	/**
	 * Creates an empty {@Code LineChange}
	 */
	public LineChange() {
		// empty
	}

	public Bug getBug() {
		return bug;
	}

	public void setBug(Bug bug) {
		this.bug = bug;
	}

	public CodeRepo getCodeRepo() {
		return codeRepo;
	}

	public void setCodeRepo(CodeRepo codeRepo) {
		this.codeRepo = codeRepo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLineText() {
		return lineText;
	}

	public void setLineText(String lineText) {
		this.lineText = lineText;
	}

	public int getOldLineNumber() {
		return oldLineNumber;
	}

	public void setOldLineNumber(int oldLineNumber) {
		this.oldLineNumber = oldLineNumber;
	}

	public Integer getNewLineNumberIndex() {
		return newLineNumberIndex;
	}

	public void setNewLineNumberIndex(Integer newLineNumberIndex) {
		this.newLineNumberIndex = newLineNumberIndex;
	}

	public LineChangeKind getKind() {
		return kind;
	}

	public void setKind(LineChangeKind kind) {
		this.kind = kind;
	}
}
