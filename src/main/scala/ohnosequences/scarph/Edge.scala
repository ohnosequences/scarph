package ohnosequences.scarph

import ohnosequences.pointless._, AnyTaggedType._, AnyTypeSet._, AnyFn._

trait AnyEdge extends Denotation[AnyEdgeType] { edge =>

  // NOTE: if I remove this from here type inference fails. Most likely a bug
  type Tpe <: AnyEdgeType

  type Source <: AnyVertex.ofType[Tpe#SourceType]
  val  source: Source

  type Target <: AnyVertex.ofType[Tpe#TargetType]
  val  target: Target

  /* Get source/target from this representation */
  abstract class GetSource {

    type Out = Tagged[Source]

    def apply(edgeRep: Tagged[Me]): Out
  }
  abstract class GetTarget { 
    
    type Out = Tagged[Target] 

    def apply(edgeRep: Tagged[Me]): Out
  }

}

class Edge[
    S <: AnyVertex.ofType[ET#SourceType],
    ET <: AnyEdgeType, 
    T <: AnyVertex.ofType[ET#TargetType]
  ](val source: S, val tpe: ET, val target: T) extends AnyEdge { 
    type Source = S
    type Tpe = ET 
    type Target = T
  }

object AnyEdge {

  import AnyEdgeType._

  type ofType[ET <: AnyEdgeType] = AnyEdge { type Tpe = ET }
  type withSource[S <: AnyVertex] = AnyVertex { type Source = S }

  type -->[S <: AnyVertexType, T <: AnyVertexType] = AnyEdge { type Tpe <: S ==> T }
}

trait AnySealedEdge extends AnyEdge { sealedEdge =>
  
  type Tpe <: AnySealedEdgeType

  final type Raw = raw
  type Other

  case class raw(val fields: Tagged[Tpe#Record], val other: Other)

  // double tagging FTW!
  final def fields[R <: AnyTypeSet](r: R)(implicit
    p: R As RawOf[tpe.Record]
  ): Tagged[tpe.Record] = ( tpe.record =>> p(r) )

  implicit def propertyOps(rep: Tagged[Me]): RecordRepOps[Tpe#Record] = new RecordRepOps(rep.fields) 
}

abstract class SealedEdge [
  S <: AnyVertex.ofType[ET#SourceType],
  ET <: AnySealedEdgeType,
  T <: AnyVertex.ofType[ET#TargetType]
](
  val source: S,
  val tpe: ET,
  val target: T
) 
extends AnySealedEdge { 

  type Source = S
  type Tpe = ET 
  type Target = T
}
