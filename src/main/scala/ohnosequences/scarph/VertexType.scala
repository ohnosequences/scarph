package ohnosequences.scarph

import ohnosequences.typesets._

/*
  Declares a Vertex type. They are essentially classified by its label, a `String`.
*/
trait AnyVertexType {
  val label: String

  type Props <: TypeSet
  val props: Props
}

// class VertexType(val label: String) extends AnyVertexType
class VertexType[Ps <: TypeSet : boundedBy[AnyProperty]#is](val label: String, val props: Ps = ∅) 
  extends AnyVertexType { type Props = Ps }

object AnyVertexType {
  implicit def vertexTypeOps[VT <: AnyVertexType](et: VT) = VertexTypeOps(et)
}

case class VertexTypeOps[VT <: AnyVertexType](val vt: VT) {
  def has[P <: AnyProperty](p: P) = HasProperty[VT, P](vt, p)
  // def :-[Ps <: TypeSet : boundedBy[AnyProperty]#is](props: Ps): FinalVertexType[Ps] =
  //   new FinalVertexType(vt.label, props) 
}

// trait AnyFinalVertexType extends AnyVertexType {
//   type Props <: TypeSet
//   val props: Props
// }

// class FinalVertexType[Ps <: TypeSet : boundedBy[AnyProperty]#is](val label: String, val props: Ps) 
//   extends AnyFinalVertexType {
//     type Props = Ps
//   }

// TODO: some kind of conversion from a normal VertexType + set of props to a FinalVertexType
