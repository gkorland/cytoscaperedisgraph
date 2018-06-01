# Cytoscape plugin for Neo4j

Neo4j Graphs are often too large for Cytoscape, this plugin allows you to write Cypher queries and import the result as a network. 
Queries can be parameterized and stored for reuse.  

The plugin can be downloaded from the [Cytoscape App Store](http://apps.cytoscape.org/apps/cytoscapeneo4jplugin)

## Features
- Connect to Neo4j with a username/password
- Import all nodes and edges from Neo4j into Cytoscape
- Import a cypher query into Cystoscape
- Import a stored query ([template](doc/template.md)) into Cytoscape
- Export a Cytoscape Network to Neo4j
- Get shortest path between selected nodes
- Show all edges (relationships) between all nodes in the network or only between selected nodes
- Expand all nodes in the network
- Expand single node (Context menu)
- Expand single node through a single edge (relationships) (Context menu)