
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

  val vertexPropertyAssoc: ZipWithProps[VertexTypes, Properties]
  val   edgePropertyAssoc: ZipWithProps[EdgeTypes, Properties]
```

These two _values_ store sets of pairs `(vertexType/edgeType, it's properties)`

```scala
  val verticesWithProperties = vertexPropertyAssoc(vertexTypes, properties)
  val    edgesWithProperties =   edgePropertyAssoc(edgeTypes,   properties)

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
      e: VT ∈ schema.VertexTypes,
      f: FilterProps[VT, schema.Properties]
    ): f.Out = f(schema.properties)
```

This method returns properties that are associated with the given **edge** type

```scala
    def edgeProperties[ET <: Singleton with AnyEdgeType](edgeType: ET)(implicit
      e: ET ∈ schema.EdgeTypes,
      f: FilterProps[ET, schema.Properties]
    ): f.Out = f(schema.properties)
  }
}

case class GraphSchema[
    Ds <: TypeSet : boundedBy[AnyGraphSchema]#is,
    Ps <: TypeSet : boundedBy[AnyProperty]#is,
    Vs <: TypeSet : boundedBy[AnyVertexType]#is,
    Es <: TypeSet : boundedBy[AnyEdgeType]#is
  ](val label: String,
    val dependencies: Ds = ∅,
    val properties:   Ps = ∅,
    val vertexTypes:  Vs = ∅,
    val edgeTypes:    Es = ∅
  )(implicit
    val vertexPropertyAssoc: ZipWithProps[Vs, Ps],
    val   edgePropertyAssoc: ZipWithProps[Es, Ps]
  ) extends AnyGraphSchema {

  type Dependencies = Ds
  type Properties   = Ps
  type VertexTypes  = Vs
  type EdgeTypes    = Es

}

```


------

### Index

+ src
  + test
    + scala
      + ohnosequences
        + scarph
          + [properties.scala][test/scala/ohnosequences/scarph/properties.scala]
          + restricted
            + [SimpleSchema.scala][test/scala/ohnosequences/scarph/restricted/SimpleSchema.scala]
            + [RestrictedSchemaTest.scala][test/scala/ohnosequences/scarph/restricted/RestrictedSchemaTest.scala]
            + [SimpleSchemaImplementation.scala][test/scala/ohnosequences/scarph/restricted/SimpleSchemaImplementation.scala]
          + [edges.scala][test/scala/ohnosequences/scarph/edges.scala]
          + [vertices.scala][test/scala/ohnosequences/scarph/vertices.scala]
          + titan
            + [expressions.scala][test/scala/ohnosequences/scarph/titan/expressions.scala]
            + [TitanSchemaTest.scala][test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]
            + [TitanGodsTest.scala][test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]
            + [godsImplementation.scala][test/scala/ohnosequences/scarph/titan/godsImplementation.scala]
            + [godsSchema.scala][test/scala/ohnosequences/scarph/titan/godsSchema.scala]
          + [vertexTypes.scala][test/scala/ohnosequences/scarph/vertexTypes.scala]
          + [edgeTypes.scala][test/scala/ohnosequences/scarph/edgeTypes.scala]
  + main
    + scala
      + ohnosequences
        + scarph
          + [Expressions.scala][main/scala/ohnosequences/scarph/Expressions.scala]
          + [Denotation.scala][main/scala/ohnosequences/scarph/Denotation.scala]
          + [EdgeType.scala][main/scala/ohnosequences/scarph/EdgeType.scala]
          + [VertexType.scala][main/scala/ohnosequences/scarph/VertexType.scala]
          + [Vertex.scala][main/scala/ohnosequences/scarph/Vertex.scala]
          + [Schema.scala][main/scala/ohnosequences/scarph/Schema.scala]
          + [Edge.scala][main/scala/ohnosequences/scarph/Edge.scala]
          + titan
            + [TEdge.scala][main/scala/ohnosequences/scarph/titan/TEdge.scala]
            + [TVertex.scala][main/scala/ohnosequences/scarph/titan/TVertex.scala]
            + [TSchema.scala][main/scala/ohnosequences/scarph/titan/TSchema.scala]
          + [Property.scala][main/scala/ohnosequences/scarph/Property.scala]

[test/scala/ohnosequences/scarph/properties.scala]: ../../../../test/scala/ohnosequences/scarph/properties.scala.md
[test/scala/ohnosequences/scarph/restricted/SimpleSchema.scala]: ../../../../test/scala/ohnosequences/scarph/restricted/SimpleSchema.scala.md
[test/scala/ohnosequences/scarph/restricted/RestrictedSchemaTest.scala]: ../../../../test/scala/ohnosequences/scarph/restricted/RestrictedSchemaTest.scala.md
[test/scala/ohnosequences/scarph/restricted/SimpleSchemaImplementation.scala]: ../../../../test/scala/ohnosequences/scarph/restricted/SimpleSchemaImplementation.scala.md
[test/scala/ohnosequences/scarph/edges.scala]: ../../../../test/scala/ohnosequences/scarph/edges.scala.md
[test/scala/ohnosequences/scarph/vertices.scala]: ../../../../test/scala/ohnosequences/scarph/vertices.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: ../../../../test/scala/ohnosequences/scarph/titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: ../../../../test/scala/ohnosequences/scarph/titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: ../../../../test/scala/ohnosequences/scarph/titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/vertexTypes.scala]: ../../../../test/scala/ohnosequences/scarph/vertexTypes.scala.md
[test/scala/ohnosequences/scarph/edgeTypes.scala]: ../../../../test/scala/ohnosequences/scarph/edgeTypes.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: Expressions.scala.md
[main/scala/ohnosequences/scarph/Denotation.scala]: Denotation.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: EdgeType.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: VertexType.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: Vertex.scala.md
[main/scala/ohnosequences/scarph/Schema.scala]: Schema.scala.md
[main/scala/ohnosequences/scarph/Edge.scala]: Edge.scala.md
[main/scala/ohnosequences/scarph/titan/TEdge.scala]: titan/TEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TVertex.scala]: titan/TVertex.scala.md
[main/scala/ohnosequences/scarph/titan/TSchema.scala]: titan/TSchema.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: Property.scala.md