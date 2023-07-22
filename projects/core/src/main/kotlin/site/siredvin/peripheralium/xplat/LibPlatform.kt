package site.siredvin.peripheralium.xplat

object LibPlatform : BasePlatform {
    private var _IMPL: BaseInnerPlatform? = null
    private val _informationTracker = ModInformationTracker()

    fun configure(impl: BaseInnerPlatform) {
        _IMPL = impl
    }

    override val baseInnerPlatform: BaseInnerPlatform
        get() {
            if (_IMPL == null) {
                throw IllegalStateException("You should configure peripheralium LibPlatform first")
            }
            return _IMPL!!
        }

    override val modInformationTracker: ModInformationTracker
        get() = _informationTracker
}
