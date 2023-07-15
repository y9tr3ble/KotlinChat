import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress

class ChatClient(private val serverHost: String, private val serverPort: Int) {
    private var nickname: String? = null

    fun getUserInput(nickname: String?): String? {
        if (nickname == null) {
            println("\nEnter your nickname: ")
        } else {
            println("\nEnter your message: ")
        }
        return readLine()
    }

    fun start() = runBlocking {
        val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress(serverHost, serverPort))
        val output = socket.openWriteChannel(autoFlush = true)
        val input = socket.openReadChannel()

        // coroutine for reading messages from server
        launch(Dispatchers.IO) {
            while(true) {
                val message = input.readUTF8Line()
                message?.let {
                    println("\n> $it")
                    getUserInput(nickname)
                }
            }
        }

        // coroutine for user input
        launch(Dispatchers.IO) {
            while (true) {
                val userIn = getUserInput(nickname)
                if (nickname == null) {
                    nickname = userIn // set nickname on first input
                }
                userIn?.let {
                    output.writeStringUtf8("$it\n")
                }
            }
        }
    }
}

fun main() {
    val client = ChatClient("localhost", 9999)
    client.start()
}