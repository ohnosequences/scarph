package ohnosequences.scarph.titan.test

import GodsSchema._
import ohnosequences.scarph.titan._

object GodsImplementation {

  /*
    ### Vertices
  */
  implicit case object titan    extends TitanVertex(Titan)
  implicit case object god      extends TitanVertex(God)
  implicit case object demigod  extends TitanVertex(Demigod)
  implicit case object human    extends TitanVertex(Human)
  implicit case object monster  extends TitanVertex(Monster)
  implicit case object location extends TitanVertex(Location)

  /*
    ### Edges
  */
  implicit case object titanFather  extends TitanEdge(god,     TitanFather,  titan)
  implicit case object godFather    extends TitanEdge(demigod, GodFather,    god)
  implicit case object humanMother  extends TitanEdge(demigod, HumanMother,  human)
  implicit case object brother      extends TitanEdge(god,     Brother,      god)
  implicit case object pet          extends TitanEdge(god,     Pet,          monster)
  implicit case object battled      extends TitanEdge(demigod, Battled,      monster)
  implicit case object godLives     extends TitanEdge(god,     GodLives,     location)
  implicit case object monsterLives extends TitanEdge(monster, MonsterLives, location)

  /*
    ### Indexes
  */
  case object titanNameIndex extends TitanStandardIndex(TitanNameIndex, titan)

}
