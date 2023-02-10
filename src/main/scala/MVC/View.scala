package MVC

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.{Button, TextArea}

class View {

  //Компоненты соединённые с файлом .fxml
  @FXML
  var msgField: TextArea = _
  var enterMsg: Button = _

  // При событии нажаия на кнопу отправляем запускаем метод
  def sendMsg(actionEvent: ActionEvent): Unit = {
    println("New msg: " + msgField.getText)
  }

}

object View {

}
