package ru.santaev.reactivekotlin

import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.Test

class ObservableFilterTests {

    @Test
    fun testInts() {
        val observable = Observable
            .just(1, 2, 3, 4, 5)
            .filter { it % 2 == 0 }
        val observer: Observer<Int> = mockk(relaxUnitFun = true)

        observable.subscribe(observer)

        verifySequence {
            observer.onNext(2)
            observer.onNext(4)
            observer.onSuccess()
        }
    }

    @Test
    fun testStrings() {
        val observable = Observable
            .just("a", "ab", "ac", "dd")
            .filter { it.contains('a') }
        val observer: Observer<String> = mockk(relaxUnitFun = true)

        observable.subscribe(observer)

        verifySequence {
            observer.onNext("a")
            observer.onNext("ab")
            observer.onNext("ac")
            observer.onSuccess()
        }
    }

    @Test
    fun testIntsWithExceptionOnPredicate() {
        val arr = arrayOf(1, 2, 3)
        val observable = Observable
            .just(1, 2, 3, 4, 5)
            .filter { arr[it] < 5 }
        val observer: Observer<Int> = mockk(relaxUnitFun = true)

        observable.subscribe(observer)

        verifySequence {
            observer.onNext(1)
            observer.onNext(2)
            observer.onNext(3)
            observer.onError(ArrayIndexOutOfBoundsException())
        }
    }
}

