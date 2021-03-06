package de.unistuttgart.iste.rss.bugminer.computing.vagrant;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import de.unistuttgart.iste.rss.bugminer.model.entities.SystemSpecification;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.unistuttgart.iste.rss.bugminer.annotations.DataDirectory;
import de.unistuttgart.iste.rss.bugminer.annotations.Strategy;
import de.unistuttgart.iste.rss.bugminer.computing.ClusterStrategy;
import de.unistuttgart.iste.rss.bugminer.computing.CommandExecutor;
import de.unistuttgart.iste.rss.bugminer.computing.InvalidSshConfigException;
import de.unistuttgart.iste.rss.bugminer.computing.SshConfig;
import de.unistuttgart.iste.rss.bugminer.computing.SshConfigParser;
import de.unistuttgart.iste.rss.bugminer.model.entities.Cluster;
import de.unistuttgart.iste.rss.bugminer.model.entities.Node;
import de.unistuttgart.iste.rss.bugminer.model.entities.NodeStatus;

@Strategy(type = ClusterStrategy.class, name = VagrantStrategy.NAME)
@Component
public class VagrantStrategy implements ClusterStrategy {
	public static final String NAME = "vagrant";

	@Autowired
	@DataDirectory
	private Path dataPath;

	@Autowired
	private CommandExecutor executor;

	@Autowired
	private VagrantBoxes boxes;

	@Autowired
	private VagrantStatusParser statusParser;

	@Autowired
	SshConfigParser sshConfigParser;

	private final Logger logger = Logger.getLogger(VagrantStrategy.class);

	private static final String VAGRANTFILE_TEMPLATE = "#!/usr/bin/ruby%n"
			+ "Vagrant.configure(\"2\") do |config|%n"
			+ "  config.vm.box = \"%s\"%n"
			+ "  config.vm.provider \"virtualbox\" do |v|%n"
			+ "    v.memory = %d%n"
			+ "    v.cpus = %d%n"
			+ "    v.name = \"%s\"%n"
			+ "  end%n"
			+ "end%n";

	protected VagrantStrategy() {
		// managed bean
	}

	@Override
	public boolean isAvailable() {
		try {
			executor.execute("vagrant", "global-status");
			return true;
		} catch (IOException e) {
			logger.debug("Vagrant is not available", e);
			return false;
		}
	}

	@Override
	public Node createNode(Cluster cluster) throws IOException {
		Node node = new Node();
		node.setSystemSpecification(SystemSpecification.UBUNTU_1404);
		node.setCluster(cluster);
		Path nodePath = getPath(node);
		if (Files.exists(nodePath)) {
			throw new IOException("The directory for the node to initialize already exists: "
					+ nodePath);
		}
		Files.createDirectories(nodePath);

		Files.write(nodePath.resolve("Vagrantfile"),
				getVagrantfileContent(node).getBytes(StandardCharsets.UTF_8));

		// need to start the node in order to get ssh config
		startNode(node);
		node.setSshConfig(getSshConfig(node));

		return node;
	}

	@Override
	public NodeStatus getNodeStatus(Node node) throws IOException {
		Path nodePath = getPath(node);
		if (!Files.exists(nodePath)) {
			return NodeStatus.OFFLINE;
		}
		String output = executor.execute(nodePath, "vagrant", "status").getOutput();
		return statusParser.parseStatusOutput(output);
	}

	@Override
	public void startNode(Node node) throws IOException {
		executor.execute(getPath(node), "vagrant", "up");
	}

	@Override
	public void stopNode(Node node) throws IOException {
		Path nodePath = getPath(node);
		if (!Files.exists(nodePath)) {
			return;
		}
		executor.execute(nodePath, "vagrant", "halt");
	}

	@Override
	public void destroyNode(Node node) throws IOException {
		Path nodePath = getPath(node);
		if (!Files.exists(nodePath)) {
			return;
		}
		executor.execute(nodePath, "vagrant", "destroy", "-f");
		FileUtils.deleteDirectory(nodePath.toFile());
	}

	public SshConfig getSshConfig(Node node) throws IOException {
		Path nodePath = getPath(node);
		String sshConfigStr = executor.execute(nodePath, "vagrant", "ssh-config").getOutput();
		try {
			return sshConfigParser.parse(sshConfigStr);
		} catch (InvalidSshConfigException e) {
			throw new IOException("Vagrant reported invalid ssh config: " + sshConfigStr, e);
		}
	}

	private Path getPath(Node node) {
		if (node.getId() == null) {
			throw new IllegalArgumentException("The node is not persisted yet");
		}
		if (node.getCluster() == null) {
			throw new IllegalArgumentException("The node does not have a cluster");
		}
		return dataPath.resolve("vagrant").resolve(node.getCluster().getName())
				.resolve(node.getId().toString());
	}

	private String getVagrantfileContent(Node node) {
		if (!boxes.hasSystem(node.getSystemSpecification())) {
			throw new UnsupportedOperationException(
					"There is no vagrant box for this system specification");
		}

		String boxName = boxes.getName(node.getSystemSpecification());
		return String.format(VAGRANTFILE_TEMPLATE, boxName, node.getMemory().toMiB(),
				node.getCpuCount(), node.getCluster().getName() + "-" + node.getId());
	}
}
