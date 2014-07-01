
```scala
package ohnosequences.scarph

import ohnosequences.typesets._

trait AnyGraphSchema {

  val label: String

  type Dependencies <: TypeSet
  val  dependencies: Dependencies

  type Properties <: TypeSet
  val  properties: Properties

  type VertexTypes <: TypeSet
  val  vertexTypes: VertexTypes

  type EdgeTypes <: TypeSet
  val  edgeTypes: EdgeTypes
```

These two _values_ store sets of pairs `(vertexType/edgeType, it's properties)`

```scala
  type VerticesWithProperties <: TypeSet
  val  verticesWithProperties: VerticesWithProperties = vertexPropertyAssoc(vertexTypes, properties)

  type EdgesWithProperties <: TypeSet
  val  edgesWithProperties: EdgesWithProperties = edgePropertyAssoc(edgeTypes, properties)

  val vertexPropertyAssoc: ZipWithProps.Aux[VertexTypes, Properties, VerticesWithProperties]
  val   edgePropertyAssoc: ZipWithProps.Aux[EdgeTypes, Properties, EdgesWithProperties]

  override def toString = s"""${label} schema:
  vertexTypes: ${verticesWithProperties}
    edgeTypes: ${edgesWithProperties}"""

}

object AnyGraphSchema {
```

Additional methods

```scala
  implicit def schemaOps[S <: AnyGraphSchema](sch: S): GraphSchemaOps[S] = GraphSchemaOps[S](sch)
  case class   GraphSchemaOps[S <: AnyGraphSchema](schema: S) {
```

This method returns properties that are associated with the given **vertex** type

```scala
    def vertexProperties[VT <: Singleton with AnyVertexType](vertexType: VT)(implicit
      e: VT ? schema.VertexTypes,
      f: FilterProps[VT, schema.Properties]
    ): f.Out = f(schema.properties)
```

This method returns properties that are associated with the given **edge** type

```scala
    def edgeProperties[ET <: Singleton with AnyEdgeType](edgeType: ET)(implicit
      e: ET ? schema.EdgeTypes,
      f: FilterProps[ET, schema.Properties]
    ): f.Out = f(schema.properties)
  }
}

case class GraphSchema[
    Ds <: TypeSet : boundedBy[AnyGraphSchema]#is,
    Ps <: TypeSet : boundedBy[AnyProperty]#is,
    Vs <: TypeSet : boundedBy[AnyVertexType]#is,
    Es <: TypeSet : boundedBy[AnyEdgeType]#is,
    VP <: TypeSet,
    EP <: TypeSet
  ](val label: String,
    val dependencies: Ds = ?,
    val properties:   Ps = ?,
    val vertexTypes:  Vs = ?,
    val edgeTypes:    Es = ?
  )(implicit
    val vertexPropertyAssoc: ZipWithProps.Aux[Vs, Ps, VP],
    val   edgePropertyAssoc: ZipWithProps.Aux[Es, Ps, EP]
  ) extends AnyGraphSchema {

  type Dependencies = Ds
  type Properties   = Ps
  type VertexTypes  = Vs
  type EdgeTypes    = Es
  type VerticesWithProperties = VP
  type    EdgesWithProperties = EP

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
          + ops
            + [default.scala][main/scala/ohnosequences/scarph/ops/default.scala]
            + [typelevel.scala][main/scala/ohnosequences/scarph/ops/typelevel.scala]
          + [Property.scala][main/scala/ohnosequences/scarph/Property.scala]
          + titan
            + [TitanEdge.scala][main/scala/ohnosequences/scarph/titan/TitanEdge.scala]
            + [TitanGraphSchema.scala][main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]
            + [TitanVertex.scala][main/scala/ohnosequences/scarph/titan/TitanVertex.scala]
          + [Vertex.scala][main/scala/ohnosequences/scarph/Vertex.scala]
          + [VertexType.scala][main/scala/ohnosequences/scarph/VertexType.scala]
  + test
    + scala
      + ohnosequences
        + scarph
          + titan
            + [expressions.scala][test/scala/ohnosequences/scarph/titan/expressions.scala]
            + [godsImplementation.scala][test/scala/ohnosequences/scarph/titan/godsImplementation.scala]
            + [godsSchema.scala][test/scala/ohnosequences/scarph/titan/godsSchema.scala]
            + [TitanGodsTest.scala][test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]
            + [TitanSchemaTest.scala][test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]

[main/scala/ohnosequences/scarph/Denotation.scala]: Denotation.scala.md
[main/scala/ohnosequences/scarph/Edge.scala]: Edge.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: EdgeType.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: Expressions.scala.md
[main/scala/ohnosequences/scarph/GraphSchema.scala]: GraphSchema.scala.md
[main/scala/ohnosequences/scarph/ops/default.scala]: ops/default.scala.md
[main/scala/ohnosequences/scarph/ops/typelevel.scala]: ops/typelevel.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: Property.scala.md
[main/scala/ohnosequences/scarph/titan/TitanEdge.scala]: titan/TitanEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]: titan/TitanGraphSchema.scala.md
[main/scala/ohnosequences/scarph/titan/TitanVertex.scala]: titan/TitanVertex.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: Vertex.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: VertexType.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: ../../../../test/scala/ohnosequences/scarph/titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: ../../../../test/scala/ohnosequences/scarph/titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: ../../../../test/scala/ohnosequences/scarph/titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala.md