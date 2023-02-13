import MVC.{Controller, Model, View}
import javafx.application.Application
import javafx.application.Application.launch
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

//класс запуска UI
class Mainframe extends Application {
  override def start(primaryStage: Stage): Unit = {
//    Контроллер должен управлять настройкой и запуском проекта
    val view: View = Controller.chatRoom(primaryStage, "Chat")
    Model.mainChatView = view
//    Инициализация тестовых данных
    Controller.init(view)
  }
}

object Mainframe extends App {
//  Launch a standalone application. This method is typically called from the main method().
  launch(classOf[Mainframe], args: _*)
}

