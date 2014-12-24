package ohnosequences.scarph.impl.titan

/* Here are methods for creating Titan schema from an abstract schema description */
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


  /* This takes an edge type and returns Titan `Multiplicity` (i.e. edge arities) */
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
      /* We want this to happen all in _one_ transaction */
      val mgmt = g.value.getManagementSystem

      propertiesMapper(sch.properties).map{ _.apply(mgmt) }
      edgeTypesMapper(sch.edges).map{ _.apply(mgmt) }
      vertexTypesMapper(sch.vertices).map{ _.apply(mgmt) }
      indexMapper(sch.indexes).map{ _.apply(mgmt) }

      mgmt.commit
    }
  }

}
