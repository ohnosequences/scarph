package ohnosequences.scarph

import ohnosequences.pointless._, AnyTypeSet._, AnyFn._
import ohnosequences.pointless.ops.typeSet._

trait AnyGraphSchema {

  val label: String

  // type Dependencies <: AnyTypeSet.Of[AnyGraphSchema]
  // val  dependencies: Dependencies

  type VertexTypes <: AnyTypeSet.Of[AnyVertexType]
  val  vertexTypes: VertexTypes

  type EdgeTypes <: AnyTypeSet.Of[AnyEdgeType]
  val  edgeTypes: EdgeTypes

  type Indexes <: AnyTypeSet.Of[AnyIndex]
  val  indexes: Indexes
}

case class GraphSchema[
    // Ds <: AnyTypeSet.Of[AnyGraphSchema],
    Vs <: AnyTypeSet.Of[AnyVertexType],
    Es <: AnyTypeSet.Of[AnyEdgeType],
    Is <: AnyTypeSet.Of[AnyIndex]
  ](val label: String,
    // val dependencies: Ds = ∅,
    val vertexTypes:  Vs = ∅,
    val edgeTypes: Es    = ∅,
    val indexes: Is      = ∅
  ) extends AnyGraphSchema {

  // type Dependencies = Ds
  type VertexTypes  = Vs
  type EdgeTypes    = Es
  type Indexes      = Is
}

object AnyGraphSchema {

  // type DependenciesOf[GS <: AnyGraphSchema] = GS#Dependencies
  type VertexTypesOf[GS <: AnyGraphSchema] = GS#VertexTypes
  type EdgeTypesOf[GS <: AnyGraphSchema] = GS#EdgeTypes
  type IndexesOf[GS <: AnyGraphSchema] = GS#Indexes

  implicit def graphSchemaOps[GS <: AnyGraphSchema](gs: GS):
        GraphSchemaOps[GS] =
    new GraphSchemaOps[GS](gs)
}
import AnyGraphSchema._

class GraphSchemaOps[GS <: AnyGraphSchema](gs: GS) {

  def properties(implicit props: SchemaProperties[GS]): props.Out = props(gs)
}


// TODO: move it somewhere?
/* This op aggregates properties of vertex types and edge types and unites them */
trait SchemaProperties[S <: AnyGraphSchema] extends Fn1[S] with OutBound[AnyTypeSet]

object SchemaProperties {

  implicit def aggregate[
      GS <: AnyGraphSchema,
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
