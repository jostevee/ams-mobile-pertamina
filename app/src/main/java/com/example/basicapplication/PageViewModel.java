package com.example.basicapplication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {
    private final MutableLiveData<String> mTitle = new MutableLiveData<>();
    private final LiveData<String> mText = Transformations.map(mTitle, input -> "Contact not available in " + input);
    public void setIndex(String index) {
        mTitle.setValue(index);
    }

    public void setName(String name) {
        mTitle.setValue(name);
    }

    public LiveData<String> getText() {
        return mText;
    }
}
