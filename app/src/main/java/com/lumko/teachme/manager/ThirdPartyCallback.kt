package com.lumko.teachme.manager

import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.Data
import com.lumko.teachme.model.Response
import com.lumko.teachme.model.ThirdPartyLogin

interface ThirdPartyCallback {
    fun onThirdPartyLogin(res: Data<Response>, provider: Int, thirdPartyLogin: ThirdPartyLogin)
    fun onErrorOccured(error: BaseResponse)
}