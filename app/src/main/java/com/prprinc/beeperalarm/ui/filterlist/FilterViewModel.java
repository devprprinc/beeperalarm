/*
 *     This file is part of BeeperAlarm app.
 *
 *     BeeperAlarm app is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     BeeperAlarm app is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with BeeperAlarm app.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.prprinc.beeperalarm.ui.filterlist;


import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class FilterViewModel extends ViewModel {

    private static final String TAG = "FilterViewModel";

    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList> mFilterList;

    public FilterViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is FILTER LIST fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}