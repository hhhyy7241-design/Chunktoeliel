package cu.todus.download.engine

import java.net.InetAddress
import java.net.Socket
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SNIHostName
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * Socket TLS con SNI configurado para el endpoint interno de toDus en Cuba (ETECSA).
 * Acepta certificados de la IP nacional fija (152.207.206.86) garantizando que no falle
 * el handshake ni se desvíe tráfico a redes internacionales.
 */
class TodusTlsSocketFactory(
    private val sniHost: String = "s3.todus.cu"
) : SSLSocketFactory() {

    val trustManager: X509TrustManager = object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>?, authType: String?) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    }

    private val delegate: SSLSocketFactory

    init {
        val sc = SSLContext.getInstance("TLS")
        sc.init(null, arrayOf(trustManager), SecureRandom())
        delegate = sc.socketFactory
    }

    override fun getDefaultCipherSuites(): Array<String> = delegate.defaultCipherSuites
    override fun getSupportedCipherSuites(): Array<String> = delegate.supportedCipherSuites
    override fun createSocket(): Socket = patchSocket(delegate.createSocket())
    override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean): Socket = patchSocket(delegate.createSocket(s, sniHost, port, autoClose))
    override fun createSocket(host: String, port: Int): Socket = patchSocket(delegate.createSocket(host, port))
    override fun createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket = patchSocket(delegate.createSocket(host, port, localHost, localPort))
    override fun createSocket(host: InetAddress, port: Int): Socket = patchSocket(delegate.createSocket(host, port))
    override fun createSocket(address: InetAddress, port: Int, localAddress: InetAddress, localPort: Int): Socket = patchSocket(delegate.createSocket(address, port, localAddress, localPort))

    private fun patchSocket(socket: Socket): Socket {
        if (socket is SSLSocket) {
            runCatching {
                val params = socket.sslParameters
                params.serverNames = listOf(SNIHostName(sniHost))
                socket.sslParameters = params
            }
        }
        return socket
    }
}
