package ohnosequences.scarph.impl.titan

import ohnosequences.pointless._, AnyTypeSet._, AnyFn._, AnyWrap._
import ohnosequences.pointless.ops.typeSet._
import ohnosequences.scarph._
import com.thinkaurelius.titan.core._
import com.thinkaurelius.titan.core.Multiplicity
import com.thinkaurelius.titan.core.schema._
import shapeless._, poly._
import scala.reflect._


object TitanGraphSchema {

  implicit def multiplicity[ET <: AnyEdgeType](et: ET): Multiplicity = {
    (et.in, et.out) match {
      case (One, One)  => Multiplicity.ONE2ONE
      case (One, Many) => Multiplicity.ONE2MANY
      case (Many, One) => Multiplicity.MANY2ONE
      case           _ => Multiplicity.MULTI
    }
  }

  object addPropertyKey extends Poly1 {
    implicit def default[P <: AnyProperty](implicit cc: ClassTag[P#Raw]) = 
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
    implicit def default[ET <: AnyEdgeType] = at[ET]{ (et: ET) =>
      { (m: TitanManagement) => m.makeEdgeLabel(et.label).multiplicity(multiplicity(et)).make }
    }
  }

  object addIndex extends Poly1 {
    implicit def vertexIx[Ix <: AnyCompositeIndex { type IndexedType <: AnyVertexType }] = 
      at[Ix]{ addIt(classOf[com.tinkerpop.blueprints.Vertex], _) }

    implicit def edgeIx[Ix <: AnyCompositeIndex { type IndexedType <: AnyEdgeType }] = 
      at[Ix]{ addIt(classOf[com.tinkerpop.blueprints.Edge], _) }

    def addIt[E <: com.tinkerpop.blueprints.Element, Ix <: AnyCompositeIndex](cl: Class[E], ix: Ix) = { 
      (m: TitanManagement) =>
        val propertyKey = m.getPropertyKey(ix.property.label)
        m.buildIndex(ix.label, cl)
          .addKey(propertyKey)
          .buildCompositeIndex()
    }
  }

  implicit def titanGraphOps(g: TitanGraph): 
    TitanGraphOps = 
    TitanGraphOps(g)

  case class TitanGraphOps(g: TitanGraph) {

    def createSchema[GS <: AnyGraphSchema, Ps <: AnyTypeSet](gs: GS)(implicit
        aggregateProps: SchemaProperties[GS] { type Out = Ps },
        propertiesMapper: MapToList[addPropertyKey.type, Ps] with 
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
        // property keys
        val props = aggregateProps(gs)
        propertiesMapper(props).map{ _.apply(mgmt) }
        // edge labels
        edgeTypesMapper(gs.edgeTypes).map{ _.apply(mgmt) }
        // vertex labels
        vertexTypesMapper(gs.vertexTypes).map{ _.apply(mgmt) }
        // indexes
        indexMapper(gs.indexes).map{ _.apply(mgmt) }

        mgmt.commit
      }
  }

}
