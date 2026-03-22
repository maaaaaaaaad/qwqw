package com.mad.jellomarkserver.common.persistence

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class LegacyConstraintDropRunner(
    private val jdbcTemplate: JdbcTemplate
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        jdbcTemplate.execute(
            "ALTER TABLE shop_reviews DROP CONSTRAINT IF EXISTS uk_shop_reviews_shop_member"
        )
    }
}
