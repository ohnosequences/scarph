package ohnosequences.scarph

import shapeless._
import shapeless.ops.hlist._

sealed trait Morphism {

  type Source <: HList
  val  source: Source

  type Target <: HList
  val  target: Target
}

object Morphism {

  // def par[F0 <: Singleton with Morphism, G0 <: Singleton with Morphism](f0: F0, g0: G0)(implicit 
  //   pSource: Prepend[F0#Source, G0#Source],
  //   pTarget: Prepend[F0#Target, G0#Target]
  // ): Par = new Par {

  //   val f:F0 = f0
  //   val g:G0 = g0

  //   type F = F0
  //   type G = G0
  //   implicit val prependSource = pSource
  //   implicit val prependTarget = pTarget
  // }

  def compose[G0 <: Singleton with Morphism, F0 <: Singleton with Morphism](g0: G0, f0: F0)(implicit
    eqP: (G0#Source =:= F0#Target)
  ): Compose = new Compose {

      type F = F0
      val f:F0 = f0
      type G = G0
      val g:G0 = g0
    }
    
  // implicit def toExpOps[E <: AnyEdgeType](edge: E): ExpOps[E] = ExpOps(edge)

  // case class ExpOps[E <: AnyEdgeType](val edgeT: E) {

  //   val asMorphism: Simple { type E = edgeT.type } = new Simple {

  //     type E = edgeT.type
  //     val edge:edgeT.type = edgeT
  //   }

  //   def simple: Simple { type E = edgeT.type } = asMorphism
  // }

  // def simple[E0 <: AnyEdgeType](edge0: E0) = new Simple {

  //   type E = edge0.type
  //   val edge: edge0.type = edge0

  //   type Source = edge0.SourceType :: HNil
  //   type Target = edge0.TargetType :: HNil

  //   val source = edge0.sourceType :: HNil
  //   val target = edge0.targetType :: HNil
  // }
}

  abstract class Simple[E <: AnyEdgeType](val edge: E) extends Morphism {

    type Source <: E#SourceType :: HNil
    
    type Target <: E#TargetType :: HNil
  }

  object Simple {

    // type FromEdge[X <: E0#SourceType, E0 <: AnyEdgeType, Y <: E0#TargetType] = Simple {

    //   type E = E0
    //   type Source = E#SourceType :: HNil
    //   type Target = E#TargetType :: HNil
    // }
  }

  trait Par extends Morphism {

    type F <: Morphism 

    type G <: Morphism

    val f: F
    val g: G

    implicit val prependSource: Prepend[F#Source, G#Source]
    implicit val prependTarget: Prepend[F#Target, G#Target]
    // need Prepend for both sources and targets
    type Source = prependSource.Out
    val source = (f.source:F#Source) ::: (g.source:G#Source)
    
    type Target = prependTarget.Out
    val  target = (f.target:F#Target) ::: (g.target:G#Target)
  }

  
  trait Compose extends Morphism {

    type G <: Morphism
    type F <: Morphism
    val g: G
    val f: F

    type Along <: G#Source with F#Target

    type Source = F#Source
    val  source = f.source

    type Target = G#Target
    val  target = g.target
  }
