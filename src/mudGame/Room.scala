package mudGame

import akka.actor.Actor
import akka.actor.ActorRef
import collection.immutable.Map
import collection.immutable.Map

class Room(
    //val keyword: String,
    val keword: String,
    val name: String,
    val desc: String,
    private var _loot: List [Item], //change to Set type
    private val exitNames: Array [String]) extends Actor {
  
  private var playersInRoom = collection.mutable.Buffer[ActorRef]()
  
  def loot = _loot
  
  /**
   * Prints Room Description
   */


  println("Made room: "+name)
  
  private var exits: Array[Option[ActorRef]] = Array.empty
  
  import Room._
  
  /**
   * Takes actor messages 
   */
  def receive = {
    case LinkExits(rooms) =>
      exits = exitNames.map(rooms.get)
    /**
     * remove Item from room inventory
     */
    case GetItem(itemName: String) =>
      println(itemName)
      if (loot.isEmpty) {
        sender ! Player.NoSuchItem
      } else {
        for (i <- 0 until loot.length) {
          if (itemName == loot(i).name){
            val prize = loot(i)
            val newLoot = loot.take(i) ++ loot.drop(i+1)
          _loot = newLoot
            sender ! prize
            sender ! Player.PrintItemDesc(prize)
          }
          if (loot(loot.length).name != itemName)
            sender ! Player.NoSuchItem
          }
      }
    /**
     * add item to room inventory
     */
    case DropItem(item) =>
      _loot =  item :: _loot
    /**
     * Takes actor message, calls printDesc
     */
    case PrintDesc =>
      val description = s"$name, $desc\n loot.foreach(_.name\n)"
    
    /**
     * Takes player, Adds player to playersInRoom
     */
    case AddPlayer(player: ActorRef) =>
      playersInRoom += player
    /**
     * Remove Player form playersInRoom
     */
    case DropPlayer(player: ActorRef) =>
      playersInRoom -= player
          
    /**
     * error message 
     */
    case m =>
      println("Oops! Bad message to room: "+m)
  }
} 
  
//Remove item from list
   
 
   
 
 

  

/**
 * companion object to room
 */
object Room {
  case class LinkExits(rooms: Map[String, ActorRef])
  case class GetItem(itemName: String)
  case class DropItem(item: Item)
  case object PrintDesc
  case class AddPlayer(player: ActorRef)
  case class DropPlayer(player: ActorRef)
  // More message types here
  
  /**
   * room apply method 
   */
  def apply(n: xml.Node): (String, () => Room) = {
    val keyword = (n \ "@keyword").text.trim
    val name = (n \ "@name").text.trim
    val desc = (n \ "desc").text.trim
    val items = (n \ "item").map(Item.apply).toList
    val exits = (n \ "exits").text.split(",").map(_.trim)
    (keyword, () => new Room(keyword, name, desc, items, exits))
  }
}  