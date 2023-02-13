package MVC

import Traits.{TModel, User}
import javafx.stage.Stage

case class ChatRoom(withUser: User, ctx: View)

class Model extends TModel {
  //context Stage - то с чем будем работать
  private var ctxStage: Option[Stage] = None

  def setCtxStage(stage: Stage): Unit = {
    ctxStage = Option(stage)
  }

  def getCtxStage = ctxStage.get
}

// observableArrayList для уменьшения кол-ва уведомлений и работы с ObservableList
object Model {

//  User -> Option[User]
  var hostUser: Option[User] = None
//  Сэт комнат доступных статически
  var chatrms: Set[ChatRoom] = Set()
//  Main View
  var mainChatView: View = _

  def apply(): Model = new Model ()

//  Сэт для хоста
  def setHostUser(user: User): Unit = hostUser = Option(user)
}







