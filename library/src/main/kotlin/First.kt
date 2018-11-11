package ru.santaev.reactivekotlin

fun <T> Observable<T>.first(): Observable<T> = first { true }

fun <T> Observable<T>.first(predicate: (T) -> Boolean): Observable<T> = first(predicate = Predicate(predicate))

fun <T> Observable<T>.first(predicate: Predicate<T>): Observable<T> {
    return Observable.create(
        OnSubscribe { subscriber ->
            val outputSubscriber = Subscriber(object : Observer<T> {

                var firstItemReceived = false

                override fun onNext(item: T) {
                    if (predicate.apply(item)) {
                        firstItemReceived = true
                        subscriber.onNext(item)
                        subscriber.onSuccess()
                    }
                }

                override fun onSuccess() {
                    subscriber.onSuccess()
                }

                override fun onError(error: Throwable) {
                    subscriber.onError(error)
                }

            })
            this.subscribe(outputSubscriber)
        }
    )
}

interface Predicate<T> {

    fun apply(input: T): Boolean
}

@Suppress("FunctionName")
fun <T> Predicate(predicate: (T) -> Boolean): Predicate<T> {
    return object : Predicate<T> {

        override fun apply(input: T): Boolean = predicate.invoke(input)

    }
}