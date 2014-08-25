package ohnosequences.scarph

import ohnosequences.pointless._, AnyTaggedType.{Tagged, @@, RawOf}

/*
  `AnyVertex` defines a denotation of the corresponding `VertexType`.

  Instances are modeled as instances of other type tagged with the singleton type of a `Vertex`. For example, an instance of a self of type `User` when stored/represented by a `Neo4jNode` is going to be something of type `FieldType[user.type, Neo4jNode]`  where `user.type <: AnyVertex { type VertexType = User.type; type Raw = Neo4jNode }`.
  
  They are designed to be compatible with shapeless records (maybe, we'll see).
*/

trait AnyVertex extends Denotation[AnyVertexType] { vertex =>

  /* Getters for incoming/outgoing edges */
  // abstract class GetOutEdge[E <: Singleton with AnyEdge](val e: E) {
  abstract class GetOutEdge[OE <: AnyEdge](val edge: OE) {

    // def apply(rep: vertex.Rep): e.tpe.Out[E#Rep]
    def apply(rep: Tagged[Me]): edge.tpe.Out[Tagged[OE]]
  }
  abstract class GetInEdge[IE <: AnyEdge](val edge: IE) {

    def apply(rep: Tagged[Me]): edge.tpe.In[Tagged[IE]]
  }

}

trait GetSource[V <: AnyVertex, OE <: AnyEdge with AnyEdge.withSource[V]] {

  def apply(vertex: Tagged[V]): OE#Tpe#Out[Tagged[OE]]
}


abstract class Vertex[VT <: AnyVertexType](val tpe: VT) extends AnyVertex { type Tpe = VT }

object AnyVertex {

  type ofType[VT <: AnyVertexType] = AnyVertex { type Tpe = VT }
}

object Vertex {

  type RepOf[V <: Singleton with AnyVertex] = Tagged[V]
}

// this denotation stuff is weird
trait AnySealedVertex extends AnyVertex { sealedVertex =>

  type Me = sealedVertex.type
  type Tpe <: AnySealedVertexType
  val tpe: Tpe
  type Other

  final type Raw = (Other, Tagged[Tpe#Record])

  import ohnosequences.pointless.ops.typeSet.As

  // double tagging FTW!
  final def fields[R <: AnyTypeSet](r: R)(implicit 
    p: R As RawOf[Tpe#Record]
  )
  : @@[Tpe#Record] = (tpe.record: Tpe#Record) =>> p(r)
}

object AnySealedVertex {

  // It's amazing that I need this
  type RepOfSealedVertex[SV <: AnySealedVertex] = SV#Raw with (SV#Other, Tagged[SV#Tpe#Record]) with Tag[SV]

  implicit def propertyOps[SV <: AnySealedVertex](rep: RepOfSealedVertex[SV]): RecordRepOps[SV#Tpe#Record] = {

    val fields: Tagged[ SV#Tpe#Record ] = rep._2

    new RecordRepOps[SV#Tpe#Record](fields)
  }

  implicit def recOps[SV <: AnySealedVertex](recEntry: RepOfSealedVertex[SV]): RecordRepOps[SV#Tpe#Record] = new RecordRepOps[SV#Tpe#Record](recEntry._2)
    

  case class raw[SV <: AnySealedVertex](val fields: Tagged[SV#Tpe#Record], val other: SV#Other)
}

abstract class SealedVertex[VT <: AnySealedVertexType](val tpe: VT) extends AnySealedVertex { 

  type Tpe = VT
}