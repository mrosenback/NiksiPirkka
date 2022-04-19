package com.example.shareyourbestadvice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class MyViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<Advice>> advices;

    public MyViewModel(SavedStateHandle savedStateHandle) {
        if (savedStateHandle.contains("advices")) {
            advices = savedStateHandle.getLiveData("advices");
        } else {
            ArrayList<Advice> adviceList = new ArrayList<>();
            advices = new MutableLiveData<>();
            advices.setValue(adviceList);
            savedStateHandle.set("advices", adviceList);
        }
    }

    public LiveData<ArrayList<Advice>> getAdvices() {
        return advices;
    }

    public void addAdvices(Advice advice) {
        ArrayList<Advice> adviceList = advices.getValue();
        adviceList.add(advice);
        advices.setValue(adviceList);
    }
}
