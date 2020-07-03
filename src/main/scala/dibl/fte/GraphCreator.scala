/*
 Copyright 2016 Jo Pol
 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see http://www.gnu.org/licenses/gpl.html dibl
*/
package dibl.fte

import java.util

import dibl.fte.data.{ Edge, Graph, Vertex }
import dibl.fte.layout.OneFormTorus
import dibl.proto.TilesConfig
import dibl.{ Diagram, LinkProps, NewPairDiagram, PairDiagram, ThreadDiagram }
import scala.collection.JavaConverters._
object GraphCreator {

  /**
    * @param urlQuery parameters for: https://d-bl.github.io/GroundForge/tiles?
    *                 For now the tile must be a checker tile and
    *                 the patch size must span 3 columns and 2 rows of checker tiles.
    *                 A simplified ascii-art view of a pattern definition:
    *                 +----+----+----+
    *                 |....|....|....|
    *                 |....|....|....|
    *                 +----+----+----+
    *                 |....|XXXX|....|
    *                 |....|XXXX|....|
    *                 +----+----+----+
    * @return The X's in the pattern definition are added to the returned graph.
    */
  def fromThreadDiagram(urlQuery: String): Option[Graph] = {
    val patternConfig = TilesConfig(urlQuery)
    val stitchConfigs = urlQuery.split("&")
      .filter(_.startsWith("droste"))
      .sortBy(identity)
      .map(_.replace("=.*", ""))
    val initialThreadDiagram = ThreadDiagram(NewPairDiagram.create(patternConfig))
    val diagram = stitchConfigs.foldLeft(initialThreadDiagram)(droste)
    val scale = Math.pow(2, stitchConfigs.length + 1).toInt
    graphFrom(getTopoLinks(diagram, scale, patternConfig))
  }

  /**
   * fold transformation
   *
   * @param threadDiagram accumulator
   * @param stitchConfig  listItem
   * @return
   */
  private def droste(threadDiagram: Diagram, stitchConfig: String): Diagram = {
    ThreadDiagram(PairDiagram(stitchConfig, threadDiagram))
  }

  def fromPairDiagram(urlQuery: String): Option[Graph] = {
    val config = TilesConfig(urlQuery)
    val diagram = NewPairDiagram.create(config)
    graphFrom(getTopoLinks(diagram, scale = 1, config))
  }

  def graphFrom(topoLinks: Seq[TopoLink]): Option[Graph] = {
    val graph = new Graph()

    // TODO for now this check prevents eternal loops for bandage/sheered pair diagrams
    if (topoLinks.exists(l => l.sourceId == l.targetId)) return None

    // create vertices
    implicit val vertexMap: Map[String, Vertex] = topoLinks
      .map(_.targetId).distinct.zipWithIndex
      .map { case (nodeId, i) =>
        // The initial coordinates don't matter as long as they are unique.
        nodeId -> graph.createVertex(i, 0)
      }.toMap
    println(vertexMap.keys.toArray.sortBy(identity).mkString(","))
    if (vertexMap.keys.head.length == 5) { // droste pattern
      println(topoLinks.mkString(";"))
    }

    // create edges
    implicit val edges: Array[Edge] = topoLinks.map { link =>
      graph.addNewEdge(new Edge(
        vertexMap(link.sourceId),
        vertexMap(link.targetId)
      ))
    }.toArray

    // add edges to vertices in clockwise order
    val linksBySource = topoLinks.groupBy(_.sourceId).map { case (id, links) =>
      id -> (links.filter(_.isRightOfSource) ++ links.filter(_.isLeftOfSource))
    }
    topoLinks.groupBy(_.targetId).foreach { case (id, links) =>
      val allFour = linksBySource(id) ++ links.filter(_.isLeftOfTarget) ++ links.filter(_.isRightOfTarget)
      println(s"$id: " + allFour.mkString(";"))
      allFour.map(topoLink => findEdge(topoLink).foreach(vertexMap(id).addEdge))
    }

    lazy val oldFaces = graph.getFaces
    lazy val edgesPerNewFace = Face.facesFrom(topoLinks).map(_.counterClockWise)

    val newFaces = edgesPerNewFace.map(topoLinks =>
      new dibl.fte.data.Face(toJavaEdgeList(topoLinks))
    ).asJava

    lazy val oldFacesAsString = oldFaces.toArray().map(toStr).mkString("old faces with vertex number:\n", "\n", "")
    lazy val newFacesAsString1 = newFaces.toArray().map(toStr).mkString("new faces with vertex number:\n", "\n", "")
    lazy val newFacesAsString2 = edgesPerNewFace.mkString("new faces with IDs showed online when hovering over a stitch:\n", "\n", "")
    lazy val logging = s"$oldFacesAsString\n$newFacesAsString1\n$newFacesAsString2"
    println(logging)

    if (new OneFormTorus(graph).layout(oldFaces))
      Some(graph)
    else None
  }

  private def toStr(a: Any) = {
    a.asInstanceOf[dibl.fte.data.Face].getEdges.toArray().map(toS).mkString(",")
  }

  private def toS(edge: Any) = {
    edge.toString.replaceAll(".00", "").replaceAll(".0,0.", "").replaceAll("-0", "")
  }

  private def toJavaEdgeList(topoLinks: Seq[TopoLink])
                            (implicit edges: Array[Edge],
                             vertexMap: Map[String, Vertex]
                            ): util.LinkedList[Edge] = {
    new java.util.LinkedList[Edge]() {
      topoLinks.foreach(findEdge(_).foreach(add))
    }
  }

  private def findEdge(link: TopoLink)
                      (implicit edges: Array[Edge],
                       vertexMap: Map[String, Vertex]
                      ): Option[Edge] = {
    edges.find(edge =>
      edge.getStart == vertexMap(link.sourceId) &&
        edge.getEnd == vertexMap(link.targetId)
    )
  }

  private def getTopoLinks(implicit diagram: Diagram, scale: Int, config: TilesConfig) = {
    TopoLink
      .simplify(diagram.links.filter(inCenterBottomTile))
      .sortBy(l => l.targetId -> l.sourceId)
  }

  private def inCenterBottomTile(link: LinkProps)
                                (implicit diagram: Diagram, scale: Int, config: TilesConfig): Boolean = {
    // The top and side tiles of a diagram may have irregularities along the outer edges.
    // So select links arriving in the center bottom checker tile.
    val cols = config.patchWidth / 3
    val rows = config.patchHeight / 2

    val target = diagram.node(link.target)
    val source = diagram.node(link.source)
    val y = unScale(target.y)
    val x = unScale(target.x)
    y >= rows && x >= cols && x < cols * 2 && !link.withPin && target.id != "" && source.id != ""
  }

  /** Revert [[NewPairDiagram]].toPoint
    *
    * @param i     value for x or y
    * @param scale value 1 or factor to also undo [[ThreadDiagram]].scale
    * @return x/y on canvas reduced to row/col number in the input
    */
  private def unScale(i: Double)(implicit scale: Int): Int = (i / scale / 15 - 2).toInt
}
