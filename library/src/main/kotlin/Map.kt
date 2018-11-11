package ru.santaev.reactivekotlin


import ru.santaev.reactivekotlin.*

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

interface ItemTransformer<I, O> {

    fun transform(input: I): O
}

@Suppress("FunctionName")
fun <I, O> ItemTransformer(transformFunction: (I) -> O): ItemTransformer<I, O> {
    return object : ItemTransformer<I, O> {

        override fun transform(input: I): O = transformFunction.invoke(input)
    }
}