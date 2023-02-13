package MVC

import javafx.beans.property.ReadOnlyStringWrapper
import javafx.event.ActionEvent
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.TableColumn.CellDataFeatures
import javafx.scene.control.{Button, ScrollPane, TableColumn, TableView, TextArea, TextInputDialog}
import javafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import Traits.{Msg, TModel, User}

import java.net.URL
import java.util.{Optional, ResourceBundle}

//ctx == View

class View extends Initializable {

  //маркер приватного чата
  private var privateChat: Boolean = false

  // ссылка на модель
  private val model: TModel = Model()

  def getModel: TModel = model


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
  @FXML
  var scroll: ScrollPane = _  // на всякий


  //методы связанные с fxml
  @FXML
  def handle(ke: KeyEvent): Unit = {
//    при нажатии на энтер (обработчик)
    if (ke.getCode.getCode equals KeyCode.ENTER.getCode) {
      sendMsg()
    }
  }

  @FXML
  def sendMsg(): Unit = {
    //сохранили текст в лист msg
    Controller.pushMsg(this, msgField.getText())
    //очистили поле ввода
    msgField.setText("")
  }

//  Во вью буду использовать только проверки, а сам функционал будет в контроллере
  @FXML
  def openPrivateChat(e: MouseEvent) = {
//    таблица не заполненна и нет ещё приватного чата
    if(usersTable.getSelectionModel != null && !privateChat) {
      //получаем выбранного юзера из таблицы юзеров
      val user: Option[User] = Option(usersTable.getSelectionModel.getSelectedItem)
      user match {
        case user: Some[User] => {

          Controller.openPrivateChat(user.get)

          // А потом очищаем выбранного юзера
          usersTable.getSelectionModel.clearSelection()
        }
        case _ => //ничего не делаем
      }

    }
  }



  //настройки приватного чата
  def initPrivateChat(flag: Boolean): Unit = {
    // false -> true
    privateChat = flag
    // запрет выбора пользователей из таблицы в приватном режиме
    usersTable.setSelectionModel(null)
  }

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
    chatTable.setItems(model.msgList)

    //имя юзера и ссылка на актор
    usersTable.setItems(model.usersList)

//    Эта модель выбора используется для определения того,
//    какие компоненты выбраны в данный момент и как они должны быть отрисованы.
//    null - по стандарту
    chatTable.setSelectionModel(null)

    // запускаем диалоговое окно в которое нужно вписать данные, если хоста нет,
    // иначе просто окно чата (приватного)
    Model.hostUser getOrElse userNameImpute()
  }

}

