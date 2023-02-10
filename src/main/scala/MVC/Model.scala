package MVC

import akka.actor.ActorRef
import javafx.collections.{FXCollections, ObservableList}

//--1
case class User(name: String, actorRef: Option[ActorRef])

case class Msg(msg: String, user: User)

case class ChatRoom(host: User, guest: User)

//--2
// observableArrayList для уменьшения кол-ва уведомлений и работы с ObservableList
object Model {
  val msgList: ObservableList[Msg] = FXCollections.observableArrayList()
  val usersList: ObservableList[User] = FXCollections.observableArrayList()
  var hostUser: User = null
}