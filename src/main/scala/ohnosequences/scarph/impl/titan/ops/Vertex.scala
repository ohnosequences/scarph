// package ohnosequences.scarph.impl.titan.ops

// import ohnosequences.pointless._, AnyTypeSet._, AnyWrap._
// import ohnosequences.scarph._, AnyPropertiesHolder._, AnyVertex._, AnyEdge._
// import ohnosequences.scarph.impl.titan._, AnyTitanVertex._
// import scalaz._

// object vertex {
//   import ohnosequences.scarph.ops.vertex._

//   // /* Getting a property from any TitanVertex */
//   // implicit def unsafeGetVertexProperty[V <: AnyTitanVertex, P <: AnyProperty]
//   //   (implicit hasProp: VertexTypeOf[V] HasProperty P):
//   //         GetVertexProperty[V, P] = 
//   //     new GetVertexProperty[V, P] {

//   //       def apply(raw: RawOf[V], prop: P): Out = prop(raw.getProperty[RawOf[P]](prop.label))
//   //     }

//   /* Retrieving edges */
//   import com.tinkerpop.blueprints.Direction
//   import scala.collection.JavaConversions._

//   implicit def unsafeGetOneOutEdge[E <: AnyTitanEdge.withType[OneOut]]:
//         GetOutEdge[E] = 
//     new GetOutEdge[E] {

//       def apply(raw: RawOf[SourceOf[E]], e: E): Out = {
        
//         val it = raw.getEdges(Direction.OUT, e.tpe.label)
//           .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]

//         it.headOption.map{ e.apply(_) }
//       }
//     }

//   implicit def unsafeGetManyOutEdge[E <: AnyTitanEdge.withType[ManyOut]]:
//         GetOutEdge[E] = 
//     new GetOutEdge[E] {

//       def apply(raw: RawOf[SourceOf[E]], e: E): Out = {
        
//         val it = raw.getEdges(Direction.OUT, e.tpe.label)
//           .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]

//         it.toList.map{ e.apply(_) }
//       }
//     }

//   /* IN */
//   implicit def unsafeGetOneInEdge[E <: AnyTitanEdge.withType[OneIn]]:
//         GetInEdge[E] = 
//     new GetInEdge[E] {

//       def apply(raw: RawOf[TargetOf[E]], e: E): Out = {
        
//         val it = raw.getEdges(Direction.IN, e.tpe.label)
//           .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]

//         it.headOption.map{ e.apply(_) }
//       }
//     }

//   implicit def unsafeGetManyInEdge[E <: AnyTitanEdge.withType[ManyIn]]:
//         GetInEdge[E] = 
//     new GetInEdge[E] {

//       def apply(raw: RawOf[TargetOf[E]], e: E): Out = {
        
//         val it = raw.getEdges(Direction.IN, e.tpe.label)
//           .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]

//         it.toList.map{ e.apply(_) }
//       }
//     }

// }
