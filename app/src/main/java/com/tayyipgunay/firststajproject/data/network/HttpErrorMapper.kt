package com.tayyipgunay.firststajproject.data.network

import com.squareup.moshi.Moshi
import com.tayyipgunay.firststajproject.core.error.AppError
import com.tayyipgunay.firststajproject.core.error.ProblemJson
import retrofit2.Response
import javax.inject.Inject
class HttpErrorMapper @Inject constructor(
    private val moshi: Moshi
) {

    private val adapter = moshi.adapter(ProblemJson::class.java)

    fun <T> map(response: Response<T>): AppError.Http {

        val raw = response.errorBody()?.string()

        val message = raw?.let {
            safeParse(it)
        }
            ?.let {problemJson->
                extractMessage(problemJson)
            }

        return AppError.Http(
            status = response.code(),
            message = message,
            raw = raw
        )
    }

    private fun safeParse(json: String): ProblemJson? =
        try {
            adapter.fromJson(json)
        } catch (_: Exception) {
            null
        }

    private fun extractMessage(problem: ProblemJson): String? {

        val fromFields = problem.fieldErrors
            ?.joinToString("\n") { "- ${it.field}: ${it.message}" }

        return fromFields
            ?: problem.detail
            ?: problem.title
    }
}


/*class HttpErrorMapper @Inject constructor(
    private val moshi: Moshi
) {
    private val problemAdapter = moshi.adapter(ProblemJson::class.java)

    fun <T> map(response: Response<T>): AppError.Http {
        val raw = response.errorBody()?.string()
        println("raw httpErrorMapper: $raw")


        val api = try {
            raw?.let { raw->
                problemAdapter.fromJson(raw)?.toApiError()
            }
        } catch (_: Exception) {
            null
        }

        return AppError.Http(
            status = response.code(),
            api = api,
            raw = raw
        )
    }
}*/
