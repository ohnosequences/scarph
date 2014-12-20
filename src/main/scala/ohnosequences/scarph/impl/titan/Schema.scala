package ohnosequences.scarph.impl.titan

/* Here are methods for creating Titan schema from an abstract schema description */
object schema {

  import shapeless._, poly._

  import com.thinkaurelius.titan.core._, Multiplicity._
  import com.thinkaurelius.titan.core.schema._
  import com.tinkerpop.blueprints.Direction
  import scala.reflect._

  import ohnosequences.cosas._, typeSets._, fns._
  import ohnosequences.cosas.ops.typeSets._

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.containers._, s.indexes._, s.schemas._


  /* This takes an edge type and returns Titan `Multiplicity` (i.e. edge arities) */
  trait EdgeTypeMultiplicity[ET <: AnyEdge] extends Fn1[ET] with Out[Multiplicity]

  object EdgeTypeMultiplicity extends EdgeTypeMultiplicity_2 {

    implicit def one2one[ET <: AnyEdge { 
      type InC <: ExactlyOne
      type OutC <: ExactlyOne
    }]: EdgeTypeMultiplicity[ET] =
    new EdgeTypeMultiplicity[ET] { def apply(et: In1): Out = Multiplicity.ONE2ONE }
  }

  trait EdgeTypeMultiplicity_2 extends EdgeTypeMultiplicity_3 {

    implicit def one2many[ET <: AnyEdge {
      type InC <: ExactlyOne
      type OutC <: AnyContainer
    }]: EdgeTypeMultiplicity[ET] =
    new EdgeTypeMultiplicity[ET] { def apply(et: In1): Out = Multiplicity.ONE2MANY }

    implicit def many2one[ET <: AnyEdge {
      type InC <: AnyContainer
      type OutC <: ExactlyOne
    }]: EdgeTypeMultiplicity[ET] =
    new EdgeTypeMultiplicity[ET] { def apply(et: In1): Out = Multiplicity.MANY2ONE }
  }

  trait EdgeTypeMultiplicity_3 {

    implicit def many2many[ET <: AnyEdge {
      type InC <: AnyContainer
      type OutC <: AnyContainer
    }]: EdgeTypeMultiplicity[ET] =
    new EdgeTypeMultiplicity[ET] { def apply(et: In1): Out = Multiplicity.MULTI }
  }


  /* Following `Poly1` functions create separate Titan schema elements from
     the scarph properties/vertices/edges/indexes. They return functions of
     the `TitanManagement => TitanManagement` type, so that we can iterate them
     later on the schema type-sets.
  */
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

    implicit def vertexIx[Ix <: AnyCompositeIndex { type IndexedType <: AnyVertex }]
      (implicit propLabels: MapToList[propertyLabel.type, Ix#Properties] with InContainer[String]) =
      at[Ix]{ (ix: Ix) => { (m: TitanManagement) =>

          propLabels(ix.properties)
            .foldLeft(m.buildIndex(ix.label, classOf[com.tinkerpop.blueprints.Vertex])){
              (builder, lbl) => builder.addKey(m.getPropertyKey(lbl))
            }.buildCompositeIndex : TitanIndex
        }
      }

    implicit def edgeIx[Ix <: AnyCompositeIndex { type IndexedType <: AnyEdge }]
      (implicit propLabels: MapToList[propertyLabel.type, Ix#Properties] with InContainer[String]) =
      at[Ix]{ (ix: Ix) => { (m: TitanManagement) =>

          propLabels(ix.properties)
            .foldLeft(m.buildIndex(ix.label, classOf[com.tinkerpop.blueprints.Edge])){
              (builder, lbl) => builder.addKey(m.getPropertyKey(lbl))
            }.buildCompositeIndex : TitanIndex
        }
      }
  }

  implicit def titanGraphOps(g: TitanGraph): 
    TitanGraphOps = 
    TitanGraphOps(g)

  case class TitanGraphOps(g: TitanGraph) {

    def createSchema[GS <: AnySchema](gs: GS)(implicit
      propertiesMapper: MapToList[addPropertyKey.type, gs.Properties] with 
                        InContainer[TitanManagement => PropertyKey],
      edgeTypesMapper: MapToList[addEdgeLabel.type, gs.Edges] with 
                       InContainer[TitanManagement => EdgeLabel],
      vertexTypesMapper: MapToList[addVertexLabel.type, gs.Vertices] with 
                         InContainer[TitanManagement => VertexLabel],
      indexMapper: MapToList[addIndex.type, gs.Indexes] with 
                   InContainer[TitanManagement => TitanIndex]
    ) = {
      /* We want this to happen all in _one_ transaction */
      val mgmt = g.getManagementSystem

      propertiesMapper(gs.properties).map{ _.apply(mgmt) }
      edgeTypesMapper(gs.edges).map{ _.apply(mgmt) }
      vertexTypesMapper(gs.vertices).map{ _.apply(mgmt) }
      indexMapper(gs.indexes).map{ _.apply(mgmt) }

      mgmt.commit
    }
  }

}
