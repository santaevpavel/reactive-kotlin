package ru.santaev.reactivekotlin

interface Observable<T> {

    fun subscribe(observer: Observer<T>): Disposable

    fun subscribe(
        onNext: (T) -> Unit,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ): Disposable {
        return subscribe(observer = object : Observer<T> {

            override fun onNext(item: T) = onNext(item)

            override fun onSuccess() = onSuccess()

            override fun onError(error: Throwable) = onError.invoke(error)
        })
    }


    companion object
}

interface Observer<T> {

    fun onNext(item: T)

    fun onSuccess()

    fun onError(error: Throwable)
}

class Subscriber<T>(observer: Observer<T>): Observer<T> by observer {

    var isDisposed: Boolean = false

    var onDispose: OnDispose = EmptyOnDispose
}

interface OnDispose{

    fun onDispose()
}

interface OnSubscribe<T> {

    fun onSubscribe(subscriber: Subscriber<T>)
}

interface Disposable {

    val isDisposed: Boolean

    fun dispose()
}

class DisposableImpl(private val onDispose: OnDispose): Disposable {

    override var isDisposed: Boolean = false

    override fun dispose() {
        if (isDisposed) return
        isDisposed = true
        onDispose.onDispose()
    }
}

fun <T> Observable.Companion.just(vararg items: T): Observable<T> {
    return create(onSubscribe = OnSubscribe { subscriber ->
        items.forEach { subscriber.onNext(it) }
        subscriber.onSuccess()
    })
}

fun <T> Observable.Companion.empty(): Observable<T> {
    return create(onSubscribe = OnSubscribe { subscriber ->
        subscriber.onSuccess()
    })
}

fun <T> Observable.Companion.error(throwable: Throwable): Observable<T> {
    return create(onSubscribe = OnSubscribe { subscriber ->
        subscriber.onError(throwable)
    })
}

fun <T, R> Observable<T>.map(transformFunction: (T) -> R): Observable<R> = map(ItemTransformer(transformFunction))

fun <T, R> Observable<T>.map(transformer: ItemTransformer<T, R>): Observable<R> {
    return Observable.create(
        OnSubscribe { subscriber ->
            val outputSubscriber = Subscriber(object : Observer<T> {

                override fun onNext(item: T) {
                    subscriber.onNext(transformer.transform(item))
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

fun <T> Observable.Companion.create(onSubscribe: OnSubscribe<T>): Observable<T> {
    return object : Observable<T> {

        override fun subscribe(observer: Observer<T>): Disposable {
            val subscriber = Subscriber(observer)
            val onDispose = object : OnDispose by subscriber.onDispose { }
            val disposable = DisposableImpl(onDispose)
            onSubscribe.onSubscribe(subscriber)
            return disposable
        }

    }
}

interface ItemTransformer<I, O> {

    fun transform(input: I): O
}

@Suppress("FunctionName")
fun <I, O> ItemTransformer(transformFunction: (I) -> O): ItemTransformer<I, O> {
    return object : ItemTransformer<I, O> {

        override fun transform(input: I): O = transformFunction.invoke(input)
    }
}

@Suppress("FunctionName")
private fun <T> OnSubscribe(onSubscribe: (Subscriber<T>) -> Unit): OnSubscribe<T> {
    return object : OnSubscribe<T> {

        override fun onSubscribe(subscriber: Subscriber<T>) {
            onSubscribe.invoke(subscriber)
        }
    }
}

private object EmptyOnDispose: OnDispose {

    override fun onDispose() {
    }
}