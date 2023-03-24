package be.ugent.quotes.data.remote

class QueueBackend {

    public var cardNumber : String? = null
        get() {return field;}
        set(value) {field = value}


    // Singleton
    companion object {

        @Volatile private var instance: QueueBackend? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: return QueueBackend().also { instance = it }
            }
    }
}