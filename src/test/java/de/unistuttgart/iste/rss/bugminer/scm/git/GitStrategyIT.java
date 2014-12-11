package de.unistuttgart.iste.rss.bugminer.scm.git;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.unistuttgart.iste.rss.bugminer.TestConfig;
import de.unistuttgart.iste.rss.bugminer.annotations.DataDirectory;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnection;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnector;
import de.unistuttgart.iste.rss.bugminer.config.EntityFactory;
import de.unistuttgart.iste.rss.bugminer.model.CodeRepo;
import de.unistuttgart.iste.rss.bugminer.model.CodeRevision;
import de.unistuttgart.iste.rss.bugminer.model.Project;
import de.unistuttgart.iste.rss.bugminer.testutils.VagrantMachine;
import de.unistuttgart.iste.rss.bugminer.utils.ExecutionResult;

@ContextConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class GitStrategyIT {
	@Autowired
	GitStrategy strategy;

	@Rule
	@Autowired
	public VagrantMachine vagrantMachine;

	@Autowired
	@DataDirectory
	Path dataDirectory;

	@Autowired
	EntityFactory entityFactory;

	@Autowired
	private SshConnector connector;

	private Path repoPath;

	@Before
	public void setUpGitRepo() throws IOException, GitAPIException {
		repoPath = dataDirectory.resolve("scm").resolve("project").resolve("main");
		SimpleRepo.bareCloneTo(repoPath);
	}

	@Test
	public void testPushToSsh() throws IOException {
		Project project = entityFactory.make(Project.class);
		project.setName("project");
		CodeRepo repo = entityFactory.make(CodeRepo.class);
		repo.setProject(project);
		repo.setName("main");
		CodeRevision revision = new CodeRevision(repo, SimpleRepo.FIRST_COMMIT);

		strategy.pushTo(repo, vagrantMachine.getNode(), "dest", revision);
		strategy.checkout(vagrantMachine.getNode(), "dest", revision);

		try (SshConnection connection = connector.connect(vagrantMachine.getSshConfig())) {
			ExecutionResult result = connection.execute("cat", "dest/fileA");
			assertThat(result.getOutput(), is(SimpleRepo.INITIAL_A_CONTENTS));
		}
	}
}
