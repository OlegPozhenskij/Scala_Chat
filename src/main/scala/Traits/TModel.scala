package Traits

import akka.actor.ActorRef
import javafx.collections.{FXCollections, ObservableList}

case class User(name: String, actorRef: Option[ActorRef])

case class Msg(msg: String, user: User)

// Просто перенесли общие элементы в отдельный трейт
trait TModel {
  val msgList: ObservableList[Msg] = FXCollections.observableArrayList()
  val usersList: ObservableList[User] = FXCollections.observableArrayList()
}
