package ohnosequences.scarph

import ohnosequences.pointless._, AnyTypeSet._, AnyFn._
import ohnosequences.pointless.ops.typeSet._

trait AnySchemaType extends AnyType {

  type VertexTypes <: AnyTypeSet.Of[AnyVertexType]
  val  vertexTypes: VertexTypes

  type EdgeTypes <: AnyTypeSet.Of[AnyEdgeType]
  val  edgeTypes: EdgeTypes

  type Indexes <: AnyTypeSet.Of[AnyIndex]
  val  indexes: Indexes
}

case class SchemaType[
    Vs <: AnyTypeSet.Of[AnyVertexType],
    Es <: AnyTypeSet.Of[AnyEdgeType],
    Is <: AnyTypeSet.Of[AnyIndex]
  ](val label: String,
    val vertexTypes:  Vs = ∅,
    val edgeTypes: Es    = ∅,
    val indexes: Is      = ∅
  ) extends AnySchemaType {

  type VertexTypes  = Vs
  type EdgeTypes    = Es
  type Indexes      = Is
}

object AnySchemaType {

  type VertexTypesOf[GS <: AnySchemaType] = GS#VertexTypes
  type EdgeTypesOf[GS <: AnySchemaType] = GS#EdgeTypes
  type IndexesOf[GS <: AnySchemaType] = GS#Indexes

  implicit def schemaTypeOps[GS <: AnySchemaType](gs: GS):
        SchemaTypeOps[GS] =
    new SchemaTypeOps[GS](gs)
}
import AnySchemaType._

class SchemaTypeOps[GS <: AnySchemaType](gs: GS) {

  def properties(implicit props: SchemaProperties[GS]): props.Out = props(gs)
}


// TODO: move it somewhere?
/* This op aggregates properties of vertex types and edge types and unites them */
trait SchemaProperties[S <: AnySchemaType] extends Fn1[S] with OutBound[AnyTypeSet]

object SchemaProperties {

  implicit def aggregate[
      GS <: AnySchemaType,
      VP <: AnyTypeSet, 
      EP <: AnyTypeSet, 
      U <: AnyTypeSet
    ](implicit
      vp: AggregateProperties[VertexTypesOf[GS]] { type Out = VP },
      ep: AggregateProperties[EdgeTypesOf[GS]]   { type Out = EP },
      u: (VP ∪ EP) { type Out = U }
    ):  SchemaProperties[GS] with Out[U] =
    new SchemaProperties[GS] with Out[U] {

      def apply(gs: GS): Out = u( vp(gs.vertexTypes), ep(gs.edgeTypes) )
    }
}
