import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.Test
import ru.santaev.reactivekotlin.Observable
import ru.santaev.reactivekotlin.Observer
import ru.santaev.reactivekotlin.just

class ObservableTests {

    @Test
    fun testJust() {
        val observable = Observable.just(1, 2, 3)
        val observer: Observer<Int> = mockk()
        observable.subscribe(observer)

        verifySequence {
            observer.onNext(1)
            observer.onNext(2)
            observer.onNext(3)
            observer.onSuccess()
        }
    }

}
