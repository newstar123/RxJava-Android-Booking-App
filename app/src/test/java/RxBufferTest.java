import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class RxBufferTest {

    @Test public void testRxBuffer(){
        Observable.range(1,20).buffer(2,1).subscribe(new Action1<List<Integer>>() {
            @Override public void call(List<Integer> integers) {

            }
        });
    }

    @Test public void testIterateTwoLists(){
        List<Integer> integers1 = Arrays.asList(1, 2, 3);
        List<String> integers = Arrays.asList("P", "O", "R");
        List<String> first = Observable.from(integers1).flatMapIterable(new Func1<Integer, Iterable<String>>() {
            @Override public Iterable<String> call(Integer integer) {
                return integers;
            }
        }, new Func2<Integer, String, String>() {
            @Override public String call(Integer integer, String s) {
                return s + integer;
            }
        }).doOnNext(new Action1<String>() {
            @Override public void call(String s) {

            }
        }).toList().toBlocking().first();
    }
}
