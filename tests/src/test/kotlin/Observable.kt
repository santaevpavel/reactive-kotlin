package ru.santaev.reactivekotlin

import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.Test

class MapTests {

    @Test
    fun testJustOneItem() {
        val observable = Observable.just(1)
        val observer: Observer<Int> = mockk(relaxUnitFun = true)

        observable.subscribe(observer)

        verifySequence {
            observer.onNext(1)
            observer.onSuccess()
        }
    }

    @Test
    fun testJustFew() {
        val observable = Observable.just(1, 2, 3)
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
    fun testEmpty() {
        val observable = Observable.empty<Int>()
        val observer: Observer<Int> = mockk(relaxUnitFun = true)

        observable.subscribe(observer)

        verifySequence {
            observer.onSuccess()
        }
    }

    @Test
    fun testError() {
        val error = RuntimeException("RuntimeException")
        val observable = Observable.error<Int>(error)
        val observer: Observer<Int> = mockk(relaxUnitFun = true)

        observable.subscribe(observer)

        verifySequence {
            observer.onError(error)
        }
    }
}

