package extensions

import korlibs.io.concurrent.atomic.KorAtomicInt


fun KorAtomicInt.sub(int: Int){
    this.addAndGet(-int)
}
