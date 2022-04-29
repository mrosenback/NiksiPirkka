package com.example.shareyourbestadvice;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyWorker extends Worker {

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        //Data outputData =

        if () {
            return Result.failure(data);
        } else if () {
            return Result.retry()
        }

        return Result.success(data);
    }
}
