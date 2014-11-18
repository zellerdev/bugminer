package de.unistuttgart.iste.rss.bugminer.computing.vagrant;

import static org.mockito.Mockito.*;
import de.unistuttgart.iste.rss.bugminer.computing.MemoryQuantity;
import de.unistuttgart.iste.rss.bugminer.model.Architecture;
import de.unistuttgart.iste.rss.bugminer.model.Cluster;
import de.unistuttgart.iste.rss.bugminer.model.Node;
import de.unistuttgart.iste.rss.bugminer.model.OperatingSystem;
import de.unistuttgart.iste.rss.bugminer.model.SystemSpecification;

public class VagrantTestData {
	public static final String BOX_NAME = "thebox";
	public static final String CLUSTER_NAME = "thecluster";
	public static final int NODE_ID = 123;
	public static final SystemSpecification SPEC = new SystemSpecification(OperatingSystem.LINUX,
			Architecture.X86_64, "Ubuntu", "14.04");

	public static Node prepareNode() {
		Node node = mock(Node.class);
		Cluster cluster = mock(Cluster.class);
		when(node.getCluster()).thenReturn(cluster);
		when(node.getSystemSpecification()).thenReturn(SPEC);
		when(cluster.getName()).thenReturn(CLUSTER_NAME);
		when(node.getId()).thenReturn(NODE_ID);
		when(node.getMemory()).thenReturn(MemoryQuantity.fromGiB(1));
		when(node.getCpuCount()).thenReturn(2);
		return node;
	}
}