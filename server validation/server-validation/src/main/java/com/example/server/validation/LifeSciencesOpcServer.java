import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.ManagedNamespace;
import org.eclipse.milo.opcua.sdk.server.model.nodes.objects.FolderNode;
import org.eclipse.milo.opcua.sdk.server.model.nodes.variables.BaseDataVariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LifeSciencesOpcServer {

    private static OpcUaServer server;

    public static void main(String[] args) throws Exception {
        server = new OpcUaServer();

        // Create namespace for the OPC server
        String namespaceUri = "urn:eclipse:milo:example";

        ManagedNamespace namespace = server.getNamespaceManager().registerAndAdd(
            namespaceUri, (namespaceIndex) -> new ManagedNamespace(server, namespaceIndex)
        );

        // Start the server
        server.startup().get();

        // Create a folder for life sciences process simulation
        UaFolderNode folder = new UaFolderNode(server.getNodeMap(),
                new NodeId(1, "LifeSciencesProcess"),
                new UaFolderNode(server.getNodeMap(), new NodeId(1, "LifeSciencesFolder"), "Life Sciences Process")
        );

        // Create simulation variables
        BaseDataVariableNode temperatureNode = createVariable("Temperature", 25.0);
        BaseDataVariableNode pressureNode = createVariable("Pressure", 101.3);
        BaseDataVariableNode humidityNode = createVariable("Humidity", 50.0);

        // Add nodes to folder
        folder.addComponent(temperatureNode);
        folder.addComponent(pressureNode);
        folder.addComponent(humidityNode);

        // Schedule a task to simulate real-time value changes
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            double temp = 20 + Math.random() * 10;
            double pressure = 100 + Math.random() * 5;
            double humidity = 40 + Math.random() * 20;

            temperatureNode.setValue(new DataValue(temp));
            pressureNode.setValue(new DataValue(pressure));
            humidityNode.setValue(new DataValue(humidity));

        }, 0, 2, TimeUnit.SECONDS);
    }

    private static BaseDataVariableNode createVariable(String name, double initialValue) {
        NodeId nodeId = new NodeId(1, name);
        UaVariableNode node = new UaVariableNode(server.getNodeMap(), nodeId, name);
        node.setValue(new DataValue(initialValue));
        return (BaseDataVariableNode) node;
    }
}
