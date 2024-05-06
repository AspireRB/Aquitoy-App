package com.aspire.aquitoy.ui.requests

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aspire.aquitoy.data.DatabaseService
import com.aspire.aquitoy.ui.requests.model.ServiceInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RequestsViewModel @Inject constructor(private val databaseService: DatabaseService) : ViewModel() {
    val getServiceList: LiveData<List<ServiceInfo>> by lazy {
        MutableLiveData<List<ServiceInfo>>().also { liveData ->
            databaseService.getAllServices { services, error ->
                if (error != null) {
                    Log.d("REALTIME", "ERROR REQUEST")
                } else {
                    liveData.postValue(services)
                }
            }
        }
    }
}
