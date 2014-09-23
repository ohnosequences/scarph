package ohnosequences.scarph.impl.titan

import ohnosequences.pointless._, AnyTypeSet._, AnyFn._, AnyWrap._
import ohnosequences.pointless.ops.typeSet._
import ohnosequences.scarph._
import com.thinkaurelius.titan.core._
import com.thinkaurelius.titan.core.Multiplicity
import com.thinkaurelius.titan.core.schema._
import shapeless._, poly._
import scala.reflect._


object TitanSchemaType {

  implicit def one2one[ET <: AnyEdgeType with OneIn with OneOut](et: ET): Multiplicity = Multiplicity.ONE2ONE
  implicit def one2many[ET <: AnyEdgeType with OneIn with ManyOut](et: ET): Multiplicity = Multiplicity.ONE2MANY
  implicit def many2one[ET <: AnyEdgeType with ManyIn with OneOut](et: ET): Multiplicity = Multiplicity.MANY2ONE
  implicit def many2many[ET <: AnyEdgeType with ManyIn with ManyOut](et: ET): Multiplicity = Multiplicity.MULTI

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
    implicit def default[ET <: AnyEdgeType](implicit multi: ET => Multiplicity) = at[ET]{ (et: ET) =>
      { (m: TitanManagement) => m.makeEdgeLabel(et.label).multiplicity(multi(et)).make }
    }
  }

  object addIndex extends Poly1 {
    implicit def vertexIx[Ix <: AnyCompositeIndex { type IndexedType <: AnyVertexType }] = 
      at[Ix]{ (ix: Ix) => { (m: TitanManagement) =>
          m.buildIndex(ix.label, classOf[com.tinkerpop.blueprints.Vertex])
            .indexOnly(m.getVertexLabel(ix.indexedType.label))
            .addKey(m.getPropertyKey(ix.property.label))
            .buildCompositeIndex()
        }
      }

    implicit def edgeIx[Ix <: AnyCompositeIndex { type IndexedType <: AnyEdgeType }] = 
      at[Ix]{ (ix: Ix) => { (m: TitanManagement) =>
          m.buildIndex(ix.label, classOf[com.tinkerpop.blueprints.Edge])
            .indexOnly(m.getEdgeLabel(ix.indexedType.label))
            .addKey(m.getPropertyKey(ix.property.label))
            .buildCompositeIndex()
        }
      }
  }

  implicit def titanGraphOps(g: TitanGraph): 
    TitanGraphOps = 
    TitanGraphOps(g)

  case class TitanGraphOps(g: TitanGraph) {

    def createSchema[GS <: AnySchemaType, Ps <: AnyTypeSet](gs: GS)(implicit
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
