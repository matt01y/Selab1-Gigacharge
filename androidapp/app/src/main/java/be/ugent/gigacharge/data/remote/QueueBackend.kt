package be.ugent.gigacharge.data.remote

class QueueBackend {

    var cardNumber: String? = null


    // Singleton
    companion object {

        @Volatile
        private var instance: QueueBackend? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: return QueueBackend().also { instance = it }
            }
    }
}