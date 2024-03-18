import org.jetbrains.annotations.NotNull
import java.util.stream.Collectors
import kotlin.streams.toList


data class Chat( // Чат — это общение с одним человеком, так называемые direct messages.
    var id: Int,
    var listMessagers: ListMessagers = ListMessagers() // В каждом чате есть сообщения от 1 до нескольких
)

object ListChats {
    private val chats = mutableListOf<Chat>()
    var countId: Int = 0;
    var countIdMessager: Int = 0;

    //Можно создавать чаты, удалять их, получать список имеющихся чатов
    fun createChat(messager: Messager): MutableList<Chat> {
        val chat: Chat = Chat(++countId, ListMessagers().addMessager(++countIdMessager, messager))
        chats.add(chat)
        return chats
    }

    fun deleteChat(id: Int): Int {
        val index = getindexOf(id)
        if (index != null) {
            chats.removeAt(index)
            return 1
        }
        return 0
    }

    fun getChat(): MutableList<Chat> { // Возвращает список чатов
        return chats
    }


    fun getUnreadChats(): List<Chat> { // Возвращает список непрочитанных чатов
        return chats.filter { chat: Chat -> chat.listMessagers.getUnreadMessagers().isNotEmpty() }
    }


    fun getUnreadChatsCount(): Int { // сколько чатов не прочитано
        return getUnreadChats().size
    }

    private fun getindexOf(id: Int): Int? { // Возвращает индекс элемента в списке
        val chat = chats.filter { chat: Chat -> chat.id == id }
        return if (chat.isNotEmpty()) {
            chats.indexOf(chat[0])
        } else {
            null
        }

    }

    fun addMessager(id: Int, messager: Messager): ListMessagers? {
        val index = getindexOf(id)
        if (index != null) {
            return chats[index].listMessagers.addMessager(++countIdMessager, messager)
        }
        return null

    }

    fun editMessager(id: Int, idMessager: Int, text: String): Int {
        val index = getindexOf(id)
        if (index != null) {
            return chats[index].listMessagers.editMessager(idMessager, text)
        }
        return 0
    }

    fun deleteMessager(id: Int, idMessager: Int): Int {
        val index = getindexOf(id)
        if (index != null) {
            chats[index].listMessagers.deleteMessager(idMessager)
            //if(chats[index].listMessagers.getSize() == 0){ //если в чате нет сообщений, то удалить чат
            //    deleteChat(id)
            //}
            return 1
        }
        return 0
    }

    // Получить список последних сообщений из чатов (можно в виде списка строк).
    // Если сообщений в чате нет (все были удалены), то пишется «нет сообщений».
    fun getLastMessagers():String {

        return chats
            .joinToString(separator = "; ") {"IdChat: " + it.id.toString() + " " +
                it.listMessagers.getListMessagers()
                    .filter { messager: Messager -> messager.text == it.listMessagers.getLastMessager() }
                    .joinToString { "IdMessager: "+ it.id.toString() +" "+ it.text + " IdUser: "+ it.idUser }
            }



        /*
        chats.forEach {
            print(
                "" + if (it.listMessagers.getListMessagers().isNotEmpty()) {
                    it.listMessagers.getListMessagers()
                        .filter { messager: Messager -> messager.text == it.listMessagers.getLastMessager() }
                        .toMutableList().last()
                } else {
                    "Сообщений нет"
                } + "; "
            )
        }
        println()
         */


        /*
        val lastMessagers = mutableListOf<String>()
        val lastMessagersIterator = chats.iterator()

        while (lastMessagersIterator.hasNext()) {
            val chat = lastMessagersIterator.next()
            lastMessagers.add("idChat: " + chat.id + " LastMessager: " + chat.listMessagers.getLastMessager())

        }
        return lastMessagers
    */
    }

    //Получить список сообщений из чата, указав:
    //ID собеседника;
    //количество сообщений. После того как вызвана эта функция,
    // все отданные сообщения автоматически считаются прочитанными.
    fun getChatMessagers(id: Int, idUser: Int): List<Messager>? {
        val index = getindexOf(id)
        if (index != null) {
            return chats[index].listMessagers.getMessagersUser(idUser)
        }
        return null
    }

    fun clear() {
        chats.clear()
        countId = 0
        countIdMessager = 0
    }
}


data class Messager(
    var id: Int,
    var text: String,
    var idUser: Int,
    // В каждом чате есть прочитанные и непрочитанные сообщения
    var notRead: Boolean = true, // true - не прочитано, false - прочитано
    val time: Long = System.currentTimeMillis() // время отправки сообщения
)

class ListMessagers {
    private val messagers = mutableListOf<Messager>()

    //Можно создавать сообщения, редактировать их и удалять. Для простоты — можно удалять и свои, и чужие
    fun addMessager(id: Int, messager: Messager): ListMessagers {
        messagers.add(messager.copy(id = id))
        return this
    }

    fun deleteMessager(id: Int): Int {
        val index = getindexOf(id)
        if (index != null) {
            messagers.removeAt(index)
            return 1
        }
        return 0
    }

    fun editMessager(id: Int, text: String): Int {

        val index = getindexOf(id)
        if (index != null) {
            messagers[index] = messagers[index].copy(text = text, notRead = true)
            return 1
        }
        return 0
    }

    fun getListMessagers(): MutableList<Messager> {
        return messagers
    }

    fun getLastMessager(): String { //получить последнее сообщение
        val sizeList = getSize()
        var lastMessager = "Сообщений нет"
        if (sizeList >= 1) {
            lastMessager = "" + messagers.last().text
        }
        return lastMessager
    }

    fun getLast(): Messager? { //получить последнее сообщение


        if (messagers.isNotEmpty()) {
            return messagers.last()
        }
        return null
    }

    fun getUnreadMessagers(): List<Messager> {
        return messagers.filter { it.notRead }
    }


    fun getMessagersUser(id: Int): List<Messager> { //получить все сообщения по id пользователя

        val list = messagers.filter { messager: Messager -> messager.idUser == id }
        for (unit in list) {
            unit.notRead = false
        }
        return list

    }

    fun getSize(): Int {
        return messagers.size
    }

    fun getindexOf(id: Int): Int? { // Возвращает индекс элемента в списке

        val messager = messagers.filter { messager: Messager -> messager.id == id }
        return if (messager.isNotEmpty()) {
            messagers.indexOf(messager[0])
        } else {
            null
        }
    }

    // индекс последнего элемента
    fun getindexOfLast(): Int? { // Возвращает индекс элемента в списке

        return if (messagers.isNotEmpty()) {
            return messagers.last().id
        } else {
            null
        }
    }

    override fun toString(): String {
        return "ListMessagers(messagers=$messagers)"

    }


}

fun main() {
    val messager1 = Messager(1, "Сообщение 1", 1)
    val messager2 = Messager(2, "Сообщение 2", 3)
    val messager3 = Messager(3, "Сообщение 3", 1)
    val messager4 = Messager(4, "Сообщение 4", 2)
    //val messager5 = Messager(5, "Сообщение 5", 1)
    val messager6 = Messager(6, "Сообщение 6", 2)
    val messager7 = Messager(7, "Сообщение 7", 1)
    println(ListChats.createChat(messager1)) // создаём чат 1 // [Chat(id=1, listMessagers=ListMessagers(messagers=[Messager(id=1, text=Сообщение 1, idUser=1, notRead=true, time=1710441702467)]))]

    ListChats.addMessager(1, messager3) // добавляем ещё одно сообщение в чат 1

    //список сообщений пользователя 1 в чате 1 // [Messager(id=1, text=Сообщение 1, idUser=1, notRead=false, time=1710441702467), Messager(id=2, text=Сообщение 3, idUser=1, notRead=false, time=1710441702467)]
    println(ListChats.getChatMessagers(1, 1))

    ListChats.addMessager(1, messager2) // добавляем ещё одно сообщение в чат 1

    //список сообщений пользователя 3 в чате 1 // [Messager(id=3, text=Сообщение 2, idUser=3, notRead=false, time=1710441702467)]
    println(ListChats.getChatMessagers(1, 3))

    println(ListChats.createChat(messager6)) // создаём чат 2 // [Chat(id=1, listMessagers=ListMessagers(messagers=[Messager(id=1, text=Сообщение 1, idUser=1, notRead=false, time=1710441702467), Messager(id=2, text=Сообщение 3, idUser=1, notRead=false, time=1710441702467), Messager(id=3, text=Сообщение 2, idUser=3, notRead=false, time=1710441702467)])), Chat(id=2, listMessagers=ListMessagers(messagers=[Messager(id=4, text=Сообщение 6, idUser=2, notRead=true, time=1710441702467)]))]
    ListChats.addMessager(2, messager7) // добавляем ещё одно сообщение в чат 2

    println(ListChats.getUnreadChats()) // список чатов с непрочитанными сообщениями // [Chat(id=2, listMessagers=ListMessagers(messagers=[Messager(id=4, text=Сообщение 6, idUser=2, notRead=true, time=1710441702467), Messager(id=5, text=Сообщение 7, idUser=1, notRead=true, time=1710441702467)]))]
    println(ListChats.getUnreadChatsCount()) // количество чатов с непрочитанными сообщениями // 1

    ListChats.addMessager(1, messager4) // добавляем ещё одно сообщение в чат 1
    println(ListChats.getUnreadChats()) // список чатов с непрочитанными сообщениями // [Chat(id=1, listMessagers=ListMessagers(messagers=[Messager(id=1, text=Сообщение 1, idUser=1, notRead=false, time=1710441702467), Messager(id=2, text=Сообщение 3, idUser=1, notRead=false, time=1710441702467), Messager(id=3, text=Сообщение 2, idUser=3, notRead=false, time=1710441702467), Messager(id=6, text=Сообщение 4, idUser=2, notRead=true, time=1710441702467)])), Chat(id=2, listMessagers=ListMessagers(messagers=[Messager(id=4, text=Сообщение 6, idUser=2, notRead=true, time=1710441702467), Messager(id=5, text=Сообщение 7, idUser=1, notRead=true, time=1710441702467)]))]
    println(ListChats.getUnreadChatsCount()) // количество чатов с непрочитанными сообщениями // 2

    ListChats.editMessager(1, 1, "Отредактированное сообщение 1")

    //список сообщений пользователя 1 в чате 1 // [Messager(id=1, text=Отредактированное сообщение 1, idUser=1, notRead=false, time=1710441702467), Messager(id=2, text=Сообщение 3, idUser=1, notRead=false, time=1710441702467)]
    println(ListChats.getChatMessagers(1, 1))

    println( ListChats.getLastMessagers()) // список последних сообщений из всех чатов // IdChat: 1 IdMessager: 6 Сообщение 4 IdUser: 2; IdChat: 2 IdMessager: 5 Сообщение 7 IdUser: 1

    ListChats.deleteMessager(1, 1) // удалим сообщение 1 из чата 1

    //список сообщений пользователя 1 в чате 1 // [Messager(id=2, text=Сообщение 3, idUser=1, notRead=false, time=1710441702467)]
    println(ListChats.getChatMessagers(1, 1))

    ListChats.deleteChat(1)// удалим чат 1
    println( ListChats.getLastMessagers() )// список последних сообщений из всех чатов // IdChat: 2 IdMessager: 5 Сообщение 7 IdUser: 1

}