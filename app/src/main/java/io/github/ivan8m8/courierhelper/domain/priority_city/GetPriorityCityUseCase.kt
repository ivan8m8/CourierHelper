package io.github.ivan8m8.courierhelper.domain.priority_city

import io.github.ivan8m8.courierhelper.data.db.daos.PriorityCityDao
import io.github.ivan8m8.courierhelper.data.models.PriorityCity
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.schedulers.Schedulers

class GetPriorityCityUseCase(
    private val dao: PriorityCityDao
) {

    operator fun invoke(): Maybe<PriorityCity> {
        return dao.get()
            .subscribeOn(Schedulers.io())
    }
}