package de.unistuttgart.iste.rss.bugminer.scm.git;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public class SimpleRepo {
	public static final String FIRST_COMMIT = "892e9753ab1e1d3fbcd9e75518ae01d6e8144e21";

	public static void bareCloneTo(Path path) throws GitAPIException, IOException {
		cloneTo(path, true);
	}

	public static void cloneTo(Path path) throws GitAPIException, IOException {
		cloneTo(path, false);
	}

	private static void cloneTo(Path path, boolean bare) throws GitAPIException, IOException {
		URL bundleURL = GitStrategyIT.class.getResource("simpleRepo.bundle");
		Git.cloneRepository()
				.setURI(bundleURL.toString())
				.setDirectory(path.toFile())
				.setBare(bare)
				.call();
		if (!bare) {
			Git.open(path.toFile()).checkout().setName("master").call();
		}
	}
}
