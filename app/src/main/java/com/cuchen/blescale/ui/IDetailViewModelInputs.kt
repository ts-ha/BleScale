package com.cuchen.blescale.ui

interface IDetailViewModelInputs {
    fun startBle()
    fun stopBle()
    fun openImdbClicked(deviceAddress: String)
    fun rateClicked()
    fun setScale(scale: Int, value: String)


}
