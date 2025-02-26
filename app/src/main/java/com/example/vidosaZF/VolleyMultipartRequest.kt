import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.NetworkResponse
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import java.io.*
import java.util.HashMap

abstract class VolleyMultipartRequest(
    method: Int,
    url: String,
    private val listener: Response.Listener<NetworkResponse>,
    private val errorListener: Response.ErrorListener
) : Request<NetworkResponse>(method, url, errorListener) {

    private val boundary = "apiclient-${System.currentTimeMillis()}"

    override fun parseNetworkResponse(response: NetworkResponse): Response<NetworkResponse> {
        return try {
            Response.success(response, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            Response.error(VolleyError(e))
        }
    }

    override fun deliverResponse(response: NetworkResponse) {
        listener.onResponse(response)
    }

    override fun deliverError(error: VolleyError) {
        errorListener.onErrorResponse(error)
    }

    override fun getBodyContentType(): String {
        return "multipart/form-data; boundary=$boundary"
    }

    override fun getBody(): ByteArray {
        val bos = ByteArrayOutputStream()
        val dos = DataOutputStream(bos)

        try {
            val params = getByteData()
            for ((key, value) in params) {
                buildPart(dos, key, value)
            }

            dos.writeBytes("--$boundary--\r\n")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bos.toByteArray()
    }

    private fun buildPart(dos: DataOutputStream, parameterName: String, data: DataPart) {
        try {
            dos.writeBytes("--$boundary\r\n")
            dos.writeBytes("Content-Disposition: form-data; name=\"$parameterName\"; filename=\"${data.fileName}\"\r\n")
            dos.writeBytes("Content-Type: ${data.type}\r\n\r\n")
            dos.write(data.content)
            dos.writeBytes("\r\n")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    protected abstract fun getByteData(): Map<String, DataPart>

    class DataPart(
        var fileName: String,
        var content: ByteArray,
        var type: String = "application/octet-stream"
    )
}
