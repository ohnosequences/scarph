package ohnosequences.scarph.impl.titan.ops

import ohnosequences.pointless._, AnyTypeSet._, AnyWrap._
import ohnosequences.scarph._, AnyPropertiesHolder._, AnyVertex._
import ohnosequences.scarph.impl.titan._, AnyTitanVertex._

object vertex {
  import ohnosequences.scarph.ops.vertex._

  /* Getting a property from any TitanVertex */
  implicit def unsafeGetProperty[V <: AnyTitanVertex, P <: AnyProperty]
    (implicit hasProp: P ∈ PropertiesOf[VertexTypeOf[V]]):
          GetProperty[V, P] = 
      new GetProperty[V, P] {

        def apply(rep: ValueOf[V], prop: P): Out = prop(rep.raw.getProperty[RawOf[P]](prop.label))
      }

  /* Retrieving edges */
  // import com.tinkerpop.blueprints.Direction
  // import scala.collection.JavaConversions._

  // // TODO: when we get all edges with the given label, they can come from vertices with the wrong type

  // /* OUT */
  // implicit def unsafeGetOneOutEdge [
  //   E <: Singleton with AnyTitanEdge { type Tpe <: From[tvertex.Tpe] with OneOut }
  // ]
  // (e: E): GetOutEdge[E] = new GetOutEdge[E](e) {

  //     def apply(rep: ValueOf[Me]): E#Tpe#Out[ValueOf[E]] = {
        
  //       val it = rep.getEdges( Direction.OUT, e.tpe.label )
  //         .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]

  //       it.headOption map { (e:E) =>> _ }
  //     }
  //   }

  // implicit def unsafeGetManyOutEdge [
  //   OE <: AnyTitanEdge { type Tpe <: From[tvertex.Tpe] with ManyOut }
  // ]
  // (edge: OE)
  // : GetOutEdge[OE] = new GetOutEdge[OE](edge) {

  //     def apply(rep: ValueOf[Me]): OE#Tpe#Out[ValueOf[OE]] = {

  //       val it = rep.getEdges( Direction.OUT, edge.tpe.label )
  //         .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]

  //       it.toList map { (edge: OE) =>> _ }
  //     }
  //   }

  // /* IN */
  // implicit def unsafeGetOneInEdge [
  //   IE <: Singleton with AnyTitanEdge { type Tpe <: To[tvertex.Tpe] with OneIn }
  // ]
  // (edge: IE): GetInEdge[IE] = new GetInEdge[IE](edge) {

  //     def apply(rep: ValueOf[Me]): IE#Tpe#In[ValueOf[IE]] = {

  //       val it = rep.getEdges(Direction.IN, edge.tpe.label)
  //         .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]

  //       it.headOption map { (edge: IE) =>> _ }
  //     }
  //   }

  // implicit def unsafeGetManyInEdge [
  //   IE <: Singleton with AnyTitanEdge { type Tpe <: To[tvertex.Tpe] with ManyIn }
  // ]
  // (edge: IE): GetInEdge[IE] = new GetInEdge[IE](edge) {
        
  //     def apply(rep: ValueOf[Me]): edge.tpe.In[ValueOf[IE]] = {

  //       val it = rep.getEdges( Direction.IN, edge.tpe.label )
  //         .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]

  //       it.toList map { (edge: IE) =>> _ }
  //     }
  //   }

}
