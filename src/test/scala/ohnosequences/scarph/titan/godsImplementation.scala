package ohnosequences.scarph.titan.test

import GodsSchema._
import ohnosequences.scarph.titan._

case class GodsImplementation(tGraph: com.thinkaurelius.titan.core.TitanGraph) {

  /*
    ### Vertices
  */
  implicit case object titan    extends TitanVertex(tGraph, Titan)
  implicit case object god      extends TitanVertex(tGraph, God)
  implicit case object demigod  extends TitanVertex(tGraph, Demigod)
  implicit case object human    extends TitanVertex(tGraph, Human)
  implicit case object monster  extends TitanVertex(tGraph, Monster)
  implicit case object location extends TitanVertex(tGraph, Location)

  /*
    ### Edges
  */
  implicit case object titanFather  extends TitanEdge(tGraph, god,     TitanFather,  titan)
  implicit case object godFather    extends TitanEdge(tGraph, demigod, GodFather,    god)
  implicit case object humanMother  extends TitanEdge(tGraph, demigod, HumanMother,  human)
  implicit case object brother      extends TitanEdge(tGraph, god,     Brother,      god)
  implicit case object pet          extends TitanEdge(tGraph, god,     Pet,          monster)
  implicit case object battled      extends TitanEdge(tGraph, demigod, Battled,      monster)
  implicit case object godLives     extends TitanEdge(tGraph, god,     GodLives,     location)
  implicit case object monsterLives extends TitanEdge(tGraph, monster, MonsterLives, location)

}
