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

  implicit def titanManagementOps(mgmt: TitanManagement): 
    TitanManagementOps = 
    TitanManagementOps(mgmt)

  case class TitanManagementOps(mgmt: TitanManagement) {

    def addPropertyKey[P <: AnyProperty](prop: P) = {
      val clazz = prop.classTag.runtimeClass.asInstanceOf[Class[RawOf[P]]]
      mgmt.makePropertyKey(prop.label).dataType(clazz).make
    }

    def addEdgeLabel[ET <: AnyEdgeType](et: ET) = {
      mgmt.makeEdgeLabel(et.label).multiplicity(multiplicity(et)).make()
    }

    def addVertexLabel[VT <: AnyVertexType](vt: VT) = {
      mgmt.makeVertexLabel(vt.label).make()
    }

    // TODO: for all titan index types
    def addIndex[Ix <: AnyIndex](ix: Ix) = {
      // TODO: now only for vertices, add a classTag for IndexedType
      // val clazz = ???.classTag.runtimeClass.asInstanceOf[Class[Ix#IndexedType#Raw]]
      val propertyKey = mgmt.getPropertyKey(ix.property.label)
      mgmt.buildIndex(ix.label, classOf[com.tinkerpop.blueprints.Vertex])
        .addKey(propertyKey)
        .buildCompositeIndex()
    }
  }

  implicit def titanGraphOps(g: TitanGraph): 
    TitanGraphOps = 
    TitanGraphOps(g)

  case class TitanGraphOps(g: TitanGraph) {

    def createSchema[GS <: AnyGraphSchema, Ps <: AnyTypeSet](gs: GS)(implicit
        props: SchemaProperties[GS] { type Out = Ps },
        propsList: ToList[Ps] with InContainer[AnyProperty],
        edgeTypeList: ToList[gs.EdgeTypes] with InContainer[AnyEdgeType],
        vertexTypeList: ToList[gs.VertexTypes] with InContainer[AnyVertexType],
        indexList: ToList[gs.Indexes] with InContainer[AnyIndex]
      ) = {
        // we want all this happen in a one transaction
        val mgmt = g.getManagementSystem

        propsList(props(gs)).map{ mgmt.addPropertyKey(_) }
        edgeTypeList(gs.edgeTypes).map{ mgmt.addEdgeLabel(_) }
        vertexTypeList(gs.vertexTypes).map{ mgmt.addVertexLabel(_) }
        indexList(gs.indexes).map{ mgmt.addIndex(_) }

        mgmt.commit
      }
  }

}
