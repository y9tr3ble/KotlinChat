import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClientHandler(private val socket: Socket, private val server: ChatServer) {
    private var name: String = "undefined"
    private val output by lazy { socket.openWriteChannel(autoFlush = true) }
    private val input by lazy { socket.openReadChannel() }

    suspend fun handle() {
        withContext(Dispatchers.IO) {
            name = input.readUTF8Line() ?: "undefined"
            server.broadcast("$name joined chat!", this@ClientHandler)

            var message: String?
            do {
                message = input.readUTF8Line()
                message?.let {
                    withContext(Dispatchers.Default) {
                        server.broadcast("$name: $it", this@ClientHandler)
                    }
                }
            } while (message != null && socket.isClosed.not())

            withContext(Dispatchers.Default) {
                server.broadcast("$name left chat!", this@ClientHandler)
            }
        }
    }

    suspend fun sendMessage(message: String) {
        withContext(Dispatchers.IO) {
            output.writeStringUtf8("$message\n")
            output.flush()
        }
    }
}