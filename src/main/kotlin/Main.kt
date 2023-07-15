fun main() {
    val server = ChatServer(Config.host, Config.port)
    server.start()
}