package ru.santaev.reactivekotlin

import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.Test

class ObservableTakeTests {

    @Test
    fun testInts() {
        val observable = Observable
            .just(1, 2, 3, 4, 5)
            .take(3)
        val observer: Observer<Int> = mockk(relaxUnitFun = true)

        observable.subscribe(observer)

        verifySequence {
            observer.onNext(1)
            observer.onNext(2)
            observer.onNext(3)
            observer.onSuccess()
        }
    }

    @Test
    fun testStrings() {
        val observable = Observable
            .just("a", "b", "c", "d")
            .take(1)
        val observer: Observer<String> = mockk(relaxUnitFun = true)

        observable.subscribe(observer)

        verifySequence {
            observer.onNext("a")
            observer.onSuccess()
        }
    }

    @Test
    fun testOnError() {
        val observable = Observable
            .create(object : OnSubscribe<Int> {

                override fun onSubscribe(subscriber: Subscriber<Int>) {
                    subscriber.onNext(1)
                    subscriber.onNext(2)
                    subscriber.onError(RuntimeException())
                }
            })
            .take(3)
        val observer: Observer<Int> = mockk(relaxUnitFun = true)

        observable.subscribe(observer)

        verifySequence {
            observer.onNext(1)
            observer.onNext(2)
            observer.onError(RuntimeException())
        }
    }
}

