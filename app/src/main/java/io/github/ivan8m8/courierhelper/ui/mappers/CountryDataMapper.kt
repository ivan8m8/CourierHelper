package io.github.ivan8m8.courierhelper.ui.mappers

import io.github.ivan8m8.courierhelper.data.models.CountryData

class CountryDataMapper {

    fun forCountry(
        countryIso2Name: String,
        countryData: List<CountryData>,
    ): List<CountryData> {
        val boostCountries = boostCountriesStartingWith(countryIso2Name)
        return countryData
            .sortedWith(
                compareBy(
                    { !boostCountries.contains(it.iso2Name) },
                    { boostCountries.indexOf(it.iso2Name) }
                )
            )
    }

    private fun boostCountriesStartingWith(iso2Name: String) = boostCountriesByGdp()
        .toMutableList()
        .apply {
            remove(iso2Name)
            add(0, iso2Name)
        }
        .toList()

    private fun boostCountriesByGdp() = listOf(RU, KZ, UA, UZ, BY, GE, AM, KG, TJ)
}

private const val RU = "RU" // 11
private const val KZ = "KZ" // 48
private const val UA = "UA" // 58
private const val UZ = "UZ" // 70
private const val BY = "BY" // 86
private const val GE = "GE" // 107
private const val AM = "AM" // 115
private const val KG = "KG" // 140
private const val TJ = "TJ" // 141