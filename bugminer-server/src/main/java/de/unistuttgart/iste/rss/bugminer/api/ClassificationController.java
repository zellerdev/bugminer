package de.unistuttgart.iste.rss.bugminer.api;

import java.util.Collection;
import java.util.Optional;

import de.unistuttgart.iste.rss.bugminer.model.entities.User;
import de.unistuttgart.iste.rss.bugminer.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.unistuttgart.iste.rss.bugminer.api.exceptions.NotFoundException;
import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;
import de.unistuttgart.iste.rss.bugminer.model.entities.Classification;
import de.unistuttgart.iste.rss.bugminer.model.entities.IssueTracker;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.model.repositories.BugRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ClassificationRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.IssueTrackerRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ProjectRepository;

@RestController
@RequestMapping(value = "/api")
public class ClassificationController {

	@Autowired
	private ClassificationRepository classificationRepo;

	@Autowired
	private BugRepository bugRepo;

	@Autowired
	private ProjectRepository projectRepo;

	@Autowired
	private IssueTrackerRepository issueTrackerRepo;

	@Autowired
	private UserRepository userRepo;

	protected ClassificationController() {
		// managed bean
	}

	/**
	 * Returns all classifications for a given bug
	 *
	 * @param name the name of the bugs project
	 * @param issueTrackerName the name of the bugs issue tracker
	 * @param key the bugs key
	 * @return the classifications for the given bug
	 */
	@RequestMapping(value = "/projects/{name}/bugs/{issueTrackerName}/{key}/classifications",
			method = RequestMethod.GET)
	public Collection<Classification> classificationsForBug(
			@PathVariable(value = "name") String name,
			@PathVariable(value = "issueTrackerName") String issueTrackerName,
			@PathVariable(value = "key") String key) {
		Bug bug = getBugByProjectAndIssueTrackerAndKey(name, issueTrackerName, key);

		return classificationRepo.findByBug(bug);
	}

	/**
	 * Returns the classification with the given id
	 *
	 * @param id the classifications id
	 * @return the classifiaction with the given id
	 */
	@RequestMapping(value = "/projects/{name}/bugs/{issueTrackerName}/{key}/classifications/{id}",
			method = RequestMethod.GET)
	public Classification classification(@PathVariable(value = "id") String id) {
		return classificationRepo.findOne(id);
	}

	/**
	 * creates a new classification for the given bug
	 *
	 * @param name the name of the bugs project
	 * @param issueTrackerName the name of the bugs issue tracker
	 * @param key the bugs key
	 * @param classification the classification to add
	 * @return the newly added classification
	 */
	@RequestMapping(value = "/projects/{name}/bugs/{issueTrackerName}/{key}/classifications",
			method = RequestMethod.PUT)
	public Classification putClassification(
			@PathVariable(value = "name") String name,
			@PathVariable(value = "issueTrackerName") String issueTrackerName,
			@PathVariable(value = "key") String key,
			@RequestParam(value = "user") String userID,
			@RequestBody Classification classification) {
		User user = userRepo.findById(userID).orElseThrow(NotFoundException::new);

		Bug bug = getBugByProjectAndIssueTrackerAndKey(name, issueTrackerName, key);

		Optional<Classification>
				classificationFromDB = classificationRepo.findFirstByBugAndUser(bug, user);
		if (classificationFromDB.isPresent()) {
			classificationFromDB.get().setLineChangeClassifications(classification.getLineChangeClassifications());
			classification = classificationFromDB.get();
		} else {
			classification.setBug(bug);
			classification.setUser(user);
		}

		return classificationRepo.save(classification);
	}

	private Bug getBugByProjectAndIssueTrackerAndKey(String projectName,
			String issueTrackerName, String key) {
		Project project = projectRepo.findByName(projectName)
				.orElseThrow(() -> new NotFoundException());
		IssueTracker issueTracker =
				issueTrackerRepo.findByProjectAndName(project, issueTrackerName)
				.orElseThrow(() -> new NotFoundException());
		return bugRepo.findByProjectAndIssueTrackerAndKey(project, issueTracker, key)
				.orElseThrow(() -> new NotFoundException());
	}
}
