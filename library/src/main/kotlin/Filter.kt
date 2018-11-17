package ru.santaev.reactivekotlin

fun <T> Observable<T>.filter(predicate: (T) -> Boolean): Observable<T> = filter(predicate = Predicate(predicate))

fun <T> Observable<T>.filter(predicate: Predicate<T>): Observable<T> {
    return Observable.create(
        OnSubscribe { subscriber ->
            val outputSubscriber = Subscriber(object : Observer<T> {

                override fun onNext(item: T) {
                    if (predicate.apply(item)) {
                        subscriber.onNext(item)
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
