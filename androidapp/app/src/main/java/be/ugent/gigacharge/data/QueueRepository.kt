package be.ugent.quotes.data

import android.util.Log
import be.ugent.quotes.data.remote.QueueBackend
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuotesRepository @Inject constructor(
    private val backend: QueueBackend
) {

    suspend fun setCardNumber(cardNumber:String){
        Log.i("repository","KAARTNUMMER GEZET NAAR $cardNumber")
        backend.cardNumber = cardNumber;
    }

    fun getCardNumber() = backend.cardNumber

}
