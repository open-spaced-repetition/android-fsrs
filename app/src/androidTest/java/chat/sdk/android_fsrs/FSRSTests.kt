package chat.sdk.android_fsrs

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@RunWith(AndroidJUnit4::class)
class FSRSTests {

    @Before
    fun setUp() {

    }

    @Test
    fun testFlashCards() {

        val f = FSRS()
        val card = FSRSCard()

        assert(card.status == Status.New)

        f.p.w = listOf(
            1.14, 1.01, 5.44, 14.67, 5.3024, 1.5662, 1.2503, 0.0028,
            1.5489, 0.1763, 0.9953, 2.7473, 0.0179, 0.3105, 0.3976, 0.0, 2.0902
        )

        val now = Date(1669721400 * 1000)
        var schedulingCards = f.repeat(card, now)

        println(schedulingCards)

        val ratings: List<Rating> = listOf(
            Rating.Good, Rating.Good, Rating.Good, Rating.Good, Rating.Good,
            Rating.Good, Rating.Again, Rating.Again, Rating.Good, Rating.Good,
            Rating.Good, Rating.Good, Rating.Good, Rating.Hard, Rating.Easy, Rating.Good
        )

        val ivlHistory: MutableList<Double> = mutableListOf()
        val statusHistory: MutableList<Status> = mutableListOf()

        for (rating in ratings) {
            schedulingCards[rating]?.let { s ->
                val card = s.card
                ivlHistory.add(card.scheduledDays)

                val revlog = s.reviewLog
                statusHistory.add(revlog.status)
                val now = card.due
                schedulingCards = f.repeat(card, now)

                logSchedulingInfo(schedulingCards)
            }
        }

        println(ivlHistory)
        println(statusHistory)

        assert(ivlHistory == listOf(0.0, 5.0, 16.0, 43.0, 106.0, 236.0, 0.0, 0.0, 12.0, 25.0, 47.0, 85.0, 147.0, 147.0, 351.0, 551.0))
        assert(statusHistory == listOf(
            Status.New, Status.Learning, Status.Review, Status.Review, Status.Review, Status.Review,
            Status.Review, Status.Relearning, Status.Relearning, Status.Review, Status.Review, Status.Review,
            Status.Review, Status.Review, Status.Review, Status.Review
        ))

    }

    fun logSchedulingInfo(schedulingCards: Map<Rating, SchedulingInfo>) {
        schedulingCards.forEach { (rating, s) ->
            println("Rating: $rating")
            println("Card: ${s.card.data()}")
            println("Review Log: ${s.reviewLog.data()}")
        }
    }
}