import Actors.{ChatListener, PortChecker}
import MVC.{Controller, Model, View}
import akka.actor.{ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}
import javafx.application.{Application, Platform}
import javafx.application.Application.launch
import javafx.stage.Stage

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._
import scala.collection.mutable
import scala.concurrent.ExecutionContext

class MainApp extends Application {
  override def start(primaryStage: Stage): Unit = {
    val view: View = Controller.chatRoom(primaryStage, "Chat")
    Model.mainChatView = view
    primaryStage.setOnCloseRequest(_ => {
      Model.system.terminate()
      Platform.exit()
      Model.system.whenTerminated.map(_ => {
        System.exit(0)
      })(ExecutionContext.global)
    })
  }
}

object MainApp {
  def main(args: Array[String]): Unit = {
//    Читаем читаем конфигурацию и создаём её объект
    val conf: Config = ConfigFactory.load("application.conf")
//    Получаем порты
    val listOfPorts: mutable.Buffer[Int] = conf.getIntList("seed-ports").asScala.map(_.toInt)
//    Получаем хост hostname = "127.0.0.1"
    val hostname: String = conf.getString("hostname")

//    Создаём акторную систему, которая распоряжается акторами (тяжёлый объект, создавать только 1 раз)
    val system = ActorSystem()

    val props = PortChecker.props(hostname, listOfPorts)
    props.withDispatcher("javafx-dispatcher")

    // Запускаем актор в котором задаётся порт клиента, этот порт сохраняется в модель,
    // а потом читается при создании конфига

    system.actorOf(props, "check-port-actor")


    // Он принимает функцию в качестве аргумента, которая будет вызвана
    // при завершении работы системы, позволяя пользователю выполнять любые
    // необходимые задачи очистки до завершения работы системы. Функция может
    // принимать любое количество аргументов и возвращать результат любого типа.
    // Это позволяет выполнять широкий спектр задач при завершении работы системы,
    // таких как сохранение любых соответствующих данных или закрытие любых открытых ресурсов.
    system.whenTerminated.map(_ => {
      // переопределяем порт вставляя новое значение (akka.remote.artery.canonical.port) из файла cluster-akka.conf
      val config = ConfigFactory.parseString(s"""akka.remote.artery.canonical.port=${Model.port}""")
        .withFallback(ConfigFactory.load("cluster-akka.conf"))

      // создаём уникальную акторную систему
      val system = ActorSystem("ClusterSystem", config)
      // создаём пропер обработчика чата
      val props = Props[ChatListener]

      // сохраняем систему в контроллер
      Controller.setSystem(system)
      // создаём уникальный Актор chatListener
      system.actorOf(props, "chatListener")
    })(ExecutionContext.global)

    //запускаем приложение
    launch(classOf[MainApp], args: _*)
  }
}