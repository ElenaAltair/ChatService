import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class ListChatsTest {

    @Before
    fun clearBeforeTest() {
        ListChats.clear()
    }

    @Test
    fun createChat() {
        var messager1 = Messager(1, "Сообщение 1", 1)
        val chats = ListChats.createChat(messager1)

        assertEquals(1, chats.size)
    }

    @Test
    fun addMessager1() {
        var messager1 = Messager(1, "Сообщение 1", 1)
        ListChats.createChat(messager1)

        var messager2 = Messager(2, "Сообщение 2", 1)
        ListChats.addMessager(1, messager2)
        var messager3 = Messager(3, "Сообщение3", 1)
        ListChats.addMessager(1, messager3)

        //assertEquals(3, ListChats.countIdMessager)
        assertEquals("IdChat: 1 IdMessager: 3 Сообщение3 IdUser: 1", ListChats.getLastMessagers())

    }

    @Test
    fun addMessager2() { // добавления сообщения в несуществующий чат
        var messager2 = Messager(2, "Сообщение 2", 1)
        ListChats.addMessager(1, messager2)

        // количество чатов с непрочитанными сообщениями
        assertEquals(0, ListChats.getUnreadChatsCount())

        // сообщения в ещё не созданном чате 1 пользователя 1
        assertEquals(null, ListChats.getChatMessagers(1, 1))
    }

    @Test
    fun deleteMessager() {
        var messager1 = Messager(1, "Сообщение 1", 1)
        ListChats.createChat(messager1)

        assertEquals( 1, ListChats.deleteMessager(1, 1))
    }
}