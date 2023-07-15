import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress
import java.util.*
import kotlin.collections.HashSet

class ChatServer(private val host: String, private val port: Int) {
    private val clients = Collections.synchronizedSet(HashSet<ClientHandler>())

    fun start() = runBlocking {
        val server = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(InetSocketAddress(host, port))
        println("Server is running on port $port")

        while (true) {
            val client = server.accept()
            launch(Dispatchers.IO) {
                ClientHandler(client, this@ChatServer).also {
                    clients.add(it)
                }.handle()
            }
        }
    }

    suspend fun broadcast(message: String, sender: ClientHandler) {
        clients.filter { it != sender }.forEach { it.sendMessage(message) }
    }
}