package de.unistuttgart.iste.rss.bugminer.testutils;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.unistuttgart.iste.rss.bugminer.computing.NodeConnection;
import de.unistuttgart.iste.rss.bugminer.computing.vagrant.NodeConnectionFactory;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.ExternalResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.unistuttgart.iste.rss.bugminer.computing.SshConfig;
import de.unistuttgart.iste.rss.bugminer.computing.vagrant.VagrantStrategy;
import de.unistuttgart.iste.rss.bugminer.config.EntityFactory;
import de.unistuttgart.iste.rss.bugminer.model.entities.Cluster;
import de.unistuttgart.iste.rss.bugminer.model.entities.Node;
import de.unistuttgart.iste.rss.bugminer.model.entities.SystemSpecification;

@Component
@Scope("prototype")
public class VagrantMachine extends ExternalResource {
	private SshConfig sshConfig;
	private NodeConnection nodeConnection;

	@Autowired
	private VagrantStrategy vagrant;

	@Autowired
	private EntityFactory entityFactory;

	@Autowired
	private NodeConnectionFactory nodeConnectionFactory;

	private Node node;

	private final Logger logger = Logger.getLogger(VagrantMachine.class.getName());

	private Node createNode() throws IOException {
		Cluster cluster = entityFactory.make(Cluster.class);
		cluster.setName("unittest-" + UUID.randomUUID());
		cluster.setProvider("vagrant");
		node = vagrant.createNode(cluster);
		return node;
	}

	@Override
	protected void before() throws Throwable {
		if (!vagrant.isAvailable()) {
			throw new AssumptionViolatedException("Vagrant is required, but not installed");
		}

		try {
			node = createNode();
			vagrant.startNode(node);
			sshConfig = vagrant.getSshConfig(node);
			nodeConnection = nodeConnectionFactory.connectTo(node);
		} catch (Exception e) {
			throw new RuntimeException("Unable to prepare vagrant node for VagrantMachine rule", e);
		}
	}

	public SshConfig getSshConfig() {
		return sshConfig;
	}

	public Node getNode() {
		return node;
	}

	public NodeConnection getNodeConnection() {
		return nodeConnection;
	}

	@Override
	protected void after() {
		try {
			vagrant.destroyNode(node);
			nodeConnection.close();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Unable to destroy vagrant node for Vagrantmachine rule", e);
		}
	}
}
