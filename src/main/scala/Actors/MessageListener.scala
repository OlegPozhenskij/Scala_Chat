package Actors

import Actors.MessageListener.{PrivateMsg, PublicMsg}
import akka.actor.{Actor, ActorRef}
import akka.cluster.ClusterEvent.MemberUp

object MessageListener {

  trait Messages

  case class PublicMsg(msg: String) extends Messages

  case class PrivateMsg(msg: String, whom: ActorRef) extends Messages

  case class GetName() extends Messages

}

// TODO прочитать больше про акторы в документации
class MessageListener extends Actor {
  override def receive: Receive = {
    case MemberUp => // заглушка
    case PublicMsg(msg) => println(msg) // просто пока выведем сообщение
    case PrivateMsg(msg, whom) => println(msg, whom) // сообщ и кто вывел
  }
}
