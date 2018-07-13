package de.novatec.aws.sample

import java.time.LocalDateTime

class Application {

    fun handleRequest(): String {

        val initDate: LocalDateTime = LocalDateTime.of(2017, 7, 1, 0, 0)
        var helperDate: LocalDateTime = initDate
        var quarters: List<Quarter> = mutableListOf()

        while (!helperDate.isAfter(LocalDateTime.now())) {
            quarters += Quarter(helperDate, helperDate.plusMonths(3).minusDays(1), 0)
            helperDate = helperDate.plusMonths(3)
        }

        var nextPageToken = ""
        @Suppress("UselessCallOnNotNull")
        do {
            val result = GooglePlusAccessor().get(nextPageToken)

            result.items.forEach { r ->
                val q = quarters.find {
                    r.published.isAfter(it.startDate) && r.published.isBefore(it.endDate)
                }
                if(q != null)
                    q.postCount = q.postCount.plus(1)
            }
            nextPageToken = result.nextPageToken
        } while (!result.nextPageToken.isNullOrBlank())
        var resultStr = ""
        quarters.forEach {
            resultStr += """googlePlus_posts{year="${it.startDate.year}",quarter="${((it.startDate.monthValue - 1) / 3) + 1}"} ${it.postCount.toDouble()}
"""
        }
        return resultStr
    }
}

data class Quarter(
        val startDate: LocalDateTime,
        val endDate: LocalDateTime,
        var postCount: Int
)
