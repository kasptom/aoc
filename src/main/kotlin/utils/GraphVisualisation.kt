package utils

import org.jgrapht.Graph
import org.jgrapht.graph.SimpleGraph
import org.jgrapht.nio.Attribute
import org.jgrapht.nio.DefaultAttribute
import org.jgrapht.nio.dot.DOTExporter
import java.io.StringWriter
import java.io.Writer

class GraphVisualisation {
    interface GraphVertex : Comparable<GraphVertex> {
        val v: String
    }

    interface GraphEdge {
        val v1: GraphVertex
        val v2: GraphVertex
        val label: String
    }

    fun viewGraph(vertices: Set<GraphVertex>, edges: Set<GraphEdge>) {
        val graph: Graph<GraphVertex, GraphEdge> = SimpleGraph(GraphEdge::class.java)

        vertices.sorted().forEach {
            graph.addVertex(it)
        }
        edges.sortedBy { it.v1 }.forEach {
            graph.addEdge(it.v1, it.v2, it)
        }
        renderGraph(graph)
    }

    private fun renderGraph(graph: Graph<GraphVertex, GraphEdge>) {
        val exporter: DOTExporter<GraphVertex, GraphEdge> =
            DOTExporter { v -> v.v }
        exporter.setEdgeAttributeProvider { e ->
            val map: MutableMap<String, Attribute> =
                LinkedHashMap()
            map["label"] = DefaultAttribute.createAttribute(e.label)
//            map["orientation"] = DefaultAttribute.createAttribute("portrait")
            map
        }
        exporter.setVertexAttributeProvider { v ->
            val map: MutableMap<String, Attribute> =
                LinkedHashMap()
            map["label"] = DefaultAttribute.createAttribute(v.toString())
//            map["orientation"] = DefaultAttribute.createAttribute("portrait")
            map
        }
        exporter.setGraphAttributeProvider {
            val map: MutableMap<String, Attribute> =
                LinkedHashMap()
//            map["orientation"] = DefaultAttribute.createAttribute("landscape")
            map["ordering"] = DefaultAttribute.createAttribute("out")
            map
        }

        val writer: Writer = StringWriter()
        exporter.exportGraph(graph, writer)
        println(writer)
    }
}