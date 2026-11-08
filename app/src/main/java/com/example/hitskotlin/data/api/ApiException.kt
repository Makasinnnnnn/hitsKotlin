package com.example.hitskotlin.data.api

import java.io.IOException

class ApiException(val code: Int, message: String) : IOException(message)
