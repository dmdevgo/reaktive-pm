package dev.garage.rpm.app.main.model

import java.util.concurrent.atomic.AtomicReference


class TokenStorage {

    private var tokenRef = AtomicReference<String>("")

    fun saveToken(token: String) {
        tokenRef.set(token)
    }

    fun getToken(): String {
        return tokenRef.get()
    }

    fun clear() {
        tokenRef.set("")
    }
}