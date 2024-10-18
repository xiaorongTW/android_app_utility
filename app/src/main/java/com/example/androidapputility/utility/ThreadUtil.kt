package com.example.androidapputility.utility

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import org.jetbrains.annotations.TestOnly
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class ThreadUtil {

    companion object {

        // -- Test Only
        private const val TAG = "ThreadUtil"

        private class TestCallableTask : Callable<String> {
            override fun call(): String {
                Log.w(TAG, "TestCallableTask start -->")
                Log.w(TAG, "Wait 500ms ...")
                Thread.sleep(500)
                Log.w(TAG, "TestCallableTask stop <--")
                return "Invoke call() in TestCallableTask."
            }
        }

        @TestOnly
        fun execRunnableByThread() {
            Log.d(TAG, "Test case: execRunnableByThread start -->")
            val thread = Thread {
                Log.w(TAG, "    ${Thread.currentThread().name} start -->")
                Log.w(TAG, "        Run task  -->")
                Thread.sleep(1000)
                Log.w(TAG, "        Task done <--")
                Log.w(TAG, "    ${Thread.currentThread().name} done  <--")
            }
            thread.start()
            Log.d(TAG, "Test case: execRunnableByThread done  <--")
        }

        @TestOnly
        fun execRunnableByHandlerThread() {
            Log.d(TAG, "Test case: execRunnableByHandlerThread start -->")
            val handlerThread1 = HandlerThread("BackgroundThread")
            handlerThread1.start()

            val backgroundHandler = Handler(handlerThread1.looper)
            val mainHandler = Handler(Looper.getMainLooper())

            backgroundHandler.post {
                Log.w(TAG, "    ${Thread.currentThread().name} start -->")
                Log.w(TAG, "        Run task posted by backgroundHandler -->")
                Thread.sleep(1000)
                Log.w(TAG, "        Task done <--")
                Log.w(TAG, "    ${Thread.currentThread().name} done  <--")

                mainHandler.post {
                    Log.w(TAG, "    ${Thread.currentThread().name} start -->")
                    Log.w(TAG, "        Run task posted by mainHandler -->")
                    Thread.sleep(1000)
                    Log.w(TAG, "        Task done <--")
                    Log.w(TAG, "    ${Thread.currentThread().name} done  <--")
                }
            }
            Log.d(TAG, "Test case: execRunnableByHandlerThread done  <--")
        }

        @TestOnly
        fun execRunnableByExecutor() {
            Log.d(TAG, "Test case: execRunnableByExecutor start -->")
            val executor = Executors.newSingleThreadExecutor()
            executor.execute {
                Log.w(TAG, "    ${Thread.currentThread().name} start -->")
                Log.w(TAG, "        Run task  -->")
                Thread.sleep(1000)
                Log.w(TAG, "        Task done <--")
                Log.w(TAG, "    ${Thread.currentThread().name} done  <--")
            }
            executor.shutdown()
            Log.d(TAG, "Test case: execRunnableByExecutor done  -->")
        }

        @TestOnly
        fun execCallableBySingleThreadExecutor() {
            val task = TestCallableTask()

            Log.d(TAG, "Create a executor:")
            val executor = Executors.newSingleThreadExecutor()
            val future = executor.submit(task)
            if (!future.isDone)
                Log.d(TAG, "Task was not done yet!")

            Log.d(TAG, "Get result ...")
            val result = future.get()   // Will block until task was done
            Log.d(TAG, "result: $result")
            if (future.isDone)
                Log.d(TAG, "Task was done!")

            executor.shutdown()
            Log.d(TAG, "Shutdown a executor. Bye!")
        }

        @TestOnly
        fun execRunnableBySingleThreadExecutor() {
            Log.d(TAG, "Create a executor:")
            val executor = Executors.newSingleThreadExecutor()

            for (i in 1..5) {
                executor.execute {
                    Log.w(TAG, "Run task $i --->")
                    Thread.sleep(1000)
                    Log.w(TAG, "Task $i complete!")
                }
            }

            executor.shutdown()
            Log.d(TAG, "Shutdown a executor. Bye!")
        }

        @TestOnly
        fun execRunnableByFixedThreadPool() {
            Log.d(TAG, "Create a executor:")
            val executor = Executors.newFixedThreadPool(3)

            for (i in 1..5) {
                executor.execute {
                    Log.w(TAG, "Run task $i --->")
                    Thread.sleep(1000)
                    Log.w(TAG, "Task $i complete!")
                }
            }

            executor.shutdown()
            Log.d(TAG, "Shutdown a executor. Bye!")
        }

        @TestOnly
        fun execRunnableByScheduledThreadPool() {
            Log.d(TAG, "Create a scheduler:")
            val scheduler = Executors.newScheduledThreadPool(2)

            Log.d(TAG, "Delay 3 seconds to run Task 1 ...")
            scheduler.schedule({
                Log.w(TAG, "Hello, Task 1!")
            }, 3, TimeUnit.SECONDS)

            Log.d(TAG, "Per 2 seconds to run Task 2 ...")
            scheduler.scheduleWithFixedDelay({
                Log.w(TAG, "Hello, Task 2!")
            }, 1, 2, TimeUnit.SECONDS)

            Thread.sleep(10000)
            scheduler.shutdown()
            Log.d(TAG, "Shutdown a scheduler. Bye!")
        }

        @TestOnly
        fun execRunnableByCachedThreadPool() {
            Log.d(TAG, "Create a executor:")
            val executor = Executors.newCachedThreadPool()

            for (i in 1..5) {
                executor.execute {
                    Log.w(TAG, "Run task $i --->")
                    Thread.sleep(1000)
                    Log.w(TAG, "Task $i complete!")
                }
            }

            executor.shutdown()
            Log.d(TAG, "Shutdown a executor. Bye!")
        }

        @TestOnly
        fun execCompletableFuture() {
            Log.d(TAG, "Create an future:")
            val future = CompletableFuture.supplyAsync {
                Log.w(TAG, "Start a task -->")
                Log.w(TAG, "Wait 500ms ...")
                Thread.sleep(500)
                Log.w(TAG, "Task was stopped <--")
                "Task Complete!"
            }

            if (!future.isDone)
                Log.d(TAG, "future was not done yet!")
            future.thenAccept { result ->
                Log.w(TAG, "Result: $result")
                if (future.isDone)
                    Log.d(TAG, "future was done!")
            }

            Log.d(TAG, "Bye!")
        }

        @TestOnly
        fun execCompletableFutureWithChain() {
            Log.d(TAG, "Create an future:")
            val future = CompletableFuture.supplyAsync {
                Log.w(TAG, "Start a task -->")
                Log.w(TAG, "Wait 500ms ...")
                Thread.sleep(500)
                Log.w(TAG, "Task was stopped <--")
                System.currentTimeMillis()
            }.thenApply { result -> // Support chain operation
                "Task Complete! time: " + result + "ms."
            }

            if (!future.isDone)
                Log.d(TAG, "future was not done yet!")
            future.thenAccept { result ->
                Log.w(TAG, "Result: $result")
                if (future.isDone)
                    Log.d(TAG, "future was done!")
            }

            Log.d(TAG, "Bye!")
        }

        @TestOnly
        fun execCompletableFutureWithErrorHandling() {
            Log.d(TAG, "Create an future:")
            val future = CompletableFuture.supplyAsync {
                Log.w(TAG, "Start a task -->")
                Log.w(TAG, "Wait 500ms ...")
                Thread.sleep(500)
                throw RuntimeException("Got error!")
                Log.w(TAG, "Task was stopped <--")
                true
            }.exceptionally { ex ->
                Log.e(TAG, "Got exception: " + ex.message)
                false
            }

            if (!future.isDone)
                Log.d(TAG, "future was not done yet!")
            future.thenAccept { result ->
                Log.w(TAG, "Result: $result")
                if (future.isDone)
                    Log.d(TAG, "future was done!")
            }

            Log.d(TAG, "Bye!")
        }

        @TestOnly
        fun execCompletableFutureWithCombine() {
            Log.d(TAG, "Create an future1:")
            val future1 = CompletableFuture.supplyAsync {
                Log.w(TAG, "Start a task1 -->")
                Log.w(TAG, "Wait 500ms ...")
                Thread.sleep(500)
                Log.w(TAG, "Task1 was stopped <--")
                100
            }

            Log.d(TAG, "Create an future2:")
            val future2 = CompletableFuture.supplyAsync {
                Log.w(TAG, "Start a task2 -->")
                Log.w(TAG, "Wait 1000ms ...")
                Thread.sleep(1000)
                Log.w(TAG, "Task2 was stopped <--")
                200
            }

            if (!future1.isDone)
                Log.d(TAG, "future1 was not done yet!")
            if (!future2.isDone)
                Log.d(TAG, "future2 was not done yet!")

            val combinedFuture = future1.thenCombine(future2) { result1, result2 ->
                result1 + result2
            }
            combinedFuture.thenAccept { result ->
                Log.w(TAG, "Result: $result")
                if (future1.isDone)
                    Log.d(TAG, "future1 was done!")
                if (future2.isDone)
                    Log.d(TAG, "future2 was done!")
            }

            Log.d(TAG, "Bye!")
        }

        @TestOnly
        fun execCompareAtomicWithThreadLocal() {
            Log.d(TAG, "Test case: execCompareAtomicWithThreadLocal start -->")
            val threadLocalInt: ThreadLocal<Int> = ThreadLocal()
            val atomicInt = AtomicInteger(0)

            val thread1 = Thread {
                Log.w(TAG, "    ${Thread.currentThread().name} start -->")
                Log.w(TAG, "        Set threadLocalInt 100 ...")
                threadLocalInt.set(100)
                Log.w(TAG, "        Increase atomicInt 1000 ...")
                repeat(1000) {
                    atomicInt.incrementAndGet()
                }
                Log.w(TAG, "        Increase atomicInt 1000 done")
                Log.w(TAG, "        Wait 2000 ms ...")
                Thread.sleep(2000)
                Log.w(TAG, "        threadLocalInt: ${threadLocalInt.get()}")
                Log.w(TAG, "        atomicInt: ${atomicInt.get()}")
                Log.w(TAG, "    ${Thread.currentThread().name} done  <--")
            }

            val thread2 = Thread {
                Log.i(TAG, "    ${Thread.currentThread().name} start -->")
                Log.i(TAG, "        Set threadLocalInt 200 ...")
                threadLocalInt.set(200)
                Log.i(TAG, "        Increase atomicInt 1000 ...")
                repeat(1000) {
                    atomicInt.incrementAndGet()
                }
                Log.i(TAG, "        Increase atomicInt 1000 done")
                Log.i(TAG, "        threadLocalInt: ${threadLocalInt.get()}")
                Log.i(TAG, "        atomicInt: ${atomicInt.get()}")
                Log.i(TAG, "    ${Thread.currentThread().name} done  <--")
            }

            thread1.start()
            thread2.start()

            thread1.join()
            thread2.join()
            Log.d(TAG, "Test case: execRunnableByThread done  <--")
        }
    }
}