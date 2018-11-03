package ru.santaev.reactivekotlin

interface Observable<T> {

    fun subscribe(observer: Observer<T>): Disposable

    fun subscribe(
        onNext: () -> T,
        onSuccess: () -> Unit,
        onError: () -> Throwable
    ): Disposable

    companion object {

        fun <T> create(): Observable<T> {
            TODO("Not implemented")
        }
    }
}

interface Observer<T> {

    fun onNext(item: T)

    fun onSuccess()

    fun onError()
}

interface Disposable {

    fun dispose()
}

fun <T> Observable.Companion.just(vararg items: T): Observable<T> {
    TODO("Not implemented")
}