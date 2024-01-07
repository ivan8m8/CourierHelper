package io.github.ivan8m8.courierhelper.domain.priority_city

import io.github.ivan8m8.courierhelper.data.db.daos.PriorityCityDao
import io.github.ivan8m8.courierhelper.data.models.PriorityCity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

class SetPriorityCityUseCase(
    private val dao: PriorityCityDao
) {

    operator fun invoke(city: PriorityCity): Completable {
        return Completable.fromCallable { dao.clearAndInsert(city) }
            .subscribeOn(Schedulers.io())
    }
}