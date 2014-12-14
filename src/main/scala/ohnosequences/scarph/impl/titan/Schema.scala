package ohnosequences.scarph.impl.titan

// Schema stuff:

import ohnosequences.cosas._, AnyTypeSet._, AnyFn._, AnyWrap._
import ohnosequences.cosas.ops.typeSet._
import ohnosequences.scarph._
import com.thinkaurelius.titan.core._
import com.thinkaurelius.titan.core.Multiplicity
import com.tinkerpop.blueprints.Direction
import com.thinkaurelius.titan.core.schema._
import shapeless._, poly._
import scala.reflect._


/* Here are methods for creating Titan schema from an abstract schema description */
object schema {

  /* This takes an edge type and returns Titan `Multiplicity` (i.e. edge arities) */
  trait EdgeTypeMultiplicity[ET <: AnyEdgeType] extends Fn1[ET] with Out[Multiplicity]

  object EdgeTypeMultiplicity extends EdgeTypeMultiplicity_2 {

    implicit def one2one[ET <: AnyEdgeType { 
      type InC <: ExactlyOne.type
      type OutC <: ExactlyOne.type
    }]: EdgeTypeMultiplicity[ET] =
    new EdgeTypeMultiplicity[ET] { def apply(et: In1): Out = Multiplicity.ONE2ONE }
  }

  trait EdgeTypeMultiplicity_2 extends EdgeTypeMultiplicity_3 {

    implicit def one2many[ET <: AnyEdgeType {
      type InC <: ExactlyOne.type
      type OutC <: AnyContainer
    }]: EdgeTypeMultiplicity[ET] =
    new EdgeTypeMultiplicity[ET] { def apply(et: In1): Out = Multiplicity.ONE2MANY }

    implicit def many2one[ET <: AnyEdgeType {
      type InC <: AnyContainer
      type OutC <: ExactlyOne.type
    }]: EdgeTypeMultiplicity[ET] =
    new EdgeTypeMultiplicity[ET] { def apply(et: In1): Out = Multiplicity.MANY2ONE }
  }

  trait EdgeTypeMultiplicity_3 {

    implicit def many2many[ET <: AnyEdgeType {
      type InC <: AnyContainer
      type OutC <: AnyContainer
    }]: EdgeTypeMultiplicity[ET] =
    new EdgeTypeMultiplicity[ET] { def apply(et: In1): Out = Multiplicity.MULTI }
  }


  /* Following `Poly1` functions create separate Titan schema elements from
     the scarph properties/vertices/edges/indexes. They return functions of
     the `TitanManagement => TitanManagement` type, so that we can iterate them
     later on the schema type-sets.
  */
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

  object propertyLabel extends Poly1 {
    implicit def default[P <: AnyProp] = at[P]{ _.label }
  }

  object addIndex extends Poly1 {
    implicit def localIx[Ix <: AnyLocalEdgeIndex]
      (implicit propLabels: MapToList[propertyLabel.type, Ix#Properties] with InContainer[String]) =
      at[Ix]{ (ix: Ix) => { (m: TitanManagement) =>
          val direction: Direction = (ix.indexType: AnyLocalIndexType) match {
            case OnlySourceCentric => Direction.OUT
            case OnlyTargetCentric => Direction.IN
            case BothEndsCentric   => Direction.BOTH
          }
          val lbl: EdgeLabel = m.getEdgeLabel(ix.indexedType.label)
          val props: List[PropertyKey] = propLabels(ix.properties).map{ m.getPropertyKey(_) }

          m.buildEdgeIndex(lbl, ix.label, direction, props: _*) : TitanIndex
        }
      }

    implicit def vertexIx[Ix <: AnyCompositeIndex { type IndexedType <: AnyVertexType }]
      (implicit propLabels: MapToList[propertyLabel.type, Ix#Properties] with InContainer[String]) =
      at[Ix]{ (ix: Ix) => { (m: TitanManagement) =>

          propLabels(ix.properties)
            .foldLeft(m.buildIndex(ix.label, classOf[com.tinkerpop.blueprints.Vertex])){
              (builder, lbl) => builder.addKey(m.getPropertyKey(lbl))
            }.buildCompositeIndex : TitanIndex
        }
      }

    implicit def edgeIx[Ix <: AnyCompositeIndex { type IndexedType <: AnyEdgeType }]
      (implicit propLabels: MapToList[propertyLabel.type, Ix#Properties] with InContainer[String]) =
      at[Ix]{ (ix: Ix) => { (m: TitanManagement) =>

          propLabels(ix.properties)
            .foldLeft(m.buildIndex(ix.label, classOf[com.tinkerpop.blueprints.Edge])){
              (builder, lbl) => builder.addKey(m.getPropertyKey(lbl))
            }.buildCompositeIndex : TitanIndex
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
                   InContainer[TitanManagement => TitanIndex]
    ) = {
      /* We want this to happen all in _one_ transaction */
      val mgmt = g.getManagementSystem

      propertiesMapper(gs.properties).map{ _.apply(mgmt) }
      edgeTypesMapper(gs.edgeTypes).map{ _.apply(mgmt) }
      vertexTypesMapper(gs.vertexTypes).map{ _.apply(mgmt) }
      indexMapper(gs.indexes).map{ _.apply(mgmt) }

      mgmt.commit
    }
  }

}
