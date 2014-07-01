package ohnosequences.scarph.titan.test

import GodsSchema._
import ohnosequences.scarph.titan._

object GodsImplementation {

  /*
    ### Vertices
  */
  implicit case object titan    extends TVertex(Titan)
  implicit case object god      extends TVertex(God)
  implicit case object demigod  extends TVertex(Demigod)
  implicit case object human    extends TVertex(Human)
  implicit case object monster  extends TVertex(Monster)
  implicit case object location extends TVertex(Location)

  /*
    ### Edges
  */
  implicit case object titanFather  extends TEdge(god,     TitanFather,  titan)
  implicit case object godFather    extends TEdge(demigod, GodFather,    god)
  implicit case object humanMother  extends TEdge(demigod, HumanMother,  human)
  implicit case object brother      extends TEdge(god,     Brother,      god)
  implicit case object pet          extends TEdge(god,     Pet,          monster)
  implicit case object battled      extends TEdge(demigod, Battled,      monster)
  implicit case object godLives     extends TEdge(god,     GodLives,     location)
  implicit case object monsterLives extends TEdge(monster, MonsterLives, location)

}
