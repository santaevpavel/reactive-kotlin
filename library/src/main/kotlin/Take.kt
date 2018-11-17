package ru.santaev.reactivekotlin


fun <T> Observable<T>.take(count: Int): Observable<T> {
    return takeWhile(object : Predicate<T> {

        private var numberOfReceivedItems = 0

        override fun apply(input: T): Boolean {
            numberOfReceivedItems++
            return numberOfReceivedItems <= count
        }
    })
}

fun <T> Observable<T>.takeWhile(predicate: (T) -> Boolean): Observable<T> {
    return takeWhile(Predicate(predicate))
}

fun <T> Observable<T>.takeWhile(predicate: Predicate<T>): Observable<T> {
    return Observable.create(
        OnSubscribe { subscriber ->
            val outputSubscriber = Subscriber(
                DisposableObserverProxy(object : DisposableObserver<T>() {

                    override fun onNext(item: T) {
                        if (!predicate.apply(item)) {
                            onSuccess()
                            dispose()
                        } else {
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
            )
            this.subscribe(outputSubscriber)
        }
    )
}

abstract class DisposableObserver<T>: Observer<T>, Disposable {

    override var isDisposed: Boolean = false

    override fun dispose() {
        isDisposed = true
    }
}

class DisposableObserverProxy<T>(private val observer: DisposableObserver<T>) : Observer<T> {

    override fun onNext(item: T) {
        if (!observer.isDisposed) observer.onNext(item)
    }

    override fun onSuccess() {
        if (!observer.isDisposed) observer.onSuccess()
    }

    override fun onError(error: Throwable) {
        if (!observer.isDisposed) observer.onError(error)
    }

}