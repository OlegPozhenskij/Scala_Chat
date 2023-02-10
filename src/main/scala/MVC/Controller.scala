package MVC

object Controller {

  //--3
  def init() = {
    //None - заглушка
    val user1 = User("Pozhenskii", None)
    val user2 = User("Chvetkov", None)
    Model.usersList.addAll(user1, user2)

    val msg_1 = Msg("Hi, bro", user1)
    val msg_2 = Msg("What's up?", user2)
    Model.msgList.addAll(msg_1, msg_2)
  }

  //--4
  def sendMsg(msg: String) = {
    val newMsg = Msg(msg, Model.hostUser)
    Model.msgList.add(newMsg)
  }

  //--5
  def setHostUserName(name: String) =
    Model.hostUser = new User(name, null)

}
