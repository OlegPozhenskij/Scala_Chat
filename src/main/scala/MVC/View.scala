package MVC

import javafx.beans.property.ReadOnlyStringWrapper
import javafx.event.ActionEvent
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.TableColumn.CellDataFeatures
import javafx.scene.control.{Button, TableColumn, TableView, TextArea, TextInputDialog}
import javafx.scene.input.MouseEvent

import java.net.URL
import java.util.{Optional, ResourceBundle}

//--9
class View extends Initializable {

  //--6
  //Компоненты соединённые с файлом .fxml
  @FXML
  var msgField: TextArea = _
  @FXML
  var enterMsg: Button = _
  @FXML
  var chatTable: TableView[Msg] = _
  @FXML
  var userId: TableColumn[Msg, String] = _
  @FXML
  var msgId: TableColumn[Msg, String] = _
  @FXML
  var usersTable: TableView[User] = _
  @FXML
  var usersId: TableColumn[User, String] = _

  //--7
  @FXML
  def sendMsg(actionEvent: ActionEvent): Unit = {
    //сохранили текст в лист msg
    Controller.sendMsg(msgField.getText())
    //очистили поле ввода
    msgField.setText("")
  }

  //--11 - пока заглушка
  @FXML
  def openPrivateChat(e: MouseEvent) = {
    println(usersTable.getSelectionModel.getSelectedItem.name)
    // убираем выделенное
    usersTable.getSelectionModel.clearSelection()
  }

  //--8
//  Метода создающий минидиалог для инициализации хоста
  def userNameImpute(): Unit = {
    val dialog = new TextInputDialog("User name")

    dialog.setTitle(null)
    dialog.setHeaderText("Enter your name:")
    dialog.setContentText("Name:")

//    Получаем данные
    val result: Optional[String] = dialog.showAndWait

//    Если значение присутствует, выполняет данное действие со значением,
//    в противном случае ничего не делает.
    result.ifPresent((name: String) => Controller.setHostUserName(name))
  }

  // Метод запустится как только загрузится Room.fxml в Mainframe
  // Метод нужен, когда требуется производить действия в Room.dxml,
  //--10
  override def initialize(location: URL, resources: ResourceBundle): Unit = {

    // как настройки, которые ничего не возвращают---

    // Инициализация таблиы userId (Что-то по типу лисенера, как только появляется новый msg в )
    userId.setCellValueFactory((msg: CellDataFeatures[Msg, String]) => {
      new ReadOnlyStringWrapper(msg.getValue.user.name)
    })

    //ещё одна фабрика по добавлению значения в ячейку
    usersId.setCellValueFactory((user: CellDataFeatures[User, String]) => {
      new ReadOnlyStringWrapper(user.getValue.name)
    })

    //ещё одна фабрика по добавлению значения в ячейку
    msgId.setCellValueFactory((msg: CellDataFeatures[Msg, String]) => {
      new ReadOnlyStringWrapper(msg.getValue.msg)
    })



    // TODO   implement line wrapper later (слушатель заполнения строки)



    // инициализация ячеек и вставка значений

    // в себе содержит и msg, и user
    chatTable.setItems(Model.msgList)

    //имя юзера и ссылка на актор
    usersTable.setItems(Model.usersList)

//    Эта модель выбора используется для определения того,
//    какие компоненты выбраны в данный момент и как они должны быть отрисованы.
//    null - по стандарту
    chatTable.setSelectionModel(null)

    // запускаем диалоговое окно в которое нужно вписать данные
    userNameImpute()
  }

}

