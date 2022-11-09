package com.example.stockmarketapp.data.csv

import com.opencsv.CSVReader
import com.example.stockmarketapp.data.mapper.toIntradayInfo
import com.example.stockmarketapp.data.remote.dto.IntradayInfoDto
import com.example.stockmarketapp.domain.model.CompanyListing
import com.example.stockmarketapp.domain.model.IntradayInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.MonthDay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntradayInfoParser @Inject constructor() : CSVParser<IntradayInfo> {
    override suspend fun parse(stream: InputStream): List<IntradayInfo> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return withContext(Dispatchers.IO){
            csvReader.readAll().drop(1).mapNotNull { line->
                val timestamp = line.getOrNull(0) ?: return@mapNotNull null
                val close = line.getOrNull(4) ?: return@mapNotNull null
                val dto = IntradayInfoDto(timestamp=timestamp, close = close.toDouble())
                dto.toIntradayInfo()
            }
                .filter { it->
                if(LocalDate.now().dayOfWeek==DayOfWeek.MONDAY){
                    it.date.dayOfMonth == LocalDate.now().minusDays(3).dayOfMonth
                }else if(LocalDate.now().dayOfWeek==DayOfWeek.SUNDAY){
                    it.date.dayOfMonth == LocalDate.now().minusDays(2).dayOfMonth
                }
                else{
                    it.date.dayOfMonth == LocalDate.now().minusDays(1).dayOfMonth
                }

            }
                .sortedBy {
                it.date.hour
            }
        }.also {
            csvReader.close()
        }
    }

}