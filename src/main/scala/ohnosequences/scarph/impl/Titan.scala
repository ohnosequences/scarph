package ohnosequences.scarph.impl

import shapeless._
import ohnosequences.cosas._, AnyFn._ 
import ohnosequences.cosas.ops.typeSet._
import ohnosequences.scarph._
import com.thinkaurelius.titan.core._, schema._
import scala.collection.JavaConversions._

case class titan(val graph: TitanGraph) {

  // val mgmt: TitanManagement = graph.getManagementSystem

  import com.tinkerpop.blueprints.Compare._
  import com.tinkerpop.blueprints.{ Query => BQuery }

  case object toBlueprintsCondition extends Poly1 {
    implicit def eq[C <: AnyEqual]          = at[C] { c => { q: BQuery => q.has(c.property.label, EQUAL, c.value) } }
    implicit def ne[C <: AnyNotEqual]       = at[C] { c => { q: BQuery => q.has(c.property.label, NOT_EQUAL, c.value) } }
    implicit def le[C <: AnyLess]           = at[C] { c => { q: BQuery => q.has(c.property.label, LESS_THAN, c.value) } }
    implicit def lq[C <: AnyLessOrEqual]    = at[C] { c => { q: BQuery => q.has(c.property.label, LESS_THAN_EQUAL, c.value) } }
    implicit def gr[C <: AnyGreater]        = at[C] { c => { q: BQuery => q.has(c.property.label, GREATER_THAN, c.value) } }
    implicit def gq[C <: AnyGreaterOrEqual] = at[C] { c => { q: BQuery => q.has(c.property.label, GREATER_THAN_EQUAL, c.value) } }
  }

  trait ToBlueprintsPredicate[P <: AnyPredicate] extends Fn2[P, BQuery] with Out[BQuery]

  object ToBlueprintsPredicate {

    implicit def convert[P <: AnyPredicate]
      (implicit m: MapFoldSet[toBlueprintsCondition.type, P#Conditions, BQuery => BQuery]):
        ToBlueprintsPredicate[P] =
    new ToBlueprintsPredicate[P] {
      def apply(p: In1, q: In2): Out = {
        def id[A]: A => A = x => x
        def compose[A, B, C](f: A => B, g: B => C): A => C = x => g(f(x))
        val addConditions = m(p.conditions, id, compose)
        addConditions(q)
      }
    }
  }

  implicit def evalSimpleVertexQuery[
    V <: AnyVertexType,
    P <: AnyPredicate.On[V]
  ](implicit transform: ToBlueprintsPredicate[P]): 
      EvalPath[P, Query[V], TitanVertex] =
  new EvalPath[P, Query[V], TitanVertex] {
    def apply(in: In, path: Path): Out = {
      transform(in.value, graph.query)
        .vertices.asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]]
        .toList.map{ new LabeledBy[TitanVertex, P#ElementType]( _ ) }
    }
  }

  implicit def evalSimpleEdgeQuery[
    E <: AnyEdgeType,
    P <: AnyPredicate.On[E]
  ](implicit transform: ToBlueprintsPredicate[P]): 
      EvalPath[P, Query[E], TitanEdge] =
  new EvalPath[P, Query[E], TitanEdge] {
    def apply(in: In, path: Path): Out = {
      transform(in.value, graph.query)
        .edges.asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]
        .toList.map{ new LabeledBy[TitanEdge, P#ElementType]( _ ) }
    }
  }


  import com.tinkerpop.blueprints.Direction

  implicit def evalGetVertexProperty[P <: AnyProp { type Owner <: AnyVertexType }]:
      EvalPath[TitanVertex, GetProperty[P], P#Raw] =
  new EvalPath[TitanVertex, GetProperty[P], P#Raw] {
    def apply(in: In, path: Path): Out = List(path.prop( in.value.getProperty[P#Raw](path.prop.label) ))
  }

  implicit def evalGetEdgeProperty[P <: AnyProp { type Owner <: AnyEdgeType }]:
      EvalPath[TitanEdge, GetProperty[P], P#Raw] =
  new EvalPath[TitanEdge, GetProperty[P], P#Raw] {
    def apply(in: In, path: Path): Out = List(path.prop( in.value.getProperty[P#Raw](path.prop.label) ))
  }

  implicit def evalGetSource[E <: AnyEdgeType]:
      EvalPath[TitanEdge, GetSource[E], TitanVertex] =
  new EvalPath[TitanEdge, GetSource[E], TitanVertex] {
    def apply(in: In, t: Path): Out = List(new LabeledBy[TitanVertex, E#SourceType]( in.value.getVertex(Direction.OUT) ))
  }

  implicit def evalGetTarget[E <: AnyEdgeType]:
      EvalPath[TitanEdge, GetTarget[E], TitanVertex] =
  new EvalPath[TitanEdge, GetTarget[E], TitanVertex] {
    def apply(in: In, t: Path): Out = List(new LabeledBy[TitanVertex, E#TargetType]( in.value.getVertex(Direction.IN) ))
  }

  implicit def evalGetOutEdges[E <: AnyEdgeType]:
      EvalPath[TitanVertex, GetOutEdges[E], TitanEdge] =
  new EvalPath[TitanVertex, GetOutEdges[E], TitanEdge] {
    def apply(in: In, path: Path): Out = {
      val mgmt = graph.getManagementSystem
      val lbl = mgmt.getEdgeLabel(path.edge.label)
      val result = in.value
        .getTitanEdges(Direction.OUT, lbl)
        .toList.map{ new LabeledBy[TitanEdge, E]( _ ) }
      mgmt.commit
      result
    }
  }

  implicit def evalGetInEdges[E <: AnyEdgeType]:
      EvalPath[TitanVertex, GetInEdges[E], TitanEdge] =
  new EvalPath[TitanVertex, GetInEdges[E], TitanEdge] {
    def apply(in: In, path: Path): Out = {
      val mgmt = graph.getManagementSystem
      val lbl = mgmt.getEdgeLabel(path.edge.label)
      val result = in.value
        .getTitanEdges(Direction.IN, lbl)
        .toList.map{ new LabeledBy[TitanEdge, E]( _ ) }
      mgmt.commit
      result
    }
  }

  implicit def evalGetOutVertices[E <: AnyEdgeType]:
      EvalPath[TitanVertex, GetOutVertices[E], TitanVertex] =
  new EvalPath[TitanVertex, GetOutVertices[E], TitanVertex] {
    def apply(in: In, path: Path): Out = {
      in.value
        .getVertices(Direction.OUT, path.edge.label)
        .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]]
        .toList.map{ new LabeledBy[TitanVertex, E#TargetType]( _ ) }
    }
  }

  implicit def evalGetInVertices[E <: AnyEdgeType]:
      EvalPath[TitanVertex, GetInVertices[E], TitanVertex] =
  new EvalPath[TitanVertex, GetInVertices[E], TitanVertex] {
    def apply(in: In, path: Path): Out = {
      in.value
        .getVertices(Direction.IN, path.edge.label)
        .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]]
        .toList.map{ new LabeledBy[TitanVertex, E#SourceType]( _ ) }
    }
  }

}

// Schema stuff:

import ohnosequences.cosas._, AnyTypeSet._, AnyFn._, AnyWrap._
import ohnosequences.cosas.ops.typeSet._
import ohnosequences.scarph._
import com.thinkaurelius.titan.core._
import com.thinkaurelius.titan.core.Multiplicity
import com.thinkaurelius.titan.core.schema._
import shapeless._, poly._
import scala.reflect._


object titanSchema {

  trait EdgeTypeMultiplicity[ET <: AnyEdgeType] extends Fn1[ET] with Out[Multiplicity]

  object EdgeTypeMultiplicity {

    implicit def one2one[ET <: AnyEdgeType { 
      type InArity <: AnyOneArity
      type OutArity <: AnyOneArity
    }]: EdgeTypeMultiplicity[ET] =
    new EdgeTypeMultiplicity[ET] { def apply(et: In1): Out = Multiplicity.ONE2ONE }

    implicit def one2many[ET <: AnyEdgeType {
      type InArity <: AnyOneArity
      type OutArity <: AnyManyArity
    }]: EdgeTypeMultiplicity[ET] =
    new EdgeTypeMultiplicity[ET] { def apply(et: In1): Out = Multiplicity.ONE2MANY }

    implicit def many2one[ET <: AnyEdgeType {
      type InArity <: AnyManyArity
      type OutArity <: AnyOneArity
    }]: EdgeTypeMultiplicity[ET] =
    new EdgeTypeMultiplicity[ET] { def apply(et: In1): Out = Multiplicity.MANY2ONE }

    implicit def many2many[ET <: AnyEdgeType {
      type InArity <: AnyManyArity
      type OutArity <: AnyManyArity
    }]: EdgeTypeMultiplicity[ET] =
    new EdgeTypeMultiplicity[ET] { def apply(et: In1): Out = Multiplicity.MULTI }
  }


  object addPropertyKey extends Poly1 {
    implicit def default[P <: AnyProp](implicit cc: ClassTag[P#Raw]) = 
      at[P]{ (prop: P) =>
        { (m: TitanManagement) =>
          val clazz = cc.runtimeClass.asInstanceOf[Class[P#Raw]]
          m.makePropertyKey(prop.label).dataType(clazz).make
        }
      }
  }

  object addVertexLabel extends Poly1 {
    implicit def default[VT <: AnyVertexType] = at[VT]{ (vt: VT) =>
      { (m: TitanManagement) => m.makeVertexLabel(vt.label).make }
    }
  }

  object addEdgeLabel extends Poly1 {
    implicit def default[ET <: AnyEdgeType](implicit multi: EdgeTypeMultiplicity[ET]) = at[ET]{ (et: ET) =>
      { (m: TitanManagement) => m.makeEdgeLabel(et.label).multiplicity(multi(et)).make }
    }
  }

  object addIndex extends Poly1 {
    implicit def vertexIx[Ix <: AnySimpleIndex { type IndexedType <: AnyVertexType }] = 
      at[Ix]{ (ix: Ix) => { (m: TitanManagement) =>
          m.buildIndex(ix.label, classOf[com.tinkerpop.blueprints.Vertex])
            .indexOnly(m.getVertexLabel(ix.indexedType.label))
            .addKey(m.getPropertyKey(ix.property.label))
            .buildCompositeIndex
        }
      }

    implicit def edgeIx[Ix <: AnySimpleIndex { type IndexedType <: AnyEdgeType }] = 
      at[Ix]{ (ix: Ix) => { (m: TitanManagement) =>
          m.buildIndex(ix.label, classOf[com.tinkerpop.blueprints.Edge])
            .indexOnly(m.getEdgeLabel(ix.indexedType.label))
            .addKey(m.getPropertyKey(ix.property.label))
            .buildCompositeIndex
        }
      }
  }

  implicit def titanGraphOps(g: TitanGraph): 
    TitanGraphOps = 
    TitanGraphOps(g)

  case class TitanGraphOps(g: TitanGraph) {

    def createSchema[GS <: AnySchema](gs: GS)(implicit
      propertiesMapper: MapToList[addPropertyKey.type, gs.Properties] with 
                        InContainer[TitanManagement => PropertyKey],
      edgeTypesMapper: MapToList[addEdgeLabel.type, gs.EdgeTypes] with 
                       InContainer[TitanManagement => EdgeLabel],
      vertexTypesMapper: MapToList[addVertexLabel.type, gs.VertexTypes] with 
                         InContainer[TitanManagement => VertexLabel],
      indexMapper: MapToList[addIndex.type, gs.Indexes] with 
                   InContainer[TitanManagement => TitanGraphIndex]
    ) = {
      // we want all this happen in a one transaction
      val mgmt = g.getManagementSystem

      propertiesMapper(gs.properties).map{ _.apply(mgmt) }
      edgeTypesMapper(gs.edgeTypes).map{ _.apply(mgmt) }
      vertexTypesMapper(gs.vertexTypes).map{ _.apply(mgmt) }
      indexMapper(gs.indexes).map{ _.apply(mgmt) }

      mgmt.commit
    }
  }

}
