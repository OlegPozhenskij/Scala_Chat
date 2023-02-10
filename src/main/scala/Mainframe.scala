import javafx.application.Application
import javafx.application.Application.launch
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

//класс запуска UI
class Mainframe extends Application {
  override def start(primaryStage: Stage): Unit = {
    MVC.Controller.init()
    // url ресурса (файла)
    val cl = getClass.getResource("Room.fxml")

    //Specify the scene to be used on this stage.
    primaryStage.setScene(new Scene(FXMLLoader.load(cl)))

    primaryStage.setTitle("Title")

    //    Attempts to show this Window by setting visibility to true
    primaryStage.show()
  }
}

object Mainframe extends App {
//  Launch a standalone application. This method is typically called from the main method().
  launch(classOf[Mainframe], args: _*)
}

