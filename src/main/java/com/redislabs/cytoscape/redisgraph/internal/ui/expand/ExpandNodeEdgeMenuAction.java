package com.redislabs.cytoscape.redisgraph.internal.ui.expand;

import com.redislabs.cytoscape.redisgraph.internal.Services;
import com.redislabs.cytoscape.redisgraph.internal.client.CypherQuery;
import com.redislabs.cytoscape.redisgraph.internal.client.ClientException;
import com.redislabs.cytoscape.redisgraph.internal.tasks.ExpandNodeTask;
import com.redislabs.cytoscape.redisgraph.internal.tasks.ExpandNodeTask.Direction;
import com.redislabs.cytoscape.redisgraph.internal.tasks.importgraph.DefaultImportStrategy;
import com.redislabs.cytoscape.redisgraph.internal.tasks.importgraph.ImportGraphStrategy;
import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;

import javax.swing.*;


public class ExpandNodeEdgeMenuAction implements CyNodeViewContextMenuFactory {

    private final transient Services services;
    private ImportGraphStrategy importGraphStrategy;
    private JMenu menu;
    private CyNetworkView networkView;
    private View<CyNode> nodeView;
    private Direction direction;

    public ExpandNodeEdgeMenuAction(Services services) {
        super();
        this.importGraphStrategy = new DefaultImportStrategy();
        this.services = services;
    }

    public void addMenuItemsEdges(Record record) {
        String result = record.get("r", "");
        String menuTitle = this.direction == Direction.IN ? "<- " : " - ";
        menuTitle = menuTitle + result + (this.direction == Direction.OUT ? " ->" : " - ");
        JMenuItem menuItem = new JMenuItem(menuTitle);
        ExpandNodeTask expandNodeTask = new ExpandNodeTask(nodeView, networkView, this.services, true);
        expandNodeTask.setEdge("`" + result + "`");
        menuItem.addActionListener(expandNodeTask);

        this.menu.add(menuItem);
    }


    @Override
    public CyMenuItem createMenuItem(CyNetworkView networkView, View<CyNode> nodeView) {
        this.networkView = networkView;
        this.nodeView = nodeView;
        CyNode cyNode = (CyNode) nodeView.getModel();
        try {
            Long refid = networkView.getModel().getRow(cyNode).get(this.importGraphStrategy.getRefIDName(), Long.class);
            this.menu = new JMenu("Expand node on:");

            this.direction = Direction.BIDIRECTIONAL;
            String query = "match (n)-[r]-() where ID(n) = " + refid + " return distinct type(r) as r";
            CypherQuery cypherQuery = CypherQuery.builder().query(query).build();
            StatementResult result = this.services.getRedisGraphClient().getResults(cypherQuery);
            result.forEachRemaining(this::addMenuItemsEdges);

            this.direction = Direction.IN;
            query = "match (n)<-[r]-() where ID(n) = " + refid + " return distinct type(r) as r";
            cypherQuery = CypherQuery.builder().query(query).build();
            result = this.services.getRedisGraphClient().getResults(cypherQuery);
            result.forEachRemaining(this::addMenuItemsEdges);

            this.direction = Direction.OUT;
            query = "match (n)-[r]->() where ID(n) = " + refid + " return distinct type(r) as r";
            cypherQuery = CypherQuery.builder().query(query).build();
            result = this.services.getRedisGraphClient().getResults(cypherQuery);
            result.forEachRemaining(this::addMenuItemsEdges);

            CyMenuItem cyMenuItem = new CyMenuItem(this.menu, 0.5f);

            return cyMenuItem;

        } catch (ClientException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return null;

    }


}
