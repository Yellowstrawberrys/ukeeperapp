import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.ukeeper.ukeeper.SocialManager
import org.ukeeper.ukeeper.db.DataManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class KeeperService(private val db: DataManager, private val srm:SocialManager) : Service() {

    private val dsf = SimpleDateFormat("yyyy-MM-dd")

    var isOk: Boolean = true
    var th:Thread? = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        th = Thread {
            while (isOk) {
                val a = db.getLatest(dsf.format(Date(System.currentTimeMillis())))
                if(a !=null && a+(1000*60*60*3) >= getCurrentMin()) {
                    srm.broadcastWarningMessage(
                        db,
                        "정지효님씨가 위험에 처했습니다.\n" +
                                "전화를 해보시는건 어떠신가요?"
                    )
                    isOk = false
                }
                Thread.sleep(1000*60)
            }
        }

        th?.start()

        return START_STICKY
    }

    private fun getCurrentMin(): Float {
        val calendar: Calendar = Calendar.getInstance()
        return (calendar.get(Calendar.HOUR_OF_DAY)*60 + calendar.get(Calendar.MINUTE)).toFloat()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        isOk = false
        th?.interrupt()
        super.onDestroy()
    }
}