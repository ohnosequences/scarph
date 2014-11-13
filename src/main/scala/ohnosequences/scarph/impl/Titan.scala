package ohnosequences.scarph.impl

import ohnosequences.cosas._, AnyFn._
import ohnosequences.scarph._
import com.thinkaurelius.titan.core._, schema._
import scala.collection.JavaConversions._

case class titan(val graph: TitanGraph) {

  // val mgmt: TitanManagement = graph.getManagementSystem
  trait ToTitanCondition[C <: AnyCompareCondition] extends Fn1[C] with Out[com.tinkerpop.blueprints.Compare]

  object ToTitanCondition {

    implicit def default[C <: AnyCompareCondition]:
        ToTitanCondition[C] =
    new ToTitanCondition[C] {
      def apply(in: In1): Out = com.tinkerpop.blueprints.Compare.EQUAL
    }
  }

  implicit def evalSimpleVertexQuery[
    V <: AnyVertexType,
    P <: AnyAndPredicate { 
      type Body <: AnyEmptyPredicate
      type Condition <: AnyCompareCondition
      type ElementType = V
    }](implicit toTitan: ToTitanCondition[P#Condition]):
      EvalPath[P, Query[V], TitanVertex] =
  new EvalPath[P, Query[V], TitanVertex] {
    def apply(in: In, path: Path): Out = {
      val cond = in.value.condition
      graph.query.has(cond.property.label, toTitan(cond), cond.value)
        .vertices.asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]]
        .toList.map{ new LabeledBy[TitanVertex, P#ElementType]( _ ) }
    }
  }

  implicit def evalSimpleEdgeQuery[
    E <: AnyEdgeType,
    P <: AnyAndPredicate { 
      type Body <: AnyEmptyPredicate
      type Condition <: AnyCompareCondition
      type ElementType = E
    }](implicit toTitan: ToTitanCondition[P#Condition]):
      EvalPath[P, Query[E], TitanEdge] =
  new EvalPath[P, Query[E], TitanEdge] {
    def apply(in: In, path: Path): Out = {
      val cond = in.value.condition
      graph.query.has(cond.property.label, toTitan(cond), cond.value)
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
    def apply(in: In, path: Path): Out = List(new LabeledBy[TitanVertex, E#Source]( in.value.getVertex(Direction.OUT) ))
  }

  implicit def evalGetTarget[E <: AnyEdgeType]:
      EvalPath[TitanEdge, GetTarget[E], TitanVertex] =
  new EvalPath[TitanEdge, GetTarget[E], TitanVertex] {
    def apply(in: In, path: Path): Out = List(new LabeledBy[TitanVertex, E#Target]( in.value.getVertex(Direction.IN) ))
  }

  implicit def evalGetOutEdges[E <: AnyEdgeType]:
      EvalPath[TitanVertex, GetOutEdges[E], TitanEdge] =
  new EvalPath[TitanVertex, GetOutEdges[E], TitanEdge] {
    def apply(in: In, path: Path): Out = {
      in.value
        .getEdges(Direction.OUT, path.edge.label)
        .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]
        .toList.map{ new LabeledBy[TitanEdge, E]( _ ) }
    }
  }

  implicit def evalGetInEdges[E <: AnyEdgeType]:
      EvalPath[TitanVertex, GetInEdges[E], TitanEdge] =
  new EvalPath[TitanVertex, GetInEdges[E], TitanEdge] {
    def apply(in: In, path: Path): Out = {
      in.value
        .getEdges(Direction.IN, path.edge.label)
        .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]
        .toList.map{ new LabeledBy[TitanEdge, E]( _ ) }
      // FIXME: to avoid casting here, we should use getTitanEdges instead of getEdges,
      // but it requires having an EdgeLabel, which we can get only from TitanGraph#TitanManagement,
      // so maybe we can have it as a common evaluation context
    }
  }

  implicit def evalGetOutVertices[E <: AnyEdgeType]:
      EvalPath[TitanVertex, GetOutVertices[E], TitanVertex] =
  new EvalPath[TitanVertex, GetOutVertices[E], TitanVertex] {
    def apply(in: In, path: Path): Out = {
      in.value
        .getVertices(Direction.OUT, path.edge.label)
        .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]]
        .toList.map{ new LabeledBy[TitanVertex, E#Target]( _ ) }
    }
  }

  implicit def evalGetInVertices[E <: AnyEdgeType]:
      EvalPath[TitanVertex, GetInVertices[E], TitanVertex] =
  new EvalPath[TitanVertex, GetInVertices[E], TitanVertex] {
    def apply(in: In, path: Path): Out = {
      in.value
        .getVertices(Direction.IN, path.edge.label)
        .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]]
        .toList.map{ new LabeledBy[TitanVertex, E#Source]( _ ) }
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
