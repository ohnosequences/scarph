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
  }

  implicit def titanGraphOps(g: TitanGraph): 
    TitanGraphOps = 
    TitanGraphOps(g)

  case class TitanGraphOps(g: TitanGraph) {

    def createSchema[GS <: AnyGraphSchema, Ps <: AnyTypeSet](gs: GS)(implicit
        props: SchemaProperties[GS] { type Out = Ps },
        propsList: ToList[Ps] with InContainer[AnyProperty],
        edgeTypeList: ToList[gs.EdgeTypes] with InContainer[AnyEdgeType],
        vertexTypeList: ToList[gs.VertexTypes] with InContainer[AnyVertexType]
      ) = {
        // we want all this be a one transaction
        val mgmt = g.getManagementSystem

        propsList(props(gs)).map{ mgmt.addPropertyKey(_) }
        edgeTypeList(gs.edgeTypes).map{ mgmt.addEdgeLabel(_) }
        vertexTypeList(gs.vertexTypes).map{ mgmt.addVertexLabel(_) }

        mgmt.commit
      }
  }

}
