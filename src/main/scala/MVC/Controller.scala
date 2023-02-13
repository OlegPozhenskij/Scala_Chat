package MVC

import Traits.{Msg, User}
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

object Controller {

  //--3
  def init(ctx: View) = {
    //None - заглушка
    val user1 = User("Pozhenskii", None)
    val user2 = User("Tchvetkov", None)
    ctx.getModel.usersList.addAll(user1, user2)

    val msg_1 = Msg("Hi, bro", user1)
    val msg_2 = Msg("What's up?", user2)
    ctx.getModel.msgList.addAll(msg_1, msg_2)
  }

  //--4
  def pushMsg(ctx: View, msg: String) = {
    val newMsg = Msg(msg, Model.hostUser.get) // создале месаге
    ctx.getModel.msgList.add(newMsg) // добавили месаге
    ctx.chatTable.scrollTo(Int.MaxValue) // скрол к последнему сообщению
  }

  def setHostUserName(name: String) =
    Model.hostUser = Option(User(name, None)) // определили хочта

  // инициализация всей комнаты и всего MVC
  def chatRoom(stage: Stage, title: String): View = {
    //TODO тут была ошибка, добавил слэш в путь
    val fxmlLoader = new FXMLLoader(getClass.getResource("/Room.fxml"))
    val root: Parent = fxmlLoader.load()

    //получение класса контроллера из файла Room.fxml, строчка 6 в файле
    val view = fxmlLoader.getController.asInstanceOf[View]
    //добавили сцену в Model
    view.getModel.asInstanceOf[Model].setCtxStage(stage)

    //настройка сцены
    stage.setTitle(title)
    stage.setScene(new Scene(root))
    stage.show()

    view
  }


  def openPrivateChat(withUser: User): Unit = {
    //если уже открыто, то сфокусировать вывести вкладку в верхний слой
    if (Model.chatrms exists (chatRoom => chatRoom.withUser == withUser)) {
      Model.chatrms.filter(chatRoom => chatRoom.withUser == withUser).foreach(chatRoom => {
        chatRoom.ctx.getModel.asInstanceOf[Model].getCtxStage.requestFocus()
      })
    } else { //иначе создать
      val stage = new Stage()
      val ctx: View = chatRoom(stage, s"Private chat with ${withUser.name}")
      ctx.initPrivateChat(true)
      ctx.getModel.usersList.add(withUser)
      val room: ChatRoom = ChatRoom(withUser, ctx)
      Model.chatrms += room
      stage.setOnCloseRequest(_ => {
        Model.chatrms -= room
      })
    }
  }

}
