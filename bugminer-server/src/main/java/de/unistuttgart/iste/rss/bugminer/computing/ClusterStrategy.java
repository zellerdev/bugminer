package de.unistuttgart.iste.rss.bugminer.computing;

import java.io.IOException;

import de.unistuttgart.iste.rss.bugminer.model.entities.Cluster;
import de.unistuttgart.iste.rss.bugminer.model.entities.Node;
import de.unistuttgart.iste.rss.bugminer.model.entities.NodeStatus;

public interface ClusterStrategy {
	boolean isAvailable();

	void initializeCluster(Cluster cluster) throws IOException;

	void initializeNode(Node node) throws IOException;

	NodeStatus getNodeStatus(Node node) throws IOException;

	void startNode(Node node) throws IOException;

	void stopNode(Node node) throws IOException;

	void destroyNode(Node node) throws IOException;

	SshConfig getSshConfig(Node node) throws IOException;
}