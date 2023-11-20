package site.siredvin.peripheralium.api.peripheral

interface IObservingPeripheralPlugin : IPeripheralPlugin {
    fun onFirstAttach()
    fun onLastDetach()
}
