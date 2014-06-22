
```scala
package ohnosequences.scarph

import ohnosequences.typesets._
import scala.reflect._
```

Properties

```scala
trait AnyProperty extends AnyDenotation {
  val label: String
  type TYPE <: AnyProperty
}
```

Evidence that an arbitrary type `Smth` has property `P`

```scala
sealed class HasProperty[S, P <: AnyProperty]
```

or a set of properties `Ps`

```scala
sealed class HasProperties[S, Ps <: TypeSet : boundedBy[AnyProperty]#is] 

object AnyProperty {
```

This implicit is a bridge from `HasProperties` to `HasProperty`

```scala
  implicit def FromSetToAProperty[T, P <: AnyProperty, Ps <: TypeSet]
    (implicit ps: T HasProperties Ps, ep: P ? Ps): HasProperty[T, P] = new HasProperty[T, P]
}
```

Properties sould be defined as case objects: `case object Name extends Property[String]`

```scala
class Property[V](implicit c: ClassTag[V]) extends AnyProperty with Denotation[AnyProperty] {
  val label = this.toString
```

Property denotes itself

```scala
  type Tpe = this.type
  val  tpe = this: Tpe

  type Raw = V 
}

object Property {
```

For context bounds: `P <: AnyProperty: Property.Of[X]#is`

```scala
  type Of[S] = { type is[P <: AnyProperty] = S HasProperty P }
}

class HasPropertiesOps[T](t: T) {
```

Handy way of creating an implicit evidence saying that this vertex type has that property

```scala
  def has[P <: AnyProperty](p: P) = new (T HasProperty P)
  def has[Ps <: TypeSet : boundedBy[AnyProperty]#is](ps: Ps) = new (T HasProperties Ps)
```

Takes a set of properties and filters out only those, which this vertex "has"

```scala
  def filterMyProps[Ps <: TypeSet : boundedBy[AnyProperty]#is](ps: Ps)
    (implicit f: FilterProps[T, Ps]) = f(ps)
}
```


This trait should be mixed to the types that _can have properties_,
meaning that you are going to _get properties_ from it


```scala
trait CanHaveProperties { self: AnyDenotation =>
```

Read a property from this representation

```scala
  trait AnyGetProperty {
    type Property <: AnyProperty
    val p: Property

    def apply(rep: self.Rep): p.Raw
  }

  abstract class GetProperty[P <: AnyProperty](val p: P) 
    extends AnyGetProperty { type Property = P }

  implicit def propertyOps(rep: self.Rep): PropertyOps = PropertyOps(rep)
  case class   PropertyOps(rep: self.Rep) {

    def get[P <: AnyProperty: Property.Of[self.Tpe]#is](p: P)
    (implicit mkGetter: P => GetProperty[P]): P#Raw = mkGetter(p).apply(rep)

  }
```

If have just an independent getter for a particular property:

```scala
  implicit def idGetter[P <: AnyProperty: Property.Of[self.Tpe]#is](p: P)
    (implicit getter: GetProperty[P]) = getter
}



import shapeless._, poly._
import ohnosequences.typesets._
```


For a given arbitrary type `Smth`, filters any property set, 
leaving only those which have the `Smth HasProperty _` evidence


```scala
trait FilterProps[Smth, Ps <: TypeSet] extends DepFn1[Ps] {
  type Out <: TypeSet
}

object FilterProps extends FilterProps2 {
  // the case when there is this evidence (leaving the head)
  implicit def consFilter[Smth, H <: AnyProperty, T <: TypeSet, OutT <: TypeSet]
    (implicit
      h: Smth HasProperty H,
      t: Aux[Smth, T, OutT]
    ): Aux[Smth, H :~: T, H :~: OutT] =
      new FilterProps[Smth, H :~: T] { type Out = H :~: OutT
        def apply(s: H :~: T): Out = s.head :~: t(s.tail)
      }
}

trait FilterProps2 {
  def apply[Smth, Ps <: TypeSet](implicit filt: FilterProps[Smth, Ps]): Aux[Smth, Ps, filt.Out] = filt

  type Aux[Smth, In <: TypeSet, O <: TypeSet] = FilterProps[Smth, In] { type Out = O }
  
  implicit def emptyFilter[Smth]: Aux[Smth, ?, ?] =
    new FilterProps[Smth, ?] {
      type Out = ?
      def apply(s: ?): Out = ?
    }

  // the low-priority case when there is no evidence (just skipping head)
  implicit def skipFilter[Smth, H <: AnyProperty, T <: TypeSet, OutT <: TypeSet]
    (implicit t: Aux[Smth, T, OutT]): Aux[Smth, H :~: T, OutT] =
      new FilterProps[Smth, H :~: T] { type Out = OutT
        def apply(s: H :~: T): Out = t(s.tail)
      }
}
```

This applies `FilterProps` to a list of `Smth`s (`Ts` here)

```scala
trait ZipWithProps[Ts <: TypeSet, Ps <: TypeSet] extends DepFn2[Ts, Ps] {
  type Out <: TypeSet
}

object ZipWithProps {
  def apply[Ts <: TypeSet, Ps <: TypeSet]
    (implicit z: ZipWithProps[Ts, Ps]): Aux[Ts, Ps, z.Out] = z

  type Aux[Ts <: TypeSet, Ps <: TypeSet, O <: TypeSet] = ZipWithProps[Ts, Ps] { type Out = O }
  
  implicit def emptyZipWithProps[Ps <: TypeSet]: Aux[?, Ps, ?] =
    new ZipWithProps[?, Ps] {
      type Out = ?
      def apply(s: ?, ps: Ps): Out = ?
    }

  implicit def consZipWithProps[H, T <: TypeSet, Ps <: TypeSet, OutT <: TypeSet]
    (implicit 
      h: FilterProps[H, Ps],
      t: Aux[T, Ps, OutT]
    ): Aux[H :~: T, Ps, (H, h.Out) :~: OutT] =
      new ZipWithProps[H :~: T, Ps] { type Out = (H, h.Out) :~: OutT
        def apply(s: H :~: T, ps: Ps): Out = (s.head, h(ps)) :~: t(s.tail, ps)
      }
}

```


------

### Index

+ src
  + main
    + scala
      + ohnosequences
        + scarph
          + [Denotation.scala][main/scala/ohnosequences/scarph/Denotation.scala]
          + [Edge.scala][main/scala/ohnosequences/scarph/Edge.scala]
          + [EdgeType.scala][main/scala/ohnosequences/scarph/EdgeType.scala]
          + [Expressions.scala][main/scala/ohnosequences/scarph/Expressions.scala]
          + [GraphSchema.scala][main/scala/ohnosequences/scarph/GraphSchema.scala]
          + [Property.scala][main/scala/ohnosequences/scarph/Property.scala]
          + titan
            + [TEdge.scala][main/scala/ohnosequences/scarph/titan/TEdge.scala]
            + [TSchema.scala][main/scala/ohnosequences/scarph/titan/TSchema.scala]
            + [TVertex.scala][main/scala/ohnosequences/scarph/titan/TVertex.scala]
          + [Vertex.scala][main/scala/ohnosequences/scarph/Vertex.scala]
          + [VertexType.scala][main/scala/ohnosequences/scarph/VertexType.scala]
  + test
    + scala
      + ohnosequences
        + scarph
          + [edges.scala][test/scala/ohnosequences/scarph/edges.scala]
          + [edgeTypes.scala][test/scala/ohnosequences/scarph/edgeTypes.scala]
          + [properties.scala][test/scala/ohnosequences/scarph/properties.scala]
          + restricted
            + [RestrictedSchemaTest.scala][test/scala/ohnosequences/scarph/restricted/RestrictedSchemaTest.scala]
            + [SimpleSchema.scala][test/scala/ohnosequences/scarph/restricted/SimpleSchema.scala]
            + [SimpleSchemaImplementation.scala][test/scala/ohnosequences/scarph/restricted/SimpleSchemaImplementation.scala]
          + titan
            + [expressions.scala][test/scala/ohnosequences/scarph/titan/expressions.scala]
            + [godsImplementation.scala][test/scala/ohnosequences/scarph/titan/godsImplementation.scala]
            + [godsSchema.scala][test/scala/ohnosequences/scarph/titan/godsSchema.scala]
            + [TitanGodsTest.scala][test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]
            + [TitanSchemaTest.scala][test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]
          + [vertexTypes.scala][test/scala/ohnosequences/scarph/vertexTypes.scala]
          + [vertices.scala][test/scala/ohnosequences/scarph/vertices.scala]

[main/scala/ohnosequences/scarph/Denotation.scala]: Denotation.scala.md
[main/scala/ohnosequences/scarph/Edge.scala]: Edge.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: EdgeType.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: Expressions.scala.md
[main/scala/ohnosequences/scarph/GraphSchema.scala]: GraphSchema.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: Property.scala.md
[main/scala/ohnosequences/scarph/titan/TEdge.scala]: titan/TEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TSchema.scala]: titan/TSchema.scala.md
[main/scala/ohnosequences/scarph/titan/TVertex.scala]: titan/TVertex.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: Vertex.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: VertexType.scala.md
[test/scala/ohnosequences/scarph/edges.scala]: ../../../../test/scala/ohnosequences/scarph/edges.scala.md
[test/scala/ohnosequences/scarph/edgeTypes.scala]: ../../../../test/scala/ohnosequences/scarph/edgeTypes.scala.md
[test/scala/ohnosequences/scarph/properties.scala]: ../../../../test/scala/ohnosequences/scarph/properties.scala.md
[test/scala/ohnosequences/scarph/restricted/RestrictedSchemaTest.scala]: ../../../../test/scala/ohnosequences/scarph/restricted/RestrictedSchemaTest.scala.md
[test/scala/ohnosequences/scarph/restricted/SimpleSchema.scala]: ../../../../test/scala/ohnosequences/scarph/restricted/SimpleSchema.scala.md
[test/scala/ohnosequences/scarph/restricted/SimpleSchemaImplementation.scala]: ../../../../test/scala/ohnosequences/scarph/restricted/SimpleSchemaImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: ../../../../test/scala/ohnosequences/scarph/titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: ../../../../test/scala/ohnosequences/scarph/titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: ../../../../test/scala/ohnosequences/scarph/titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala.md
[test/scala/ohnosequences/scarph/vertexTypes.scala]: ../../../../test/scala/ohnosequences/scarph/vertexTypes.scala.md
[test/scala/ohnosequences/scarph/vertices.scala]: ../../../../test/scala/ohnosequences/scarph/vertices.scala.md