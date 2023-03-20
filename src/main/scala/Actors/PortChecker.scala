package Actors

import MVC.Controller
import akka.actor.{Actor, Props}
import akka.io.Tcp.{Connect, Connected}
import akka.io.{IO, Tcp}

import java.net.InetSocketAddress
import scala.collection.mutable

object PortChecker {
  def props(hostname: String, ports: mutable.Buffer[Int]): Props =
    Props(new PortChecker(hostname, ports: mutable.Buffer[Int]))
}

class PortChecker(hostname: String, ports: mutable.Buffer[Int]) extends Actor {

  import context.system

  val connection: Iterator[Int] = ports.iterator
  var port: Int = connection.next()

  IO(Tcp) ! Connect(new InetSocketAddress(hostname, port))

  //Перебирает свободные порты, находит свободные и присваивает их Контролеру
  override def receive: Receive = {
    case c @ Connected(remote, local) =>
      port = connection.next()
      println("Log: Is it a free port?:" + port)
      IO(Tcp) ! Connect(new InetSocketAddress(hostname, port))

    //По прибытии ошибки останавливаем систему
    case Tcp.CommandFailed(_: Tcp.Connect) =>
      println("Log: found free port:" + port)
      Controller.setPort(port)
      //Останавливает работы данного актора
      context.stop(self)
      //Останавливает работы Акторной систему к которой принадлежит Актор,
      //после чего срабатывает Future в объекте MainApp
      context.system.terminate()
  }
}