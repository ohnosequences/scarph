
```scala
package ohnosequences.scarph.impl.titan
```

Here are methods for creating Titan schema from an abstract schema description

```scala
object schema {

  import shapeless._, poly._

  import com.thinkaurelius.titan.core._, Multiplicity._
  import com.thinkaurelius.titan.core.schema._
  import com.tinkerpop.blueprints.Direction
  import scala.reflect._

  import ohnosequences.cosas._, typeSets._, fns._, types._
  import ohnosequences.cosas.ops.typeSets._

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.containers._, s.indexes._, s.schemas._
```

This takes an edge type and returns Titan `Multiplicity` (i.e. edge arities)

```scala
  trait EdgeTypeMultiplicity[ET <: AnyEdge] extends Fn1[ET] with Out[Multiplicity]

  object EdgeTypeMultiplicity extends EdgeTypeMultiplicity_2 {

    implicit def one2one[ET <: AnyEdge { 
      type Source <: AnyGraphType { type Container = ExactlyOne }
      type Target <: AnyGraphType { type Container = ExactlyOne }
    }]: EdgeTypeMultiplicity[ET] =
    new EdgeTypeMultiplicity[ET] { def apply(et: In1): Out = Multiplicity.ONE2ONE }
  }

  trait EdgeTypeMultiplicity_2 extends EdgeTypeMultiplicity_3 {

    implicit def one2many[ET <: AnyEdge {
      type Source <: AnyGraphType { type Container = ExactlyOne }
    }]: EdgeTypeMultiplicity[ET] =
    new EdgeTypeMultiplicity[ET] { def apply(et: In1): Out = Multiplicity.ONE2MANY }

    implicit def many2one[ET <: AnyEdge {
      type Target <: AnyGraphType { type Container = ExactlyOne }
    }]: EdgeTypeMultiplicity[ET] =
    new EdgeTypeMultiplicity[ET] { def apply(et: In1): Out = Multiplicity.MANY2ONE }
  }

  trait EdgeTypeMultiplicity_3 {

    implicit def many2many[ET <: AnyEdge]: 
        EdgeTypeMultiplicity[ET] =
    new EdgeTypeMultiplicity[ET] { def apply(et: In1): Out = Multiplicity.MULTI }
  }
```

Following `Poly1` functions create separate Titan schema elements from
the scarph properties/vertices/edges/indexes. They return functions of
the `TitanManagement => TitanManagement` type, so that we can iterate them
later on the schema type-sets.


```scala
  object addPropertyKey extends Poly1 {
    implicit def default[P <: AnyGraphProperty](implicit cc: ClassTag[P#Raw]) = 
      at[P]{ (prop: P) =>
        { (m: TitanManagement) =>
          val clazz = cc.runtimeClass.asInstanceOf[Class[P#Raw]]
          m.makePropertyKey(prop.label).dataType(clazz).make
        }
      }
  }

  object addVertexLabel extends Poly1 {
    implicit def default[VT <: AnyVertex] = at[VT]{ (vt: VT) =>
      { (m: TitanManagement) => m.makeVertexLabel(vt.label).make }
    }
  }

  object addEdgeLabel extends Poly1 {
    implicit def default[ET <: AnyEdge](implicit multi: EdgeTypeMultiplicity[ET]) = at[ET]{ (et: ET) =>
      { (m: TitanManagement) => m.makeEdgeLabel(et.label).multiplicity(multi(et)).make }
    }
  }

  object propertyLabel extends Poly1 {
    implicit def default[P <: AnyGraphProperty] = at[P]{ _.label }
  }

  object addIndex extends Poly1 {
    implicit def localIx[Ix <: AnyLocalEdgeIndex]
      (implicit propLabels: MapToList[propertyLabel.type, Ix#Properties] with InContainer[String]) =
      at[Ix]{ (ix: Ix) => { (m: TitanManagement) =>
          val direction: Direction = (ix.indexType: AnyLocalIndexType) match {
            case OnlySourceCentric => Direction.OUT
            case OnlyTargetCentric => Direction.IN
            case BothEndsCentric   => Direction.BOTH
          }
          val lbl: EdgeLabel = m.getEdgeLabel(ix.indexedType.label)
          val props: List[PropertyKey] = propLabels(ix.properties).map{ m.getPropertyKey(_) }

          m.buildEdgeIndex(lbl, ix.label, direction, props: _*) : TitanIndex
        }
      }

    private def setUniqueness[Ix <: AnyCompositeIndex](ix: Ix, builder: TitanManagement.IndexBuilder): 
      TitanManagement.IndexBuilder = if (ix.uniqueness.bool) builder.unique else builder

    implicit def vertexIx[Ix <: AnyCompositeIndex { type IndexedType <: AnyVertex }]
      (implicit propLabels: MapToList[propertyLabel.type, Ix#Properties] with InContainer[String]) =
      at[Ix]{ (ix: Ix) => { (mgmt: TitanManagement) =>

          val builder = propLabels(ix.properties)
            .foldLeft(mgmt.buildIndex(ix.label, classOf[com.tinkerpop.blueprints.Vertex])){
              (builder, lbl) => builder.addKey(mgmt.getPropertyKey(lbl))
            }

          val elemLabel = mgmt.getVertexLabel(ix.indexedType.label)

          setUniqueness(ix, builder).buildCompositeIndex : TitanIndex
        }
      }

    implicit def edgeIx[Ix <: AnyCompositeIndex { type IndexedType <: AnyEdge }]
      (implicit propLabels: MapToList[propertyLabel.type, Ix#Properties] with InContainer[String]) =
      at[Ix]{ (ix: Ix) => { (mgmt: TitanManagement) =>

          val builder = propLabels(ix.properties)
            .foldLeft(mgmt.buildIndex(ix.label, classOf[com.tinkerpop.blueprints.Edge])){
              (builder, lbl) => builder.addKey(mgmt.getPropertyKey(lbl))
            }

          val elemLabel = mgmt.getEdgeLabel(ix.indexedType.label)

          setUniqueness(ix, builder).buildCompositeIndex : TitanIndex
        }
      }
  }

  implicit def titanGraphOps[S <: AnySchema](g: S := TitanGraph): 
    TitanGraphOps[S] = 
    TitanGraphOps[S](g)

  case class TitanGraphOps[S <: AnySchema](g: S := TitanGraph) {

    def createSchema(sch: S)(implicit
      propertiesMapper: MapToList[addPropertyKey.type, S#Properties] with 
                        InContainer[TitanManagement => PropertyKey],
      edgeTypesMapper: MapToList[addEdgeLabel.type, S#Edges] with 
                       InContainer[TitanManagement => EdgeLabel],
      vertexTypesMapper: MapToList[addVertexLabel.type, S#Vertices] with 
                         InContainer[TitanManagement => VertexLabel],
      indexMapper: MapToList[addIndex.type, S#Indexes] with 
                   InContainer[TitanManagement => TitanIndex]
    ) = {
```

We want this to happen all in _one_ transaction

```scala
      val mgmt = g.value.getManagementSystem

      propertiesMapper(sch.properties).map{ _.apply(mgmt) }
      edgeTypesMapper(sch.edges).map{ _.apply(mgmt) }
      vertexTypesMapper(sch.vertices).map{ _.apply(mgmt) }
      indexMapper(sch.indexes).map{ _.apply(mgmt) }

      mgmt.commit
    }
  }

}

```


------

### Index

+ src
  + test
    + scala
      + ohnosequences
        + scarph
          + [ContainersTest.scala][test/scala/ohnosequences/scarph/ContainersTest.scala]
          + [ScalazEquality.scala][test/scala/ohnosequences/scarph/ScalazEquality.scala]
          + titan
            + [TwitterTitanTest.scala][test/scala/ohnosequences/scarph/titan/TwitterTitanTest.scala]
          + [TwitterSchema.scala][test/scala/ohnosequences/scarph/TwitterSchema.scala]
    + resources
  + main
    + scala
      + ohnosequences
        + scarph
          + [GraphTypes.scala][main/scala/ohnosequences/scarph/GraphTypes.scala]
          + [Containers.scala][main/scala/ohnosequences/scarph/Containers.scala]
          + impl
            + titan
              + [Schema.scala][main/scala/ohnosequences/scarph/impl/titan/Schema.scala]
              + [Evals.scala][main/scala/ohnosequences/scarph/impl/titan/Evals.scala]
              + [Predicates.scala][main/scala/ohnosequences/scarph/impl/titan/Predicates.scala]
          + [Paths.scala][main/scala/ohnosequences/scarph/Paths.scala]
          + [Indexes.scala][main/scala/ohnosequences/scarph/Indexes.scala]
          + [Evals.scala][main/scala/ohnosequences/scarph/Evals.scala]
          + [Conditions.scala][main/scala/ohnosequences/scarph/Conditions.scala]
          + [Steps.scala][main/scala/ohnosequences/scarph/Steps.scala]
          + [Predicates.scala][main/scala/ohnosequences/scarph/Predicates.scala]
          + [Schemas.scala][main/scala/ohnosequences/scarph/Schemas.scala]
          + [Combinators.scala][main/scala/ohnosequences/scarph/Combinators.scala]
          + syntax
            + [GraphTypes.scala][main/scala/ohnosequences/scarph/syntax/GraphTypes.scala]
            + [Paths.scala][main/scala/ohnosequences/scarph/syntax/Paths.scala]
            + [Conditions.scala][main/scala/ohnosequences/scarph/syntax/Conditions.scala]
            + [Predicates.scala][main/scala/ohnosequences/scarph/syntax/Predicates.scala]

[test/scala/ohnosequences/scarph/ContainersTest.scala]: ../../../../../../test/scala/ohnosequences/scarph/ContainersTest.scala.md
[test/scala/ohnosequences/scarph/ScalazEquality.scala]: ../../../../../../test/scala/ohnosequences/scarph/ScalazEquality.scala.md
[test/scala/ohnosequences/scarph/titan/TwitterTitanTest.scala]: ../../../../../../test/scala/ohnosequences/scarph/titan/TwitterTitanTest.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[main/scala/ohnosequences/scarph/GraphTypes.scala]: ../../GraphTypes.scala.md
[main/scala/ohnosequences/scarph/Containers.scala]: ../../Containers.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Schema.scala]: Schema.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Evals.scala]: Evals.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Predicates.scala]: Predicates.scala.md
[main/scala/ohnosequences/scarph/Paths.scala]: ../../Paths.scala.md
[main/scala/ohnosequences/scarph/Indexes.scala]: ../../Indexes.scala.md
[main/scala/ohnosequences/scarph/Evals.scala]: ../../Evals.scala.md
[main/scala/ohnosequences/scarph/Conditions.scala]: ../../Conditions.scala.md
[main/scala/ohnosequences/scarph/Steps.scala]: ../../Steps.scala.md
[main/scala/ohnosequences/scarph/Predicates.scala]: ../../Predicates.scala.md
[main/scala/ohnosequences/scarph/Schemas.scala]: ../../Schemas.scala.md
[main/scala/ohnosequences/scarph/Combinators.scala]: ../../Combinators.scala.md
[main/scala/ohnosequences/scarph/syntax/GraphTypes.scala]: ../../syntax/GraphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/Paths.scala]: ../../syntax/Paths.scala.md
[main/scala/ohnosequences/scarph/syntax/Conditions.scala]: ../../syntax/Conditions.scala.md
[main/scala/ohnosequences/scarph/syntax/Predicates.scala]: ../../syntax/Predicates.scala.md