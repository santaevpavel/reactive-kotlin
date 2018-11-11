package ru.santaev.reactivekotlin

import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.Test

class ObservableTests {

    @Test
    fun testMapIntToInt() {
        val observable = Observable
            .just(1, 2, 3)
            .map { it * it }
        val observer: Observer<Int> = mockk(relaxUnitFun = true)

        observable.subscribe(observer)

        verifySequence {
            observer.onNext(1)
            observer.onNext(4)
            observer.onNext(9)
            observer.onSuccess()
        }
    }

    @Test
    fun testMapIntToString() {
        val observable = Observable
            .just(1, 2, 3)
            .map { "a".repeat(it) }
        val observer: Observer<String> = mockk(relaxUnitFun = true)

        observable.subscribe(observer)

        verifySequence {
            observer.onNext("a")
            observer.onNext("aa")
            observer.onNext("aaa")
            observer.onSuccess()
        }
    }

    @Test
    fun testMultipleMap() {
        val observable = Observable
            .just(1, 2, 3)
            .map { 2 * it }
            .map { "a".repeat(it) }
        val observer: Observer<String> = mockk(relaxUnitFun = true)

        observable.subscribe(observer)

        verifySequence {
            observer.onNext("aa")
            observer.onNext("aaaa")
            observer.onNext("aaaaaa")
            observer.onSuccess()
        }
    }
}

