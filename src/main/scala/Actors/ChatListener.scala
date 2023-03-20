package Actors


import MVC.{Controller, Model}
import Trait.{Msg, User}
import akka.actor.{Actor, ActorLogging, ActorSelection}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import conf.JsonSerializable
import javafx.application.Platform

object ChatListener {

  trait ChatCommands extends JsonSerializable

  final case class Init() extends ChatCommands

  final case class IAm(user: User) extends ChatCommands

  final case class Chatting(msg: Msg, isPrivate: Boolean) extends ChatCommands

}

//Cluster Listener -> Chat Listener
class ChatListener() extends Actor with ActorLogging {

  //Сохраняем FrameActor в Controller, настройка записывается при старте проги
  Controller.setFrameActor(self)

  import ChatListener._

  //TODO нужны для того, чтобы сначала получить все ссылки и юзеров путём метода MemberUp
  //TODO а потом в методе Init, добавить всех юзеров и отослать им себя
  var bufferActorRef: Set[ActorSelection] = Set()
  var bufferUsers: Set[User] = Set()

//  Получаем кластер
  val cluster: Cluster = Cluster(context.system)

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    //#subscribe
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
    //#subscribe
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {

//    member = Member(address = akka://ClusterSystem@127.0.0.1:25251, status = Up) это и есть кластер
//    Данный кейс запускается каждый раз при создании кластера, после прогрузки MainApp
//    TODO MemberUp выполняется самым первым
    case MemberUp(member) =>
      println("Member Up")

      val actorSel = context.actorSelection(member.address.toString + "/user/chatListener")
      println(s"address prishedhego membera: ${actorSel.toString()}")

      val selfSel = context.actorSelection(self.path)
      println(s"address this actora: ${selfSel.toString()}")

      val model = Option(Model.mainChatView)

      //Инициализация другого клиента
      if (!selfSel.equals(actorSel)) {
        println("Not equals!")
        model match {
//          Если уже есть модель, то отправляем сообщение (IAM) другому юзеру,
//          для того чтобы в том юзере инициализировать этот
          case Some(_) => {
            println("+++ model")
            Model.hostUser.map(u => actorSel ! IAm(u))
          }
//          Сохраняем новый chatListener в буффер
          case None => {
            println("--- model")
            bufferActorRef += actorSel
          }
        }
      } else {
        println("equals!")
      }


    //    TODO u: IAm выполняется после метода MemberUp, когда появляются новые юзеры
//    Данный метод будет вызываться, для инициализации других юзеров в своей системе
    case u: IAm =>
      println("IAM")
      val model = Option(Model.mainChatView)
      model match {
        case Some(_) =>
          print("dobavlayo usera tak kak model est")
          Platform.runLater(() => {
            Model.mainChatView.getModel.usersList.add(u.user)
          })
          // присоединяем узлы кластера
          cluster.joinSeedNodes(Seq(sender().path.address))
        case None =>
          //пока модель не сгенерировалась окончательно(появится окно с чатом), сохранять юзеров в буффер
          print("modeli net, ne dobavlayo")
          bufferUsers += u.user
      }


    //    инициализация данного клиента
    case _: Init =>
      print("Init { ")
      print(bufferUsers.mkString("Arr{", ", " , "}"))

      Platform.runLater(() => {
//        Уже имеющися юзеров добавляем к себе
//        Изначально bufferUsers пуст
        bufferUsers.foreach(user =>
          Model.mainChatView.getModel.usersList.add(user)
        )
      })

      print(" Host" + Model.hostUser)
      print(" to another actors call IAm()")
//    Инициализируем себя в других юзерах
//    Изначально bufferActorRef пуст
      Model.hostUser.map(u => bufferActorRef.foreach(_ ! IAm(u)))



    //    если юзер недоступен попадает сюда
    case UnreachableMember(member) =>
      Controller.deleteUser(member, context)

    //    если юзер удалён, попадает сюда
    case MemberRemoved(member, _) =>
      Controller.deleteUser(member, context)

    case chat: Chatting => {
      println("MSG")
      Controller.publishMsg(chat, context)
    }

    case _: MemberEvent => // ignore

  }
}
