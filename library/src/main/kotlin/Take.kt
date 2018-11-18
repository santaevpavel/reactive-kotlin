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
            val observer = object : DisposableObserver<T>() {

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

            }
            val outputSubscriber = Subscriber(
                DisposableObserverProxy(observer)
            )
            var upstreamDisposable: Disposable? = null
            val onDispose = OnDispose {
                upstreamDisposable?.dispose()
                observer.dispose()
            }
            subscriber.onDispose = onDispose
            observer.onDispose = onDispose
            upstreamDisposable = this.subscribe(outputSubscriber)
        }
    )
}

abstract class DisposableObserver<T>: Observer<T>, Disposable {

    var onDispose: OnDispose = EmptyOnDispose

    override var isDisposed: Boolean = false

    override fun dispose() {
        if (!isDisposed) {
            isDisposed = true
            onDispose.onDispose()
        }
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